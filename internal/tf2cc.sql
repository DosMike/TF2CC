CREATE TABLE `cc_servers` (
  `Address` varchar(16) NOT NULL,
  `Port` smallint(5) UNSIGNED NOT NULL,
  `Region` varchar(12) NOT NULL,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Map` varchar(64) NOT NULL DEFAULT '',
  `Network` varchar(64) NOT NULL DEFAULT '',
  `Enabled` tinyint(4) NOT NULL DEFAULT 1,
  `LastUpdate` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

ALTER TABLE `cc_servers`
  ADD UNIQUE KEY `Address` (`Address`,`Port`);
CREATE TABLE `cc_sessions` (
  `ID` int(11) NOT NULL,
  `SessionID` varchar(64) NOT NULL,
  `AccountID` int(11) NOT NULL,
  `DisplayName` varchar(64) NOT NULL,
  `AvatarURL` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

ALTER TABLE `cc_sessions`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `AccountID` (`AccountID`);

ALTER TABLE `cc_sessions`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
