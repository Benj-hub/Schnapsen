// created by Benjamin Lamprecht

import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

class Controller {
    // COLOR SCHEME
    // Reset
    public static final String RESET = "\033[0m";  // Text Reset


    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    Scanner scanner = new Scanner(System.in);
    //only temporary solution, wird mit switcher(NPC/Human player) und folgenden scanner ein player 2 implementiert
    Player p1 = new Player();
    Player p2 = new Player();
    ArrayList<Player> players;
    //collecting players
    //dealing out cards
    private Deck deck;
    //Input for tricks
    private ArrayList<Port> ports = new ArrayList<>();

    public Controller() {

        //i need to replace players with port
        this.players = new ArrayList<Player>();
        this.deck = new Deck();

        System.out.println("What's your name?");

        p1.setName("scanner.next()");
        p2.setName("Dummy");


        //only temporary solution
        players.add(p1);
        players.add(p2);

        dealCards();

        for (Card c :
                p1.getCardsInHand()) {
            System.out.println(c.getName());
        }
        System.out.println("=================================");
        for (Card c :
                p2.getCardsInHand()) {
            System.out.println(c.getName());
        }

        System.out.println("Trumpf card is: " + deck.getTrumpf().getName());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }

        firstTurn();
    }

    private void dealCards() {


        for (Player p : players) {
            ArrayList<Card> tempStapel = new ArrayList<>(deck.getStapel());

            ArrayList<Card> tempHand = new ArrayList<>();

            for (Card c : tempStapel) {

                if (tempHand.size() < 5) {
                    tempHand.add(c);
                    deck.getStapel().remove(c);
                }
                p.setCardsInHand(tempHand);
            }
        }
    }

    private void firstTurn() {
        Collections.shuffle(players);
        System.out.println();
        playerAction(players.get(0));
    }

    private void playerAction(Player p) {

        System.out.println(p.getName() + " is on turn");
        System.out.println("Please throw a Card or make another action");

        playerActionOptions(p);
        int i = scanner.nextInt();
        playerActionExecution(p, i);
        playerAction(p);
    }

    private void showCardsToThrow(Player p) {
        for (int i = 0; i < p.getCardsInHand().size(); i++) {
            printCardToThrow(p, i);
        }
    }

    private void turnSwitcher(ArrayList<Port> ports) {

        if (players.size() == ports.size()) {
            tricks();
        }

        for (Player p : players) {
            for (int i = 0; i < ports.size(); i++) {
                if (p.equals(ports.get(i).getPlayer())) {
                    if (i < players.size()) {
                        playerAction(players.get(i + 1));
                    } else {
                        playerAction(players.get(0));
                    }
                }
            }
        }
    }


    private void tricks() {

        Port winCard = ports.get(0);

        for (Port port : ports) {
            winCard = checkWinCard(winCard, port);

        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nTurn ended \n");
        findPlayerToWinCard(winCard);
        playerAction(winCard.getPlayer());
    }

    private void endingGame(Player p) {
        Player winPlayer = p;
        for (Player temp : players) {
            winPlayer = searchWinner(p, winPlayer);
        }
        announceWinner(winPlayer);
        System.exit(0);
    }


    private Card throwCard(Player p, int i) {
        i--;
        System.out.println(GREEN_BOLD + "You threw: " + p.getCardsInHand().get(i).getName() + RESET);

        Card temp = p.getCardsInHand().get(i);
        p.getCardsInHand().remove(p.getCardsInHand().get(i));
        return temp;
    }

    private void drawCard(Player p) {
        if (deck.getStapel().size() == 0) {
            if (deck.getTrumpf() != null) {
                drawTrumpf(p);
            }
            System.out.println(YELLOW_BOLD + "Stapel is empty!" + RESET);
        } else {
            drawNewCard(p);
        }
    }

    private void drawTrumpf(Player p) {
        p.getCardsInHand().add(deck.getTrumpf());
        deck.setTrumpf(null);
    }

    private void drawNewCard(Player p) {
        p.getCardsInHand().add(deck.getStapel().get(0));
        deck.getStapel().remove(deck.getStapel().get(0));
    }

    private Player findPlayerToWinCard(Port winCard) {
        for (Port p : ports) {
            if (p.getPlayer().equals(winCard.getPlayer())) {
                System.out.println("Player " + p.getPlayer().getName() + " won cards: ");

                for (int j = 0; j < ports.size(); j++) {
                    p.getPlayer().getPreviousTricks().add(ports.get(j).getCard());
                    p.getPlayer().setScore(p.getPlayer().getScore() + ports.get(j).getCard().getValue());
                    System.out.print(ports.get(j).getCard().getName());
                    if (j < ports.size() - 1) {
                        System.out.print(", ");

                    }
                }
                System.out.println();
            }
            ports.clear();
            return p.getPlayer();
        }
        return null;
    }


    private Port checkWinCard(Port winCard, Port port) {

        boolean checkIfCardIsHigher = port.getCard().getValue() > winCard.getCard().getValue();
        boolean checkIfCardmatchescolour = port.getCard().getColor().equals(winCard.getCard().getColor());
        boolean checkIfCardIsTrumpf = port.getCard().getColor().equals(deck.getTrumpfColor());
        boolean checkIfWincardIsTrumpf = winCard.getCard().getColor().equals(deck.getTrumpfColor());

        if ((checkIfCardIsHigher && checkIfCardmatchescolour) || (checkIfCardIsTrumpf && !checkIfWincardIsTrumpf)) {
            winCard = port;
        }
        return winCard;
    }

    private Player searchWinner(Player p, Player winPlayer) {

        if (p.getScore() > winPlayer.getScore()) {
            return p;
        }
        return winPlayer;
    }

    private void announceWinner(Player winPlayer) {
        if (winPlayer.getScore() < 33) {
            System.out.println("Player " + winPlayer.getName() + " called too early and lost the game");
        } else {
            System.out.println("\nPlayer " + winPlayer.getName() + " wins!");
            System.out.println("Points: " + winPlayer.getScore());
        }
    }

    private Boolean checkColour(Card card) {
        boolean checkIfCardmatchescolour = card.getColor().equals(ports.get(0).getCard().getColor());
        boolean checkIfCardIsTrumpf = card.getColor().equals(deck.getTrumpfColor());

        return checkIfCardIsTrumpf || checkIfCardmatchescolour;
    }

    private void playerActionOptions(Player p) {
        showCardsToThrow(p);

        System.out.println("Action 6: Change the Trumpfcard");

        if (deck.getStapel().size() > 0) {
            System.out.println("Action 7: Block the stapel");
        }
        System.out.println("Action 8: Bet on ending the Game and start counting!");

    }

    private void playerActionExecution(Player p, Integer i) {

        try {
            if (i < 6) {
                ports.add(new Port(p, throwCard(p, i)));
                drawCard(p);
                turnSwitcher(ports);
            }
            switch (i) {
                case 6:
                    System.out.println("Which card do you change the Trumpf with?");
                    showCardsToThrow(p);
                    i = scanner.nextInt();
                    changeTrumpfCard(p, throwCard(p, i));

                case 7:
                    if (deck.getStapel().size() > 0) {
                        System.out.println("You blocked the Stapel");
                        deck.blockStapel();
                    } else {
                        System.out.println("Stapel already blocked!");
                    }
                    playerAction(p);
                case 8:
                    endingGame(p);
            }
        } catch (InputMismatchException input) {
            System.out.println("Input couldn't be read: " + input.getMessage());
            playerActionExecution(p, i);
        }
    }

    private void changeTrumpfCard(Player p, Card card) {
        if (card.getColor().equals(deck.getTrumpf().getColor())) {
            p.getCardsInHand().add(deck.getTrumpf());
            deck.setTrumpf(card);
            System.out.println(p.getName() + " gets Trump Card.");
            System.out.println("Trump changed to " + card.getName());
        } else {
            System.out.println(card.getName() + " doesn't match");
        }

        playerAction(p);
    }

    private void printCardToThrow(Player p, Integer i) {

        int action = i + 1;

        if (ports.size() > 0) {
            if (checkColour(p.getCardsInHand().get(i))) {
                System.out.println(BLACK_BOLD + "Action " + (action) + ": Throw " + p.getCardsInHand().get(i).getName() + RESET);
            } else {
                System.out.println("Action " + (action) + ": Throw " + p.getCardsInHand().get(i).getName());
            }
        } else {
            System.out.println("Action " + (action) + ": Throw " + p.getCardsInHand().get(i).getName());
        }
    }

}