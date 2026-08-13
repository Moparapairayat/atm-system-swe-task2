package atm;

import java.time.LocalDate;

/**
 * <<control>> ServiceActivity (abstract)
 * performed on (1 - 0..*) ATM
 */
public abstract class ServiceActivity {
    protected String activityID;
    protected LocalDate date;

    public ServiceActivity(String activityID) {
        this.activityID = activityID;
        this.date = LocalDate.now();
    }

    /** + execute() : boolean  {abstract} */
    public abstract boolean execute();

    public String getActivityID() {
        return activityID;
    }
}
