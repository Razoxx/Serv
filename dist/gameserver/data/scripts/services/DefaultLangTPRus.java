package services;

import l2p.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.listener.CharListenerList;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;

public class DefaultLangTPRus extends Functions implements ScriptFile, OnPlayerEnterListener {

    @Override
    public void onPlayerEnter(Player player) {
        if (getPlayerLang(player) == 0) {
            player.setVar("tplang@", "ru", -1);
        }
    }

    private static int getPlayerLang(Player player) {
        String res = player.getVar("tplang@");
        if (res == null || res.isEmpty() || res.equalsIgnoreCase("en") || (res.equalsIgnoreCase("e")) || (res.equalsIgnoreCase("eng"))) {
            return 0;
        }
        return 1;
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}
