import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class NPC extends Player {

    private ArrayList<Port> thrownCards = new ArrayList<>();
    private ArrayList<Card> throwCard = new ArrayList<>();
    private ArrayList<Card> countHerz = new ArrayList<>();
    private ArrayList<Card> countKaro = new ArrayList<>();
    private ArrayList<Card> countPik = new ArrayList<>();
    private ArrayList<Card> countKreuz = new ArrayList<>();
    private String trumpf = controller.deck.getTrumpf().getColor();
    private ArrayList<Card> countTrumpfCards = filterCards(controller.deck.getTrumpf());

    NPC(int i, Controller controller) {
        super(controller);
        name = "Machine-" + i;
        couldBeInOthersHand();
    }

    @Override
    protected Port playerAction() {
        throwCard.clear();
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
        System.out.println("no advanced throw first Card");
        if (throwCard.isEmpty()) {
            simpleConditionsToThrowCard();
        }
        if (throwCard.size() == 0){
            System.out.println("throwCard is EMPTY");
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

    private void thrownCards(Card card) {
        thrownCards.add(new Port(this, card));
    }

    private int possiblePoints() {
        int possiblePoints = 0;
        int thrownpoints = 0;

        for (Port c : thrownCards) {
            if (c.getCard().getValue() > 9) {
                thrownpoints += c.getCard().getValue();
            }
            possiblePoints = 108 - thrownpoints;

        }
        return possiblePoints;
    }

    private String playerCouldntmatchCard() {
        for (int i = thrownCards.size()-1; i > controller.getPlayers().size(); i--) {
            Card dealCard = thrownCards.get(controller.getPlayers().size()-1).getCard();
            Player temp = thrownCards.get(i).getPlayer();
            if (notInMyTeam(temp) && !thrownCards.get(i).getCard().getColor().equals(dealCard.getColor())) {
                System.out.println(throwCard.get(i).getName() + " failed to match Card");
                System.out.println("throw " + thrownCards.get(i).getCard() + " to gain points");
                return dealCard.getColor();
            }
        }
        return null;
    }

    private boolean notInMyTeam(Player player) {
        for (Player p : notMyTeam()) {
            if (p.equals(player)) {
                return true;
            }
        }
        return false;
    }

    private void simpleConditionsToThrowCard() {
        if (66 <= (possiblePoints()) + score) {
            playHigh();
        }
        playLow();
    }

    private void advancedConditionsToThrowCard(Card card) {
        boolean trumpfisGone = countTrumpfCards.isEmpty();
        boolean isntTrumpf = !card.getColor().equals(trumpf);
        boolean highestCardInStack = card.getValue() == getHighestCardInStack(card).getValue();

        if (saveToPlay(card)) {
            throwCard.add(card);
        } else if (trumpfisGone && isntTrumpf && highestCardInStack) {
                throwCard.add(card);
            } else if (Objects.equals(playerCouldntmatchCard(), card.getColor())) {
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
        return Objects.requireNonNull(filterCards(card)).size() == 0 && !filterCards(card).equals(countTrumpfCards);
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
    protected void countCards(Port port) {
        couldBeInOthersHandRefresh(port.getCard());
        thrownCards(port.getCard());
    }

    private ArrayList<Player> myTeam() {
        for (Player player : controller.teamOne) {
            if (player.equals(this)){
                return controller.teamOne;
            }
        }
        return controller.teamTwo;
    }

    private ArrayList<Player> notMyTeam() {
        for (Player player : controller.teamOne) {
            if (player.equals(this)){
                return controller.teamTwo;
            }
        }
        return controller.teamOne;
    }
}
