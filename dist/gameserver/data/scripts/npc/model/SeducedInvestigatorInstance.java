package npc.model;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.entity.Reflection;
import l2p.gameserver.model.instances.MonsterInstance;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.components.HtmlMessage;
import l2p.gameserver.templates.npc.NpcTemplate;

public class SeducedInvestigatorInstance extends MonsterInstance {

    public SeducedInvestigatorInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(true);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        NpcInstance actor = this;
        Reflection r = actor.getReflection();
        if (r.isCollapseStarted()) {
            player.sendPacket(new HtmlMessage(player, this, "common/seducedinvestigator1.htm", val));
        } else {
            player.sendPacket(new HtmlMessage(player, this, "common/seducedinvestigator.htm", val));
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        NpcInstance actor = this;
        Reflection r = actor.getReflection();
        if (command.equalsIgnoreCase("startCollapse")) {
            if (r.isCollapseStarted()) {
                r.startCollapseTimer(5 * 1000L);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        Player player = attacker.getPlayer();
        if (player == null) {
            return false;
        }
        if (player.isPlayable()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isMovementDisabled() {
        return true;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}
