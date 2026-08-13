package atm;

/** <<control>> Diagnostic extends Maintenance */
public class Diagnostic extends Maintenance {
    private String mode;

    public Diagnostic(String activityID, String mode) {
        super(activityID);
        this.mode = mode;
    }

    @Override
    public boolean execute() {
        System.out.println("Diagnostic run in " + mode + " mode: all systems OK.");
        return true;
    }
}
