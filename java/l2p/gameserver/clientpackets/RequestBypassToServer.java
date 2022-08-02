package l2p.gameserver.clientpackets;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import l2p.gameserver.Config;
import l2p.gameserver.data.xml.holder.MultiSellHolder;
import l2p.gameserver.handler.admincommands.AdminCommandHandler;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2p.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2p.gameserver.instancemanager.OlympiadHistoryManager;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.GameObject;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.entity.Hero;
import l2p.gameserver.model.entity.olympiad.Olympiad;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.model.instances.OlympiadManagerInstance;
import l2p.gameserver.network.GameClient;
import l2p.gameserver.scripts.Scripts;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.HtmlMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.utils.BypassStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBypassToServer extends L2GameClientPacket {
    //Format: cS

    private static final Logger _log = LoggerFactory.getLogger(RequestBypassToServer.class);
    private String _bypass = null;

    @Override
    protected void readImpl() {
        _bypass = readS();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _bypass.isEmpty()) {
            return;
        }

        BypassStorage.ValidBypass bp = activeChar.getBypassStorage().validate(_bypass);
        if (bp == null) {
            _log.debug("RequestBypassToServer: Unexpected bypass : " + _bypass + " client : " + getClient() + "!");
            return;
        }

        try {
            NpcInstance npc = activeChar.getLastNpc();
            GameObject target = activeChar.getTarget();
            if (npc == null && target != null && target.isNpc()) {
                npc = (NpcInstance) target;
            }

            if (_bypass.startsWith("admin_")) {
                AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, _bypass);
            } else if (_bypass.equals("come_here") && activeChar.isGM()) {
                comeHere(getClient());
            } else if (_bypass.startsWith("player_help ")) {
                playerHelp(activeChar, _bypass.substring(12));
            } else if (_bypass.startsWith("scripts_")) {
                String command = _bypass.substring(8).trim();
                String[] word = command.split("\\s+");
                String[] args = command.substring(word[0].length()).trim().split("\\s+");
                String[] path = word[0].split(":");
                if (path.length != 2) {
                    _log.warn("Bad Script bypass!");
                    return;
                }

                activeChar.setLastNpc((NpcInstance) npc);
                
                Map<String, Object> variables = null;
                if (npc != null) {
                    variables = new HashMap<String, Object>(1);
                    variables.put("npc", npc.getRef());
                }

                if (word.length == 1) {
                    Scripts.getInstance().callScripts(activeChar, path[0], path[1], variables);
                } else {
                    Scripts.getInstance().callScripts(activeChar, path[0], path[1], new Object[]{args}, variables);
                }
            } else if (_bypass.startsWith("user_")) {
                String command = _bypass.substring(5).trim();
                String word = command.split("\\s+")[0];
                String args = command.substring(word.length()).trim();
                IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(word);

                if (vch != null) {
                    vch.useVoicedCommand(word, activeChar, args);
                } else {
                    _log.warn("Unknow voiced command '" + word + "'");
                }
            } else if (_bypass.startsWith("npc_")) {
                int endOfId = _bypass.indexOf('_', 5);
                String id;
                if (endOfId > 0) {
                    id = _bypass.substring(4, endOfId);
                } else {
                    id = _bypass.substring(4);
                }
                GameObject object = activeChar.getVisibleObject(Integer.parseInt(id));
                if (object != null && object.isNpc() && endOfId > 0 && activeChar.isInRange(object.getLoc(), Creature.INTERACTION_DISTANCE)) {
                    activeChar.setLastNpc((NpcInstance) object);
                    ((NpcInstance) object).onBypassFeedback(activeChar, _bypass.substring(endOfId + 1));
                }
            } else if (_bypass.startsWith("_olympiad?")) {
                String[] ar = _bypass.replace("_olympiad?", "").split("&");
                String firstVal = ar[0].split("=")[1];
                String secondVal = ar[1].split("=")[1];

                if (firstVal.equalsIgnoreCase("move_op_field")) {
                    if (!Config.ENABLE_OLYMPIAD_SPECTATING) {
                        return;
                    }

                    // Переход в просмотр олимпа разрешен только от менеджера или с арены.
                    if ((activeChar.getLastNpc() instanceof OlympiadManagerInstance && activeChar.getLastNpc().isInRange(activeChar, Creature.INTERACTION_DISTANCE)) || activeChar.getOlympiadObserveGame() != null) {
                        Olympiad.addSpectator(Integer.parseInt(secondVal) - 1, activeChar);
                    }
                }
            } else if (_bypass.startsWith("_diary")) {
                String params = _bypass.substring(_bypass.indexOf("?") + 1);
                StringTokenizer st = new StringTokenizer(params, "&");
                int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                int heroid = Hero.getInstance().getHeroByClass(heroclass);
                if (heroid > 0) {
                    Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
                }
            } else if (_bypass.startsWith("_match")) {
                String params = _bypass.substring(_bypass.indexOf("?") + 1);
                StringTokenizer st = new StringTokenizer(params, "&");
                int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                int heropage = Integer.parseInt(st.nextToken().split("=")[1]);

                OlympiadHistoryManager.getInstance().showHistory(activeChar, heroclass, heropage);
            } else if (_bypass.startsWith("manor_menu_select?")) // Navigate throught Manor windows
            {
                GameObject object = activeChar.getTarget();
                if (object != null && object.isNpc()) {
                    ((NpcInstance) object).onBypassFeedback(activeChar, _bypass);
                }
            } else if (_bypass.startsWith("multisell ")) {
                MultiSellHolder.getInstance().SeparateAndSend(Integer.parseInt(_bypass.substring(10)), activeChar, 0);
            } else if (_bypass.startsWith("Quest ")) {
                String p = _bypass.substring(6).trim();
                int idx = p.indexOf(' ');
                if (idx < 0) {
                    activeChar.processQuestEvent(p, "", npc);
                } else {
                    activeChar.processQuestEvent(p.substring(0, idx), p.substring(idx).trim(), npc);
                }
            } else if (bp.bbs) {
                if (!Config.COMMUNITYBOARD_ENABLED) {
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
                } else {
                    ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(_bypass);
                    handler.onBypassCommand(activeChar, _bypass);
                }
            }
        } catch (Exception e) {
            //_log.error("", e);
            String st = "Bad RequestBypassToServer: " + _bypass;
            GameObject target = activeChar.getTarget();
            if (target != null && target.isNpc()) {
                st = st + " via NPC #" + ((NpcInstance) target).getNpcId();
            }
            _log.error(st, e);
        }
    }

    private static void comeHere(GameClient client) {
        GameObject obj = client.getActiveChar().getTarget();
        if (obj != null && obj.isNpc()) {
            NpcInstance temp = (NpcInstance) obj;
            Player activeChar = client.getActiveChar();
            temp.setTarget(activeChar);
            temp.moveToLocation(activeChar.getLoc(), 0, true);
        }
    }

    private static void playerHelp(Player activeChar, String path) {
        HtmlMessage html = new HtmlMessage(5);
        html.setFile(path);
        activeChar.sendPacket(html);
    }
}
