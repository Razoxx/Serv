package l2p.gameserver.model;

public interface PlayersInWorld {

    public void storePlayer(GameObject player);

    public void removePlayer(GameObject player);

    public Player get(int objId);

    public void run();
}