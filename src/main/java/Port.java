// created by Benjamin Lamprecht

import java.util.ArrayList;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Port port = (Port) o;
        return Objects.equals(player, port.player) &&
                Objects.equals(card, port.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, card);
    }
}
