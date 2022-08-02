/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import l2p.gameserver.listener.actor.OnDeathListener;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.actor.listener.CharListenerList;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.utils.ItemFunctions;

/**
 *
 * @author Deprecated
 */
public class ItemToClanWar extends Functions implements ScriptFile, OnDeathListener {

    /* Включение / Отключение скрипта */
    private final boolean Allowed = true;

    /* Уровень участвующих кланов */
    private static final int clan_level = 6;

    /* Награда и кол-во */
    private static final int item_id = 4037;
    private static final int item_id_count = 3;

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
    public void onDeath(Creature actor, Creature killer) {
        if (!Allowed) {
            return;
        }

        if (actor == null) {
            return;
        }

        if (killer == null) {
            return;
        }

        if (killer == actor) {
            return;
        }

        if (killer.getLevel() < 85 && actor.getLevel() < 85) {
            return;
        }

        ItemInstance item;

        if (killer.isPlayer() && actor.isPlayer()) {

            final boolean atwar = killer.getPlayer() != null && actor.getPlayer().atMutualWarWith(killer.getPlayer());

            if (atwar) {

                if (killer.getPlayer().getClan().getLevel() >= clan_level && actor.getPlayer().getClan().getLevel() >= clan_level) {
                    item = ItemFunctions.createItem(item_id);
                    item.setCount(item_id_count);

                    killer.getPlayer().getInventory().addItem(item);
                    killer.getPlayer().sendPacket(SystemMessage2.obtainItems(item));
                }
            }
        }
    }

}
