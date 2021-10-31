public class ClientActions implements Actions {
    @Override
    public String BidItem(int id, int cost) {
        return "BID: " + id + "," + cost;
    }

    @Override
    public String SellItem(String item, int price, int quantity) {
        return "SELL: " + item + "," + price + "," + quantity;
    }

    @Override
    public String TryBuyItem(int id, int quantity) {
        return "OFFER " + id + "," + quantity;
    }

    @Override
    public boolean PurchasedItem(String text) {
        return text.contains("_SOLD_");
    }
}
