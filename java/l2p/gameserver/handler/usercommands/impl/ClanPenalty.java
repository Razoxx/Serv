package l2p.gameserver.handler.usercommands.impl;

import java.text.SimpleDateFormat;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.handler.usercommands.IUserCommandHandler;
import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.components.HtmlMessage;
import l2p.gameserver.utils.HtmlUtils;

/**
 * Support for command: /clanpenalty
 */
public class ClanPenalty implements IUserCommandHandler {

    private static final int[] COMMAND_IDS = {100, 114};

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (COMMAND_IDS[0] != id) {
            return false;
        }

        long leaveClan = 0;
        if (activeChar.getLeaveClanTime() != 0) {
            leaveClan = activeChar.getLeaveClanTime() + 1 * 24 * 60 * 60 * 1000L;
        }
        long deleteClan = 0;
        if (activeChar.getDeleteClanTime() != 0) {
            deleteClan = activeChar.getDeleteClanTime() + 10 * 24 * 60 * 60 * 1000L;
        }
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String html = HtmCache.getInstance().getHtml("command/penalty.htm", activeChar);

        if (activeChar.getClanId() == 0) {
            if (leaveClan == 0 && deleteClan == 0) {
                html = html.replaceFirst("%reason%", "No penalty is imposed.");
                html = html.replaceFirst("%expiration%", " ");
            } else if (leaveClan > 0 && deleteClan == 0) {
                html = html.replaceFirst("%reason%", "Penalty for leaving clan.");
                html = html.replaceFirst("%expiration%", format.format(leaveClan));
            } else if (deleteClan > 0) {
                html = html.replaceFirst("%reason%", "Penalty for dissolving clan.");
                html = html.replaceFirst("%expiration%", format.format(deleteClan));
            }
        } else if (activeChar.getClan().canInvite()) {
            html = html.replaceFirst("%reason%", "No penalty is imposed.");
            html = html.replaceFirst("%expiration%", " ");
        } else {
            html = html.replaceFirst("%reason%", "Penalty for expelling clan member.");
            html = html.replaceFirst("%expiration%", format.format(activeChar.getClan().getExpelledMemberTime()));
        }

        HtmlMessage msg = new HtmlMessage(5);
        msg.setHtml(HtmlUtils.bbParse(html));
        activeChar.sendPacket(msg);
        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}