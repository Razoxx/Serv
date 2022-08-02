package services;

import l2p.gameserver.listener.actor.player.OnPlayerUpLvlListener;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.listener.CharListenerList;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;

public class ItemsLvlUp extends Functions implements ScriptFile, OnPlayerUpLvlListener {

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

    @Override
    public void onLvlUp(Player player) {
        if (player.isLangRus()) {
            player.sendMessage("Поздравляем, Вы получили " + player.getLevel() + " уровень.");
        } else {
            player.sendMessage("Congratulations, you've got " + player.getLevel() + " lvl.");
        }
    }

}
