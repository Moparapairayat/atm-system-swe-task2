package atm;

/** <<control>> Withdrawal extends Transaction */
public class Withdrawal extends Transaction {
    public Withdrawal(String transactionID, double amount, Account account) {
        super(transactionID, amount, account);
    }

    @Override
    public boolean execute() {
        boolean success = account.withdraw(amount);
        if (success) {
            account.recordTransaction(this);
            System.out.println("Withdrawal successful. New balance: " + account.checkBalance());
        } else {
            System.out.println("Withdrawal failed: insufficient funds or invalid amount.");
        }
        return success;
    }
}
