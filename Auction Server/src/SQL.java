import java.sql.*;

public class SQL {
    private Connection connection;

    public SQL() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    public void CreateAuctionDB() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists items");
            statement.executeUpdate("create table items (id integer primary key autoincrement, name string, price integer, quantity integer)");
            statement.executeUpdate("insert into items (name, price, quantity) values('Scooter', 100, 3)");
            statement.executeUpdate("insert into items (name, price, quantity) values('Pencil', 10, 200)");
            statement.executeUpdate("insert into items (name, price, quantity) values('iPhone 12', 999, 2)");
            ResultSet rs = statement.executeQuery("select * from items");
            while (rs.next()) {
                // read the result set
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
    }

    public void InsertDB(String name, int price, int quantity) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");
            // Insert
            PreparedStatement query = connection.prepareStatement("insert into items (name, price, quantity) values(?,?,?)");
            query.setString(1, name);
            query.setInt(2, price);
            query.setInt(3, quantity);
            query.execute();


            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("select * from items");
            while (rs.next()) {
                // read the result set
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
    }

    public String PrintAuctionItems() {
        StringBuilder auctionList = new StringBuilder();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auction.db");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("select * from items");

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
}