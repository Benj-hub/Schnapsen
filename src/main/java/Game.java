// created by Benjamin Lamprecht

public class Game {

    public static void main(String[] args) {
    Controller controller = new Controller();
    Player player = controller.getPlayers().get(0);

        while (controller.isGameRuns()) {

            while (controller.getPlayers().size() > controller.getPorts().size()) {
                assert player != null;
                controller.getPorts().add(player.playerAction());
                controller.printCardsOnTable();
                player = controller.turnSwitcher(player);
            }
                player = controller.tricks();
                controller.addScore(player);
                controller.getPorts().clear();
            if (controller.isNoCardsInHand()){
                controller.endingGame(player);
            }
        }
    }
    }
