package atm;

/** <<control>> Transfer extends Transaction */
public class Transfer extends Transaction {
    private Account toAccount;

    public Transfer(String transactionID, double amount, Account fromAccount, Account toAccount) {
        super(transactionID, amount, fromAccount);
        this.toAccount = toAccount;
    }

    @Override
    public boolean execute() {
        boolean success = account.transfer(amount, toAccount);
        if (success) {
            account.recordTransaction(this);
            System.out.println("Transfer successful. New balance: " + account.checkBalance());
        } else {
            System.out.println("Transfer failed: insufficient funds or invalid amount.");
        }
        return success;
    }
}
