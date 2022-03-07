// created by Benjamin Lamprecht

import java.util.Objects;

public class Card {

    private String name;
    private int value;
    private String color;

    public Card(String name, String color, int value) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value &&
                Objects.equals(name, card.name) &&
                Objects.equals(color, card.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, color);
    }
}
