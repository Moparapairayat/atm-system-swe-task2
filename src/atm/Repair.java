package atm;

/** <<control>> Repair extends ServiceActivity */
public class Repair extends ServiceActivity {
    private String issueDescription;

    public Repair(String activityID, String issueDescription) {
        super(activityID);
        this.issueDescription = issueDescription;
    }

    @Override
    public boolean execute() {
        System.out.println("Repair completed for issue: " + issueDescription);
        return true;
    }
}
