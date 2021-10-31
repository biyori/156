import java.util.ArrayList;

public class ServerParser {
    ArrayList<AuctionModel> items;

    ServerParser() {
        items = new ArrayList<>();
    }

    public void Read(String text) {
        String[] parse;
        parse = text.split("\t");//Tab & Space
        if (parse.length == 4) {
            int id = Integer.parseInt(parse[0].replace("ID: ", "").trim());
            String name = parse[1].replace("Name: ", "").trim();
            int units = Integer.parseInt(parse[2].replace("Units: ", "").trim());
            int price = Integer.parseInt(parse[3].replace("Price: ", "").trim());


            if (text.contains("Name: " + name)) {
                //System.out.print("PARSED: " + id + " " + name + " " + units + " " + price + "\n");
                AuctionModel ins = new AuctionModel();
                ins.id = id;
                ins.name=name;
                ins.units = units;
                ins.cost = price;
                items.add(ins);
            }
        }
    }

    public int AuctionSize() {
        return items.size();
    }

    public void UpdateAuctionList() {
        if(!items.isEmpty())
            items = new ArrayList<>();
    }


}
