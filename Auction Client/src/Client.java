import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client {
    public static void main(String[] args) {

        String hostname = "localhost";
        int port = 43595;

        // Create item list of possible items to sell
        AuctionItems items = new AuctionItems();
        List<String> itemList = items.LoadItemsFromFile("items.txt");

        for (int i = 0; i < 5; i++) {
            System.out.println("Creating client " + i);
            try {
                Socket socket = new Socket(hostname, port);
                new ClientThread(socket, itemList).start();
            } catch (IOException e) {
                System.out.println("Client initialization error: " + e.getMessage());
            }
        }
    }
}
