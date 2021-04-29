// created by Benjamin Lamprecht

import java.util.ArrayList;

public class main {

    public static void main(String[] args) {

        Controller controller = new Controller();

        while (Controller.isGameRuns()) {
            Player player = Controller.getPlayers().get(0);
            while (Controller.getPlayers().size() > Controller.getPorts().size()) {
                assert player != null;
                Controller.getPorts().add(player.playerAction());
                player = Controller.turnSwitcher(player);
                Controller.printCardsOnTable();
            }
            player = Controller.tricks();
            Controller.addScore(player);
            Controller.getPorts().clear();
        }
    }
}
