// created by Benjamin Lamprecht

public class Card {

    private String name;
    private int value;
    private String color;

    public void setName(String name) {
        this.name = name;
    }

    public Card(String name, String color, int value) {
        this.name = name;
        this.value = value;
        this.color = color;
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
}
