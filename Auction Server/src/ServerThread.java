import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ServerThread extends Thread {
    private final Socket socket;
    private final SQL sql = new SQL();

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        System.out.println("Current Thread Name: " + Thread.currentThread().getName());
        // gets the ID of the current thread
        System.out.println("Current Thread ID: " + Thread.currentThread().getId());

        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);


            String text, trimmedText, client_uuid;

            // Register with client before proceeding
            do {
                text = reader.readLine();
                if (text.contains("REGISTER: ")) {
                    int offset = text.indexOf(' ') + 1;
                    client_uuid = text.substring(offset);
                } else
                    client_uuid = "";
                writer.println("REGISTER_AUCTION" + "\n" + "\n_END_");
            } while (client_uuid.isEmpty());

            System.out.println("Success your UUID has been registered: " + client_uuid);
            do {
                // Broadcast winners if any
                text = reader.readLine();
                if (text != null) {
                    trimmedText = text.trim();

                    if (trimmedText.contains("GET_AUCTION_ITEMS")) {
                        //System.out.println("Sending list to " + client_uuid);
                        writer.println("AUCTION_ITEMS: " + trimmedText + "\n" + sql.PrintAuctionItems(false) + "\n");
                    } else if(trimmedText.contains("CURRENT_AUCTION")) {
                        System.out.println("Sending list to " + client_uuid);
                        writer.println("CURRENT_ITEM: " + trimmedText + "\n" + sql.AuctionItem(true) + "\n");
                    } else if (trimmedText.contains("SELL\t")) {
                        //SELL	Prescriptions	4	572
                        String[] split = trimmedText.split("\t");
                        System.out.println("ADDING item: " + split[1]);
                        if (!sql.ItemExists(split[1])) { // Item name
                            sql.InsertDB(split[1], Integer.parseInt(split[3]), Integer.parseInt(split[2]));
                            writer.println("ITEM_CONFIRMED: " + split[1] + "\n");
                        } else {
                            writer.println("ITEM_FAILED: " + split[1] + "\n");
                        }
                    } else if (trimmedText.contains("BID\t")) {
                        //BID	11	427 (id, cost)
                        String[] split = trimmedText.split("\t");
                        System.out.println("TRYING TO BID ON item id: " + split[1]);
                        if(!sql.BiddingUserExists(client_uuid, Integer.parseInt(split[1])))
                            sql.AddUserToAuction(UUID.fromString(client_uuid),Integer.parseInt(split[1]),Integer.parseInt(split[2]),0);
                        if (sql.ItemExists(Integer.parseInt(split[1]))) { // Item id (int cast differentiation)
                            if (sql.UpdateItemPrice(Integer.parseInt(split[1]), Integer.parseInt(split[2]))) {
                                writer.println("BID_CONFIRMED: " + split[1] + "\n");
                                sql.UpdateUserBid(UUID.fromString(client_uuid),Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                            }else
                                writer.println("BID_FAILED: " + split[1] + " BID TOO LOW");
                        } else {
                            System.out.println("BID FAILED!!!!###");
                            writer.println("BID_FAILED: " + split[1] + "\n");
                        }
                    } else if (trimmedText.contains("REGISTER: ")) {
                        //BID	11	427 (id, cost)
                        String[] split = trimmedText.split(" ");
                        System.out.println("REGISTER UUID: " + split[1]);
                        writer.println("UID_CONFIRMED: " + split[1] + "\n");
                    } else {
                        System.out.println("NOT A VALID COMMAND -> " + trimmedText);
                    }

                    // Update auction timer
                    // Default we insert new items with an expiring timer of 0
                    Item currentItem = sql.CurrentAuctionItem(false);
                    if(currentItem.item_expires == 0) { // If the item is 0 set the timer to 60 seconds into the future
                        System.out.println("Updating ITEM " + currentItem.Id);
                        //sql.UpdateAuctionTimer(currentItem.Id);
                        sql.UpdateAuctionTimer(currentItem.Id,10);
                    } else { // Check if item expires
                        long expiry = currentItem.item_expires;
                        long currentTime = System.currentTimeMillis();
                        long duration = expiry - currentTime;
                        System.out.println("Auction Timer: "+ (expiry - currentTime));
                        if(duration <= 0) {
                            System.out.println("Congratulations " + client_uuid + " YOU WON " + currentItem.Name);
                            writer.println(sql.FindWinner(currentItem.Id)); // Broadcast winner
                            sql.CloseItem(currentItem.Id);
                        }
                    }
                } else
                    trimmedText = "";
                writer.println("Server: " + trimmedText + "\n" + "\n_END_");

            } while (text != null && !text.equals("_CLOSE_"));
            System.out.println("Client disconnected");
            socket.close();
            this.join();
        } catch (IOException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}