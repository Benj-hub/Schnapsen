// created by Benjamin Lamprecht

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

class Controller {

    //Input for tricks
    static ArrayList<Port> ports = new ArrayList<>();
    //collecting players
    private static ArrayList<Player> players = new ArrayList<>();
    private static boolean gameRuns = true;

    public static boolean isGameRuns() {
        return gameRuns;
    }

    public static ArrayList<Port> getPorts() {
        return ports;
    }

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    Controller() {
        System.out.println("What's your name?");
        //creation of Player1
        Player p1 = new HumanPlayer();
        players.add(p1);
        createOtherPlayer();
        dealCards();

        for (Player player :
                players) {
            System.out.println(player.getName());
            for (Card c : player.getCardsInHand()) {
                System.out.println(c.getName());
            }
            System.out.println("=================================");
        }
        for (Card c : Deck.getStapel()) {
            System.out.println(c.getName());
        }
        System.out.println(Fonts.BLUE_BOLD + "Trumpf card is: " + Deck.getTrumpf().getName() + Fonts.RESET);
    }

    private static void gameRuns(){
        gameRuns = false;
    }

    static void addScore(Player player){
        int newScore = 0;
        for (Port p : ports) {
            newScore += p.getCard().getValue();
        }
        player.setScore(newScore);
    }

    static void printCardsOnTable() {
        System.out.println(Fonts.YELLOW_BOLD + "On the Table: " + Fonts.RESET);
        for (Port p : ports) {
            System.out.println(Fonts.YELLOW_BOLD + p.getCard().getName() + Fonts.RESET);
        }
    }
    static Player turnSwitcher(Player player) {
        System.out.println("started turnswitcher");
        System.out.println(Fonts.BLUE_BOLD + "Turn ended" + Fonts.RESET);
        int i = 1;
        if (ports.size() > 0) {
            for (Player p : players) {
                    if (p.equals(ports.get(ports.size()-1).getPlayer())) {
                        if (i < players.size()) {
                            return players.get(i);
                        } else {
                            return players.get(0);
                        }
                    }
                    i++;
            }
        }
        return player;
    }
    static Player tricks() {
        System.out.println("started tricks");

        Port winCard = checkWinCard();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        Player temp = findPlayersWinCard(winCard);
        ports.clear();
        return temp;
    }

    public static void endingGame(Player p) {
        gameRuns();
        Player winPlayer = p;
        for (Player temp : players) {
            winPlayer = searchWinner(temp, winPlayer);
        }
        announceWinner(winPlayer);
    }

    private static Player findPlayersWinCard(Port winCard) {
        System.out.println("started findPlayerWinCard");
        for (Port p : ports) {
            if (p.getPlayer().equals(winCard.getPlayer())) {
                System.out.println(Fonts.RED_BOLD + "Player " + p.getPlayer().getName() + " won cards: " + Fonts.RESET);

                    int newPlayerscore;
                for (int j = 0; j < ports.size(); j++) {
                    p.getPlayer().getPreviousTricks().add(ports.get(j).getCard());
                    newPlayerscore = p.getPlayer().getScore() + ports.get(j).getCard().getValue();
                    p.getPlayer().setScore(newPlayerscore);
                    System.out.print(Fonts.RED_BOLD + ports.get(j).getCard().getName() + Fonts.RESET);
                    if (j < ports.size() - 1) {
                        System.out.print(", ");

                    }
                }
                System.out.println();
            }
            return p.getPlayer();
        }
        return null;
    }

    private static Port findBetterCard(Port master, Port slave) {
        boolean checkIfSlaveIsHigher = slave.getCard().getValue() > master.getCard().getValue();
        boolean checkIfSlaveMatchescolour = slave.getCard().getColor().equals(master.getCard().getColor());
        boolean checkIfSlaveIsTrumpf = slave.getCard().getColor().equals(Deck.getTrumpfColor());
        boolean checkIfMasterIsTrumpf = master.getCard().getColor().equals(Deck.getTrumpfColor());

        if ((checkIfSlaveIsHigher && checkIfSlaveMatchescolour) || (checkIfSlaveIsTrumpf && !checkIfMasterIsTrumpf)) {
            master = slave;
        }
        return master;
    }

    static Port checkWinCard() {
        Port winCard = ports.get(0);
        Port temp;
        for (Port port : ports) {
            temp = findBetterCard(winCard, port);
            winCard = temp;
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
            System.out.println(Fonts.BLUE_BOLD + "Player " + winPlayer.getName() + " called too early and lost the game" +
                    '\n' + "Your Score: " + winPlayer.getScore());
        } else {
            System.out.println(Fonts.PURPLE_BOLD + "\nPlayer " + winPlayer.getName() + " wins!");
            System.out.println("Points: " + winPlayer.getScore() + Fonts.RESET);
        }
        System.out.println("Others score(s):");
        for (Player p : players) {
            if (!p.getName().equals(winPlayer.getName())){
                System.out.println(Fonts.BLACK_BOLD + p.getName() + ", score: " + p.getScore() + Fonts.RESET);
            }
        }
    }

    static Boolean checkColour(Card card) {
        boolean checkIfCardmatchescolour = card.getColor().equals(ports.get(0).getCard().getColor());
        boolean checkIfCardIsTrumpf = card.getColor().equals(Deck.getTrumpfColor());

        return checkIfCardIsTrumpf || checkIfCardmatchescolour;
    }

    private static void createOtherPlayer() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("how many players do you want to play against?");

        int anzahlSpieler = 1; //scanner.nextInt();
        for (int i = 0; i < anzahlSpieler; i++) {
            System.out.println("Is Player" + (i + 2) + " another Human or a Machine?");
            System.out.println("write: human");
            System.out.println("write: machine");

            String otherPlayer = "machine";//scanner.next();

            if (otherPlayer.equals("human")) {

                players.add(new HumanPlayer());

            } else {
                players.add(new NPC(i + 1));
            }
        }
        Collections.shuffle(players);
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
}