package ai.freya;

import l2p.commons.util.Rnd;
import l2p.gameserver.ai.CtrlEvent;
import l2p.gameserver.ai.Fighter;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.entity.Reflection;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.serverpackets.ExShowScreenMessage;
import l2p.gameserver.serverpackets.components.NpcString;
import l2p.gameserver.utils.Location;

public class IceCaptainKnight extends Fighter {

    private long _destroyTimer = 0;
    private boolean _destroyUsed = false;
    private boolean _isHard = false;
    Reflection r = _actor.getReflection();

    public IceCaptainKnight(NpcInstance actor) {
        super(actor);
        //actor.setItemDropEnabled(false);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        _destroyTimer = System.currentTimeMillis();
        _isHard = r.getInstancedZoneId() == 144;
        for (Player p : r.getPlayers()) {
            this.notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5);
        }
    }

    @Override
    protected void thinkAttack() {
        if (!_destroyUsed && _destroyTimer + 60 * 1000L < System.currentTimeMillis()) {
            _destroyUsed = true;
            int mode = Rnd.get(3);
            if (!r.isDefault()) {
                for (Player p : r.getPlayers()) {
                    p.sendPacket(new ExShowScreenMessage(NpcString.THE_SPACE_FEELS_LIKE_ITS_GRADUALLY_STARTING_TO_SHAKE, 5000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, true));
                }
            }
            switch (mode) {
                case 0:
                    Functions.npcShout(getActor(), NpcString.ARCHER);
                    break;
                case 1:
                    Functions.npcShout(getActor(), NpcString.MY_KNIGHTS);
                    break;
                case 2:
                    Functions.npcShout(getActor(), NpcString.I_CAN_TAKE_IT_NO_LONGER);
                    break;
                case 3:
                    Functions.npcShout(getActor(), NpcString.ARCHER_);
                    break;
            }
            int count = _isHard ? 7 : 5;
            for (int i = 0; i < count; i++) {
                r.addSpawnWithoutRespawn(_isHard ? 18856 : 18855, Location.findAroundPosition(_actor, 350, 370), _actor.getGeoIndex());
            }
        }

        super.thinkAttack();
    }

    @Override
    protected void teleportHome() {
    }
}
