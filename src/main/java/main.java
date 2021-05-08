// created by Benjamin Lamprecht

public class main {

    public static void main(String[] args) {
    Controller controller = new Controller();
    Player player = controller.getPlayers().get(0);

        while (controller.isGameRuns() && !controller.isNoCardsInHand()) {

            while (controller.getPlayers().size() > controller.getPorts().size()) {
                assert player != null;
                controller.getPorts().add(player.playerAction());
                controller.printCardsOnTable();
                player = controller.turnSwitcher(player);
            }
                player = controller.tricks();
                controller.addScore(player);
                //controller.printPlayerscore();
                controller.getPorts().clear();
        }
    }
    }