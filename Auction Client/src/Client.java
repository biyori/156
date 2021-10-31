import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.*;

public class Client {
    public static void main(String[] args) {

        String hostname = "localhost";
        int port = 43595;

        for (int i = 0; i < 5; i++) {
            System.out.println(("Creating client " + i));
            try (Socket socket = new Socket(hostname, port)) {

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                Console console = System.console();
                String text;

                // do {
                text = "FYCK YEAH SPAM";//console.readLine("Enter text: ");

                writer.println(text);

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String currLine = "";
                while (!currLine.contains("_END_")) {
                    if (currLine.length() > 0)
                        System.out.println("Data: " + currLine);
                    currLine = reader.readLine();
                }

                //} while (!text.equals("bye"));

            } catch (UnknownHostException ex) {

                System.out.println("Server not found: " + ex.getMessage());

            } catch (IOException ex) {

                System.out.println("I/O error: " + ex.getMessage());
            }
        }
    }
}
