package services;

import l2p.gameserver.Config;
import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.ExBR_GamePoint;
import l2p.gameserver.scripts.Functions;

public class CoinPoinExch extends Functions {

    public void show() {
        Player player = (Player) getSelf();
        if (player == null) {
            return;
        }

        String append = player.isLangRus() ? "Обмен валют<br>" : "Currency exchange <br>";
        append += "<br>";

        append += player.isLangRus() ? "Уважаемый игрок!<br>" : "Dear player! <br>";
        append += player.isLangRus() ? "Тут вы можете обменять:<br>" : "Here you can exchange: <br>";
        append += player.isLangRus() ? "Game Coin на баланс в Item Mall<br>" : "Game Coin to balance Item Mall <br>";
        append += player.isLangRus() ? "баланс в Item Mall на Game Coin.<br>" : "balance in the Item Mall for Game Coin. <br>";
        append += player.isLangRus() ? "Пожалуйста выберите направление:<br>" : "Please select a direction: <br>";
        append += "<button value=\"Coin -> ItemMall\" action=\"bypass -h scripts_services.CoinPoinExch:ShowC2P \" width=250 height=15><br>";
        append += "<button value=\"ItemMall -> Coin\" action=\"bypass -h scripts_services.CoinPoinExch:ShowP2C \" width=250 height=15><br>";
        show(append, player, null);
    }

    public void ShowP2C() {
        Player player = (Player) getSelf();
        if (player == null) {
            return;
        }

        if (player.getPremiumPoints() < 30) {
            String append = player.isLangRus() ? "Ваш баланс слишком мал для исполнение данной функции!" : "Your balance is too small for the implementation of this function!";
            show(append, player, null);
            return;
        }


        String append2 = player.isLangRus() ? "Курс обмена: 30 баланса в ItemMall = 1 GameCoin<br>" : "Exchange rate: 30 balance ItemMall = 1 GameCoin <br>";
        append2 += player.isLangRus() ? "Укажите количество которые вы обмениваете!<br>" : "Enter the amount you exchange! <br>";
        append2 += "<edit var=\"exch2\" width=70> <br>";
        append2 += player.isLangRus() ? "<button value=\"Обменять\" action=\"bypass -h scripts_services.CoinPoinExch:DoP2C $exch2\" width=150 height=15><br> <br>" : "<button value=\"Exchange\" action=\"bypass -h scripts_services.CoinPoinExch:DoP2C $exch2\" width=150 height=15><br> <br>";
        show(append2, player, null);

    }

    public void DoP2C(String[] param) {
        Player player = (Player) getSelf();
        if (player == null) {
            return;
        }

        String coinsToEx = param[0];
        if (!checkInteger(coinsToEx)) {
            player.sendMessage(player.isLangRus() ? "" + player.getName() + ", Пишите только цифры!" : "" + player.getName() + ", Write only numbers!");
            return;
        }
        int _coinsToEx = Integer.parseInt(param[0]);

        if (player.getPremiumPoints() < _coinsToEx || _coinsToEx < 30) {
            player.sendMessage(player.isLangRus() ? "" + player.getName() + ", У вас не хватает баланса для обмена" : "" + player.getName() + ", You do not have enough balance to exchange");
            return;
        }

        player.reducePremiumPoints(_coinsToEx);
        player.sendPacket(new ExBR_GamePoint(player));
        double _coinsToExDouble = _coinsToEx / 30;
        int _finalAmmount = (int) Math.ceil(_coinsToExDouble);
        addItem(player, Config.EXCH_COIN_ID, _finalAmmount);
        player.sendMessage(player.isLangRus() ? "" + player.getName() + ", Успешно добавились " + _finalAmmount + " L2Game Coin" : "" + player.getName() + ", Successfully added " + _finalAmmount + " L2Game Coin");
        player.sendChanges();
    }

    public void ShowC2P() {
        Player player = (Player) getSelf();
        if (player == null) {
            return;
        }

        if (player.getInventory().getCountOf(Config.EXCH_COIN_ID) <= 0) {
            String append = player.isLangRus() ? "У вас нету GameCoin в инвентаре!" : "You have no L2Game Coin in inventory!";
            show(append, player, null);
            return;
        }


        String append2 = player.isLangRus() ? "Курс обмена: 1 GameCoin = 30 баланса в ItemMall <br>" : "Exchange rate: 1 GameCoin = 30 balance ItemMall <br>";
        append2 += player.isLangRus() ? "Укажите количество которые вы обмениваете!<br>" : "Enter the amount you exchange! <br>";
        append2 += "<edit var=\"exch1\" width=70> <br>";
        append2 += player.isLangRus() ? "<button value=\"Обменять\" action=\"bypass -h scripts_services.CoinPoinExch:DoC2P $exch1\" width=150 height=15><br> <br>" : "<button value=\"Exchange\" action=\"bypass -h scripts_services.CoinPoinExch:DoC2P $exch1\" width=150 height=15><br> <br>";
        show(append2, player, null);

    }

    public void DoC2P(String[] param) {
        Player player = (Player) getSelf();
        if (player == null) {
            return;
        }

        String coinsToEx = param[0];
        if (!checkInteger(coinsToEx)) {
            player.sendMessage(player.isLangRus() ? "" + player.getName() + ", Пишите только цифры!" : "" + player.getName() + ", Write only numbers!");
            return;
        }
        int _coinsToEx = Integer.parseInt(param[0]);

        if (player.getInventory().getCountOf(Config.EXCH_COIN_ID) < _coinsToEx || _coinsToEx <= 0) {
            player.sendMessage(player.isLangRus() ? "" + player.getName() + ", У вас не хватает вещей для обмена" : "" + player.getName() + ", You do not have enough things to share");
            return;
        }

        removeItem(player, Config.EXCH_COIN_ID, _coinsToEx);
        int finPoint = (_coinsToEx * 30);
        finPoint *= -1;
        player.reducePremiumPoints(finPoint);
        player.sendPacket(new ExBR_GamePoint(player));
        player.sendMessage(player.isLangRus() ? "" + player.getName() + ", Успешно добавились " + _coinsToEx * 30 + " баланса в ItemMall" : "" + player.getName() + ", Successfully added " + _coinsToEx * 30 + " balance ItemMall ");
        player.sendChanges();
    }

    public boolean checkInteger(String number) {
        try {
            int x = Integer.parseInt(number);
            number = Integer.toString(x);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }
}