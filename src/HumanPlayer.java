import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class HumanPlayer extends Player {

    private Scanner scanner = new Scanner(System.in);

    private void showCardsToThrow() {
        for (int i = 0; i < cardsInHand.size(); i++) {
            printCardToThrow(i);
        }
    }

    @Override
    protected void playerAction() {
        System.out.println("started playerAction");
        playerActionOptions();
        Card card = playerActionExecution();
        Controller.turnSwitcher(this, card);

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
                    System.out.println("Which card do you change the Trumpf with?");
                    showCardsToThrow();
                    i = scanner.nextInt();
                    changeTrumpfCard(throwCard(i));

                case 7:
                    if (Deck.getStapel().size() > 0) {
                        System.out.println("You blocked the Stapel");
                        Deck.blockStapel();
                    } else {
                        System.out.println("Stapel already blocked!");
                    }
                    playerAction();
                case 8:
                    Controller.endingGame();
            }
        } catch (InputMismatchException input) {
            System.out.println("Input couldn't be read: " + input.getMessage());
            playerActionExecution();
        }
        return null;
    }

    private Card throwCard(int i) {
        System.out.println("throwCard");
        i--;
        System.out.println(Fonts.GREEN_BOLD + "You threw: " + cardsInHand.get(i).getName() + Fonts.RESET);

        Card temp = cardsInHand.get(i);
        cardsInHand.remove(cardsInHand.get(i));
        return temp;
    }

    private void playerActionOptions() {
        System.out.println(Fonts.BLUE_BOLD + name + " is on turn" + Fonts.RESET);

        System.out.println("Please throw a Card or make another action");

        showCardsToThrow();

        System.out.println("Action 6: Change the Trumpfcard");

        if (Deck.getStapel().size() > 0) {
            System.out.println("Action 7: Block the stapel");
        }
        System.out.println("Action 8: Bet on ending the Game and start counting!");
    }
}
