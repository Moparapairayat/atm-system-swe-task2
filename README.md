# ATM System — Realistic ATM Simulator (Task 2, Part 1 — Activity C)

Java Swing desktop application implementing the class model designed in
Activities A and B (see the accompanying `.drawio` UML diagrams). It presents
the project as a realistic ATM kiosk with a card slot, secure keypad, customer
transaction screens, and a technician service panel.

## Classes implemented
Card, Customer, Account, Bank, ATM,
Transaction (abstract) -> Withdrawal, Deposit, Transfer, BalanceInquiry,
ATMTechnician,
ServiceActivity (abstract) -> Maintenance (abstract) -> Replenishment, Upgrade, Diagnostic
                            -> Repair

## How to compile and run
```
javac -d bin src/atm/*.java
java -cp bin atm.Main
```
(Requires a JDK, version 11 or later. If only a JRE is installed, install a JDK first.)

## Demo login details (seeded sample data)
| Customer | Card number           | PIN  | Accounts                     |
|----------|------------------------|------|-------------------------------|
| Ziana Mehnaz Ruhee  | 1111-2222-3333-4444 | 1234 | ACC-1001 (savings), ACC-1002 (current) |
| Mopara Pair Ayat    | 5555-6666-7777-8888 | 4321 | ACC-2001 (savings) |

Technician access has no PIN in this demo. The technician is **Mopara Pair Ayat**.

## Visual ATM experience
- Screen-synchronised card insertion/ejection, cash dispensing/collection, and printed receipt simulation
- Guided physical flow: processing screen → take cash → receipt selection → take receipt → take card
- Card insertion and PIN verification, including a three-attempt PIN lockout
- ATM-style transaction menu: balance inquiry, withdrawal, deposit, transfer, mini statement, and card eject
- Quick withdrawal amounts and custom amount entry, followed by confirmation screens
- Technician service mode: replenish cash, upgrade system, run diagnostics, and repair ATM

## What has been tested
- Successful login, wrong-PIN retry (up to 3 attempts), and lockout after 3 failed attempts
- Check balance, deposit, withdraw, transfer between two different customers' accounts
- Withdrawal with insufficient funds (rejected, error message, no crash)
- Non-numeric amount entry (rejected with a friendly message, no crash)
- Unrecognised card number (rejected)
- All four technician service activities: Replenishment, Upgrade, Diagnostic, Repair
