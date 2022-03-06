// created by Benjamin Lamprecht

import java.util.ArrayList;

public abstract class Player {

    protected int score;
    protected String name;
    protected ArrayList<Card> previousTricks;
    protected ArrayList<Card> cardsInHand;
    protected Controller controller;


    public Player(Controller controller) {

        this.controller = controller;

        this.score = 0;
        this.name = "Default Player";
        this.previousTricks = new ArrayList<>();
        this.cardsInHand = new ArrayList<>();
    }

    protected abstract PlayerCard playerAction();

    protected abstract Card playerActionExecution();

    protected abstract Card callPairs();

    protected Card throwCard(int i) {
        //System.out.println("throwCard");
        i--;
        System.out.println(Fonts.GREEN_BOLD + name + " threw: " + cardsInHand.get(i).getName() + Fonts.RESET);

        Card temp = cardsInHand.get(i);
        cardsInHand.remove(temp);
        return temp;
    }

    protected Card throwCard(Card card) {
        System.out.println(Fonts.GREEN_BOLD + name + " threw: " + card.getName() + Fonts.RESET);

        cardsInHand.remove(card);
        return card;
    }

    protected boolean cardMatchesPair(Card master, Card slave) {
        boolean cardIsKingOrQueen = master.getValue() == 4 || master.getValue() == 3;
        boolean slaveIsKingOrQueen = slave.getValue() == 4 || slave.getValue() == 3;
        boolean matchesColour = master.getColor().equals(slave.getColor());
        boolean isDifferentCard = !master.equals(slave);

        return cardIsKingOrQueen && slaveIsKingOrQueen && matchesColour && isDifferentCard;
    }

    private void drawNewCard() {
        cardsInHand.add(controller.deck.getDeck().get(0));
        controller.deck.getDeck().remove(controller.deck.getDeck().get(0));
    }

    private void drawTrump() {
        cardsInHand.add(controller.deck.getTrump());
        controller.deck.setTrump(null);
    }

    protected void drawCard() {
        if (controller.deck.getDeck().size() == 0) {
            if (controller.deck.getTrump() != null) {
                drawTrump();
            }
            System.out.println(Fonts.YELLOW_BOLD + "Stapel is empty!" + Fonts.RESET);
        } else {
            drawNewCard();
        }
    }

    protected void changeTrumpfCard(Card card) {
        if (card.getColor().equals(controller.getDeck().getTrump().getColor()) && card.getValue() == 2) {
            cardsInHand.add(controller.deck.getTrump());
            controller.deck.setTrump(card);
            getCardsInHand().remove(card);
            System.out.println(name + " gets Trump Card.");
            System.out.println("Trump changed to " + card.getName());
        } else {
            System.out.println(card.getName() + " doesn't match");
        }
    }

    protected void printCardToThrow(Integer i) {

        int action = i + 1;

        if (controller.playerCards.size() > 0) {
            if (controller.checkColour(cardsInHand.get(i))) {
                System.out.println(Fonts.GREEN_BOLD + "Action " + (action) + ": Throw " + cardsInHand.get(i).getName() + Fonts.RESET);
            } else {
                System.out.println("Action " + (action) + ": Throw " + cardsInHand.get(i).getName());
            }
        } else {
            System.out.println("Action " + (action) + ": Throw " + cardsInHand.get(i).getName());
        }
    }

    protected int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    protected String getName() {
        return name;
    }


    protected ArrayList<Card> getPreviousTricks() {
        return previousTricks;
    }


    protected ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    protected void setCardsInHand(ArrayList<Card> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    protected abstract void countCards(PlayerCard playerCard);
}