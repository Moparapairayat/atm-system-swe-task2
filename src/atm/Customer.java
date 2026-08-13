package atm;

import java.util.ArrayList;
import java.util.List;

/**
 * <<entity>> Customer
 * holds (1 - 1) Card, owns (1 - 1..*) Account
 */
public class Customer {
    private String customerID;
    private String name;
    private Card card;
    private List<Account> accounts = new ArrayList<>();

    public Customer(String customerID, String name, Card card) {
        this.customerID = customerID;
        this.name = name;
        this.card = card;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public Card getCard() {
        return card;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public String getName() {
        return name;
    }

    public String getCustomerID() {
        return customerID;
    }
}
