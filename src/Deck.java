// created by Benjamin Lamprecht

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

class Deck {

    private static final String DB_DECK = "DoppelDeutscheKarten.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_DECK;

    private Card trumpf;
    private String trumpfColor;
    private ArrayList<Card> stapel;

    public Deck() {
        this.stapel = collectStapel(new ArrayList<Card>());
        Collections.shuffle(this.stapel);
        this.trumpf = trumpf(stapel);
        this.trumpfColor = this.trumpf.getColor();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void blockStapel() {
        ArrayList<Card> tempStapel = new ArrayList<>(stapel);
        for (Card c : tempStapel) {
            this.stapel.remove(c);
        }
    }

    public Card trumpfChange(Card card) {
        if (this.getTrumpf().getColor().equals(card.getColor())) {
            Card temp = this.getTrumpf();
            System.out.println("Trumpf: " + this.getTrumpf().getName() + " exchanged to " + card.getName());
            setTrumpf(card);
            return temp;
        } else {
            System.out.println("Cannot change Trumpf");
            return card;
        }
    }

    private Card trumpf(ArrayList<Card> stapel) {
        Card card = stapel.get(0);
        this.stapel.remove(card);
        return card;
    }

    private ArrayList<Card> collectStapel(ArrayList<Card> stapel) {

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(CONNECTION_STRING);

            try (Statement statement = conn.createStatement()) {

                ResultSet results = statement.executeQuery("SELECT * FROM Deck ORDER BY ROWID ASC");
                while (results.next()) {
                    Card card = new Card(results.getString("NAME"), results.getString("COLOR"), results.getInt("VALUE"));
                    stapel.add(card);
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

    public void setTrumpf(Card trumpf) {
        this.trumpf = trumpf;
    }

    public ArrayList<Card> getStapel() {
        return stapel;
    }
}
