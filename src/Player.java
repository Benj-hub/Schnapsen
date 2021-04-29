// created by Benjamin Lamprecht

import java.util.ArrayList;
import java.util.Scanner;

public abstract class Player {

    protected int score;
    protected String name;
    protected ArrayList<Card> previousTricks;
    protected ArrayList<Card> cardsInHand;

    public Player() {
        this.score = 0;
        this.name = "Default Player";
        this.previousTricks = new ArrayList<>();
        this.cardsInHand = new ArrayList<>();
    }

    protected abstract Port playerAction();

    protected abstract Card playerActionExecution();

    protected abstract Card callPairs();

    protected boolean cardMatchesPair(Card master, Card slave) {
        boolean cardIsKoeningOrDame = master.getValue() == 4 || master.getValue() == 3;
        boolean slaveIsKoenigOrDame = slave.getValue() == 4 || slave.getValue() == 3;
        boolean matchesColour = master.getColor().equals(slave.getColor());

        return cardIsKoeningOrDame && slaveIsKoenigOrDame && matchesColour;
    }

    private void drawNewCard() {
        cardsInHand.add(Deck.getStapel().get(0));
        Deck.getStapel().remove(Deck.getStapel().get(0));
    }

    private void drawTrumpf() {
        cardsInHand.add(Deck.getTrumpf());
        Deck.setTrumpf(null);
    }

    protected void drawCard() {
        if (Deck.getStapel().size() == 0) {
            if (Deck.getTrumpf() != null) {
                drawTrumpf();
            }
            System.out.println(Fonts.YELLOW_BOLD + "Stapel is empty!" + Fonts.RESET);
        } else {
            drawNewCard();
        }
    }

    protected void changeTrumpfCard(Card card) {
        if (card.getColor().equals(Deck.getTrumpf().getColor())) {
            cardsInHand.add(Deck.getTrumpf());
            Deck.setTrumpf(card);
            System.out.println(name + " gets Trump Card.");
            System.out.println("Trump changed to " + card.getName());
        } else {
            System.out.println(card.getName() + " doesn't match");
        }

        playerAction();
    }

    protected void printCardToThrow(Integer i) {

        int action = i + 1;

        if (Controller.ports.size() > 0) {
            if (Controller.checkColour(cardsInHand.get(i))) {
                System.out.println(Fonts.BLACK_BOLD + "Action " + (action) + ": Throw " + cardsInHand.get(i).getName() + Fonts.RESET);
            } else {
                System.out.println("Action " + (action) + ": Throw " + cardsInHand.get(i).getName());
            }
        } else {
            System.out.println("Action " + (action) + ": Throw " + cardsInHand.get(i).getName());
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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