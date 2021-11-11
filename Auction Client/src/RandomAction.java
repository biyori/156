import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomAction extends ClientActions {
    private final List<String> itemList;
    private int id;
    private AuctionModel model;

    public RandomAction(List<String> itemList) {
        this.itemList = itemList;
    }

    public String GenerateRandomAction() {
        id = randInt(0, 1000);
        String command = "BONK";
        if (id <= 100) {
            return SellItem(itemList.get(id), randInt(1, 5), randInt(100, 1000));
        } else if (id <= 500) {
            //System.out.println("IDLING DOING NOTHING");
            command = "NOTHING";
        } else if (id > 900) {
            command = "BIDDING";
            System.out.println("BIDDING ON ITEM");
            if(model !=null)
                return BidItem(model.id, model.cost + 10); // Increase amount by 10
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
