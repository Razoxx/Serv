package l2p.gameserver.templates.spawn;

import l2p.gameserver.utils.Location;

public interface SpawnRange {

    Location getRandomLoc(int geoIndex);
}
