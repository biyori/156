import java.io.*;
import java.net.Socket;

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


            String text, reverseText;

            do {
                text = reader.readLine();
                if (text != null) {
                    reverseText = text.trim();

                    if (reverseText.contains("GET_AUCTION_ITEMS")) {
                        //System.out.println("Sending item list from thread " + Thread.currentThread().getId());
                        writer.println("AUCTION_ITEMS: " + reverseText + "\n" + sql.PrintAuctionItems(false) + "\n");
                    } else if (reverseText.contains("SELL\t")) {
                        //SELL	Prescriptions	4	572
                        String[] split = reverseText.split("\t");
                        System.out.println("ADDING item: " + split[1]);
                        if (!sql.ItemExists(split[1])) { // Item name
                            sql.InsertDB(split[1], Integer.parseInt(split[3]), Integer.parseInt(split[2]));
                            writer.println("ITEM_CONFIRMED: " + split[1] + "\n");
                        } else {
                            writer.println("ITEM_FAILED: " + split[1] + "\n");
                        }
                    } else if (reverseText.contains("BID\t")) {
                        //BID	11	427 (id, cost)
                        String[] split = reverseText.split("\t");
                        System.out.println("TRYING TO BID ON item id: " + split[1]);
                        if (sql.ItemExists(Integer.parseInt(split[1]))) { // Item id (int cast differentiation)
                            if (sql.UpdateItemPrice(Integer.parseInt(split[1]), Integer.parseInt(split[2])))
                                writer.println("BID_CONFIRMED: " + split[1] + "\n");
                            else
                                writer.println("BID_FAILED: " + split[1] + " BID TOO LOW");
                        } else {
                            System.out.println("BID FAILED!!!!###");
                            writer.println("BID_FAILED: " + split[1] + "\n");
                        }
                    } else if (reverseText.contains("REGISTER\t")) {
                        //BID	11	427 (id, cost)
                        String[] split = reverseText.split("\t");
                        System.out.println("REGISTER UUID: " + split[1]);
                        writer.println("UID_CONFIRMED: " + split[1] + "\n");

                    }else {
                        System.out.println("WTF DID U SEND?? " + reverseText);
                    }

                    //sql.InsertDB(reverseText, 10, 10);
                } else
                    reverseText = "";
                writer.println("Server: " + reverseText + "\n" + "\n_END_");

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