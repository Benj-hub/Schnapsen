// created by Benjamin Lamprecht 2020

import java.util.InputMismatchException;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private final Scanner scanner = new Scanner(System.in);

    HumanPlayer(Controller controller) {
        super(controller);

        System.out.println("Hello there!");
        System.out.print("name: ");
        name = scanner.nextLine();
        if (name.equals("")) {
            name = "General Kenobi";
        }
        System.out.print(name + "!, ");
    }

    private void showCardsToThrow() {
        for (int i = 0; i < cardsInHand.size(); i++) {
            printCardToThrow(i);
        }
    }

    @Override
    protected PlayerCard playerAction() {
        //System.out.println("started playerAction");
        playerActionOptions();
        return new PlayerCard(this, playerActionExecution());
    }

    protected Card playerActionExecution() {
        Card card;
        //System.out.println("started playerActionExecution");

        try {
            int i = scanner.nextInt();

            if (i < 6) {
                drawCard();
                card = throwCard(i);
                return card;
            } else if (controller.playerCards.size() != 0) {
                System.out.println("You have to deal to make that action!");
            }
            switch (i) {
                case 6:
                    if (controller.playerCards.size() == 0) {
                        Card temp = callPairs();
                        if (temp != null) {
                            return throwCard(temp);
                        }
                    }

                case 7:
                        System.out.println("Which card do you change the Trump with?");
                        showCardsToThrow();
                        i = scanner.nextInt();
                        changeTrumpCard(getCardsInHand().get(i-1));
                        break;
                case 8:
                        if (controller.getDeck().getDeck().size() > 0) {
                            System.out.println(name + " blocked the Deck");
                            controller.deck.blockDeck();
                            System.out.println("What action shall follow?");
                        } else {
                            System.out.println("Deck already blocked!");
                        }
                        break;
                case 9:
                        controller.endingGame(this);
                        break;
            }
        } catch (InputMismatchException input) {
            System.out.println("Input couldn't be read: " + input.getMessage());
        }
        card = playerActionExecution();
        return card;
    }

    @Override
    protected Card callPairs() {
        System.out.println("Show me what you got!");

        showCardsToThrow();
        int m = scanner.nextInt();
        Card master = cardsInHand.get(m - 1);
        System.out.println("...and?");
        int s = scanner.nextInt();
        Card slave = cardsInHand.get(s - 1);

        if (checkPair(master, slave)) {
            if (master.getValue() < slave.getValue()) {
                return master;
            } else {
                return slave;
            }
        } else {
            System.out.println("No Pairs in Deck!");
            playerActionExecution();
        }
        return null;
    }

    @Override
    protected void countCards(PlayerCard playerCard) {

    }

    private boolean checkPair(Card master, Card slave) {
        if (cardMatchesPair(master, slave)) {
            if (controller.deck.getTrumpColor().equals(master.getColor())) {
                score += 40;
                System.out.println("40 Points for Griff... *cough* " + name + "!");
            } else {
                score += 20;
                System.out.println("20 Points for Griff... *cough* " + name + "!");
            }
            return true;
        }
        return false;
    }

    private void playerActionOptions() {
        System.out.println(Fonts.BLUE_BOLD + name + " is on turn" + Fonts.RESET);

        System.out.println("Please throw a Card or make another action");

        showCardsToThrow();

        if (controller.playerCards.size() == 0) {
            System.out.println("Action 6: Call Pairs");

            System.out.println("Action 7: Change the Trump Card");

            if (controller.deck.getDeck().size() > 0) {
                System.out.println("Action 8: Block the deck");
            }
            System.out.println("Action 9: Bet on ending the Game and start counting!");

        }
    }
}
