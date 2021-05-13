// created by Benjamin Lamprecht

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

class Deck {

    private final String DB_DECK = "DoppelDeutscheKarten.db";
    private final String CONNECTION_STRING = "jdbc:sqlite::resource:" + DB_DECK;

    private Card trumpf;
    private String trumpfColor;
    private ArrayList<Card> stapel;

    public Deck() {
        stapel = collectStapel(new ArrayList<Card>());
    }

    protected void dealDeck (){
        Collections.shuffle(stapel);
        trumpf = trumpf(stapel);
        trumpfColor = trumpf.getColor();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void blockStapel() {
        ArrayList<Card> tempStapel = new ArrayList<>(stapel);
        for (Card c : tempStapel) {
            stapel.remove(c);
        }
    }

    /*public Card trumpfChange(Card card) {
        if (this.getTrumpf().getColor().equals(card.getColor())) {
            Card temp = this.getTrumpf();
            System.out.println("Trumpf: " + this.getTrumpf().getName() + " exchanged to " + card.getName());
            trumpf = card;
            return temp;
        } else {
            System.out.println("Cannot change Trumpf");
            return card;
        }
    }

     */

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

    public Card getTrumpf() {
        return trumpf;
    }

    public String getTrumpfColor() {
        return trumpfColor;
    }

    public ArrayList<Card> getStapel() {
        return stapel;
    }

    public void setTrumpf(Card trumpf) {
        this.trumpf = trumpf;
    }
}
