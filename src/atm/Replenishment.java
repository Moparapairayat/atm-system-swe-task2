package atm;

/** <<control>> Replenishment extends Maintenance */
public class Replenishment extends Maintenance {
    private String itemType;
    private double quantity;

    public Replenishment(String activityID, String itemType, double quantity) {
        super(activityID);
        this.itemType = itemType;
        this.quantity = quantity;
    }

    @Override
    public boolean execute() {
        System.out.println("Replenished " + quantity + " unit(s) of " + itemType + ".");
        return true;
    }
}
