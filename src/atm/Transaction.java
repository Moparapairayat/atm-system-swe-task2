package atm;

import java.time.LocalDate;

/**
 * <<control>> Transaction (abstract)
 * recorded against (* - 1) Account
 */
public abstract class Transaction {
    protected String transactionID;
    protected double amount;
    protected LocalDate date;
    protected Account account;

    public Transaction(String transactionID, double amount, Account account) {
        this.transactionID = transactionID;
        this.amount = amount;
        this.account = account;
        this.date = LocalDate.now();
    }

    /** + execute() : boolean  {abstract} */
    public abstract boolean execute();

    public String getTransactionID() {
        return transactionID;
    }

    public double getAmount() {
        return amount;
    }
}
