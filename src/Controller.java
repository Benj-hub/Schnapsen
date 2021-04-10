// created by Benjamin Lamprecht

import java.util.ArrayList;
import java.util.Collections;
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

        for (Card c:
             p1.getCardsInHand()) {
            System.out.println(c.getName());
        }
        System.out.println("=================================");
        for (Card c:
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

    private void turnSwitcher(ArrayList<Port> ports) {

        for (Player p : players) {
            for (int i = 0; i < ports.size(); i++) {
                if (p.equals(ports.get(i).getPlayer())) {
                       if (i < players.size()){
                           playerAction(players.get(i+1));
                       } else {
                           playerAction(players.get(0));
                       }
                }
            }
        }
    }

    private void tricks(ArrayList<Port> ports) {

        Port winCard = ports.get(0);


        if (ports.size() == players.size()) {

            for (Port port : ports) {

                boolean a = port.getCard().getValue() > winCard.getCard().getValue();
                boolean b = port.getCard().getColor().equals(winCard.getCard().getColor());
                boolean c = port.getCard().getColor().equals(deck.getTrumpf().getColor());
                boolean d = !winCard.getCard().getColor().equals(deck.getTrumpf().getColor());

                if ((a && b) || (c && d)) {
                    winCard = port;
                }
            }
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
                playerAction(p.getPlayer());
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nTurn ended \n");
        turnSwitcher(ports);
    }

    private void endingGame(Player p) {
        Player winPlayer = p;
        int i = 1;
        for (Player temp : players) {

            if (temp.getScore() > winPlayer.getScore()) {
                winPlayer = temp;
            }
            if (i == players.size())
                System.out.println("Player " + temp.getName() + ": " + temp.getScore());
            i++;
        }
        if (winPlayer.getScore() < 33) {
            System.out.println("Player " + p.getName() + " called too early and lost the game");
        } else {
            System.out.println("\nPlayer " + winPlayer.getName() + " wins!");
            System.out.println("Points: " + winPlayer.getScore());
        }
        System.exit(0);
    }

    private void playerAction(Player p) {

        int j = 1;
        boolean haveMatchCardInHand = false;

        System.out.println(p.getName() + " is on turn");
        System.out.println("Please throw a Card or make another action");

        if (ports.size() > 0) {
            for (int i = 0; i < p.getCardsInHand().size(); i++) {
                if (ports.get(0).getCard().getColor().equals(p.getCardsInHand().get(i).getColor())
                        || deck.getTrumpf().getColor().equals(p.getCardsInHand().get(i).getColor())) {
                    haveMatchCardInHand = true;
                    System.out.println("Action " + (i + 1) + ": Throw " + p.getCardsInHand().get(i).getName());

                    j = scanner.nextInt();

                    if (j < 6) {
                        j--;
                        System.out.println(GREEN_BOLD + "You threw: " + p.getCardsInHand().get(j).getName() + RESET);
                        System.out.println();
                        p.getCardsInHand().remove(p.getCardsInHand().get(j));
                        drawCard(p);
                        Port port = new Port(p, p.getCardsInHand().get(j));
                        ports.add(port);
                        tricks(ports);
                    }
                }

                }
        } if (!haveMatchCardInHand) {
            for (int i = 0; i < p.getCardsInHand().size(); i++) {

                System.out.println("Action " + (i + 1) + ": Throw " + p.getCardsInHand().get(i).getName());

            }

            System.out.println("Action 6: Change the Trumpfcard");

            if (deck.getStapel().size() > 0) {
                System.out.println("Action 7: Block the stapel");
            }
            System.out.println("Action 8: Bet on ending the Game and start counting!");

            j = scanner.nextInt();

            if (j < 6) {
                j--;
                System.out.println(GREEN_BOLD + "You threw: " + p.getCardsInHand().get(j).getName() + RESET);
                System.out.println();
                p.getCardsInHand().remove(p.getCardsInHand().get(j));
                drawCard(p);
                Port port = new Port(p, p.getCardsInHand().get(j));
                ports.add(port);
                tricks(ports);
            }
        }

        switch (j) {
            case 6:
                p.getCardsInHand().add(deck.trumpfChange(showCardsInHand(p)));
            case 7:
                if (deck.getStapel().size() > 0) {
                    deck.blockStapel();
                    playerAction(p);
                } else {
                    System.out.println("Stapel already blocked!");
                }
            case 8:
                endingGame(p);
        }
        playerAction(p);
    }

    private Card showCardsInHand(Player p) {

        for (int i = 1; i < p.getCardsInHand().size(); i++) {

            System.out.println("Action " + i + ": Throw " + p.getCardsInHand().get(i - 1).getName());

        }
        System.out.println("Action 6: Go back to options");

        int i = scanner.nextInt();

        if (i < 6) {
            System.out.println("You threw: " + p.getCardsInHand().get(i - 1));
            System.out.println();
            p.getCardsInHand().remove(p.getCardsInHand().get(i - 1));
            return p.getCardsInHand().get(i - 1);
        }

        if (i == 6) {
            playerAction(p);
        }
        showCardsInHand(p);
        return null;
    }

    private void drawCard(Player p) {
        if (deck.getStapel().size() == 0) {
            p.getCardsInHand().add(deck.getTrumpf());
            deck.setTrumpf(null);
            System.out.println(YELLOW_BOLD + "Stapel is empty!" + RESET);
        } else {
            p.getCardsInHand().add(deck.getStapel().get(0));
            deck.getStapel().remove(deck.getStapel().get(0));
        }
    }

}
