// created by Benjamin Lamprecht

import java.util.Objects;

public class PlayerCard {
    private final Player player;
    private final Card card;

    protected Player getPlayer() {
        return player;
    }

    protected Card getCard() {
        return card;
    }

    protected PlayerCard(Player player, Card card) {
        this.player = player;
        this.card = card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerCard playerCard = (PlayerCard) o;
        return Objects.equals(player, playerCard.player) &&
                Objects.equals(card, playerCard.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, card);
    }
}
