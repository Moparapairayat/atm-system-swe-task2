# BITHM National Bank ATM Simulator

A Java Swing desktop application that turns an ATM UML class model into an interactive, realistic ATM simulation. The project includes customer banking transactions, technician maintenance functions, physical-style card/cash/receipt animations, and ATM-like sound feedback.

Built for the OTHM Level 4 & 5 in IT Software Engineering coursework (Task 2, Part 1–a).

## Highlights

- Realistic ATM kiosk interface with card reader, cash dispenser, receipt printer, and keypad
- Animated card insertion/ejection, cash collection, deposit intake, and receipt printing
- Customer authentication with a four-digit PIN and three-attempt lockout
- Balance inquiry, cash withdrawal, cash deposit, fund transfer, and mini statement
- ATM cash availability validation before a withdrawal is completed
- Technician service console for replenishment, upgrade, diagnostic, and repair activities
- Automatic ATM-style audio feedback for keypad, card reader, cash dispenser, receipt printer, and errors

## UML model implementation

The application follows the Boundary–Control–Entity UML design.

| Layer | Classes |
|---|---|
| Boundary | `ATM` |
| Control | `Bank`, `Transaction`, `Withdrawal`, `Deposit`, `Transfer`, `BalanceInquiry`, `ServiceActivity`, `Maintenance`, `Replenishment`, `Upgrade`, `Diagnostic`, `Repair` |
| Entity | `Card`, `Customer`, `Account`, `ATMTechnician` |
| User interface | `ATMGui`, `SoundEffects`, `Main` |

## Demo accounts

On the welcome screen, choose a demo card before inserting it.

| Customer | Card number | PIN | Accounts |
|---|---|---|---|
| Ziana Mehnaz Ruhee | `1111-2222-3333-4444` | `1234` | `ACC-1001` savings, `ACC-1002` current |
| Mopara Pair Ayat | `5555-6666-7777-8888` | `4321` | `ACC-2001` savings |

Technician access is available from the welcome screen. The demo technician is **Mopara Pair Ayat**.

## Run the application

### Requirements

- JDK 11 or later
- Windows, macOS, or Linux desktop environment with Java Swing support

### Compile and start

```powershell
javac -d bin src/atm/*.java
java -cp bin atm.Main
```

The `bin` directory is generated during compilation and is intentionally excluded from Git.

## Customer journey

```text
Choose demo card → Insert card → Enter PIN → Select transaction
→ Process transaction → Collect cash or confirm deposit
→ Optional receipt → Take card
```

## Project structure

```text
src/
└── atm/
    ├── Main.java               Application entry point and demo data
    ├── ATMGui.java             Swing ATM kiosk interface and animations
    ├── SoundEffects.java       Generated ATM-style sound effects
    ├── ATM.java                ATM boundary model
    ├── Bank.java               Bank transaction control
    ├── Transaction.java        Abstract transaction model
    ├── Withdrawal.java
    ├── Deposit.java
    ├── Transfer.java
    ├── BalanceInquiry.java
    ├── Card.java
    ├── Customer.java
    ├── Account.java
    ├── ATMTechnician.java
    ├── ServiceActivity.java
    ├── Maintenance.java
    ├── Replenishment.java
    ├── Upgrade.java
    ├── Diagnostic.java
    └── Repair.java
```

## Validation completed

- Successful and unsuccessful PIN authentication, including three-attempt lockout
- Balance inquiry, deposit, withdrawal, and transfer flows
- Invalid amounts, insufficient account funds, and insufficient ATM cash
- Cash/receipt/card animation flows
- Technician maintenance and repair actions
- Full project compilation with `javac`

## Scope note

This is a software simulation for academic use. Card readers, cash dispensers, receipts, and sounds are visual/audio simulations; no physical banking hardware or live banking system is connected.

## Author

- Student: Mopara Pair Ayat
- Instructor: Ziana Mehnaz Ruhee
