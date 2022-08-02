package l2p.gameserver.model;

import java.util.Collections;
import java.util.List;
import l2p.gameserver.instancemanager.ReflectionManager;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.serverpackets.L2GameServerPacket;
import l2p.gameserver.serverpackets.components.IStaticPacket;
import l2p.gameserver.templates.item.WeaponTemplate;
import l2p.gameserver.utils.Log;

public class ObservePoint
        extends Creature {

    private final Player _player;

    public ObservePoint(Player player) {
        super(player.getObjectId(), player.getTemplate());
        _player = player;
        setFlying(true);
    }

    @Override
    public Player getPlayer() {
        return _player;
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public int getMoveSpeed() {
        return 400;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        if ((getReflection() == ReflectionManager.DEFAULT) && (!isMovementDisabled()) && (!getPlayer().getPlayerAccess().CanUseMovingObservationPoint)) {
            Log.add("Movable observation point from player " + getPlayer().toString() + " somehow appeared in main world !", "warning");
        }
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        return false;
    }

    public List<L2GameServerPacket> deletePacketList(Player forPlayer) {
        return Collections.emptyList();
    }

    public boolean isObservePoint() {
        return true;
    }

    @Override
    public boolean isInvul() {
        return true;
    }

    @Override
    public boolean isHealBlocked() {
        return true;
    }

    @Override
    public boolean isEffectImmune() {
        return true;
    }

    @Override
    public void sendPacket(IStaticPacket p) {
        _player.sendPacket(p);
    }

    @Override
    public void sendPacket(IStaticPacket... packets) {
        _player.sendPacket(packets);
    }

    @Override
    public void sendPacket(List<? extends IStaticPacket> packets) {
        _player.sendPacket(packets);
    }

    protected void broadcastMove() {
        _player.sendPacket(movePacket());
    }

    public void broadcastStopMove() {
        _player.sendPacket(stopMovePacket());
    }

    @Override
    public void validateLocation(int broadcast) {
    }

    @Override
    public boolean isCreature() {
        return false;
    }
}
