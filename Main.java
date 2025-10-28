import java.util.*;

// Singleton Pattern
class FileDatabase {
    private static FileDatabase instance;
    private FileDatabase() {}
    public static FileDatabase getInstance() {
        if (instance == null) instance = new FileDatabase();
        return instance;
    }
    public void saveUser(String userData) {
        System.out.println("Saved to file: " + userData);
    }
}

// Factory + Builder Pattern
interface Account {
    void open();
    String getDetails();
}
class SavingsAccount implements Account {
    public void open() { System.out.println("Savings account opened."); }
    public String getDetails() { return "Type: Savings"; }
}
class AccountFactory {
    public static Account createAccount(String type) {
        if (type.equalsIgnoreCase("savings")) return new SavingsAccount();
        return null;
    }
}
class User {
    private String name, email, phone;
    private Account account;
    private User(UserBuilder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.phone = builder.phone;
        this.account = builder.account;
    }
    public void register() {
        account.open();
        String data = "Name: " + name + ", Email: " + email + ", Phone: " + phone + ", " + account.getDetails();
        FileDatabase.getInstance().saveUser(data);
    }
    public static class UserBuilder {
        private String name, email, phone;
        private Account account;
        public UserBuilder setName(String name) { this.name = name; return this; }
        public UserBuilder setEmail(String email) { this.email = email; return this; }
        public UserBuilder setPhone(String phone) { this.phone = phone; return this; }
        public UserBuilder setAccount(Account account) { this.account = account; return this; }
        public User build() { return new User(this); }
    }
}

// Adapter Pattern
interface BankPayment {
    void processPayment(String amount);
}
class ThirdPartyGateway {
    public void makeTransaction(String amount) {
        System.out.println("Processed by third-party gateway: " + amount);
    }
}
class PaymentAdapter implements BankPayment {
    ThirdPartyGateway gateway = new ThirdPartyGateway();
    public void processPayment(String amount) {
        gateway.makeTransaction(amount);
    }
}

// Bridge Pattern
interface MessageSender {
    void send(String message);
}
class EmailSender implements MessageSender {
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}
class SMSSender implements MessageSender {
    public void send(String message) {
        System.out.println("SMS: " + message);
    }
}
abstract class Notification {
    protected MessageSender sender;
    public Notification(MessageSender sender) { this.sender = sender; }
    abstract void notifyUser(String message);
}
class AccountNotification extends Notification {
    public AccountNotification(MessageSender sender) { super(sender); }
    public void notifyUser(String message) {
        sender.send("Account Alert: " + message);
    }
}

// Composite Pattern
interface Employee {
    void showDetails();
}
class Developer implements Employee {
    private String name;
    public Developer(String name) { this.name = name; }
    public void showDetails() {
        System.out.println("Developer: " + name);
    }
}
class ManagerComposite implements Employee {
    private String name;
    public ManagerComposite(String name) { this.name = name; }
    public void showDetails() {
        System.out.println("Manager: " + name);
    }
}
class EmployeeGroup implements Employee {
    private List<Employee> list = new ArrayList<>();
    public void add(Employee e) { list.add(e); }
    public void showDetails() {
        for (Employee e : list) e.showDetails();
    }
}

// Chain of Responsibility
abstract class Approver {
    protected Approver next;
    public void setNext(Approver next) { this.next = next; }
    public abstract void approveRequest(double amount);
}
class Clerk extends Approver {
    public void approveRequest(double amount) {
        if (amount <= 1000) System.out.println("Clerk approved: " + amount);
        else if (next != null) next.approveRequest(amount);
    }
}
class Director extends Approver {
    public void approveRequest(double amount) {
        System.out.println("Director approved: " + amount);
    }
}

// Command Pattern
interface Command {
    void execute();
}
class BankAccount {
    int balance = 0;
    public void deposit(int amt) {
        balance += amt; System.out.println("Deposited: " + amt);
    }
    public void withdraw(int amt) {
        balance -= amt; System.out.println("Withdrawn: " + amt);
    }
}
class DepositCommand implements Command {
    BankAccount acc; int amt;
    public DepositCommand(BankAccount acc, int amt) {
        this.acc = acc; this.amt = amt;
    }
    public void execute() { acc.deposit(amt); }
}
class WithdrawCommand implements Command {
    BankAccount acc; int amt;
    public WithdrawCommand(BankAccount acc, int amt) {
        this.acc = acc; this.amt = amt;
    }
    public void execute() { acc.withdraw(amt); }
}

// Null Object Pattern
interface UserRole {
    void showPermissions();
}
class AdminUser implements UserRole {
    public void showPermissions() {
        System.out.println("Admin: Full access");
    }
}
class NullUser implements UserRole {
    public void showPermissions() {
        System.out.println("Guest: Limited access");
    }
}
class UserFactoryRole {
    public static UserRole getUserRole(String username) {
        if ("admin".equalsIgnoreCase(username)) return new AdminUser();
        return new NullUser();
    }
}

// Mediator Pattern
interface Department {
    void send(String msg);
    void receive(String msg);
}
class BankingMediator {
    Department accDept, loanDept;
    public void registerAccounts(Department d) { this.accDept = d; }
    public void registerLoans(Department d) { this.loanDept = d; }
    public void sendMessage(String msg, Department sender) {
        if (sender == accDept) loanDept.receive(msg);
        else accDept.receive(msg);
    }
}
class AccountsDept implements Department {
    BankingMediator mediator;
    public AccountsDept(BankingMediator m) { this.mediator = m; }
    public void send(String msg) { mediator.sendMessage(msg, this); }
    public void receive(String msg) { System.out.println("Accounts received: " + msg); }
}
class LoansDept implements Department {
    BankingMediator mediator;
    public LoansDept(BankingMediator m) { this.mediator = m; }
    public void send(String msg) { mediator.sendMessage(msg, this); }
    public void receive(String msg) { System.out.println("Loans received: " + msg); }
}

// Observer Pattern
interface Observer {
    void update(String msg);
}
class Customer implements Observer {
    private String name;
    public Customer(String name) { this.name = name; }
    public void update(String msg) {
        System.out.println(name + " got: " + msg);
    }
}
class LoanService {
    private List<Observer> observers = new ArrayList<>();
    public void subscribe(Observer o) { observers.add(o); }
    public void notifyAllCustomers(String msg) {
        for (Observer o : observers) o.update(msg);
    }
}

// Main class
public class Main {
    public static void main(String[] args) {
        System.out.println("--- Singleton ---");
        FileDatabase.getInstance().saveUser("Test User");

        System.out.println("--- Factory + Builder ---");
        Account acc = AccountFactory.createAccount("savings");
        User user = new User.UserBuilder()
            .setName("Arooj").setEmail("arooj@bank.com").setPhone("0300")
            .setAccount(acc).build();
        user.register();

        System.out.println("--- Adapter ---");
        BankPayment payment = new PaymentAdapter();
        payment.processPayment("Rs.5000");

        System.out.println("--- Bridge ---");
        new AccountNotification(new EmailSender()).notifyUser("Low Balance");

        System.out.println("--- Composite ---");
        EmployeeGroup group = new EmployeeGroup();
        group.add(new Developer("Ali"));
        group.add(new ManagerComposite("Sara"));
        group.showDetails();

        System.out.println("--- Chain of Responsibility ---");
        Clerk clerk = new Clerk();
        Director director = new Director();
        clerk.setNext(director);
        clerk.approveRequest(12000);

        System.out.println("--- Command ---");
        BankAccount acc1 = new BankAccount();
        new DepositCommand(acc1, 1000).execute();
        new WithdrawCommand(acc1, 300).execute();

        System.out.println("--- Null Object ---");
        UserFactoryRole.getUserRole("admin").showPermissions();
        UserFactoryRole.getUserRole("guest").showPermissions();

        System.out.println("--- Mediator ---");
        BankingMediator med = new BankingMediator();
        Department d1 = new AccountsDept(med);
        Department d2 = new LoansDept(med);
        med.registerAccounts(d1); med.registerLoans(d2);
        d1.send("Loan query"); d2.send("Balance check complete");

        System.out.println("--- Observer ---");
        LoanService loan = new LoanService();
        loan.subscribe(new Customer("Ali"));
        loan.subscribe(new Customer("Zara"));
        loan.notifyAllCustomers("New Home Loan at 3%!");
    }
}