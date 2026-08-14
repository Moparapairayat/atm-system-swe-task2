# ATM Simulator — Evidence Guide

This folder contains evidence for **Task 2, Part 1 (Activity c)**. Use original screenshots from the running application; do not edit or recreate the application UI in image software.

## Required screenshots

| File name | Capture point | What it demonstrates |
| :--- | :--- | :--- |
| `01-welcome-screen.png` | Application launch | BITHM ATM kiosk UI and student developer footer |
| `02-card-selection-and-demo-pin.png` | Select **Insert Card** | Both demo customer cards and their test PIN hints |
| `03-pin-verification.png` | Insert either card | Secure PIN entry screen and masked card number |
| `04-account-selection.png` | Enter Ayat's PIN (`4321`) | Access to both `ACC-1001` and `ACC-1002` |
| `05-transaction-menu.png` | Select an account | Customer transaction options |
| `06-cash-withdrawal.png` | Complete a valid withdrawal | Cash dispenser animation and collection flow |
| `07-deposit-flow.png` | Complete a deposit | Deposit intake and validation flow |
| `08-mini-statement.png` | Select **Mini Statement** | Account number, transaction history, and balance |
| `09-technician-console.png` | Select **Technician Access** | Maintenance, upgrade, diagnostic, and repair controls |
| `10-console-build-output.png` | Terminal execution | Successful Java compilation (`javac`) and application launch proof |

## Build evidence

`10-console-build-output.txt` and `10-console-build-output.png` record the successful Java compilation command and execution log. To repeat the run for screenshots:

```powershell
javac -d bin src/atm/*.java
java -cp bin atm.Main
```

## Demo credentials

| Customer | Card | PIN | Account selection |
| :--- | :--- | :---: | :--- |
| Ziana Mehnaz Ruhee | `1111-2222-3333-4444` | `1234` | `ACC-1001`, `ACC-1002` |
| Mopara Pair Ayat | `5555-6666-7777-8888` | `4321` | `ACC-2001` |

