package atm;

import java.util.ArrayList;
import java.util.List;

/**
 * <<control>> Bank
 * manages (1 - *) Account
 */
public class Bank {
    private String bankName;
    private String bankCode;
    private List<Account> managedAccounts = new ArrayList<>();

    public Bank(String bankName, String bankCode) {
        this.bankName = bankName;
        this.bankCode = bankCode;
    }

    public void addAccount(Account account) {
        managedAccounts.add(account);
    }

    /** + verifyTransaction(t: Transaction) : boolean */
    public boolean verifyTransaction(Transaction t) {
        // simple business rule demonstrating the Bank's control-layer role
        return t != null && t.getAmount() >= 0;
    }

    /** + processTransaction(t: Transaction) : void */
    public void processTransaction(Transaction t) {
        System.out.println("[Bank:" + bankName + "] Transaction " + t.getTransactionID()
                + " processed and logged at bank level.");
    }

    public String getBankName() {
        return bankName;
    }
}
