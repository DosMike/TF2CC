package com.github.dosmike.tf2cc.sql;

import com.github.dosmike.tf2cc.gameserver.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class DatabaseInterface {

    private static Logger logger = LoggerFactory.getLogger("Database");

    private static Connection connection = null;
    private static PreparedStatement ps_accountFromSession;
    private static PreparedStatement ps_pushServer;
    private static PreparedStatement ps_checkServer;
    static {
        try {
            ensureConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    "jdbc:mariadb://db:3306/"+System.getenv("MYSQL_DATABASE"),
                    System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASSWORD")
            );
            String prefix = System.getenv("MYSQL_TABLE_PREFIX");
            ps_accountFromSession = connection.prepareStatement("SELECT `AccountID` FROM `"+prefix+"sessions` WHERE `SessionID` = ?");
            ps_pushServer = connection.prepareStatement("INSERT INTO `"+prefix+"servers` (`Address`, `Port`, `Region`, `Name`, `Map`) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `Region` = VALUES(`Region`), `Name` = VALUES(`Name`), `Map` = VALUES(`Map`), `LastUpdate` = NOW()");
            ps_checkServer = connection.prepareStatement("SELECT `Enabled` FROM `"+prefix+"servers` WHERE `Address` = ? AND `Port` = ?");
        }
    }

    public static Optional<Long> getAccountFromSession(String session) {
        try {
            ensureConnection();
            ps_accountFromSession.setString(1, session);
            ResultSet result = ps_accountFromSession.executeQuery();
            long aid = 0;
            if (result.first()) aid = result.getLong(1);
            result.close();
            if (aid != 0) return Optional.of(aid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void pushServer(Server server) {
        String mapName = "custom";
        if (server.getMap() != null) mapName = server.getMap().getMapName();
        if (server.getRegion() == null || server.getName() == null) {
            logger.error("Could not store Server at {}: region or name was empty", server.getAddress());
            return;
        }
        try {
            ensureConnection();
            ps_pushServer.setString(1, server.getAddress().getAddress().getHostAddress());
            ps_pushServer.setInt(2, server.getAddress().getPort());
            ps_pushServer.setString(3, server.getRegion().name());
            ps_pushServer.setString(4, server.getName());
            ps_pushServer.setString(5, mapName);
            ps_pushServer.executeUpdate();
        } catch (Exception e) {
            logger.error("Could not store Server at "+server.getAddress(), e);
        }
    }

    public static int checkServer(Server server) {
        server.setDbEnabled(1); //default enabled
        try {
            ensureConnection();
            ps_checkServer.setString(1, server.getAddress().getAddress().getHostAddress());
            ps_checkServer.setInt(2, server.getAddress().getPort());
            ResultSet result = ps_checkServer.executeQuery();
            int value = 0;
            if (result.first()) {
                value = result.getInt(1);
                server.setDbEnabled(value);
            }
            result.close();
            if (value != 0) return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}
