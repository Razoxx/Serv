package l2p.gameserver.listener.actor.player.impl;

import l2p.commons.lang.reference.HardReference;
import l2p.gameserver.listener.actor.player.OnAnswerListener;
import l2p.gameserver.model.AcademList;
import l2p.gameserver.model.Player;


public class AcademAnswerListener implements OnAnswerListener {
    private final HardReference<Player> _activeChar;
    private final HardReference<Player> _academChar;

        public AcademAnswerListener(Player activeChar, Player academChar) {
            _activeChar = activeChar.getRef();
            _academChar = academChar.getRef();
        }

        @Override
        public void sayYes() {
            Player player = _activeChar.get();
            Player academChar = _academChar.get();
            if (_activeChar  == null || _academChar  == null) {
                return;
            }
            AcademList.inviteInAcademy(player, academChar);
        }

        @Override
        public void sayNo() {
            
        }
}
