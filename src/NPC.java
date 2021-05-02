import java.util.ArrayList;
import java.util.Collections;

public class NPC extends Player {
    private static ArrayList<Card> thrownCards = new ArrayList<>();

    private ArrayList<Card> throwCard = new ArrayList<>();

    NPC(int i, Controller controller) {
        super(controller);
        name = "Machine" + i;
    }

    @Override
    protected Port playerAction(){
       Port cardOutput;

        //checking if NPC is on turn
        if (controller.ports.size() == 0) {
            endingGame();
            blockStapel();
            //check if NPC should change Trumpf
            Card card = lookToChangeTrumpCard();
            if (card != null){
                changeTrumpfCard(card);
            }
            Card pairs = callPairs();
            if (pairs != null){
                executePairs(pairs);
                cardOutput = new Port(this, throwCard(pairs));
            } else {
                cardOutput = new Port(this, throwCard(throwFirstCard()));
            }
        } else {
            cardOutput = new Port(this, throwAnswer());
        }
        drawCard();
        return cardOutput;
    }

    private Card lookToChangeTrumpCard(){
        for (Card c:getCardsInHand()) {
            if (conditionsToChangeTrumpCard(c)) {
                return c;
            }
        }
        return null;
    }

    private boolean conditionsToChangeTrumpCard(Card card){
        boolean cardmatchescolour = card.getColor().equals(Deck.getTrumpfColor());
        boolean cardIsBube = card.getValue() == 2;

        return cardmatchescolour && cardIsBube;
    }

    @Override
    protected Card playerActionExecution() {
        return null;
    }

    @Override
    protected Card callPairs() {
        System.out.println(Fonts.RED_BOLD + "requesting callPairs" + Fonts.RESET);
        for (Card master : getCardsInHand()) {
            for (Card slave : getCardsInHand()) {
                if (cardMatchesPair(master, slave)) {
                    System.out.println("checking match");
                    System.out.println(master.getName() + ", " + slave.getName());
                    if (slave.getValue() < master.getValue()){
                        System.out.println("slave is smaller");
                        return slave;
                    } else {
                        System.out.println("master is smaller");
                        return master;
                    }
                }
            }
        }
        return null;
    }

    private void executePairs(Card card){
        if (Deck.getTrumpfColor().equals(card.getColor())){
            score += 40;
            System.out.println("40 Points for Griff... *cough* " + name + "!");
        } else {
            score += 20;
            System.out.println("20 Points for Griff... *cough* " + name + "!");
        }
    }

    public Card throwFirstCard() {
        if (66 <= (possiblePoints()) + score) {
            return gainPointsLooseCards();
        } else {
            return gainPointsSaveCards();
        }
    }

    public Card throwAnswer() {
        Card master = controller.getPorts().get(0).getCard();
        for (Card card : getCardsInHand()) {
            if (conditionsToTrickCard(card)) {
                throwCard.add(card);
            }
        }
        if (throwCard.isEmpty()) {
            throwCard.add(findScapeGoat());
        }
        Collections.shuffle(throwCard);
        return throwCard.get(0);
    }

    private boolean conditionsToTrickCard(Card card){
        boolean cardIsSameColour = controller.getPorts().get(0).getCard().getColor().equals(card.getColor());
        boolean cardIsHigher = controller.getPorts().get(0).getCard().getValue() < card.getValue();
        boolean cardIsTrump = card.getColor().equals(Deck.getTrumpfColor());

        return cardIsSameColour && cardIsHigher && cardIsTrump;
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
        if (getScore() > 65) {
            controller.endingGame(this);
        }
    }

    private void blockStapel() {
        System.out.println("requesting blockstapel");
        if (66 <= (possiblePoints()) + score) {
            Deck.blockStapel();
        }
    }

    private ArrayList<Card> thrownCards() {
        for (Port port : controller.ports) {
            thrownCards.add(port.getCard());
        }
        return thrownCards;
    }

    private int possiblePoints() {
        int possiblePoints = 0;
        int thrownpoints = 0;

        for (Card c : thrownCards()) {
            if (c.getValue() > 9) {
                thrownpoints += c.getValue();
            }
                possiblePoints = 108 - thrownpoints;

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
