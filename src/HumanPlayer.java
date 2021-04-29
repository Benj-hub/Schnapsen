import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private Scanner scanner = new Scanner(System.in);

    public HumanPlayer() {
        System.out.println("Hello there!");
        System.out.println("How do you want to be called?");
        name = scanner.nextLine();
        if (name.equals("")){
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
    protected Port playerAction() {
        System.out.println("started playerAction");
        playerActionOptions();
        return new Port(this, playerActionExecution());
        }

    protected Card playerActionExecution() {
        System.out.println("started playerActionExecution");

        try {
            int i = scanner.nextInt();

            if (i < 6) {
                drawCard();
                return throwCard(i);
            }
            switch (i) {
                case 6:
                    callPairs();
                case 7:
                    System.out.println("Which card do you change the Trumpf with?");
                    showCardsToThrow();
                    i = scanner.nextInt();
                    changeTrumpfCard(throwCard(i));

                case 8:
                    if (Deck.getStapel().size() > 0) {
                        System.out.println(name + " blocked the Stapel");
                        Deck.blockStapel();
                    } else {
                        System.out.println("Stapel already blocked!");
                    }
                    playerAction();
                case 9:
                    Controller.endingGame(this);
                default:
                    playerAction();
            }
        } catch (InputMismatchException input) {
            System.out.println("Input couldn't be read: " + input.getMessage());
            playerActionExecution();
        }
        return null;
    }

    @Override
    protected Card callPairs() {
        System.out.println("Show me what you got!");

        showCardsToThrow();
        int m = scanner.nextInt();
        Card master = cardsInHand.get(m);
        System.out.println("...and?");
        int s = scanner.nextInt();
        Card slave = cardsInHand.get(s);

        if (checkPair(master, slave)) {
            if (master.getValue()<slave.getValue()) {
                return master;
            } else {
                return slave;
            }
        } else {
            System.out.println("No Pairs in Deck!");
            playerAction();
        }
        return null;
    }

    private boolean checkPair(Card master, Card slave) {
        if (cardMatchesPair(master, slave)) {
            if (Deck.getTrumpfColor().equals(master.getColor())){
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

    private Card throwCard(int i) {
        //System.out.println("throwCard");
        i--;
        System.out.println(Fonts.GREEN_BOLD + name + " threw: " + cardsInHand.get(i).getName() + Fonts.RESET);

        Card temp = cardsInHand.get(i);
        cardsInHand.remove(cardsInHand.get(i));
        return temp;
    }

    private void playerActionOptions() {
        System.out.println(Fonts.BLUE_BOLD + name + " is on turn" + Fonts.RESET);

        System.out.println("Please throw a Card or make another action");

        showCardsToThrow();

        System.out.println("Action 6: Call Pairs");

        System.out.println("Action 7: Change the Trumpfcard");

        if (Deck.getStapel().size() > 0) {
            System.out.println("Action 8: Block the stapel");
        }
        System.out.println("Action 9: Bet on ending the Game and start counting!");
    }
}
