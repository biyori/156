public interface Commands {
    void ClientBid(int id, int cost);

    void ClientPurchase(int id, int quantity);

    void ClientSell(String item, int price);

    boolean UserEnteredCommand(String text);

    void HandleUserCommand(String command);
}
