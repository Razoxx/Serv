package l2p.gameserver.serverpackets.components;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.scripts.Scripts;
import l2p.gameserver.scripts.Scripts.ScriptClassAndMethod;
import l2p.gameserver.serverpackets.ActionFail;
import l2p.gameserver.serverpackets.ExNpcQuestHtmlMessage;
import l2p.gameserver.serverpackets.L2GameServerPacket;
import l2p.gameserver.serverpackets.NpcHtmlMessage;
import l2p.gameserver.utils.HtmlUtils;
import l2p.gameserver.utils.velocity.VelocityUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlMessage implements IStaticPacket {

    private static final Logger _log = LoggerFactory.getLogger(HtmlMessage.class);
    public static final String OBJECT_ID_VAR = "OBJECT_ID";
    private String _filename;
    private String _html;
    private Map<String, Object> _variables;
    private Map<String, String> _replaces;
    private NpcInstance _npc;
    private int _npcObjId;
    private int _questId;
    private boolean have_appends = false;

    public HtmlMessage(Player player, int npcId, String filename, int val) {
        List<ScriptClassAndMethod> appends = Scripts.dialogAppends.get(npcId);
        if (appends != null && appends.size() > 0) {
            have_appends = true;
            if (filename != null && filename.equalsIgnoreCase("npcdefault.htm")) {
                setHtml(""); // контент задается скриптами через DialogAppend_
            } else {
                //setFile(filename);
            }

            String replaces = "";

            // Добавить в конец странички текст, определенный в скриптах.
            Object[] script_args = new Object[]{new Integer(val)};
            for (ScriptClassAndMethod append : appends) {
                Object obj = Scripts.getInstance().callScripts(player, append.className, append.methodName, script_args);
                if (obj != null) {
                    replaces += obj;
                }
            }

            if (!replaces.equals("")) {
                replace("</body>", (HtmlUtils.bbParse(replaces) + "</body>"));
            }
        }
    }

    public HtmlMessage(Player player, NpcInstance npc, String filename, int val) {
        this(player, npc.getNpcId(), filename, val);
        _npc = npc;
        _npcObjId = npc.getObjectId();
        _filename = filename;
    }

    public HtmlMessage(Player player, NpcInstance npc) {
        this(player, npc, null, 0);
    }

    public HtmlMessage(int npcObjId) {
        _npcObjId = npcObjId;
    }

    public HtmlMessage setHtml(String text) {
        if (!text.contains("<html>")) {
            text = "<html><body>" + text + "</body></html>"; //<title>Message:</title> <br><br><br>
        }
        _html = text;
        return this;
    }

    public final HtmlMessage setFile(String file) {
        _filename = file;
        return this;
    }

    public final HtmlMessage setQuestId(int questId) {
        _questId = questId;
        return this;
    }

    public HtmlMessage addVar(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null!");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null!");
        }
        if (name.startsWith("${")) {
            throw new IllegalArgumentException("Incorrect name: " + name);
        }
        if (_variables == null) {
            _variables = new HashMap(2);
        }
        _variables.put(name, value);
        return this;
    }

    public HtmlMessage replace(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null!");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null!");
        }
        /*    if ((!name.startsWith("%")) || (!name.endsWith("%"))) {
         throw new IllegalArgumentException("Incorrect name: " + name);
         }*/
        if (_replaces == null) {
            _replaces = new LinkedHashMap(2);
        }
        _replaces.put(name, value);
        return this;
    }

    public HtmlMessage replace(String name, NpcString npcString) {
        return replace(name, HtmlUtils.htmlNpcString(npcString, ArrayUtils.EMPTY_OBJECT_ARRAY));
    }

    public HtmlMessage replace(String name, NpcString npcString, Object... arg) {
        if (npcString == null) {
            throw new IllegalArgumentException("NpcString can't be null!");
        }
        return replace(name, HtmlUtils.htmlNpcString(npcString, arg));
    }

    @Override
    public L2GameServerPacket packet(Player player) {

        CharSequence content = null;
        if ((player.isInDuel() || player.InIsDuel()) && !Config.ALLOW_HTML_IN_DUEL) {
            String html = HtmCache.getInstance().getHtml("command/htmlduel.htm", player);
            content = make(html);
            return new NpcHtmlMessage(_npcObjId, content);
        }
        if (_npc != null) {
            if (Config.ALLOW_HTML_DISTANCE_DIALOG && ArrayUtils.contains(Config.ALLOW_HTML_DISTANCE_DIALOG_NPC, _npc.getNpcId()) && !player.isInRangeZ(_npc, Creature.INTERACTION_DISTANCE)) {

                player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
                return null;
            }
        }

        /*
         _log.info("_npcObjId ="+_npcObjId+" _questId="+_questId);
         if (_npcObjId != 5 && _npcObjId != 0 && _questId == 0 && !player.isInRangeZ(_npc, Creature.INTERACTION_DISTANCE)) {
         player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
         return null;
         }*/
        if (!StringUtils.isEmpty(_html)) {
            content = make(_html);
        } else if (!StringUtils.isEmpty(_filename)) {
            if (player.isGM()) {
                player.sendMessage("HTML: " + _filename);
            }
            String htmCashe = HtmCache.getInstance().getNotNull(_filename, player);
            content = make(htmCashe);
        } else {
            _log.warn("HtmlMessage: empty dialog" + (_npc == null ? "!" : new StringBuilder().append(" npc id : ").append(_npc.getNpcId()).append("!").toString()), new Exception());
        }

        player.setLastNpc(_npc);

        player.getBypassStorage()
                .parseHtml(content, false);
        if (StringUtils.isEmpty(content)) {
            return ActionFail.STATIC;
        }
        if (_questId
                == 0) {
            return new NpcHtmlMessage(_npcObjId, content);
        }

        return new ExNpcQuestHtmlMessage(_npcObjId, content, _questId);
    }

    public Map<String, Object> getVariables() {
        return _variables;
    }

    private CharSequence make(String content) {
        if (content == null) {
            return "";
        }
        StrBuilder sb = null;
        if (_replaces != null) {
            sb = new StrBuilder(content);
            for (Map.Entry<String, String> e : _replaces.entrySet()) {
                sb.replaceAll((String) e.getKey(), (String) e.getValue());
            }
        }
        if (_npcObjId != 0) {
            if (sb == null) {
                sb = new StrBuilder(content);
            }
            sb.replaceAll("%objectId%", String.valueOf(_npcObjId));
            if (_npc != null) {
                sb.replaceAll("%npcId%", String.valueOf(_npc.getNpcId()));
            }
        }
        if (sb != null) {
            content = sb.toString();
            sb.clear();
        }
        content = VelocityUtils.evaluate(content, _variables);
        if (!content.startsWith("<html>")) {
            if (sb == null) {
                sb = new StrBuilder(content.length());
            }
            sb.append("<html><body>");
            sb.append(content);
            sb.append("</body></html>");

            if (_replaces != null) {
                for (Map.Entry<String, String> e : _replaces.entrySet()) {
                    sb.replaceAll((String) e.getKey(), (String) e.getValue());
                }
            }
            if (_npcObjId != 0) {
                sb.replaceAll("%objectId%", String.valueOf(_npcObjId));
                if (_npc != null) {
                    sb.replaceAll("%npcId%", String.valueOf(_npc.getNpcId()));
                }
            }

            return sb;
        }
        return content;
    }
}
