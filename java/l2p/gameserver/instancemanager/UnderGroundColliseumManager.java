package l2p.gameserver.instancemanager;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import l2p.gameserver.model.Zone;
import l2p.gameserver.model.Zone.ZoneType;
import l2p.gameserver.model.entity.Coliseum;
import l2p.gameserver.utils.ReflectionUtils;

public class UnderGroundColliseumManager {

    protected static Logger _log = Logger.getLogger(UnderGroundColliseumManager.class.getName());
    private static UnderGroundColliseumManager _instance;
    private HashMap<String, Coliseum> _coliseums;

    public static UnderGroundColliseumManager getInstance() {
        if (_instance == null) {
            _instance = new UnderGroundColliseumManager();
        }
        return _instance;
    }

    public UnderGroundColliseumManager() {
        List<Zone> zones = ReflectionUtils.getZonesByType(ZoneType.UnderGroundColiseum);
        if (zones.size() == 0) {
            _log.info("Not found zones for UnderGround Colliseum!!!");
        } else {
            for (Zone zone : zones) {
                getColiseums().put(zone.getName(), new Coliseum());
            }
        }
        _log.info("Loaded: " + getColiseums().size() + " UnderGround Colliseums.");
    }

    public HashMap<String, Coliseum> getColiseums() {
        if (this._coliseums == null) {
            this._coliseums = new HashMap();
        }
        return this._coliseums;
    }

    public Coliseum getColiseumByLevelLimit(int limit) {
        for (Coliseum coliseum : this._coliseums.values()) {
            if (coliseum.getMaxLevel() == limit) {
                return coliseum;
            }
        }
        return null;
    }
}