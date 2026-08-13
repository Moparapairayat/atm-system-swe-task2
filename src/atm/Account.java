package atm;

import java.util.ArrayList;
import java.util.List;

/**
 * <<entity>> Account
 */
public class Account {
    private String accountNumber;
    private double balance;
    private List<Transaction> history = new ArrayList<>();

    public Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    /** + checkBalance() : double */
    public double checkBalance() {
        return balance;
    }

    /** + deposit(amount: double) : void */
    public void deposit(double amount) {
        balance += amount;
    }

    /** + withdraw(amount: double) : boolean */
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    /** + transfer(amount: double, toAccount: Account) : boolean */
    public boolean transfer(double amount, Account toAccount) {
        if (!this.withdraw(amount)) {
            return false;
        }
        toAccount.deposit(amount);
        return true;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    void recordTransaction(Transaction t) {
        history.add(t);
    }

    public List<Transaction> getHistory() {
        return history;
    }
}
