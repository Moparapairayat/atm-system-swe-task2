package atm;

/** <<control>> BalanceInquiry extends Transaction */
public class BalanceInquiry extends Transaction {
    public BalanceInquiry(String transactionID, Account account) {
        super(transactionID, 0.0, account);
    }

    @Override
    public boolean execute() {
        System.out.println("Current balance: " + account.checkBalance());
        account.recordTransaction(this);
        return true;
    }
}
