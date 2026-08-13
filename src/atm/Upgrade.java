package atm;

/** <<control>> Upgrade extends Maintenance */
public class Upgrade extends Maintenance {
    private String upgradeType;

    public Upgrade(String activityID, String upgradeType) {
        super(activityID);
        this.upgradeType = upgradeType;
    }

    @Override
    public boolean execute() {
        System.out.println("Upgrade completed: " + upgradeType + ".");
        return true;
    }
}
