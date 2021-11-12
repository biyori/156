import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

enum ServerState {
    REGISTER_AUCTION, AUCTION_ITEMS, BID_CONFIRMED, BID_FAILED, ITEM_CONFIRMED, ITEM_FAILED, ITEM_WON, IDLE
}

public class ServerParser {
    private final UUID uid;
    ArrayList<AuctionModel> items;
    ServerState currentState = ServerState.IDLE;
    ServerState previousState;
    private boolean confirmRegistration = false;
    private int purchasedItems, purchaseValue;
    private ArrayList<String> purchaseList;

    ServerParser(UUID uid) {
        this.uid = uid;
        items = new ArrayList<>();
        purchaseList = new ArrayList<>();
    }

    public void Read(String text) {
        // Check if we have valid server messages
        ChangeState(text);

        if (currentState != ServerState.IDLE && currentState != ServerState.AUCTION_ITEMS) {
            if (shouldPrint())
                printStatus();
        }

        switch (currentState) {
            case AUCTION_ITEMS -> {
                String[] parse;
                parse = text.split("\t");//Tab & Space
                ParseAuctionItems(parse);
            }
            case BID_CONFIRMED -> {
                if (shouldPrint()) {
                    System.out.println("\tBID CONFIRMED");
                }
            }
            case BID_FAILED -> {
                if (shouldPrint()) {
                    System.out.println("\tBID FAILED");
                }
            }
            case ITEM_CONFIRMED -> {
                if (shouldPrint()) {
                    System.out.println("\tSELLING ITEM CONFIRMED");
                }
            }
            case ITEM_FAILED -> {
                if (shouldPrint()) {
                    System.out.println("\tADDING ITEM FAILED");
                }
            }
            case ITEM_WON -> {
                String[] parse;
                parse = text.split("\t");
                if (parse.length == 3) {
                    System.out.println("\tWE BOUGHT AN ITEM! " + parse[1]);
                    purchaseList.add(parse[1]); // Add item to our list
                    purchaseValue += Integer.parseInt(parse[2]); // Update the total spent amount
                    purchasedItems++; // Increment total
                }
            }
            case REGISTER_AUCTION -> System.out.println("\tREGISTERING FOR AUCTION");
            case IDLE -> {
            } // Do nothing
        }
        previousState = State();
    }

    public boolean shouldPrint() {
        return currentState != previousState;
    }

    public boolean hasRegistered() {
        return confirmRegistration;
    }

    public ServerState State() {
        return currentState;
    }

    public int getPurchasedItems() {
        return purchasedItems;
    }

    public int getPurchaseValue() {
        return purchaseValue;
    }

    private void ChangeState(String text) {
        if (text.contains("AUCTION_ITEMS")) // Update the auction item
            currentState = ServerState.AUCTION_ITEMS;
        else if (text.contains("CURRENT_ITEM")) // Get the current info for the current item
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
        } else if (text.contains("ITEM_WON")) {
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
            //System.out.print("PARSED: " + id + " " + name + " " + units + " " + price + "\n");
            AuctionModel ins = new AuctionModel();
            ins.id = id;
            ins.name = name;
            ins.units = units;
            ins.cost = price;
            items.add(ins);
        }
    }

    private String getItemNames() {
        if (purchaseList.size() > 1) {
            String str = String.join(",", purchaseList);
            return "[" + str + "]";
        } else if (purchaseList.size() == 1) {
            return "[" + purchaseList.get(0) + "]";
        }
        return "";
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
            if (items.size() == 1)
                return items.get(0);
            // Return a random item
            int rand_item = ThreadLocalRandom.current().nextInt(0, items.size() - 1);
            return items.get(rand_item);
        }
        return null;
    }

    private void printStatus() {
        if (!getItemNames().isEmpty())
            System.out.println("UID: " + uid + "\n\tPurchased " + purchasedItems + " items for total value of $" + purchaseValue + "\n\t" + getItemNames());
        else
            System.out.println("UID: " + uid + "\n\tPurchased " + purchasedItems + " items for total value of $" + purchaseValue);
    }
}
