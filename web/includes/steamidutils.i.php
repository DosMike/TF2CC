<?php

/** Converter for SteamIDs.
 * Universe is assumed 0, Type is assumed U (Individual), Instance is assumed 1
 */
class SteamID {
    public readonly int $accountID;

    /** Get the SteamID from a 32-bit AccountID (int), SteamID2, SteamID3 or SteamID64 (string) */
    public function __construct($steamid) {
        if (is_int($steamid)) {
            if (($steamid & (int)0xFFFFFFFF00000000) == (int)0x0110000100000000 || ($steamid & (int)0xFFFFFFFF00000000) == 0) {
                $this->accountID = ($steamid & (int)0xFFFFFFFF);
                return;
            }
        } elseif (is_string($steamid)) {
            if (preg_match('/^[0-9]+$/', $steamid)!==false) {
                $int = intval("{$steamid}");
                if (($steamid & (int)0xFFFFFFFF00000000) == (int)0x0110000100000000 || ($steamid & (int)0xFFFFFFFF00000000) == 0) {
                    $this->accountID = ($int & (int)0xFFFFFFFF);
                    return;
                }
            } elseif (preg_match('/^STEAM_[0-5]:[01]:[0-9]+$/')!==false) {
                $parts = explode(':',$steamid);
                $this->accountID = (intval($parts[1]) + intval($parts[2]) * 2);
                return;
            } elseif (preg_match('/^\\[[IiUMGAPCgTLca]:1:[0-9]+\\]$/')!==false) {
                $this->accountID = (intval(rtrim(substr($steamid, 5)),']'));
                return;
            }
        }
        throw new RuntimeException('Invalid SteamID format: '.$steamid);
    }

    /** Get a SteamID2 from an AccountID
     * Universe: 0(Individual/Unspecified) 1(Public)
     */
    function toSteam2($universe=0) {
        $accountID = $this->accountID & 0xFFFFFFFF; //make sure we only use the unsigned 32-bit part
        return 'STEAM_'.$universe.':'.($accountID&1).':'.(intval($accountID/2));
    }

    /** Get a SteamID3 from an AccountID. Types:
     * I(Invalid) U(Individual) G(GameServer) A(AnonymousGameServer) g(Clan/SteamGroup)
     */
    function toSteam3($type='U') {
        return '['.$type.':1:'.($this->accountID & 0xFFFFFFFF).']';
    }

    /** Get a Steam Community ID from an AccountID:
     * Universe: 0(Individual/Unspecified) 1(Public)
     * Types: 0(Invalid) 1(Individual) 3(GameServer) 4(AnonymousGameServer) 7(Clan/SteamGroup)
     * Instance: Use 1..3 for Type 1, 0 otherwise, 1 is default
     */
    function toSteam64($universe=1, $type=1, $instance=1) {
        return (($universe & 0x0FF) << 56) | (($type & 0x0F) << 52) | (($instance & 0x0FFFFF) << 32) | ($this->accountID & 0xFFFFFFFF);
    }
}
