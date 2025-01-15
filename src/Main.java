import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Personal Finance Tracker!");
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add a Transaction");
            System.out.println("2. View All Transactions");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addTransaction(scanner);
                    break;
                case 2:
                    viewTransactions();
                    break;
                case 3:
                    System.out.println("Exiting... Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void addTransaction(Scanner scanner) {
        System.out.println("Enter Transaction Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        System.out.println("Enter Description: ");
        String description = scanner.nextLine();

        System.out.println("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.println("Enter Category: ");
        String category = scanner.nextLine();

        String query = "INSERT INTO transactions (date, description, amount, category) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, date);
            statement.setString(2, description);
            statement.setDouble(3, amount);
            statement.setString(4, category);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Transaction added successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewTransactions() {
        String query = "SELECT * FROM transactions";

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.printf("%-5s %-12s %-20s %-10s %-15s\n", "ID", "Date", "Description", "Amount", "Category");
            System.out.println("---------------------------------------------------------------");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String date = resultSet.getString("date");
                String description = resultSet.getString("description");
                double amount = resultSet.getDouble("amount");
                String category = resultSet.getString("category");

                System.out.printf("%-5d %-12s %-20s %-10.2f %-15s\n", id, date, description, amount, category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
