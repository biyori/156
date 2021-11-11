import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

enum ServerState {
    REGISTER_AUCTION, AUCTION_ITEMS, BID_CONFIRMED, BID_FAILED, ITEM_CONFIRMED, ITEM_FAILED,ITEM_WON, IDLE
}

public class ServerParser {
    private final UUID uid;
    private boolean confirmRegistration = false;
    ArrayList<AuctionModel> items;
    ServerState currentState = ServerState.IDLE;

    ServerParser(UUID uid) {
        this.uid = uid;
        items = new ArrayList<>();
    }

    public void Read(String text) {
        // Check if we have valid server messages
        ChangeState(text);
        if (currentState != ServerState.IDLE) {
            System.out.println("Current State " + currentState.toString() + " UID: " + uid);
        }

        switch (currentState) {
            case AUCTION_ITEMS -> {
                String[] parse;
                parse = text.split("\t");//Tab & Space
                ParseAuctionItems(parse);
            }
            case BID_CONFIRMED -> System.out.println("BID CONFIRMED");
            case BID_FAILED -> System.out.println("BID FAILED");
            case ITEM_CONFIRMED -> System.out.println("SELLING ITEM CONFIRMED");
            case ITEM_FAILED -> System.out.println("ADDING ITEM FAILED");
            case ITEM_WON -> System.out.println("WE BOUGHT AN ITEM!");
            case REGISTER_AUCTION -> System.out.println("REGISTERING FOR AUCTION");
            case IDLE -> System.out.println("IDLING");
        }
    }

    public boolean hasRegistered() {
        return confirmRegistration;
    }

    public ServerState State() {
        return currentState;
    }

    private void ChangeState(String text) {
        if (text.contains("AUCTION_ITEMS"))
            currentState = ServerState.AUCTION_ITEMS;
        else if(text.contains("CURRENT_ITEM"))
            currentState = ServerState.AUCTION_ITEMS;
        else if (text.contains("BID_CONFIRMED"))
            currentState = ServerState.BID_CONFIRMED;
        else if (text.contains("BID_FAILED"))
            currentState = ServerState.BID_FAILED;
        else if (text.contains("ITEM_CONFIRMED"))
            currentState = ServerState.ITEM_CONFIRMED;
        else if (text.contains("ITEM_FAILED"))
            currentState = ServerState.ITEM_FAILED;
        else if (text.contains("REGISTER_AUCTION"))
            currentState = ServerState.REGISTER_AUCTION;
        else if (text.contains("UID_CONFIRMED")) {
            currentState = ServerState.IDLE;
            confirmRegistration = true;
        }else if (text.contains("ITEM_WON")) {
            currentState = ServerState.ITEM_WON;
        } else if (text.contains("_END_"))
            currentState = ServerState.IDLE;
    }

    private void ParseAuctionItems(String[] text) {
        // Validate ServerAuction messages before calling this function!
        // If the text has length 4 we can validate that the auction info is correct
        if (text.length == 4) {
            int id = Integer.parseInt(text[0].replace("ID: ", "").trim());
            String name = text[1].replace("Name: ", "").trim();
            int units = Integer.parseInt(text[2].replace("Units: ", "").trim());
            int price = Integer.parseInt(text[3].replace("Price: ", "").trim());

            // Add the auction items to the model
            System.out.print("PARSED: " + id + " " + name + " " + units + " " + price + "\n");
            AuctionModel ins = new AuctionModel();
            ins.id = id;
            ins.name = name;
            ins.units = units;
            ins.cost = price;
            items.add(ins);
        }
    }

    public int AuctionSize() {
        return items.size();
    }

    public void ResetAuctionList() {
        if (!items.isEmpty())
            items = new ArrayList<>();
    }

    public AuctionModel GetRandomItem() {
        if (!items.isEmpty()) {
            // Only 1 item return the top
            if(items.size() == 1)
                return items.get(0);
            // Return a random item
            int rand_item = ThreadLocalRandom.current().nextInt(0, items.size() - 1);
            return items.get(rand_item);
        }
        return null;
    }
}
