import java.io.*;
import java.time.LocalDate;
import java.util.*;

class Expense {
    private int id;
    private String category;
    private double amount;
    private LocalDate date;

    public Expense(int id, String category, double amount, LocalDate date) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public int getId() { return id; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return "ID: " + id + ", Category: " + category + ", Amount: $" + amount + ", Date: " + date;
    }
}

class ExpenseTracker {
    private List<Expense> expenses;
    private int nextId;
    private final String FILE_PATH = "expenses.txt";

    public ExpenseTracker() {
        expenses = new ArrayList<>();
        nextId = 1;
        loadExpenses();
    }

    public void addExpense(String category, double amount, LocalDate date) {
        Expense expense = new Expense(nextId++, category, amount, date);
        expenses.add(expense);
        saveExpenses();
        System.out.println("Expense added: " + expense);
    }

    public void viewExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            expenses.forEach(System.out::println);
        }
    }

    public void viewExpensesByCategory(String category) {
        expenses.stream()
            .filter(expense -> expense.getCategory().equalsIgnoreCase(category))
            .forEach(System.out::println);
    }

    public void generateReport(LocalDate month) {
        double total = 0;
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            if (expense.getDate().getMonth().equals(month.getMonth()) && expense.getDate().getYear() == month.getYear()) {
                total += expense.getAmount();
                categoryTotals.put(expense.getCategory(),
                        categoryTotals.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
            }
        }
        System.out.println("Monthly Total: $" + total);
        categoryTotals.forEach((cat, amt) -> System.out.println(cat + ": $" + amt));
    }

    public void deleteExpense(int id) {
        expenses.removeIf(expense -> expense.getId() == id);
        saveExpenses();
        System.out.println("Expense deleted with ID: " + id);
    }

    private void loadExpenses() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String category = parts[1];
                double amount = Double.parseDouble(parts[2]);
                LocalDate date = LocalDate.parse(parts[3]);
                expenses.add(new Expense(id, category, amount, date));
                nextId = Math.max(nextId, id + 1);
            }
        } catch (IOException e) {
            System.out.println("No saved expenses found.");
        }
    }

    private void saveExpenses() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Expense expense : expenses) {
                writer.write(expense.getId() + "," + expense.getCategory() + "," +
                        expense.getAmount() + "," + expense.getDate() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses.");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. View Expenses by Category");
            System.out.println("4. Generate Monthly Report");
            System.out.println("5. Delete Expense");
            System.out.println("6. Exit");

            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine();
                    System.out.print("Enter amount: ");
                    double amount = scanner.nextDouble();
                    System.out.print("Enter date (YYYY-MM-DD): ");
                    LocalDate date = LocalDate.parse(scanner.next());
                    tracker.addExpense(category, amount, date);
                }
                case 2 -> tracker.viewExpenses();
                case 3 -> {
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine();
                    tracker.viewExpensesByCategory(category);
                }
                case 4 -> {
                    System.out.print("Enter month (YYYY-MM): ");
                    LocalDate month = LocalDate.parse(scanner.next() + "-01");
                    tracker.generateReport(month);
                }
                case 5 -> {
                    System.out.print("Enter expense ID to delete: ");
                    int id = scanner.nextInt();
                    tracker.deleteExpense(id);
                }
                case 6 -> {
                    System.out.println("Exiting.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
