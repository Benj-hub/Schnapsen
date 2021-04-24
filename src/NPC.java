import java.util.ArrayList;
import java.util.Collections;

public class NPC extends Player {
    private static ArrayList<Card> thrownCards = new ArrayList<>();

    private ArrayList<Card> throwCard = new ArrayList<>();

    NPC(int i) {
        name = "Machine " + i;
    }

    @Override
    protected void playerAction(){
        endingGame();
        blockStapel();
        lookForPairs();
        if (Controller.ports.size() == 0){
            Controller.turnSwitcher(this, throwFirstCard());
        } else {
            Controller.turnSwitcher(this, throwAnswer());
        }
    }

    public Card throwFirstCard() {
        if (66 <= (possiblePoints()) + score) {
            return gainPointsLooseCards();
        } else {
            return gainPointsSaveCards();
        }
    }

    public void lookForPairs() {
        System.out.println("requesting lookforpairs");
        for (Card master : getCardsInHand()) {
            Card temp = master;
            for (Card slave : getCardsInHand()) {
                if (Player.cardMatchesPair(master, slave)) {
                    if (slave.getValue() < master.getValue()){
                        temp = slave;
                    }
                    callPairs(temp);
                }
            }
        }
    }

    public Card throwAnswer() {
        Port master = Controller.checkWinCard();
        for (Card card : this.getCardsInHand()) {
            Port slave = new Port(this, card);
            if (!master.equals(slave)) {
                throwCard.add(slave.getCard());
            }
        }
        if (throwCard.isEmpty()) {
            throwCard.add(findScapeGoat());
        }
        Collections.shuffle(throwCard);
        return throwCard.get(0);
    }

    private Card findScapeGoat() {
        Card scapegoat = getCardsInHand().get(0);
        for (Card slave : getCardsInHand()) {
            if (slave.getValue() < scapegoat.getValue()) {
                scapegoat = slave;
            }
        }
        return scapegoat;
    }

    private void endingGame() {
        System.out.println("requesting ending game");
        if (getScore() >= 66) {
            Controller.endingGame(this);
        }
        if (score >= 33 && (score + possiblePoints()) < 66) {
            Controller.endingGame(this);
        }
    }

    private void blockStapel() {
        System.out.println("requesting blockstapel");
        if (66 <= (possiblePoints()) + score) {
            Deck.blockStapel();
        }
    }

    private ArrayList<Card> thrownCards() {
        for (Port port : Controller.ports) {
            thrownCards().add(port.getCard());
        }
        return thrownCards;
    }

    private int possiblePoints() {
        int possiblePoints = 0;
        for (Card c : cardsInHand) {
            if (c.getValue() >= 10) {
                possiblePoints += (c.getValue() + 2);
            }
        }
        return possiblePoints;
    }

    private Card gainPointsSaveCards() {
        for (Card c : getCardsInHand()) {
            if (c.getValue() < 10) {
                throwCard.add(c);
            }
        }
        if (throwCard.isEmpty()) {
            for (Card c : cardsInHand) {
                if (c.getValue() == 11) {
                    throwCard.add(c);
                } else {
                    throwCard.add(cardsInHand.get(0));
                }
            }
        }

        Collections.shuffle(throwCard);
        System.out.println(Fonts.BLUE_BOLD + name + " threw " + throwCard.get(0).getName() + Fonts.RESET);
        return throwCard.get(0);
    }

    private Card gainPointsLooseCards() {
        Card winCard = null;
        for (Card card : cardsInHand) {
            if (card.getValue() == 11) {
                winCard = card;
                return winCard;
            }
            if (countCards().get(0) == 0 && card.getValue() == 10) {
                winCard = card;
                return winCard;
            }
            if (countCards().get(1) == 0 && card.getValue() == 4) {
                winCard = card;
                return winCard;
            }
            if (countCards().get(2) == 0 && card.getValue() == 3) {
                winCard = card;
                return winCard;
            }
        }
        if (winCard == null) {
            return gainPointsSaveCards();
        }
        System.out.println(Fonts.BLUE_BOLD + name + " threw " + winCard.getName()+ Fonts.RESET);
        return winCard;
    }

    private ArrayList<Integer> countCards() {
        ArrayList<Integer> countCards = new ArrayList<>();
        int amountAss = 0;
        int amountZehn = 0;
        int amountKoenig = 0;
        int amountDame = 0;
        int amountBube = 0;

        for (Card c : thrownCards()) {
            int i = c.getValue();
            switch (i) {
                case 11:
                    amountAss++;
                case 10:
                    amountZehn++;
                case 4:
                    amountKoenig++;
                case 3:
                    amountDame++;
                case 2:
                    amountBube++;
            }
        }
        for (Card c : cardsInHand) {
            int i = c.getValue();
            switch (i) {
                case 11:
                    amountAss--;
                case 10:
                    amountZehn--;
                case 4:
                    amountKoenig--;
                case 3:
                    amountDame--;
                case 2:
                    amountBube--;
            }
        }
        countCards.add(amountAss);
        countCards.add(amountZehn);
        countCards.add(amountKoenig);
        countCards.add(amountDame);
        countCards.add(amountBube);
        return countCards;
    }
}
