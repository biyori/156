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
                    sql.InsertDB(reverseText, 10, 10);
                } else
                    reverseText = "";
                writer.println("Server: " + reverseText + "\n" + sql.PrintAuctionItems() + "\n_END_");

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