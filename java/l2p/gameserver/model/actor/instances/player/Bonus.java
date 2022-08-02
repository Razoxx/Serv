package l2p.gameserver.model.actor.instances.player;

public class Bonus {

    public static final int NO_BONUS = 0;
    public static final int BONUS_GLOBAL_ON_AUTHSERVER = 1;
    public static final int BONUS_GLOBAL_ON_GAMESERVER = 2;
    public int bonusId = 0;
    
    private int bonusExpire;

    public int getBonusId() {
        return bonusId;
    }

    public void setBonusId(int id) {
        this.bonusId = id;
    }

    public int getBonusExpire() {
        return bonusExpire;
    }
    
    public long getBonusExpireX() {
        return bonusExpire * 1000L;
    }

    public void setBonusExpire(int bonusExpire) {
        this.bonusExpire = bonusExpire;
    }
}