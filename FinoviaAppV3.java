
// Full working code of FinoviaAppV3 with sidebar and all features
// Includes all required helper classes, GUI layout, and patterns

// === IMPORTS ===
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

// === Observer Pattern ===
interface Observer {
    void update(String message);
}

class EventNotifier {
    private static EventNotifier instance;
    private final List<Observer> observers = new ArrayList<>();

    private EventNotifier() {}

    public static EventNotifier getInstance() {
        if (instance == null) instance = new EventNotifier();
        return instance;
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void notifyObservers(String msg) {
        for (Observer o : observers) {
            o.update(msg);
        }
    }
}

// === Session (Singleton) ===
class Session {
    private static Session instance;
    private Account account;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public void login(Account acc) {
        this.account = acc;
    }

    public Account getAccount() {
        return account;
    }
}

// === Account + Factory Pattern ===
abstract class Account {
    protected String owner;
    protected double balance;
    protected String type;

    public Account(String owner, double balance) {
        this.owner = owner;
        this.balance = balance;
    }

    public abstract String getType();

    public double getBalance() {
        return balance;
    }

    public void setBalance(double b) {
        balance = b;
    }

    public String getOwner() {
        return owner;
    }
}

class SavingsAccount extends Account {
    public SavingsAccount(String owner, double balance) {
        super(owner, balance);
        this.type = "Savings";
    }

    public String getType() {
        return "Savings";
    }
}

class CurrentAccount extends Account {
    public CurrentAccount(String owner, double balance) {
        super(owner, balance);
        this.type = "Current";
    }

    public String getType() {
        return "Current";
    }
}

class AccountFactory {
    public static Account createAccount(String type, String owner, double balance) {
        if (type.equalsIgnoreCase("savings"))
            return new SavingsAccount(owner, balance);
        else
            return new CurrentAccount(owner, balance);
    }
}

// === Loan + Builder Pattern ===
class Loan {
    private String customer;
    private double amount;
    private int term;

    public Loan(String customer, double amount, int term) {
        this.customer = customer;
        this.amount = amount;
        this.term = term;
    }

    public String details() {
        double emi = (amount * 0.1 + amount) / term;
        return "Loan Approved for " + customer + "\nAmount: $" + amount + "\nTerm: " + term + " months\nEMI: $" + emi;
    }
}

class LoanBuilder {
    private String customer;
    private double amount;
    private int term;

    public LoanBuilder setCustomer(String customer) {
        this.customer = customer;
        return this;
    }

    public LoanBuilder setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public LoanBuilder setTerm(int term) {
        this.term = term;
        return this;
    }

    public Loan build() {
        return new Loan(customer, amount, term);
    }
}

// === Command Pattern ===
interface Command {
    void execute();
}

class DepositCommand implements Command {
    private Account acc;
    private double amt;

    public DepositCommand(Account acc, double amt) {
        this.acc = acc;
        this.amt = amt;
    }

    public void execute() {
        acc.setBalance(acc.getBalance() + amt);
        EventNotifier.getInstance().notifyObservers("Deposited $" + amt);
    }
}

class WithdrawCommand implements Command {
    private Account acc;
    private double amt;

    public WithdrawCommand(Account acc, double amt) {
        this.acc = acc;
        this.amt = amt;
    }

    public void execute() {
        if (acc.getBalance() >= amt) {
            acc.setBalance(acc.getBalance() - amt);
            EventNotifier.getInstance().notifyObservers("Withdrew $" + amt);
        } else {
            EventNotifier.getInstance().notifyObservers("Insufficient Balance");
        }
    }
}

class TransferCommand implements Command {
    private Account from, to;
    private double amt;

    public TransferCommand(Account from, Account to, double amt) {
        this.from = from;
        this.to = to;
        this.amt = amt;
    }

    public void execute() {
        if (from.getBalance() >= amt) {
            from.setBalance(from.getBalance() - amt);
            to.setBalance(to.getBalance() + amt);
            EventNotifier.getInstance().notifyObservers("Transferred $" + amt);
        } else {
            EventNotifier.getInstance().notifyObservers("Transfer Failed: Insufficient Balance");
        }
    }
}

class BillPaymentCommand implements Command {
    private Account acc;
    private String billType;
    private double amount;

    public BillPaymentCommand(Account acc, String billType, double amount) {
        this.acc = acc;
        this.billType = billType;
        this.amount = amount;
    }

    public void execute() {
        if (acc.getBalance() >= amount) {
            acc.setBalance(acc.getBalance() - amount);
            EventNotifier.getInstance().notifyObservers("Paid $" + amount + " for " + billType);
        } else {
            EventNotifier.getInstance().notifyObservers("Bill Payment Failed");
        }
    }
}

// === Theme (Bridge Pattern) ===
interface Theme {
    Color bg();
    Color fg();
}

class LightTheme implements Theme {
    public Color bg() { return Color.WHITE; }
    public Color fg() { return Color.BLACK; }
}

class DarkTheme implements Theme {
    public Color bg() { return Color.DARK_GRAY; }
    public Color fg() { return Color.WHITE; }
}

// === GUI Application ===
public class FinoviaAppV3 extends JFrame implements Observer {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextArea logArea;
    private JLabel balanceLabel, nameLabel;
    private Theme currentTheme = new LightTheme();

    public FinoviaAppV3() {
        setTitle("Finovia V3 - Online Banking System");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        EventNotifier.getInstance().addObserver(this);

        JPanel loginPanel = new JPanel();
        JTextField nameField = new JTextField(10);
        JComboBox<String> accTypeBox = new JComboBox<>(new String[]{"Savings", "Current"});
        JButton loginButton = new JButton("Login");
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(nameField);
        loginPanel.add(new JLabel("Account Type:"));
        loginPanel.add(accTypeBox);
        loginPanel.add(loginButton);
        add(loginPanel, BorderLayout.NORTH);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(150, 0));
        String[] buttons = {"Overview", "Deposit", "Withdraw", "Transfer", "Loan", "Bill Pay", "Toggle Theme", "Logout"};
        for (String btn : buttons) {
            JButton b = new JButton(btn);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(b);
            b.addActionListener(e -> switchPanel(btn));
        }
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(createOverviewPanel(), "Overview");
        mainPanel.add(createDepositPanel(), "Deposit");
        mainPanel.add(createWithdrawPanel(), "Withdraw");
        mainPanel.add(createTransferPanel(), "Transfer");
        mainPanel.add(createLoanPanel(), "Loan");
        mainPanel.add(createBillPanel(), "Bill Pay");
        add(mainPanel, BorderLayout.CENTER);

        logArea = new JTextArea(4, 50);
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            String name = nameField.getText();
            String type = (String) accTypeBox.getSelectedItem();
            Account acc = AccountFactory.createAccount(type, name, 1000);
            Session.getInstance().login(acc);
            nameLabel.setText("Welcome, " + acc.getOwner() + " (" + acc.getType() + ")");
            updateBalance();
            update("Logged in successfully.");
            cardLayout.show(mainPanel, "Overview");
        });

        applyTheme();
        setVisible(true);
    }

    private void switchPanel(String name) {
        if (name.equals("Toggle Theme")) toggleTheme();
        else if (name.equals("Logout")) {
            Session.getInstance().login(null);
            update("Logged out.");
            nameLabel.setText("Welcome");
            balanceLabel.setText("Balance: $0");
        } else cardLayout.show(mainPanel, name);
    }

    private JPanel createOverviewPanel() {
        JPanel p = new JPanel(new GridLayout(2, 1));
        nameLabel = new JLabel("Welcome");
        balanceLabel = new JLabel("Balance: $0");
        p.add(nameLabel);
        p.add(balanceLabel);
        return p;
    }

    private JPanel createDepositPanel() {
        JPanel p = new JPanel();
        JTextField field = new JTextField(10);
        JButton btn = new JButton("Deposit");
        p.add(new JLabel("Amount:"));
        p.add(field);
        p.add(btn);
        btn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(field.getText());
                new DepositCommand(Session.getInstance().getAccount(), amt).execute();
                updateBalance();
            } catch (Exception ex) {
                update("Invalid input.");
            }
        });
        return p;
    }

    private JPanel createWithdrawPanel() {
        JPanel p = new JPanel();
        JTextField field = new JTextField(10);
        JButton btn = new JButton("Withdraw");
        p.add(new JLabel("Amount:"));
        p.add(field);
        p.add(btn);
        btn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(field.getText());
                new WithdrawCommand(Session.getInstance().getAccount(), amt).execute();
                updateBalance();
            } catch (Exception ex) {
                update("Invalid input.");
            }
        });
        return p;
    }

    private JPanel createTransferPanel() {
        JPanel p = new JPanel();
        JTextField toField = new JTextField(10);
        JTextField amtField = new JTextField(10);
        JButton btn = new JButton("Transfer");
        p.add(new JLabel("To (Name):"));
        p.add(toField);
        p.add(new JLabel("Amount:"));
        p.add(amtField);
        p.add(btn);
        btn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amtField.getText());
                Account toAcc = AccountFactory.createAccount("Savings", toField.getText(), 1000);
                new TransferCommand(Session.getInstance().getAccount(), toAcc, amt).execute();
                updateBalance();
            } catch (Exception ex) {
                update("Transfer error.");
            }
        });
        return p;
    }

    private JPanel createLoanPanel() {
        JPanel p = new JPanel();
        JTextField amtField = new JTextField(10);
        JTextField termField = new JTextField(5);
        JButton btn = new JButton("Apply Loan");
        p.add(new JLabel("Amount:"));
        p.add(amtField);
        p.add(new JLabel("Term (months):"));
        p.add(termField);
        p.add(btn);
        btn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amtField.getText());
                int term = Integer.parseInt(termField.getText());
                Loan loan = new LoanBuilder().setCustomer(Session.getInstance().getAccount().getOwner()).setAmount(amt).setTerm(term).build();
                update(loan.details());
            } catch (Exception ex) {
                update("Invalid loan input.");
            }
        });
        return p;
    }

    private JPanel createBillPanel() {
        JPanel p = new JPanel();
        JTextField amtField = new JTextField(10);
        JButton btn = new JButton("Pay Bill");
        p.add(new JLabel("Bill Amount:"));
        p.add(amtField);
        p.add(btn);
        btn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amtField.getText());
                new BillPaymentCommand(Session.getInstance().getAccount(), "Utility", amt).execute();
                updateBalance();
            } catch (Exception ex) {
                update("Bill error.");
            }
        });
        return p;
    }

    private void updateBalance() {
        Account acc = Session.getInstance().getAccount();
        if (acc != null)
            balanceLabel.setText("Balance: $" + acc.getBalance());
    }

    private void toggleTheme() {
        currentTheme = (currentTheme instanceof LightTheme) ? new DarkTheme() : new LightTheme();
        applyTheme();
        update("Theme changed.");
    }

    private void applyTheme() {
        getContentPane().setBackground(currentTheme.bg());
        logArea.setBackground(currentTheme.bg());
        logArea.setForeground(currentTheme.fg());
        mainPanel.setBackground(currentTheme.bg());
    }

    public void update(String message) {
        logArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FinoviaAppV3::new);
    }
}
