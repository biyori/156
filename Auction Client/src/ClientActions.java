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
}
