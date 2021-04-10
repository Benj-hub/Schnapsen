// created by Benjamin Lamprecht

import java.util.ArrayList;

public class Port {
    private Player player;
    private Card card;

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }

    public Port(Player player, Card card) {
        this.player = player;
        this.card = card;
    }
}
