package l2p.gameserver.masteriopack.bbsbuffer;

import l2p.gameserver.Config;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Summon;

/**
 * @author Masterio
 *
 */
public class BBSBufferChecker {

    /**
     * Returns TRUE if player or summon's master can be buff.
     *
     * @param player
     * @return
     */
    public static boolean checkBuffConditions(Player player) {

        if (player != null && player._bbsbuffer != null) {
            if (player._bbsbuffer.getFilter().isShowBuffsForPlayer()) {
                return checkConditionsPlayer(player);
            }

            if (player.getPet() != null) {
                return checkConditionsSummon(player.getPet());
            }

            player.sendMessage(player.isLangRus() ? "Сначала вы должны принести животное!" : "First you need to bring an animal!");

        }

        return false;
    }

    /**
     * Special check for restore Cp, Hp and Mp. Returns TRUE if okay.
     *
     * @param player
     * @return
     */
    public static boolean checkRestoreConditions(Player player) {
        if (player != null && player._bbsbuffer != null) {
            if (player._bbsbuffer.getFilter().isShowBuffsForPlayer()) {
                return checkRestoreConditionsPlayer(player);
            }

            if (player.getPet() != null) {
                return checkRestoreConditionsSummon(player.getPet());
            }

            player.sendMessage(player.isLangRus() ? "Сначала вы должны принести животное!" : "First you need to bring an animal!");

        }

        return false;
    }

    /**
     * Check if can buff the player
     *
     * @param player
     * @return TRUE if buff is allowed.
     */
    private static boolean checkConditionsPlayer(Player player) {
        if (player == null) {
            return false;
        }

        if (Config.CHECK_DEATH && (player.isDead() || player.isAlikeDead() || player.isFakeDeath())) {
            player.sendMessage(player.isLangRus() ? "В данный момент нельзя использовать BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_ACTION && (player.isCastingNow() || player.isInCombat() || player.isAttackingNow())) {
            player.sendMessage(player.isLangRus() ? "В данный момент нельзя использовать BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_OLY && player.isInOlympiadMode()) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать BBS Buffer в бою или на олимпиаде!" : "You can not use BBS Buffer being on the Olympic Stadium!");
            return false;
        }

        if (Config.CHECK_EVENT && player.getEvents() != null && player.getEvents().size() > 0) {
            player.sendMessage(player.isLangRus() ? "BBS Buffer нельзя использовать в данной локации" : "BBS Buffer can not be used in this the area!");
            return false;
        }

        if (Config.CHECK_FLY && (player.isFlying() || player.isInFlyingTransform())) {
            player.sendMessage(player.isLangRus() ? "В полете нельзя использовать бафера!" : "You can not use BBS Buffer while in flight!");
            return false;
        }

        if (Config.CHECK_BOAT && player.isInBoat()) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать бафера на лодке!" : "You can not use BBS Buffer while on board ship!");
            return false;
        }

        if (Config.CHECK_MOUNTED && player.isMounted()) {
            player.sendMessage(player.isLangRus() ? "Сидя на животном нельзя использовать бафера!" : "You can not use BBS Buffer sitting on the animal!");
            return false;
        }

        if (Config.CHECK_CANT_MOVE && (player.isMovementDisabled() || player.isParalyzed() || player.isStunned() || player.isSleeping() || player.isRooted() || player.isImmobilized())) {
            player.sendMessage(player.isLangRus() ? "В данный момент нельзя использовать BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_STORE_MODE && (player.isInStoreMode() || player.isInTrade() || player.isInOfflineMode())) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать бафера во время торговли" : "While trades can not use the BBS Buffer!");
            return false;
        }

        if (Config.CHECK_FISHING && player.isFishing()) {
            player.sendMessage(player.isLangRus() ? "Во время рыбалки нельзя использовать бафера!" : "Do not use while fishing BBS Buffer!");
            return false;
        }

        if (Config.CHECK_TEMP_ACTION && (player.isLogoutStarted() || player.isTeleporting())) {
            player.sendMessage(player.isLangRus() ? "В данный момент нельзя использовать BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_DUEL && player.isInDuel()) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать бафера в дуэли!" : "During the duel, you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_CURSED_WEAPON && player.isCursedWeaponEquipped()) {
            player.sendMessage(player.isLangRus() ? "Акаманах и Зарич не могут пользоватся бафером, для хардкора :D" : "The owner of the cursed weapons can not use the BBS Buffer!");
            return false;
        }

        if (Config.CHECK_PK && player.getKarma() > 0) {
            player.sendMessage(player.isLangRus() ? "ПК не могут пользоватся бафером, для хардкора :D!" : "Killer players can not use the BBS Buffer!");
            return false;
        }

        if (Config.CHECK_CLAN_LEADER && !player.isClanLeader()) {
            player.sendMessage(player.isLangRus() ? "Лишь избранный клан лидер может воспользоватся бафером!" : "BBS Buffer can use only the clan leader!");
            return false;
        }

        if (Config.CHECK_NOBLE && !player.isNoble()) {
            player.sendMessage(player.isLangRus() ? "Лишь дворянин может использовать бафера" : "BBS Buffer can only use the Noble!");
            return false;
        }

        if (Config.CHECK_SIEGE && player.isOnSiegeField()) {
            player.sendMessage(player.isLangRus() ? "Во время осады нельзя использовать бафера!" : "BBS Buffer can not be used during the siege!");
            return false;
        }

        /*    if (BBSBufferConfig.CHECK_JAIL && player.isInJail()) {
         player.sendMessage(player.isLangRus() ? "Уголовникам нельзя юзать бафера!" : "BBS Buffer can not be used in prison!");
         return false;
         }
         */
        if (Config.CHECK_PEACEZONE && !player.isInZonePeace()) {
            player.sendMessage(player.isLangRus() ? "BBS Buffer можно использовать только в безопасной зоне!" : "BBS Buffer can be used only in a peaceful area!");
            return false;
        }

        if (Config.CHECK_ALLOWED_ZONE && !BBSBufferChecker.isInAllowedZone(player)) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать бафера в этой зоне!" : "BBS Buffer can not be used in this the area!");
            return false;
        }

        return true;
    }

    /**
     * Check if can buff the player's summon.
     *
     * @param summon
     * @return TRUE if buff is allowed.
     */
    private static boolean checkConditionsSummon(Summon summon) {

        if (summon == null) {
            return false;
        }

        if (summon.getPlayer() == null) {
            return false;
        }

        if (Config.CHECK_DEATH && (summon.isDead() || summon.isAlikeDead() || summon.isFakeDeath())) {
            summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "В данный момент нельзя использовать BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_ACTION && (summon.isCastingNow() || summon.isInCombat() || summon.isAttackingNow())) {
            summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "В данный момент нельзя использовать BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_CANT_MOVE && (summon.isMovementDisabled() || summon.isParalyzed() || summon.isStunned() || summon.isSleeping() || summon.isRooted() || summon.isImmobilized())) {
            summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "В данный момент нельзя использовать BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_PEACEZONE && !summon.isInZonePeace()) {
            summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "BBS Buffer можно использовать только в безопасной зоне!" : "BBS Buffer can be used only in a peaceful area!");
            return false;
        }

        if (Config.CHECK_ALLOWED_ZONE && !BBSBufferChecker.isInAllowedZone(summon)) {
            summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "Нельзя использовать бафера в этой зоне" : "BBS Buffer can not be used in this the area!");
            return false;
        }

        // Check player
        return BBSBufferChecker.checkConditionsPlayer(summon.getPlayer());
    }

    /**
     * Check if can buff the player
     *
     * @param player
     * @return TRUE if buff is allowed.
     */
    private static boolean checkRestoreConditionsPlayer(Player player) {
        if (player == null) {
            return false;
        }

        if (Config.RESTORE_CHECK_DEATH && (player.isDead() || player.isAlikeDead() || player.isFakeDeath())) {
            player.sendMessage(player.isLangRus() ? "В данный момент нельзя использовать бафера!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.RESTORE_CHECK_ACTION && (player.isCastingNow() || player.isInCombat() || player.isAttackingNow())) {
            player.sendMessage(player.isLangRus() ? "В данный момент нельзя использовать бафера!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.RESTORE_CHECK_OLY && player.isInOlympiadMode()) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать функцию вост. ХП/MP/CP на олимпиаде!" : "You can not use BBS Buffer being on the Olympic Stadium!");
            return false;
        }

        if (Config.RESTORE_CHECK_EVENT && player.getEvents() != null && player.getEvents().size() > 0) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать функцию вост. ХП/MP/CP в этой зоне" : "BBS Buffer can not be used in this the area!");
            return false;
        }

        if (Config.RESTORE_CHECK_FLY && (player.isFlying() || player.isInFlyingTransform())) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать функцию вост. ХП/MP/CP в полете!" : "You can not use BBS Buffer while in flight!");
            return false;
        }

        if (Config.RESTORE_CHECK_BOAT && player.isInBoat()) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать функцию вост. ХП/MP/CP на корабле!" : "You can not use BBS Buffer while on board ship!");
            return false;
        }

        if (Config.RESTORE_CHECK_MOUNTED && player.isMounted()) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать функцию вост. ХП/MP/CP сидя на животном!!" : "You can not use BBS Buffer sitting on the animal!");
            return false;
        }

        if (Config.RESTORE_CHECK_CANT_MOVE && (player.isMovementDisabled() || player.isParalyzed() || player.isStunned() || player.isSleeping() || player.isRooted() || player.isImmobilized())) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать функцию вост. ХП/MP/CP в данный момент!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.RESTORE_CHECK_STORE_MODE && (player.isInStoreMode() || player.isInTrade() || player.isInOfflineMode())) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать функцию вост. ХП/MP/CP при трейде!" : "While trades can not use the BBS Buffer!");
            return false;
        }

        if (Config.RESTORE_CHECK_FISHING && player.isFishing()) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать функцию вост. ХП/MP/CP на олимпиаде!" : "Do not use while fishing BBS Buffer!");
            return false;
        }

        if (Config.RESTORE_CHECK_TEMP_ACTION && (player.isLogoutStarted() || player.isTeleporting())) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать функцию вост. ХП/MP/CP в данный момент!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.RESTORE_CHECK_DUEL && player.isInDuel()) {
            player.sendMessage(player.isLangRus() ? "Нельзя использовать функцию вост. ХП/MP/CP в дуэли!" : "During the duel, you can not use BBS Buffer!");
            return false;
        }

        if (Config.RESTORE_CHECK_CURSED_WEAPON && player.isCursedWeaponEquipped()) {
            player.sendMessage(player.isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "The owner of the cursed weapons can not use the BBS Buffer!");
            return false;
        }

        if (Config.RESTORE_CHECK_PK && player.getKarma() > 0) {
            player.sendMessage(player.isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "Killer players can not use the BBS Buffer!");
            return false;
        }

        if (Config.RESTORE_CHECK_CLAN_LEADER && !player.isClanLeader()) {
            player.sendMessage(player.isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "BBS Buffer can use only the clan leader!");
            return false;
        }

        if (Config.RESTORE_CHECK_NOBLE && !player.isNoble()) {
            player.sendMessage(player.isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "BBS Buffer can only use the Noble!");
            return false;
        }

        if (Config.RESTORE_CHECK_SIEGE && player.isOnSiegeField()) {
            player.sendMessage(player.isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!!" : "BBS Buffer can not be used during the siege!");
            return false;
        }

        /*    if (Config.RESTORE_CHECK_JAIL && player.isInJail()) {
         player.sendMessage(player.isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "BBS Buffer can not be used in prison!");
         return false;
         }
         */
        if (Config.RESTORE_CHECK_PEACEZONE && !player.isInZonePeace()) {
            player.sendMessage(player.isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "BBS Buffer can be used only in a peaceful area!");
            return false;
        }

        if (Config.RESTORE_CHECK_ALLOWED_ZONE && !BBSBufferChecker.isInAllowedZone(player)) {
            player.sendMessage(player.isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "BBS Buffer can not be used in this the area!");
            return false;
        }

        return true;
    }

    /**
     * Check if can buff the player's summon.
     *
     * @param summon
     * @return TRUE if buff is allowed.
     */
    private static boolean checkRestoreConditionsSummon(Summon summon) {

        if (summon == null) {
            return false;
        }

        if (summon.getPlayer() == null) {
            return false;
        }

        if (Config.CHECK_DEATH && (summon.isDead() || summon.isAlikeDead() || summon.isFakeDeath())) {
            summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_ACTION && (summon.isCastingNow() || summon.isInCombat() || summon.isAttackingNow())) {
            summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_CANT_MOVE && (summon.isMovementDisabled() || summon.isParalyzed() || summon.isStunned() || summon.isSleeping() || summon.isRooted() || summon.isImmobilized())) {
            summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.CHECK_PEACEZONE && !summon.isInZonePeace()) {
            summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "BBS Buffer can be used only in a peaceful area!");
            return false;
        }

        if (Config.CHECK_ALLOWED_ZONE && !BBSBufferChecker.isInAllowedZone(summon)) {
            summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "Сейчас нельзя использовать функцию вост. ХП/MP/CP!" : "BBS Buffer can not be used in this the area!");
            return false;
        }

        // Check player
        return BBSBufferChecker.checkConditionsPlayer(summon.getPlayer());
    }

    /**
     * Returns true if character is in allowed zone.
     *
     * @param character
     * @return
     */
    public static final boolean isInAllowedZone(Creature character) {/*
         if(Config.ALLOWED_ZONES_IDS.size() == 0)
         {
         return true;
         }

         for (FastList.Node<Integer> n = Config.ALLOWED_ZONES_IDS.head(), end = Config.ALLOWED_ZONES_IDS.tail(); (n = n.getNext()) != end;) 
         {
         if(character.isInsideZone(getZoneId(n.getValue())))
         {
         return true;
         }
         }
		
         return false;
         */

        return true; // all zones id's allowed.
    }
    /*
     public static final ZoneId getZoneId(int zoneId)
     {
     ZoneId zone = null;
		
     switch (zoneId)
     {
     case 0:
     return ZoneId.PVP;
     case 1:
     return ZoneId.PEACE;
     case 2:
     return ZoneId.SIEGE;
     case 3:
     return ZoneId.MOTHER_TREE;
     case 4:
     return ZoneId.CLAN_HALL;
     case 5:
     return ZoneId.LANDING;
     case 6:
     return ZoneId.NO_LANDING;
     case 7:
     return ZoneId.WATER;
     case 8:
     return ZoneId.JAIL;
     case 9:
     return ZoneId.MONSTER_TRACK;
     case 10:
     return ZoneId.CASTLE;
     case 11:
     return ZoneId.SWAMP;
     case 12:
     return ZoneId.NO_SUMMON_FRIEND;
     case 13:
     return ZoneId.FORT;
     case 14:
     return ZoneId.NO_STORE;
     case 15:
     return ZoneId.TOWN;
     case 16:
     return ZoneId.SCRIPT;
     case 17:
     return ZoneId.HQ;
     case 18:
     return ZoneId.DANGER_AREA;
     case 19:
     return ZoneId.ALTERED;
     case 20:
     return ZoneId.NO_BOOKMARK;
     case 21:
     return ZoneId.NO_ITEM_DROP;
     case 22:
     return ZoneId.NO_RESTART;
     }
		
     return zone;
     }*/

    /**
     * Check if can buff the player
     *
     * @param player
     * @return TRUE if buff is allowed.
     */
    public static boolean checkAutoRebuffConditionsPlayer(Player player) {
        if (player == null) {
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_DEATH && (player.isDead() || player.isAlikeDead() || player.isFakeDeath())) {
            //player.sendMessage(player.isLangRus() ? "Р’ РґР°РЅРЅС‹Р№ РјРѕРјРµРЅС‚ РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_OLY && player.isInOlympiadMode()) {
            //player.sendMessage(player.isLangRus() ? "РќРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer РЅР°С…РѕРґСЏСЃСЊ РЅР° СЃС‚Р°РґРёРѕРЅРµ РћР»РёРјРїРёР°РґС‹!" : "You can not use BBS Buffer being on the Olympic Stadium!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_EVENT && player.getEvents() != null && player.getEvents().size() > 0) {
            //player.sendMessage(player.isLangRus() ? "BBS Buffer РЅРµ РјРѕРіСѓС‚ Р±С‹С‚СЊ РёСЃРїРѕР»СЊР·РѕРІР°РЅС‹ РІ СЌС‚РѕР№ РѕР±Р»Р°СЃС‚Рё!" : "BBS Buffer can not be used in this the area!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_STORE_MODE && (player.isInStoreMode() || player.isInTrade() || player.isInOfflineMode())) {
            //player.sendMessage(player.isLangRus() ? "Р’Рѕ РІСЂРµРјСЏ С‚СЂРµР№РґР° РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "While trades can not use the BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_FISHING && player.isFishing()) {
            //player.sendMessage(player.isLangRus() ? "РќРµР»СЊР·СЏ РІРѕ РІСЂРµРјСЏ СЂС‹Р±Р°Р»РєРё РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "Do not use while fishing BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_TEMP_ACTION && (player.isLogoutStarted() || player.isTeleporting())) {
            //player.sendMessage(player.isLangRus() ? "Р’ РґР°РЅРЅС‹Р№ РјРѕРјРµРЅС‚ РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_DUEL && player.isInDuel()) {
            //player.sendMessage(player.isLangRus() ? "Р’Рѕ РІСЂРµРјСЏ РґСѓСЌР»Рё РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "During the duel, you can not use BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_CURSED_WEAPON && player.isCursedWeaponEquipped()) {
            //player.sendMessage(player.isLangRus() ? "Р’Р»Р°РґРµР»РµС† РїСЂРѕРєР»СЏС‚РѕРіРѕ РѕСЂСѓР¶РёСЏ РЅРµ РјРѕР¶РµС‚ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "The owner of the cursed weapons can not use the BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_PK && player.getKarma() > 0) {
            //player.sendMessage(player.isLangRus() ? "РЈР±РёР№С†Р° РёРіСЂРѕРєРѕРІ РЅРµ РјРѕР¶РµС‚ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "Killer players can not use the BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_SIEGE && player.isOnSiegeField()) {
            //player.sendMessage(player.isLangRus() ? "BBS Buffer РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РІРѕ РІСЂРµРјСЏ РѕСЃР°РґС‹!" : "BBS Buffer can not be used during the siege!");
            return false;
        }

        /*    if (Config.AUTOREBUFF_CHECK_JAIL && player.isInJail()) {
         //player.sendMessage(player.isLangRus() ? "BBS Buffer РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РІ С‚СЋСЂСЊРјРµ!" : "BBS Buffer can not be used in prison!");
         return false;
         }
         */
        if (Config.AUTOREBUFF_CHECK_PEACEZONE && !player.isInZonePeace()) {
            //player.sendMessage(player.isLangRus() ? "BBS Buffer РјРѕР¶РЅРѕ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ С‚РѕР»СЊРєРѕ РІ Р·РѕРЅРµ!" : "BBS Buffer can be used only in a peaceful area!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_ALLOWED_ZONE && !BBSBufferChecker.isInAllowedZone(player)) {
            //player.sendMessage(player.isLangRus() ? "BBS Buffer РЅРµ РјРѕРіСѓС‚ Р±С‹С‚СЊ РёСЃРїРѕР»СЊР·РѕРІР°РЅС‹ РІ СЌС‚РѕР№ РѕР±Р»Р°СЃС‚Рё!" : "BBS Buffer can not be used in this the area!");
            return false;
        }

        return true;
    }

    /**
     * Check if can buff the player's summon.
     *
     * @param summon
     * @return TRUE if buff is allowed.
     */
    public static boolean checkAutoRebuffConditionsSummon(Summon summon) {

        if (summon == null) {
            return false;
        }

        if (summon.getPlayer() == null) {
            return false;
        }

        Player player = summon.getPlayer();

        // summon:
        if (Config.AUTOREBUFF_CHECK_DEATH && (summon.isDead() || summon.isAlikeDead() || summon.isFakeDeath())) {
            //summon.getPlayer().sendMessage(summon.getPlayer().isLangRus() ? "Р’ РґР°РЅРЅС‹Р№ РјРѕРјРµРЅС‚ РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        // player:
        if (Config.AUTOREBUFF_CHECK_OLY && player.isInOlympiadMode()) {
            //player.sendMessage(player.isLangRus() ? "РќРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer РЅР°С…РѕРґСЏСЃСЊ РЅР° СЃС‚Р°РґРёРѕРЅРµ РћР»РёРјРїРёР°РґС‹!" : "You can not use BBS Buffer being on the Olympic Stadium!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_EVENT && player.getEvents() != null && player.getEvents().size() > 0) {
            //player.sendMessage(player.isLangRus() ? "BBS Buffer РЅРµ РјРѕРіСѓС‚ Р±С‹С‚СЊ РёСЃРїРѕР»СЊР·РѕРІР°РЅС‹ РІ СЌС‚РѕР№ РѕР±Р»Р°СЃС‚Рё!" : "BBS Buffer can not be used in this the area!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_STORE_MODE && (player.isInStoreMode() || player.isInTrade() || player.isInOfflineMode())) {
            //player.sendMessage(player.isLangRus() ? "Р’Рѕ РІСЂРµРјСЏ С‚СЂРµР№РґР° РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "While trades can not use the BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_FISHING && player.isFishing()) {
            //player.sendMessage(player.isLangRus() ? "РќРµР»СЊР·СЏ РІРѕ РІСЂРµРјСЏ СЂС‹Р±Р°Р»РєРё РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "Do not use while fishing BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_TEMP_ACTION && (player.isLogoutStarted() || player.isTeleporting())) {
            //player.sendMessage(player.isLangRus() ? "Р’ РґР°РЅРЅС‹Р№ РјРѕРјРµРЅС‚ РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "At this point you can not use BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_DUEL && player.isInDuel()) {
            //player.sendMessage(player.isLangRus() ? "Р’Рѕ РІСЂРµРјСЏ РґСѓСЌР»Рё РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "During the duel, you can not use BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_CURSED_WEAPON && player.isCursedWeaponEquipped()) {
            //player.sendMessage(player.isLangRus() ? "Р’Р»Р°РґРµР»РµС† РїСЂРѕРєР»СЏС‚РѕРіРѕ РѕСЂСѓР¶РёСЏ РЅРµ РјРѕР¶РµС‚ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "The owner of the cursed weapons can not use the BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_PK && player.getKarma() > 0) {
            //player.sendMessage(player.isLangRus() ? "РЈР±РёР№С†Р° РёРіСЂРѕРєРѕРІ РЅРµ РјРѕР¶РµС‚ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ BBS Buffer!" : "Killer players can not use the BBS Buffer!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_SIEGE && player.isOnSiegeField()) {
            //player.sendMessage(player.isLangRus() ? "BBS Buffer РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РІРѕ РІСЂРµРјСЏ РѕСЃР°РґС‹!" : "BBS Buffer can not be used during the siege!");
            return false;
        }

        /*    if (Config.AUTOREBUFF_CHECK_JAIL && player.isInJail()) {
         //player.sendMessage(player.isLangRus() ? "BBS Buffer РЅРµР»СЊР·СЏ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РІ С‚СЋСЂСЊРјРµ!" : "BBS Buffer can not be used in prison!");
         return false;
         }
         */
        if (Config.AUTOREBUFF_CHECK_PEACEZONE && !player.isInZonePeace()) {
            //player.sendMessage(player.isLangRus() ? "BBS Buffer РјРѕР¶РЅРѕ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ С‚РѕР»СЊРєРѕ РІ Р·РѕРЅРµ!" : "BBS Buffer can be used only in a peaceful area!");
            return false;
        }

        if (Config.AUTOREBUFF_CHECK_ALLOWED_ZONE && !BBSBufferChecker.isInAllowedZone(summon)) {
            //player.sendMessage(player.isLangRus() ? "BBS Buffer РЅРµ РјРѕРіСѓС‚ Р±С‹С‚СЊ РёСЃРїРѕР»СЊР·РѕРІР°РЅС‹ РІ СЌС‚РѕР№ РѕР±Р»Р°СЃС‚Рё!" : "BBS Buffer can not be used in this the area!");
            return false;
        }

        return true;
    }

}
