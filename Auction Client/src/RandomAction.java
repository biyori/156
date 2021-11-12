import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomAction extends ClientActions {
    private final List<String> itemList;
    private AuctionModel model;

    public RandomAction(List<String> itemList) {
        this.itemList = itemList;
    }

    public String GenerateRandomAction() {
        int id = randInt(0, 100);
        String command = "BONK";
        if (id < 10) { // lower 10%
            return SellItem(itemList.get(randInt(0, itemList.size() - 1)), randInt(1, 5), randInt(100, 1000));
        } else if (id < 50) { // lower 50%
            //System.out.println("IDLING DOING NOTHING");
            command = "NOTHING";
        } else if (id > 70) { // upper 30%
            //command = "BIDDING";
            //System.out.println("BIDDING ON ITEM");
            if (model != null)
                return BidItem(model.id, model.cost + randInt(10, 50)); // Increase amount in range [10,50]
        }
        return command;
    }

    public void MaybeUseItem(AuctionModel item) {
        this.model = item;
    }

    private int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
}
