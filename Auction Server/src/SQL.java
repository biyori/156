import java.sql.*;
import java.util.Objects;
import java.util.UUID;

public class SQL {
    private Connection connection;

    public SQL() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println("SQL() constructor error : " + e.getMessage());
        }
    }

    /**
     * Prepare the Auction DB
     */
    public void CreateAuctionDB() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists items");
            statement.executeUpdate("drop table if exists auction_winners"); // Keep track of sold items
            // Create items table structure and some defaults
            statement.executeUpdate("create table items (id integer primary key autoincrement, name string, price integer, quantity integer, active boolean default true, item_expires integer)"); // Active column is to hide auction items that are already won
            statement.executeUpdate("insert into items (name, price, quantity, active, item_expires) values('Scooter', 100, 3, 1, 0)");
            statement.executeUpdate("insert into items (name, price, quantity, item_expires) values('Pencil', 10, 200, 0)");
            statement.executeUpdate("insert into items (name, price, quantity, item_expires) values('iPhone 12', 999, 2, 0)");
            // Create auction_winners table to log items won
            statement.executeUpdate("create table auction_winners (id integer primary key autoincrement, item_id integer, client_uuid string, price integer, quantity integer default 0)");
            statement.executeUpdate("create index idx_auction_winner_uuid on auction_winners (client_uuid)"); // Index client ID for fast lookups--not needed, but it's fun to add

            ResultSet rs = statement.executeQuery("select * from items where active = 1 LIMIT 1");
            while (rs.next()) {
                // read the result set
                System.out.println("[" + rs.getInt("id") + "] " + rs.getString("name") + " (" + rs.getInt("quantity") + ") Price: " + rs.getInt("price") + " Active: " + rs.getInt("active") + " Expires: " + rs.getInt("item_expires"));
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println("CreateAuctionDB(): " + e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean ItemExists(String name) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            // Insert
            PreparedStatement query = connection.prepareStatement("select * from items where name = ?");
            query.setString(1, name);
            ResultSet rs = query.executeQuery();
            while (rs.next()) {
                // read the result set
                System.out.println("[" + rs.getInt("id") + "] " + rs.getString("name") + " (" + rs.getInt("quantity") + ") Price: " + rs.getInt("price"));
                if (rs.getString("name").equalsIgnoreCase(name))
                    return true;
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return false;
    }

    public boolean ItemExists(int id) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            // Insert
            PreparedStatement query = connection.prepareStatement("select * from items where id = ?");
            query.setInt(1, id);
            ResultSet rs = query.executeQuery();
            while (rs.next()) {
                // read the result set
                System.out.println("[" + rs.getInt("id") + "] " + rs.getString("name") + " (" + rs.getInt("quantity") + ") Price: " + rs.getInt("price"));
                if (rs.getInt("id") == id)
                    return true;
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return false;
    }

    public void InsertDB(String name, int price, int quantity) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            // Insert
            PreparedStatement query = connection.prepareStatement("insert into items (name, price, quantity, item_expires) values(?,?,?,?)");
            query.setString(1, name);
            query.setInt(2, price);
            query.setInt(3, quantity);
            query.setInt(4, 0);
            query.execute();


            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("select * from items where active = 1");
            while (rs.next()) {
                // read the result set
                System.out.println("\t[" + rs.getInt("id") + "] " + rs.getString("name") + " (" + rs.getInt("quantity") + ") Price: " + rs.getInt("price"));
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public void UpdateAuctionTimer(int id) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            // Insert
            PreparedStatement query = connection.prepareStatement("update items set item_expires = ? where id = ?");
            query.setLong(1, System.currentTimeMillis() + 60 * 1000);
            query.setInt(2, id);
            query.execute();
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public void UpdateAuctionTimer(int id, int seconds) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            // Insert
            PreparedStatement query = connection.prepareStatement("update items set item_expires = ? where id = ?");
            query.setLong(1, System.currentTimeMillis() + seconds * 1000L);
            query.setInt(2, id);
            query.execute();
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public String AuctionItem(boolean printItems) {
        StringBuilder auctionList = new StringBuilder();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("select * from items where active = 1 limit 1");

            while (rs.next()) {
                // read the result set
                auctionList.append("ID: ").
                        append(rs.getInt("id")).
                        append("\t").append("Name: ").
                        append(rs.getString("name")).
                        append("\t").append("Units: ").
                        append(rs.getInt("quantity")).
                        append("\t").append("Price: ").
                        append(rs.getInt("price")).
                        append("\n");

                if (printItems) {
                    System.out.println("[" + rs.getInt("id") + "] " + rs.getString("name") + " (" + rs.getInt("quantity") + ") Price: " + rs.getInt("price") + "\n" +
                            "Expires: " + rs.getLong("item_expires"));
                }
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return auctionList.toString();
    }

    public Item CurrentAuctionItem(boolean printItems) {
        Item auctionItem = new Item();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("select * from items where active = 1 limit 1");

            while (rs.next()) {
                // read the result set
                auctionItem.Id = rs.getInt("id");
                auctionItem.Name = rs.getString("name");
                auctionItem.quantity = rs.getInt("quantity");
                auctionItem.price = rs.getInt("price");
                auctionItem.active = rs.getBoolean("active");
                auctionItem.item_expires = rs.getLong("item_expires");

                if (printItems) {
                    System.out.println("[" + rs.getInt("id") + "] " + rs.getString("name") + " (" + rs.getInt("quantity") + ") Price: " + rs.getInt("price") + "\n" +
                            "Expires: " + rs.getLong("item_expires"));
                }
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return auctionItem;
    }

    public int CurrentAuctionSize() {
        int auctionSize = 0;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("select count(active) as total from items where active = 1");

            while (rs.next()) {
                // read the result set
               auctionSize= rs.getInt("total");
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return auctionSize;
    }

    public String PrintAuctionItems(boolean printItems) {
        StringBuilder auctionList = new StringBuilder();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("select * from items where active = 1");

            while (rs.next()) {
                // read the result set
                auctionList.append("ID: ").
                        append(rs.getInt("id")).
                        append("\t").append("Name: ").
                        append(rs.getString("name")).
                        append("\t").append("Units: ").
                        append(rs.getInt("quantity")).
                        append("\t").append("Price: ").
                        append(rs.getInt("price")).
                        append("\n");

                if (printItems)
                    System.out.println("[" + rs.getInt("id") + "] " + rs.getString("name") + " (" + rs.getInt("quantity") + ") Price: " + rs.getInt("price"));

            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return auctionList.toString();
    }

    public boolean UpdateItemPrice(int id, int price) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");

            // Verify current price
            PreparedStatement statement = connection.prepareStatement("select * from items where id = ?");
            statement.setInt(1, id);
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                // read the result set
                if (rs.getInt("price") > price)
                    return false;
            }

            // Insert
            PreparedStatement query = connection.prepareStatement("update items set price = ? where id = ?");
            query.setInt(1, price);
            query.setInt(2, id);
            query.execute();
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return true;
    }

    public boolean BiddingUserExists(String id, int item_id) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            // Insert
            PreparedStatement query = connection.prepareStatement("select * from auction_winners where item_id = ? and client_uuid = ?");
            query.setInt(1, item_id);
            query.setString(2, id);
            ResultSet rs = query.executeQuery();
            while (rs.next()) {
                // read the result set
                if (Objects.equals(rs.getString("client_uuid"), id))
                    return true;
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return false;
    }

    public void AddUserToAuction(UUID user_id, int item_id, int price, int quantity) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            // Insert
            PreparedStatement query = connection.prepareStatement("insert into auction_winners (item_id, client_uuid, price, quantity) values (?,?,?,?)");
            query.setInt(1, item_id);
            query.setString(2, user_id.toString());
            query.setInt(3, price);
            query.setInt(4, quantity);
            query.execute();
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public void UpdateUserBid(UUID user_id, int item_id, int price) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");

            PreparedStatement statement = connection.prepareStatement("update auction_winners set price = ? where item_id = ? and client_uuid = ?");
            statement.setInt(1, price);
            statement.setInt(2, item_id);
            statement.setString(3, user_id.toString());
            statement.execute();
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public String FindWinner(int item_id) {
        StringBuilder auctionList = new StringBuilder();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            // Insert
            PreparedStatement query = connection.prepareStatement("select * from auction_winners where item_id = ? order by price DESC limit 1");
            query.setInt(1, item_id);
            ResultSet rs = query.executeQuery();
            while (rs.next()) {
                // read the result set
                auctionList.append("ITEM_WON: ").
                        append(rs.getString("client_uuid"));
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return auctionList.toString();
    }

    public void CloseItem(int item_id) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");

            PreparedStatement statement = connection.prepareStatement("update items set active = 0 where id = ?");
            statement.setInt(1, item_id);
            statement.execute();
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }
}