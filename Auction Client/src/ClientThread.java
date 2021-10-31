import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread extends Thread {
    private final Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        ServerParser sp = new ServerParser();
        System.out.println("Current Thread Name: " + Thread.currentThread().getName());
        // gets the ID of the current thread
        System.out.println("Current Thread ID: " + Thread.currentThread().getId());
        try {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String text;
            int money = 1000;

            do {
                // Reset the auction listings
                sp.UpdateAuctionList();
                text = "BUY POWER " + money;

                // Send the message
                writer.println(text);

                // Read the response
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String currLine = "";
                while (!currLine.contains("_END_")) {
                   // if (currLine.length() > 0)
                   //     System.out.println("Data: " + currLine);
                    currLine = reader.readLine();
                    sp.Read(currLine);
                }

    System.out.println("Auction Size: " + sp.AuctionSize());

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