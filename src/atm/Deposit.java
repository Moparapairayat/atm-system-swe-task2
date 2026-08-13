package atm;

/** <<control>> Deposit extends Transaction */
public class Deposit extends Transaction {
    public Deposit(String transactionID, double amount, Account account) {
        super(transactionID, amount, account);
    }

    @Override
    public boolean execute() {
        if (amount <= 0) {
            System.out.println("Deposit failed: amount must be positive.");
            return false;
        }
        account.deposit(amount);
        account.recordTransaction(this);
        System.out.println("Deposit successful. New balance: " + account.checkBalance());
        return true;
    }
}
