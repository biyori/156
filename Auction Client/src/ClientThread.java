import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ClientThread extends Thread {
    private final Socket socket;
    private final List<String> itemList;

    public ClientThread(Socket socket, List<String> itemList) {
        this.socket = socket;
        this.itemList = itemList;
    }

    public void run() {
        ServerParser sp = new ServerParser();
        ClientActions fixed_actions = new ClientActions();
        RandomAction actions = new RandomAction(itemList);// Pass itemList to the random actions
        System.out.println("Current Thread Name: " + Thread.currentThread().getName());
        // gets the ID of the current thread
        System.out.println("Current Thread ID: " + Thread.currentThread().getId());
        try {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String command;
            int money = 1000;

            int randItem = ThreadLocalRandom.current().nextInt(0, itemList.size() - 1);

            do {
                // Update auction items
                command = fixed_actions.GetAuctionItems();
                // Send the message
                writer.println(command);

                // Read the response
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String currLine = "";
                while (!currLine.contains("_END_")) {
                    currLine = reader.readLine();
                    sp.Read(currLine);
                }


                command = actions.GenerateRandomAction();
                // Send the message
                writer.println(command);
                // Read the response
                currLine = "";
                while (!currLine.contains("_END_")) {
                    System.out.println("EXECUTING ROUND 2");
                    currLine = reader.readLine();
                    sp.Read(currLine);
                }

                System.out.println("Auction Size: " + sp.AuctionSize());

                sp.ResetAuctionList();

                // Sleep for a little
                money /= 10;
                Thread.sleep(1000);
            } while (money != 1);

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