import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static int loggedInUserId = -1; // To track the logged-in user ID

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (!isLoggedIn()) {
                System.out.println("\nWelcome to Personal Finance Tracker!");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        registerUser(scanner);
                        break;
                    case 2:
                        loginUser(scanner);
                        break;
                    case 3:
                        System.out.println("Exiting... Goodbye!");
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } else {
                System.out.println("\nLogged in as user ID: " + loggedInUserId);
                System.out.println("1. Add a Transaction");
                System.out.println("2. View Your Transactions");
                System.out.println("3. Logout");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        addTransaction(scanner);
                        break;
                    case 2:
                        viewTransactions();
                        break;
                    case 3:
                        logoutUser();
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        }
    }

    private static void registerUser(Scanner scanner) {
        System.out.println("Enter Username: ");
        String username = scanner.nextLine();

        System.out.println("Enter Password: ");
        String password = scanner.nextLine();

        System.out.println("Enter Email: ");
        String email = scanner.nextLine();

        try {
            boolean success = DatabaseHelper.registerUser(username, password, email);
            if (success) {
                System.out.println("User registered successfully! You can now log in.");
            } else {
                System.out.println("Registration failed. Try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loginUser(Scanner scanner) {
        System.out.println("Enter Username: ");
        String username = scanner.nextLine();

        System.out.println("Enter Password: ");
        String password = scanner.nextLine();

        try {
            int userId = DatabaseHelper.loginUser(username, password);
            if (userId != -1) {
                loggedInUserId = userId; // Set logged-in user ID
                System.out.println("Login successful! Welcome, " + username + ".");
            } else {
                System.out.println("Invalid username or password. Try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

        String query = "INSERT INTO transactions (user_id, date, description, amount, category) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, loggedInUserId); // Add user ID to the transaction
            statement.setString(2, date);
            statement.setString(3, description);
            statement.setDouble(4, amount);
            statement.setString(5, category);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Transaction added successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewTransactions() {
        String query = "SELECT * FROM transactions WHERE user_id = ?";

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, loggedInUserId); // Fetch transactions only for the logged-in user

            try (ResultSet resultSet = statement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void logoutUser() {
        loggedInUserId = -1; // Reset logged-in user ID
        System.out.println("Logged out successfully.");
    }

    private static boolean isLoggedIn() {
        return loggedInUserId != -1;
    }
}
