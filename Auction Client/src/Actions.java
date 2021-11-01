public interface Actions {
    String BidItem(int id, int cost);

    String SellItem(String item, int price, int quantity);

    String GetAuctionItems();

    boolean PurchasedItem(String text);
}
