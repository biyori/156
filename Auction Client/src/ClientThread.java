import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ClientThread extends Thread {
    private final Socket socket;
    private final UUID uid;
    private final List<String> itemList;

    public ClientThread(Socket socket, UUID uid, List<String> itemList) {
        this.socket = socket;
        this.uid = uid;
        this.itemList = itemList;
    }

    public void run() {
        ServerParser sp = new ServerParser(uid); // Register our generated UID for communication with the server
        ClientActions fixed_actions = new ClientActions();
        RandomAction actions = new RandomAction(itemList);// Pass itemList to the random actions
        //  System.out.println("Current Thread Name: " + Thread.currentThread().getName());
        // gets the ID of the current thread
        // System.out.println("Current Thread ID: " + Thread.currentThread().getId());
        //System.out.println("Current UUID: " + uid);

        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String command;
            int money = 100000;

            do {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                // Register for auction
                if (!sp.hasRegistered()) {
                    command = fixed_actions.RegisterForAuction(uid);
                } else {
                    // Update auction item
                    command = fixed_actions.GetAuctionItem();
                }
                // Send the message
                writer.println(command);

                // Read the response
                String currLine = "";
                while (!currLine.contains("_END_")) {
                    currLine = reader.readLine();
                        sp.Read(currLine);
                }

                // Pull a random command from the auction
                AuctionModel rand_item = sp.GetRandomItem();
                actions.MaybeUseItem(rand_item);
                command = actions.GenerateRandomAction();
                // Send the message
                writer.println(command);
                // Read the response
                currLine = "";
                while (!currLine.contains("_END_")) {
                    currLine = reader.readLine();
                    sp.Read(currLine);
                }
                sp.ResetAuctionList();

                // Sleep for a little
                money -= 1;
                Thread.sleep(ThreadLocalRandom.current().nextInt(500, 3500));
            } while (money > 0);

            System.out.print("Closing connection... ");
            // Close readers + socket
            output.close();
            writer.close();
            socket.close();
            System.out.println("Success!");
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        } catch (InterruptedException ex) {
            System.out.println("Thread sleep error " + ex.getMessage());
        } catch (NullPointerException np) {
            System.out.println("Auction sent no data - closing connection");
        } finally {
            System.out.println("Client quit");
//            try { // We don't have to join the thread because I guess the program exits anyway
//                this.join();
//            } catch (InterruptedException e) {
//                System.out.println("Thread Join failed: " + e.getMessage());
//                e.printStackTrace();
//            }
        }
    }
}