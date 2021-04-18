// created by Benjamin Lamprecht

import java.util.ArrayList;
import java.util.Collections;

class Controller {

    private static ArrayList<Player> players = new ArrayList<>();
    //collecting players

    //Input for tricks
    static ArrayList<Port> ports = new ArrayList<>();

    Controller() {

        System.out.println("What's your name?");

        //only temporary solution, wird mit switcher(NPC/Human player) und folgenden scanner ein player 2 implementiert
        Player p1 = new HumanPlayer();
        p1.setName("scanner.next()");
        Player p2 = new HumanPlayer();
        p2.setName("Dummy");

        //only temporary solution
        players.add(p1);
        players.add(p2);

        for (Player player:
             players) {
            System.out.println(player.getName());
        }


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

        System.out.println(Fonts.BLUE_BOLD + "Trumpf card is: " + Deck.getTrumpf().getName() + Fonts.RESET);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }

        firstTurn();
    }

    private void dealCards() {

        for (Player p : players) {
            ArrayList<Card> tempStapel = new ArrayList<>(Deck.deck.getStapel());

            ArrayList<Card> tempHand = new ArrayList<>();

            for (Card c : tempStapel) {

                if (tempHand.size() < 5) {
                    tempHand.add(c);
                    Deck.getStapel().remove(c);
                }
                p.setCardsInHand(tempHand);
            }
        }
    }

    private void firstTurn() {
        Collections.shuffle(players);
        System.out.println();
        players.get(0).playerAction();
    }

    static void turnSwitcher(Player playerFromClass, Card card) {
        System.out.println("started turnswitcher");
        System.out.println(Fonts.BLUE_BOLD + "Turn ended" + Fonts.RESET);

        ports.add(new Port(playerFromClass, card));
        for (Port p:
                ports) {
            System.out.println(p.getCard().getName());
        }
        if (players.size() == ports.size()) {
            tricks();
        }

        for (Player p : players) {
            for (int i = 0; i < ports.size(); i++) {
                if (p.equals(ports.get(i).getPlayer())) {
                    if (i < players.size()) {
                        players.get(i + 1).playerAction();
                    } else {
                        players.get(0).playerAction();
                    }
                }
            }
        }
    }


    private static void tricks() {
        System.out.println("started tricks");

        Port winCard = ports.get(0);

        for (Port port : ports) {
            winCard = checkWinCard(winCard, port);

        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        findPlayersWinCard(winCard);
        winCard.getPlayer().playerAction();
    }

    public static void endingGame() {
        Player winPlayer = null;
        for (Player temp : players) {
            winPlayer = searchWinner(temp, winPlayer);
        }
        announceWinner(winPlayer);
        System.exit(0);
    }

    private static Player findPlayersWinCard(Port winCard) {
        System.out.println("started findPlayerWinCard");
        for (Port p : ports) {
            if (p.getPlayer().equals(winCard.getPlayer())) {
                System.out.println(Fonts.BLUE_BOLD + "Player " + p.getPlayer().getName() + " won cards: " + Fonts.RESET);

                for (int j = 0; j < ports.size(); j++) {
                    p.getPlayer().getPreviousTricks().add(ports.get(j).getCard());
                    p.getPlayer().setScore(p.getPlayer().getScore() + ports.get(j).getCard().getValue());
                    System.out.print(Fonts.BLUE_BOLD + ports.get(j).getCard().getName() + Fonts.RESET);
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

    private static Port checkWinCard(Port winCard, Port port) {

        boolean checkIfCardIsHigher = port.getCard().getValue() > winCard.getCard().getValue();
        boolean checkIfCardmatchescolour = port.getCard().getColor().equals(winCard.getCard().getColor());
        boolean checkIfCardIsTrumpf = port.getCard().getColor().equals(Deck.getTrumpfColor());
        boolean checkIfWincardIsTrumpf = winCard.getCard().getColor().equals(Deck.getTrumpfColor());

        if ((checkIfCardIsHigher && checkIfCardmatchescolour) || (checkIfCardIsTrumpf && !checkIfWincardIsTrumpf)) {
            winCard = port;
        }
        return winCard;
    }

    private static Player searchWinner(Player p, Player winPlayer) {

        if (p.getScore() > winPlayer.getScore()) {
            return p;
        }
        return winPlayer;
    }

    private static void announceWinner(Player winPlayer) {
        if (winPlayer.getScore() < 33) {
            System.out.println(Fonts.BLUE_BOLD + "Player " + winPlayer.getName() + " called too early and lost the game");
        } else {
            System.out.println("\nPlayer " + winPlayer.getName() + " wins!");
            System.out.println("Points: " + winPlayer.getScore());
        }
    }

    public static Boolean checkColour(Card card) {
        boolean checkIfCardmatchescolour = card.getColor().equals(ports.get(0).getCard().getColor());
        boolean checkIfCardIsTrumpf = card.getColor().equals(Deck.getTrumpfColor());

        return checkIfCardIsTrumpf || checkIfCardmatchescolour;
    }



}