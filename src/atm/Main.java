package atm;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Console driver application demonstrating the ATM class model
 * derived from the UML class diagrams (Activities A and B).
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

    /** Reads a line and safely parses it as a double; returns null (with a message) on invalid input. */
    private static Double readAmount(Scanner sc, String prompt) {
        System.out.print(prompt);
        String raw = sc.nextLine().trim();
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered: \"" + raw + "\". Please enter a numeric value.");
            return null;
        }
    }

    private static void customerSession(Scanner sc, ATM atm, Bank bank, Customer[] customers) {
        System.out.print("Enter card number: ");
        String cardNo = sc.nextLine().trim();

        Customer matched = null;
        for (Customer c : customers) {
            if (c.getCard().getCardNumber().equals(cardNo)) {
                matched = c;
                break;
            }
        }

        if (matched == null) {
            System.out.println("Card not recognised.");
            return;
        }

        System.out.print("Enter PIN: ");
        String pin = sc.nextLine().trim();

        boolean authenticated = atm.insertCard(matched.getCard(), pin);
        int attempts = 1;
        while (!authenticated && attempts < 3) {
            System.out.print("Incorrect PIN. Try again: ");
            pin = sc.nextLine().trim();
            authenticated = atm.insertCard(matched.getCard(), pin);
            attempts++;
        }

        if (!authenticated) {
            System.out.println("Too many incorrect attempts. Card retained.");
            return;
        }

        System.out.println("Welcome, " + matched.getName() + "!");

        List<Account> accounts = matched.getAccounts();
        Account account;
        if (accounts.size() == 1) {
            account = accounts.get(0);
        } else {
            System.out.println("Select account:");
            for (int i = 0; i < accounts.size(); i++) {
                System.out.println((i + 1) + ") " + accounts.get(i).getAccountNumber());
            }
            int idx = 0;
            try {
                idx = Integer.parseInt(sc.nextLine().trim()) - 1;
                if (idx < 0 || idx >= accounts.size()) {
                    System.out.println("Invalid choice, defaulting to first account.");
                    idx = 0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, defaulting to first account.");
            }
            account = accounts.get(idx);
        }

        boolean sessionActive = true;
        while (sessionActive) {
            atm.displayMenu();
            System.out.print("Select option: ");
            String opt = sc.nextLine().trim();
            Transaction t = null;

            switch (opt) {
                case "1":
                    t = new BalanceInquiry("TXN-" + System.nanoTime(), account);
                    break;
                case "2": {
                    Double dAmt = readAmount(sc, "Enter deposit amount: ");
                    if (dAmt != null) {
                        t = new Deposit("TXN-" + System.nanoTime(), dAmt, account);
                    }
                    break;
                }
                case "3": {
                    Double wAmt = readAmount(sc, "Enter withdrawal amount: ");
                    if (wAmt != null) {
                        t = new Withdrawal("TXN-" + System.nanoTime(), wAmt, account);
                    }
                    break;
                }
                case "4": {
                    System.out.print("Enter destination account number: ");
                    String destNo = sc.nextLine().trim();
                    Account dest = null;
                    for (Customer c : customers) {
                        for (Account a : c.getAccounts()) {
                            if (a.getAccountNumber().equals(destNo)) {
                                dest = a;
                            }
                        }
                    }
                    if (dest == null) {
                        System.out.println("Destination account not found.");
                        break;
                    }
                    Double tAmt = readAmount(sc, "Enter transfer amount: ");
                    if (tAmt != null) {
                        t = new Transfer("TXN-" + System.nanoTime(), tAmt, account, dest);
                    }
                    break;
                }
                case "5":
                    sessionActive = false;
                    System.out.println("Card ejected.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }

            if (t != null) {
                if (bank.verifyTransaction(t)) {
                    t.execute();
                    bank.processTransaction(t);
                } else {
                    System.out.println("Transaction rejected by bank.");
                }
            }
        }
    }

    private static void technicianSession(Scanner sc, ATMTechnician tech) {
        System.out.println("Technician: " + tech.getName());
        boolean active = true;
        while (active) {
            System.out.println("\n--- Service Menu ---");
            System.out.println("1) Replenishment (cash/ink/paper)");
            System.out.println("2) Upgrade (hardware/firmware/software)");
            System.out.println("3) Diagnostic (remote/on-site)");
            System.out.println("4) Repair");
            System.out.println("5) Back");
            System.out.print("Select option: ");
            String opt = sc.nextLine().trim();

            switch (opt) {
                case "1": {
                    System.out.print("Item type (cash/ink/paper): ");
                    String item = sc.nextLine().trim();
                    Double qty = readAmount(sc, "Quantity: ");
                    if (qty != null) {
                        tech.performMaintenance(new Replenishment("SA-" + System.nanoTime(), item, qty));
                    }
                    break;
                }
                case "2": {
                    System.out.print("Upgrade type (hardware/firmware/software): ");
                    String upType = sc.nextLine().trim();
                    tech.performMaintenance(new Upgrade("SA-" + System.nanoTime(), upType));
                    break;
                }
                case "3": {
                    System.out.print("Mode (remote/on-site): ");
                    String mode = sc.nextLine().trim();
                    tech.performMaintenance(new Diagnostic("SA-" + System.nanoTime(), mode));
                    break;
                }
                case "4": {
                    System.out.print("Describe issue: ");
                    String issue = sc.nextLine().trim();
                    tech.performRepair(issue);
                    break;
                }
                case "5":
                    active = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
