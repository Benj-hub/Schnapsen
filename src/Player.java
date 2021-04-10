// created by Benjamin Lamprecht
import java.util.ArrayList;

public class Player {

    private int score;
    private String name;
    private ArrayList<Card> previousTricks = new ArrayList<>();
    private ArrayList<Card> cardsInHand;

    public Player() {
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<Card> getPreviousTricks() {
        return previousTricks;
    }

    public void setPreviousTricks(ArrayList<Card> previousTricks) {
        this.previousTricks = previousTricks;
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    public void setCardsInHand(ArrayList<Card> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }
}
