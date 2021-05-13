import java.util.ArrayList;
import java.util.Collections;

public class NPC extends Player {

    private ArrayList<Card> thrownCards = new ArrayList<>();
    private ArrayList<Card> throwCard = new ArrayList<>();
    private boolean herzSaveToPlay = false;
    private boolean karoSaveToPlay = false;
    private boolean pikSaveToPlay = false;
    private boolean kreuzSaveToPlay = false;
    private ArrayList<Card> countHerz = new ArrayList<>();
    private ArrayList<Card> countKaro = new ArrayList<>();
    private ArrayList<Card> countPik = new ArrayList<>();
    private ArrayList<Card> countKreuz = new ArrayList<>();
    private String trumpf = controller.deck.getTrumpf().getColor();
    private ArrayList<Card> countTrumpfCards;

    NPC(int i, Controller controller) {
        super(controller);
        name = "Machine-" + i;
        couldBeInOthersHand();
    }

    @Override
    protected Port playerAction() {
        couldBeInOthersHand();
        Port cardOutput;
        //checking if NPC is on turn
        if (controller.ports.size() == 0) {
            endingGame();
            blockStapel();
            //check if NPC should change Trumpf
            Card card = lookToChangeTrumpCard();
            if (card != null) {
                changeTrumpfCard(card);
            }
            Card pairs = callPairs();
            if (pairs != null) {
                executePairs(pairs);
                cardOutput = new Port(this, throwCard(pairs));
            } else {
                System.out.println("throw first card");
                cardOutput = new Port(this, throwCard(throwFirstCard()));
            }
        } else {
            System.out.println("throw answer");
            cardOutput = new Port(this, throwCard(throwAnswer()));
        }
        drawCard();
        return cardOutput;
    }

    private Card lookToChangeTrumpCard() {
        if (controller.deck.getTrumpf() != null) {
            for (Card c : getCardsInHand()) {
                if (conditionsToChangeTrumpCard(c)) {
                    return c;
                }
            }
        }
        return null;
    }

    private boolean conditionsToChangeTrumpCard(Card card) {
        boolean cardmatchescolour = card.getColor().equals(controller.deck.getTrumpfColor());
        boolean cardIsBube = card.getValue() == 2;

        return cardmatchescolour && cardIsBube;
    }

    @Override
    protected Card playerActionExecution() {
        return null;
    }

    @Override
    protected Card callPairs() {
        //System.out.println(Fonts.RED_BOLD + "requesting callPairs" + Fonts.RESET);
        for (Card master : getCardsInHand()) {
            for (Card slave : getCardsInHand()) {
                if (cardMatchesPair(master, slave)) {
                    //System.out.println("checking match");
                    System.out.println(master.getName() + ", " + slave.getName());
                    if (slave.getValue() < master.getValue()) {
                        //System.out.println("slave is smaller");
                        return slave;
                    } else {
                        //System.out.println("master is smaller");
                        return master;
                    }
                }
            }
        }
        return null;
    }

    private void executePairs(Card card) {
        if (controller.deck.getTrumpfColor().equals(card.getColor())) {
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
        throwCard.clear();
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

    private boolean conditionsToTrickCard(Card card) {
        boolean cardIsSameColour = controller.getPorts().get(0).getCard().getColor().equals(card.getColor());
        boolean cardIsHigher = controller.getPorts().get(0).getCard().getValue() < card.getValue();
        boolean cardIsTrump = card.getColor().equals(controller.deck.getTrumpfColor());

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
        //System.out.println("requesting ending game");
        if (getScore() > 65) {
            controller.endingGame(this);
        }
    }

    private void blockStapel() {
        //System.out.println("requesting blockstapel");
        if (66 <= (possiblePoints()) + score) {
            controller.deck.blockStapel();
        }
    }

    private ArrayList<Card> thrownCards(Card card) {
        thrownCards.add(card);
        return thrownCards;
    }

    private int possiblePoints() {
        int possiblePoints = 0;
        int thrownpoints = 0;

        for (Card c : thrownCards) {
            if (c.getValue() > 9) {
                thrownpoints += c.getValue();
            }
            possiblePoints = 108 - thrownpoints;

        }
        return possiblePoints;
    }

    private Card gainPointsSaveCards() {
        throwCard.clear();
        for (Card c : getCardsInHand()) {
            if (c.getValue() < 10) {
                throwCard.add(c);
            }
            if (c.getValue() == 2) {
                return c;
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
        ArrayList<Card> throwCard = new ArrayList<>();
        for (Card card : cardsInHand) {

            if (card.getColor().equals("herz") && herzSaveToPlay) {
                throwCard.add(card);
            }
            if (card.getColor().equals("karo") && karoSaveToPlay) {
                throwCard.add(card);
            }
            if (card.getColor().equals("pik") && pikSaveToPlay) {
                throwCard.add(card);
            }
            if (card.getColor().equals("kreuz") && kreuzSaveToPlay) {
                throwCard.add(card);
            }

        }
        if (throwCard.isEmpty()) {
            return gainPointsSaveCards();
        }
        Collections.shuffle(throwCard);
        return throwCard.get(0);
    }

    private String playerCouldntmatchCard(ArrayList<Card> ports) {
        /*
        for (int i = thrownCards.size(); i < controller.getPlayers().size(); i--) {
            if (!thrownCards.get(thrownCards.size()-1).equals(thrownCards.get(i / 2))) {
                System.out.println("throw " + thrownCards.get(i / 2).getColor() + " to gain points");
                return thrownCards.get(i / 2).getColor();
            }
        }

         */
        return null;
    }

    private boolean advancedConditionsToThrowCard(Card card) {
        boolean trumpfisGone = countTrumpfCards.size() == 0;
        boolean isntTrumpf = !card.getColor().equals(trumpf);

        switch (card.getColor()) {
            case "herz":
                for (Card c : countHerz) {
                    if (c.getValue() > card.getValue()) {
                        return false;
                    }
                }
            case "karo":
                for (Card c : countKaro) {
                    if (c.getValue() > card.getValue()) {
                        return false;
                    }
                }
            case "pik":
                for (Card c : countPik) {
                    if (c.getValue() > card.getValue()) {
                        return false;
                    }
                }
            case "kreuz":
                for (Card c : countKreuz) {
                    if (c.getValue() > card.getValue()) {
                        return false;
                    }
                }
        }
        return trumpfisGone && isntTrumpf;
    }

    private void saveToPlay() {

        switch (trumpf) {
            case "herz":
                countTrumpfCards = countHerz;
            case "karo":
                countTrumpfCards = countKaro;
            case "pik":
                countTrumpfCards = countPik;
            case "kreuz":
                countTrumpfCards = countKreuz;
            default:
                System.out.println("no trumpf card!");
                countTrumpfCards = null;
        }

        if (countTrumpfCards.size() == 0) {
            if (countHerz.size() == 0) {
                herzSaveToPlay = true;
            }
            if (countKaro.size() == 0) {
                karoSaveToPlay = true;
            }
            if (countPik.size() == 0) {
                pikSaveToPlay = true;
            }
            if (countKreuz.size() == 0) {
                kreuzSaveToPlay = true;
            }
        }
    }

    private ArrayList<Card> filterCards(Card card) {
        if (card.getColor().equals("herz")) {
            return countHerz;
        }
        if (card.getColor().equals("karo")) {
            return countKaro;
        }
        if (card.getColor().equals("pik")) {
            return countPik;
        }
        if (card.getColor().equals("kreuz")) {
            return countKreuz;
        }
        System.out.println("Error filter Cards found null!");
        return null;
    }

    private void couldBeInOthersHand() {
        Deck deck = new Deck();
        ArrayList<Card> deckInGame = deck.getStapel();
        for (Card card : deckInGame) {
            ArrayList<Card> temp = filterCards(card);
            temp.add(card);
        }
    }

    private void couldBeInOthersHandRefresh(Card card) {
        ArrayList<Card> temp = filterCards(card);
        temp.remove(card);
    }

    @Override
    protected void countCards(Port port) {
        couldBeInOthersHandRefresh(port.getCard());
        thrownCards(port.getCard());
    }
}
