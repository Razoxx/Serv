package l2p.gameserver.model.actor.listener;

import l2p.commons.listener.Listener;
import l2p.gameserver.listener.actor.player.OnPlayerBuyPremiumListner;
import l2p.gameserver.listener.actor.player.OnPlayerEndPremiumListner;
import l2p.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2p.gameserver.listener.actor.player.OnPlayerExitListener;
import l2p.gameserver.listener.actor.player.OnPlayerPartyInviteListener;
import l2p.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2p.gameserver.listener.actor.player.OnPlayerUpLvlListener;
import l2p.gameserver.listener.actor.player.OnTeleportListener;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.entity.Reflection;

public class PlayerListenerList extends CharListenerList {

    public PlayerListenerList(Player actor) {
        super(actor);
    }

    @Override
    public Player getActor() {
        return (Player) actor;
    }

    public void onEnter() {
        if (!global.getListeners().isEmpty()) {
            for (Listener<Creature> listener : global.getListeners()) {
                if (OnPlayerEnterListener.class.isInstance(listener)) {
                    ((OnPlayerEnterListener) listener).onPlayerEnter(getActor());
                }
            }
        }

        if (!getListeners().isEmpty()) {
            for (Listener<Creature> listener : getListeners()) {
                if (OnPlayerEnterListener.class.isInstance(listener)) {
                    ((OnPlayerEnterListener) listener).onPlayerEnter(getActor());
                }
            }
        }
    }

    public void onExit() {
        if (!global.getListeners().isEmpty()) {
            for (Listener<Creature> listener : global.getListeners()) {
                if (OnPlayerExitListener.class.isInstance(listener)) {
                    ((OnPlayerExitListener) listener).onPlayerExit(getActor());
                }
            }
        }

        if (!getListeners().isEmpty()) {
            for (Listener<Creature> listener : getListeners()) {
                if (OnPlayerExitListener.class.isInstance(listener)) {
                    ((OnPlayerExitListener) listener).onPlayerExit(getActor());
                }
            }
        }
    }

    public void onTeleport(int x, int y, int z, Reflection reflection) {
        if (!global.getListeners().isEmpty()) {
            for (Listener<Creature> listener : global.getListeners()) {
                if (OnTeleportListener.class.isInstance(listener)) {
                    ((OnTeleportListener) listener).onTeleport(getActor(), x, y, z, reflection);
                }
            }
        }

        if (!getListeners().isEmpty()) {
            for (Listener<Creature> listener : getListeners()) {
                if (OnTeleportListener.class.isInstance(listener)) {
                    ((OnTeleportListener) listener).onTeleport(getActor(), x, y, z, reflection);
                }
            }
        }
    }
    
    public void onLvlUp(Player player) {
        if (!global.getListeners().isEmpty()) {
            for (Listener<Creature> listener : global.getListeners()) {
                if (OnPlayerUpLvlListener.class.isInstance(listener)) {
                    ((OnPlayerUpLvlListener) listener).onLvlUp(player);
                }
            }
        }

        if (!getListeners().isEmpty()) {
            for (Listener<Creature> listener : getListeners()) {
                if (OnPlayerUpLvlListener.class.isInstance(listener)) {
                    ((OnPlayerUpLvlListener) listener).onLvlUp(player);
                }
            }
        }
    }
    
    public void onBuyPremium(Player player) {
        if (!global.getListeners().isEmpty()) {
            for (Listener<Creature> listener : global.getListeners()) {
                if (OnPlayerBuyPremiumListner.class.isInstance(listener)) {
                    ((OnPlayerBuyPremiumListner) listener).onBuyPremium(player);
                }
            }
        }

        if (!getListeners().isEmpty()) {
            for (Listener<Creature> listener : getListeners()) {
                if (OnPlayerBuyPremiumListner.class.isInstance(listener)) {
                    ((OnPlayerBuyPremiumListner) listener).onBuyPremium(player);
                }
            }
        }
    }
    
    public void onEndPremium(Player player) {
        if (!global.getListeners().isEmpty()) {
            for (Listener<Creature> listener : global.getListeners()) {
                if (OnPlayerEndPremiumListner.class.isInstance(listener)) {
                    ((OnPlayerEndPremiumListner) listener).onEndPremium(player);
                }
            }
        }

        if (!getListeners().isEmpty()) {
            for (Listener<Creature> listener : getListeners()) {
                if (OnPlayerEndPremiumListner.class.isInstance(listener)) {
                    ((OnPlayerEndPremiumListner) listener).onEndPremium(player);
                }
            }
        }
    }

    public void onPartyInvite() {
        if (!global.getListeners().isEmpty()) {
            for (Listener<Creature> listener : global.getListeners()) {
                if (OnPlayerPartyInviteListener.class.isInstance(listener)) {
                    ((OnPlayerPartyInviteListener) listener).onPartyInvite(getActor());
                }
            }
        }

        if (!getListeners().isEmpty()) {
            for (Listener<Creature> listener : getListeners()) {
                if (OnPlayerPartyInviteListener.class.isInstance(listener)) {
                    ((OnPlayerPartyInviteListener) listener).onPartyInvite(getActor());
                }
            }
        }
    }

    public void onPartyLeave() {
        if (!global.getListeners().isEmpty()) {
            for (Listener<Creature> listener : global.getListeners()) {
                if (OnPlayerPartyLeaveListener.class.isInstance(listener)) {
                    ((OnPlayerPartyLeaveListener) listener).onPartyLeave(getActor());
                }
            }
        }

        if (!getListeners().isEmpty()) {
            for (Listener<Creature> listener : getListeners()) {
                if (OnPlayerPartyLeaveListener.class.isInstance(listener)) {
                    ((OnPlayerPartyLeaveListener) listener).onPartyLeave(getActor());
                }
            }
        }
    }
}
