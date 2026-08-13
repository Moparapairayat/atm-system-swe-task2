package atm;

import java.time.LocalDate;

/**
 * Swing application entry point that seeds the UML ATM model demo data.
 */
public class Main {

    public static void main(String[] args) {
        // ---------------- seed / demo data ----------------
        Bank bank = new Bank("BITHM National Bank", "BNB01");

        Card card1 = new Card("1111-2222-3333-4444", LocalDate.of(2028, 5, 1), "1234");
        Customer alice = new Customer("C001", "Ziana Mehnaz Ruhee", card1);
        Account aliceSavings = new Account("ACC-1001", 500.00);
        Account aliceCurrent = new Account("ACC-1002", 150.00);
        alice.addAccount(aliceSavings);
        alice.addAccount(aliceCurrent);
        bank.addAccount(aliceSavings);
        bank.addAccount(aliceCurrent);

        Card card2 = new Card("5555-6666-7777-8888", LocalDate.of(2027, 11, 1), "4321");
        Customer bob = new Customer("C002", "Mopara Pair Ayat", card2);
        Account bobSavings = new Account("ACC-2001", 1000.00);
        bob.addAccount(bobSavings);
        bank.addAccount(bobSavings);

        Customer[] customers = { alice, bob };

        ATM atm = new ATM("ATM-001", "Chattogram Branch", 50000.00, bank);
        ATMTechnician tech = new ATMTechnician("T001", "Mopara Pair Ayat");

        javax.swing.SwingUtilities.invokeLater(() ->
                new ATMGui(atm, bank, customers, tech).show());
    }

}
