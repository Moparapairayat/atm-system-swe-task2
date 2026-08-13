package atm;

/**
 * <<boundary>> ATM
 * connects to (* - 1) Bank, validates (dependency) Card
 */
public class ATM {
    private String atmID;
    private String location;
    private double cashBalance;
    private Bank bank;

    public ATM(String atmID, String location, double cashBalance, Bank bank) {
        this.atmID = atmID;
        this.location = location;
        this.cashBalance = cashBalance;
        this.bank = bank;
    }

    /** + insertCard(card: Card) : boolean  (also validates the PIN, ATM ..> Card) */
    public boolean insertCard(Card card, String pin) {
        return card.validate(pin);
    }

    /** + displayMenu() : void */
    public void displayMenu() {
        System.out.println("\n===== " + atmID + " (" + location + ") =====");
        System.out.println("1) Check Balance");
        System.out.println("2) Deposit");
        System.out.println("3) Withdraw");
        System.out.println("4) Transfer");
        System.out.println("5) Exit / Eject Card");
    }

    /** + dispenseCash(amount: double) : boolean */
    public boolean dispenseCash(double amount) {
        if (!canDispenseCash(amount)) {
            System.out.println("[ATM] Insufficient cash in machine.");
            return false;
        }
        cashBalance -= amount;
        return true;
    }

    /** Checks availability before a withdrawal changes either account or ATM cash. */
    public boolean canDispenseCash(double amount) {
        return amount > 0 && amount <= cashBalance;
    }

    public Bank getBank() {
        return bank;
    }

    public String getAtmID() {
        return atmID;
    }
}
