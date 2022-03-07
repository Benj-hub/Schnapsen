import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class NPC extends Player {

    private final ArrayList<PlayerCard> thrownCards = new ArrayList<>();
    private final ArrayList<Card> throwCard = new ArrayList<>();
    private final ArrayList<Card> countHearts = new ArrayList<>();
    private final ArrayList<Card> countDiamond = new ArrayList<>();
    private final ArrayList<Card> countSpade = new ArrayList<>();
    private final ArrayList<Card> countCross = new ArrayList<>();
    private final String trump = controller.deck.getTrump().getColor();
    private final ArrayList<Card> countTrumpCards = filterCards(controller.deck.getTrump());

    NPC(Controller controller) {
        super(controller);
        name = "Machine";
        couldBeInOthersHand();
    }

    @Override
    protected PlayerCard playerAction() {
        System.out.println(Fonts.BLUE_BOLD + name + " is on turn" + Fonts.RESET);
        throwCard.clear();
        PlayerCard cardOutput;

        //checking if NPC is on turn
        if (controller.playerCards.size() == 0) {
            endingGame();
            blockDeck();
            //check if NPC should change Trump
            Card card = lookToChangeTrumpCard();
            if (card != null) {
                changeTrumpCard(card);
            }
            Card pairs = callPairs();
            if (pairs != null) {
                executePairs(pairs);
                cardOutput = new PlayerCard(this, throwCard(pairs));
            } else {
                //System.out.println("throw first card");
                cardOutput = new PlayerCard(this, throwCard(throwFirstCard()));
            }
        } else {
            //System.out.println("throw answer");
            cardOutput = new PlayerCard(this, throwCard(throwAnswer()));
        }
        drawCard();
        return cardOutput;
    }

    private Card lookToChangeTrumpCard() {
        if (controller.deck.getTrump() != null) {
            for (Card c : getCardsInHand()) {
                if (conditionsToChangeTrumpCard(c)) {
                    return c;
                }
            }
        }
        return null;
    }

    private boolean conditionsToChangeTrumpCard(Card card) {
        boolean cardMatchesColor = card.getColor().equals(controller.deck.getTrumpColor());
        boolean cardIsPrince = card.getValue() == 2;

        return cardMatchesColor && cardIsPrince;
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
        if (controller.deck.getTrumpColor().equals(card.getColor())) {
            score += 40;
            System.out.println("40 Points for Griff... *cough* " + name + "!");
        } else {
            score += 20;
            System.out.println("20 Points for Griff... *cough* " + name + "!");
        }
    }

    private Card throwAnswer() {
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

    private Card throwFirstCard() {
        for (Card card : cardsInHand) {
            advancedConditionsToThrowCard(card);
        }
        if (throwCard.isEmpty()) {
            simpleConditionsToThrowCard();
        }
        Collections.shuffle(throwCard);
        return throwCard.get(0);
    }

    private void playHigh() {
        Card temp = cardsInHand.get(0);
        for (Card card : cardsInHand) {
            if (card.getValue() > temp.getValue())
                temp = card;
        }
        throwCard.add(temp);
    }

    private void playLow() {
        Card temp = cardsInHand.get(0);
        for (Card card : cardsInHand) {
            if (card.getValue() < temp.getValue())
                temp = card;
        }
        throwCard.add(temp);
    }

    private boolean conditionsToTrickCard(Card card) {
        boolean cardIsSameColour = controller.getPorts().get(0).getCard().getColor().equals(card.getColor());
        boolean cardIsHigher = controller.getPorts().get(0).getCard().getValue() < card.getValue();
        boolean cardIsTrump = card.getColor().equals(controller.deck.getTrumpColor());

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

    private void blockDeck() {
        if (66 <= (possiblePoints()) + score) {
            controller.deck.blockDeck();
        }
    }

    private void thrownCards(Card card) {
        thrownCards.add(new PlayerCard(this, card));
    }

    private int possiblePoints() {
        int possiblePoints = 0;
        int thrownPoints = 0;

        for (PlayerCard c : thrownCards) {
            if (c.getCard().getValue() > 9) {
                thrownPoints += c.getCard().getValue();
            }
            possiblePoints = 108 - thrownPoints;

        }
        return possiblePoints;
    }

    private String playerCannotMatchCard() {
        for (int i = thrownCards.size()-1; i > controller.getPlayers().size(); i--) {
            Card dealCard = thrownCards.get(controller.getPlayers().size()-1).getCard();
            if (!thrownCards.get(i).getCard().getColor().equals(dealCard.getColor())) {
                return dealCard.getColor();
            }
        }
        return null;
    }

    private void simpleConditionsToThrowCard() {
        if (66 <= (possiblePoints()) + score) {
            playHigh();
        }
        playLow();
    }

    private void advancedConditionsToThrowCard(Card card) {
        assert countTrumpCards != null;
        boolean trumpIsGone = countTrumpCards.isEmpty();
        boolean isNotTrump = !card.getColor().equals(trump);
        boolean highestCardInStack = card.getValue() == getHighestCardInStack(card).getValue();

        if (saveToPlay(card)) {
            throwCard.add(card);
        } else if (trumpIsGone && isNotTrump && highestCardInStack) {
                throwCard.add(card);
            } else if (Objects.equals(playerCannotMatchCard(), card.getColor())) {
        throwCard.add(card);
    } else if (highestCardInStack) {
                    throwCard.add(card);
                }
            }

    private Card getHighestCardInStack(Card master) {
        for (Card slave : cardsInHand) {
            if (slave.getValue() > master.getValue()) {
                master = slave;
            }
        }
        return master;
    }

    private boolean saveToPlay(Card card) {
        return Objects.requireNonNull(filterCards(card)).size() == 0 && !Objects.equals(filterCards(card), countTrumpCards);
    }

    private ArrayList<Card> filterCards(Card card) {
        if (card.getColor().equals("herz")) {
            return countHearts;
        }
        if (card.getColor().equals("karo")) {
            return countDiamond;
        }
        if (card.getColor().equals("pik")) {
            return countSpade;
        }
        if (card.getColor().equals("kreuz")) {
            return countCross;
        }
        System.out.println("Error filter Cards found null!");
        return null;
    }

    private void couldBeInOthersHand() {
        Deck deck = new Deck();
        ArrayList<Card> deckInGame = deck.getDeck();
        for (Card card : deckInGame) {
            Objects.requireNonNull(filterCards(card)).add(card);
        }

        for (Card card : cardsInHand) {
            Objects.requireNonNull(filterCards(card)).remove(card);
        }
    }

    private void couldBeInOthersHandRefresh(Card card) {
        Objects.requireNonNull(filterCards(card)).remove(card);
    }

    @Override
    protected void countCards(PlayerCard playerCard) {
        couldBeInOthersHandRefresh(playerCard.getCard());
        thrownCards(playerCard.getCard());
    }
}
