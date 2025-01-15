public class Transaction {
    private int id;
    private String date;
    private String description;
    private double amount;
    private String category;

    public Transaction(int id, String date, String description, double amount, String category) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    public Transaction(String date, String description, double amount, String category) {
        this(0, date, description, amount, category);
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }
}
