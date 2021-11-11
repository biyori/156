import java.util.UUID;

public class ClientActions implements Actions {
    @Override
    public String BidItem(int id, int cost) {
        return "BID\t" + id + "\t" + cost;
    }

    @Override
    public String SellItem(String item, int price, int quantity) {
        return "SELL\t" + item + "\t" + price + "\t" + quantity;
    }

    @Override
    public String GetAuctionItems() {
        return "GET_AUCTION_ITEMS ";
    }

    @Override
    public String GetAuctionItem() {
        return "CURRENT_AUCTION ";
    }

    @Override
    public String RegisterForAuction(UUID uuid) {
        return "REGISTER: " + uuid;
    }
}
