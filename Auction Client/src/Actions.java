public interface Actions {
    String BidItem(int id, int cost);

    String SellItem(String item, int price, int quantity);

    String TryBuyItem(int id, int quantity);

    boolean PurchasedItem(String text);
}
