package npc.model;

import java.util.concurrent.Future;

import l2p.commons.util.Rnd;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.GameObjectTasks;
import l2p.gameserver.model.Playable;
import l2p.gameserver.model.instances.MonsterInstance;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.serverpackets.components.NpcString;
import l2p.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 23:34/28.04.2012
 */
public class PrizeLuckyPigInstance extends MonsterInstance {

    private static final int ItemName_52_A = 14678;
    private static final int ItemName_52_B = 8755;
    private static final int ItemName_70_A = 14679;
    private static final int ItemName_70_B_1 = 5577;
    private static final int ItemName_70_B_2 = 5578;
    private static final int ItemName_70_B_3 = 5579;
    private static final int ItemName_80_A = 14680;
    private static final int ItemName_80_B_1 = 9552;
    private static final int ItemName_80_B_2 = 9553;
    private static final int ItemName_80_B_3 = 9554;
    private static final int ItemName_80_B_4 = 9555;
    private static final int ItemName_80_B_5 = 9556;
    private static final int ItemName_80_B_6 = 9557;

    private int _pickCount;
    private int _pigLevel;

    private int _temp;

    private Future<?> _task;

    public PrizeLuckyPigInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void calculateRewards(Creature lastAttacker) {
        if (!(lastAttacker instanceof Playable)) {
            return;
        }

        _task.cancel(false);

        switch (_pigLevel) {
            case 52:
                if (_pickCount >= 5) {
                    dropItem(lastAttacker.getPlayer(), ItemName_52_A, 1);
                } else if (_pickCount >= 2 && _pickCount < 5) {
                    dropItem(lastAttacker.getPlayer(), ItemName_52_B, 2);
                } else {
                    dropItem(lastAttacker.getPlayer(), ItemName_52_B, 1);
                }
                break;
            case 70:
                if (_pickCount >= 5) {
                    dropItem(lastAttacker.getPlayer(), ItemName_70_A, 1);
                } else if (_pickCount >= 2 && _pickCount < 5) {
                    if (_temp == 2) {
                        dropItem(lastAttacker.getPlayer(), ItemName_70_B_1, 2);
                    } else if (_temp == 1) {
                        dropItem(lastAttacker.getPlayer(), ItemName_70_B_2, 2);
                    } else {
                        dropItem(lastAttacker.getPlayer(), ItemName_70_B_3, 2);
                    }
                } else {
                    if (_temp == 2) {
                        dropItem(lastAttacker.getPlayer(), ItemName_70_B_1, 1);
                    } else if (_temp == 1) {
                        dropItem(lastAttacker.getPlayer(), ItemName_70_B_2, 1);
                    } else {
                        dropItem(lastAttacker.getPlayer(), ItemName_70_B_3, 1);
                    }
                }
                break;
            case 80:
                if (_pickCount >= 5) {
                    dropItem(lastAttacker.getPlayer(), ItemName_80_A, 1);
                } else if (_pickCount >= 2 && _pickCount < 5) {
                    if (_temp == 5) {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_1, 2);
                    } else if (_temp == 4) {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_2, 2);
                    } else if (_temp == 3) {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_3, 2);
                    } else if (_temp == 2) {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_4, 2);
                    } else if (_temp == 1) {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_5, 2);
                    } else {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_6, 2);
                    }
                } else {
                    if (_temp == 5) {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_1, 1);
                    } else if (_temp == 4) {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_2, 1);
                    } else if (_temp == 3) {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_3, 1);
                    } else if (_temp == 2) {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_4, 1);
                    } else if (_temp == 1) {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_5, 1);
                    } else {
                        dropItem(lastAttacker.getPlayer(), ItemName_80_B_6, 1);
                    }
                }
                break;
        }
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        _temp = Rnd.get(0, 5);

        if (_temp == 0) {
            Functions.npcSayInRange(this, 600, NpcString.OH_MY_WINGS_DISAPPEARED_ARE_YOU_GONNA_HIT_ME_IF_YOU_HIT_ME_ILL_THROW_UP_EVERYTHING_THAT_I_ATE);
        } else {
            Functions.npcSayInRange(this, 600, NpcString.OH_MY_WINGS_ACK_ARE_YOU_GONNA_HIT_ME_SCARY_SCARY_IF_YOU_HIT_ME_SOMETHING_BAD_IS_GOING_HAPPEN);
        }

        _task = ThreadPoolManager.getInstance().schedule(new GameObjectTasks.DeleteTask(this), 600000L);
    }

    public void setPickCount(int pickCount) {
        _pickCount = pickCount;
    }

    public void setPigLevel(int pigLevel) {
        _pigLevel = pigLevel;
    }
}
