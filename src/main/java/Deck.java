// created by Benjamin Lamprecht

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

class Deck {

    private final String DB_DECK = "DoppelDeutscheKarten.db";
    private final String CONNECTION_STRING = "jdbc:sqlite::resource:" + DB_DECK;

    private Card trump;
    private String trumpColor;
    private final ArrayList<Card> deck;

    public Deck() {
        deck = collectStapel(new ArrayList<Card>());
    }

    protected void dealDeck (){
        Collections.shuffle(deck);
        trump = trumpf(deck);
        trumpColor = trump.getColor();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void blockStapel() {
        ArrayList<Card> tempStapel = new ArrayList<>(deck);
        for (Card c : tempStapel) {
            deck.remove(c);
        }
    }


    public Card trumpChange(Card card) {
        if (this.getTrump().getColor().equals(card.getColor())) {
            Card temp = this.getTrump();
            System.out.println("Trumpf: " + this.getTrump().getName() + " exchanged to " + card.getName());
            trump = card;
            return temp;
        } else {
            System.out.println("Cannot change Trumpf");
            return card;
        }
    }


    private Card trumpf(ArrayList<Card> stapel) {
        Card card = stapel.get(0);
        stapel.remove(card);
        return card;
    }

    public ArrayList<Card> collectStapel(ArrayList<Card> stapel) {

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(CONNECTION_STRING);

            try (Statement statement = conn.createStatement()) {

                ResultSet results = statement.executeQuery("SELECT * FROM Deck ORDER BY ROWID ASC");
                while (results.next()) {
                    Card card = new Card(results.getString("NAME"), results.getString("COLOR"), 0);
                    String str = results.getString("VALUE");
                    switch (str){
                        case "ass":
                            card.setValue(11);
                            break;
                        case  "zehn":
                            card.setValue(10);
                            break;
                        case  "könig":
                            card.setValue(4);
                            break;
                        case  "dame":
                            card.setValue(3);
                            break;
                        case  "bube":
                            card.setValue(2);
                            break;
                        default:
                            card.setValue(0);
                            break;
                    }
                    if (card.getValue() > 0) {
                        stapel.add(card);
                    }
                }

                statement.close();
                conn.close();

            } catch (SQLException e) {
                System.out.println("List not generated: " + e.getMessage());
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Couldn't connect to db: " + e.getMessage());
        }

        return stapel;
    }

    public Card getTrump() {
        return trump;
    }

    public String getTrumpColor() {
        return trumpColor;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setTrump(Card trumpf) {
        this.trump = trumpf;
    }
}
