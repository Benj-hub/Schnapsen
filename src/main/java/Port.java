// created by Benjamin Lamprecht

import java.util.ArrayList;

public class Port {
    private Player player;
    private Card card;

    protected Player getPlayer() {
        return player;
    }

    protected Card getCard() {
        return card;
    }

    protected Port(Player player, Card card) {
        this.player = player;
        this.card = card;
    }
}
