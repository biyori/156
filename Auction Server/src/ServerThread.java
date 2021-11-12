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
                writer.println("REGISTER_AUCTION" + "\n_END_");
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
                    } else if (trimmedText.contains("CURRENT_AUCTION")) {
                        //System.out.println("Sending list to " + client_uuid);
                        writer.println("CURRENT_ITEM: " + trimmedText + "\n" + sql.AuctionItem(false) + "\n");
                    } else if (trimmedText.contains("SELL\t")) {
                        //SELL	Prescriptions	4	572
                        String[] split = trimmedText.split("\t");
                        if (!sql.ItemExists(split[1])) { // Item name
                            synchronized (this) {
                                System.out.println("Adding item: " + split[1] + " for $" + split[3]);
                                sql.InsertDB(split[1], Integer.parseInt(split[3]), Integer.parseInt(split[2]));
                                writer.println("ITEM_CONFIRMED: " + split[1] + "\n");
                            }
                        } else {
                            System.out.println("Adding item: \"" + split[1] + "\" failed -- item already exists");
                            writer.println("ITEM_FAILED: " + split[1] + "\n");
                        }
                    } else if (trimmedText.contains("BID\t")) {
                        //BID	11	427 (id, cost)
                        String[] split = trimmedText.split("\t");
                        System.out.println("Client attempting bid on item id: " + split[1]);
                        if (!sql.BiddingUserExists(client_uuid, Integer.parseInt(split[1]))) {
                            synchronized (this) {
                                sql.AddUserToAuction(UUID.fromString(client_uuid), Integer.parseInt(split[1]), Integer.parseInt(split[2]), 0);
                            }
                        }
                        if (sql.ItemExists(Integer.parseInt(split[1]))) { // Item id
                            if (sql.UpdateItemPrice(Integer.parseInt(split[1]), Integer.parseInt(split[2]))) {
                                synchronized (this) {
                                    writer.println("BID_CONFIRMED: " + split[1] + "\n");
                                    sql.UpdateUserBid(UUID.fromString(client_uuid), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                                }
                            } else
                                writer.println("BID_FAILED: " + split[1] + " BID TOO LOW");
                        } else {
                            System.out.println("BID FAILED - " + client_uuid);
                            writer.println("BID_FAILED: " + split[1] + "\n");
                        }
                    } else if (trimmedText.contains("REGISTER: ")) {
                        //BID	11	427 (id, cost)
                        String[] split = trimmedText.split(" ");
                        System.out.println("REGISTER UUID: " + split[1]);
                        writer.println("UID_CONFIRMED: " + split[1] + "\n");
                    } else if (trimmedText.contains("BONK")) {
                        writer.println("BONK " + client_uuid);
                    } else if (trimmedText.contains("NOTHING")) {
                        writer.println("SAME " + client_uuid);
                    } else {
                        System.out.println("\tINVALID COMMAND -> " + trimmedText);
                    }

                    // Update auction timer
                    // Default we insert new items with an expiring timer of 0
                    Item currentItem = sql.CurrentAuctionItem(false);

                    //Check if the auction is empty
                    if (sql.CurrentAuctionSize() == 0) {
                        System.out.println("No items left to auction");
                        break;
                    } else if (currentItem.item_expires == 0) { // If the item is 0 set the timer to 60 seconds into the future
                        System.out.println("Starting auction on item [" + currentItem.Id + "] " + currentItem.Name + ", the current bid is set at $" + currentItem.price);
                        //sql.UpdateAuctionTimer(currentItem.Id);
                        synchronized (this) {
                            sql.UpdateAuctionTimer(currentItem.Id, 60);
                        }
                    } else { // Check if item expires
                        long expiry = currentItem.item_expires;
                        long currentTime = System.currentTimeMillis();
                        long duration = expiry - currentTime;
                        if (duration % 10 == 0)
                            System.out.println("Auction Timer: " + (expiry - currentTime));
                        if (duration <= 0) {
                            System.out.println("Congratulations " + client_uuid + " you bought " + currentItem.Name + " for $" + currentItem.price);
                            writer.println(sql.FindWinner(currentItem.Id) + "\t" + currentItem.Name + "\t" + currentItem.price); // Broadcast winner
                            synchronized (this) {
                                sql.CloseItem(currentItem.Id);
                            }
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