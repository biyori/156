import java.util.UUID;

public interface Actions {
    String BidItem(int id, int cost);

    String SellItem(String item, int price, int quantity);

    String GetAuctionItems(); // Get all the items to be auctioned

    String GetAuctionItem(); // Get the current auction item

    String RegisterForAuction(UUID uuid);
}
