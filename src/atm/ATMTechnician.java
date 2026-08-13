package atm;

/**
 * <<entity>> ATMTechnician
 * performs (1 - 0..*) ServiceActivity
 */
public class ATMTechnician {
    private String technicianID;
    private String name;

    public ATMTechnician(String technicianID, String name) {
        this.technicianID = technicianID;
        this.name = name;
    }

    /** + performMaintenance(a: ServiceActivity) : boolean */
    public boolean performMaintenance(ServiceActivity activity) {
        System.out.println("[Technician:" + name + "] Performing maintenance...");
        return activity.execute();
    }

    /** + performRepair(issue: String) : boolean */
    public boolean performRepair(String issue) {
        Repair repair = new Repair("REP-" + System.currentTimeMillis(), issue);
        System.out.println("[Technician:" + name + "] Performing repair...");
        return repair.execute();
    }

    public String getName() {
        return name;
    }

    public String getTechnicianID() {
        return technicianID;
    }
}
