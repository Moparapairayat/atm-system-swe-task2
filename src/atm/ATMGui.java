package atm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/** A visual ATM kiosk interface that reuses the project's existing domain classes. */
public class ATMGui {
    private static final Color SCREEN = new Color(232, 243, 246);
    private static final Color BLUE = new Color(20, 103, 166);
    private static final Color TEAL = new Color(0, 143, 136);
    private static final Color INK = new Color(20, 42, 59);
    private static final Font BODY = new Font("Segoe UI", Font.PLAIN, 16);

    private final ATM atm;
    private final Bank bank;
    private final Customer[] customers;
    private final ATMTechnician technician;
    private final JFrame frame = new JFrame("BITHM National Bank | ATM Simulator");
    private final CardLayout pages = new CardLayout();
    private final JPanel screen = new JPanel(pages);
    private final JLabel customerLabel = new JLabel("READY", SwingConstants.CENTER);
    private final PortAnimation cardPort = new PortAnimation("CARD READER", new Color(32, 123, 184), "CARD");
    private final PortAnimation cashPort = new PortAnimation("CASH DISPENSER", new Color(45, 174, 135), "CASH");
    private final PortAnimation receiptPort = new PortAnimation("RECEIPT PRINTER", new Color(231, 201, 101), "RECEIPT");
    private final JLabel collectionHeading = new JLabel("", SwingConstants.CENTER);
    private final JLabel processingHeading = new JLabel("", SwingConstants.CENTER);
    private final JLabel processingDetail = new JLabel("", SwingConstants.CENTER);
    private final JLabel receiptHeading = new JLabel("", SwingConstants.CENTER);
    private final JLabel balanceValue = new JLabel("", SwingConstants.CENTER);
    private final JLabel detectedCardLabel = new JLabel("", SwingConstants.CENTER);
    private final JTextArea statementContent = new JTextArea();
    private JPasswordField activePinField;
    private Customer activeCustomer;
    private Customer pendingCustomer;
    private Account activeAccount;
    private int pinAttempts;
    private final NumberFormat money = NumberFormat.getCurrencyInstance(Locale.US);

    public ATMGui(ATM atm, Bank bank, Customer[] customers, ATMTechnician technician) {
        this.atm = atm;
        this.bank = bank;
        this.customers = customers;
        this.technician = technician;
        buildWindow();
    }

    public void show() { frame.setVisible(true); }

    private void buildWindow() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 850));
        frame.setSize(1180, 900);
        frame.setLocationRelativeTo(null);
        JPanel background = new JPanel(new GridLayout(1, 1));
        background.setBackground(new Color(18, 27, 37));
        background.setBorder(new EmptyBorder(15, 45, 15, 45));
        background.add(machine());
        frame.setContentPane(background);
    }

    private JPanel machine() {
        JPanel machine = new JPanel(new BorderLayout(16, 10));
        machine.setBackground(new Color(48, 57, 64));
        machine.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(143, 157, 164), 3), new EmptyBorder(10, 25, 12, 25)));
        machine.add(fascia(), BorderLayout.NORTH);
        machine.add(leftPanel(), BorderLayout.WEST);
        screen.setBackground(SCREEN);
        screen.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(10, 15, 19), 11), BorderFactory.createLineBorder(new Color(80, 95, 102), 2)), new EmptyBorder(20, 35, 20, 35)));
        addPages();
        machine.add(screen, BorderLayout.CENTER);
        machine.add(keypad(), BorderLayout.EAST);
        machine.add(bottomBay(), BorderLayout.SOUTH);
        return machine;
    }

    private JPanel fascia() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(new Color(8, 55, 94)); p.setBorder(new EmptyBorder(11, 22, 11, 22));
        JLabel brand = new JLabel("BITHM NATIONAL BANK"); brand.setForeground(Color.WHITE); brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel availability = new JLabel("●  24/7 ATM  •  SECURE BANKING"); availability.setForeground(new Color(116, 235, 175)); availability.setFont(new Font("Segoe UI", Font.BOLD, 11));
        p.add(brand, BorderLayout.WEST); p.add(availability, BorderLayout.EAST); return p;
    }

    private JPanel bottomBay() {
        JPanel p = new JPanel(new BorderLayout(20, 0)); p.setBackground(new Color(38, 45, 50)); p.setBorder(new EmptyBorder(6, 175, 0, 175));
        JPanel cash = hardwareBay("CASH DISPENSER", new Color(45, 174, 135), cashPort, 280);
        JPanel receipt = hardwareBay("RECEIPT PRINTER", new Color(231, 201, 101), receiptPort, 120);
        p.add(cash, BorderLayout.CENTER); p.add(receipt, BorderLayout.EAST); return p;
    }

    private JPanel hardwareBay(String name, Color light, PortAnimation port, int width) {
        JPanel p = column(); p.setBackground(new Color(24, 29, 33)); p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(105, 118, 123)), new EmptyBorder(5, 12, 7, 12)));
        p.add(label(name + "   ●", 9, light, SwingConstants.CENTER));
        int portHeight = name.startsWith("CASH") ? 110 : 120;
        port.setPreferredSize(new Dimension(width, portHeight)); port.setMaximumSize(new Dimension(width, portHeight)); port.setAlignmentX(Component.CENTER_ALIGNMENT); p.add(port); return p;
    }

    private JPanel leftPanel() {
        JPanel left = column();
        left.setPreferredSize(new Dimension(165, 0));
        left.setBackground(new Color(43, 50, 56));
        JLabel brand = label("CARD ACCESS", 14, Color.WHITE, SwingConstants.CENTER);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(brand);
        left.add(Box.createVerticalStrut(8));
        JLabel light = label("◒  CONTACTLESS READY", 10, new Color(91, 221, 144), SwingConstants.CENTER);
        light.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(light);
        left.add(Box.createVerticalGlue());
        cardPort.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(cardPort);
        // Status is painted inside the card-reader frame so it never overflows its border.
        return left;
    }

    private JPanel keypad() {
        JPanel holder = column();
        holder.setPreferredSize(new Dimension(170, 0));
        holder.setBackground(new Color(43, 50, 56));
        JLabel title = label("SECURE KEYPAD • ⠿", 12, new Color(210, 220, 225), SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        holder.add(title);
        holder.add(Box.createVerticalStrut(14));
        JPanel keys = new JPanel(new GridLayout(4, 3, 7, 7));
        keys.setOpaque(false);
        for (String value : new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "ENTER"}) {
            JButton key = new JButton(value);
            key.setFont(new Font("Segoe UI", Font.BOLD, value.equals("ENTER") ? 10 : 16));
            key.setForeground(Color.WHITE);
            key.setBackground(value.equals("C") ? new Color(180, 72, 67) : value.equals("ENTER") ? TEAL : new Color(82, 91, 98));
            key.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(171, 186, 194)), new EmptyBorder(5, 5, 5, 5)));
            key.setFocusPainted(false);
            key.addActionListener(e -> handleKey(value));
            keys.add(key);
        }
        holder.add(keys);
        holder.add(Box.createVerticalGlue());
        holder.add(label("●  HELP / ACCESSIBILITY", 9, new Color(183, 195, 202), SwingConstants.CENTER));
        return holder;
    }

    private void addPages() {
        screen.add(welcomePage(), "welcome");
        screen.add(loginPage(), "login");
        screen.add(cardSelectionPage(), "cardSelection");
        screen.add(menuPage(), "menu");
        screen.add(amountPage("withdraw"), "withdraw");
        screen.add(amountPage("deposit"), "deposit");
        screen.add(transferPage(), "transfer");
        screen.add(technicianPage(), "technician");
        screen.add(collectionPage(), "collection");
        screen.add(processingPage(), "processing");
        screen.add(receiptChoicePage(), "receiptChoice");
        screen.add(receiptCollectionPage(), "receiptCollection");
        screen.add(cardCollectionPage(), "cardCollection");
        screen.add(balancePage(), "balance");
        screen.add(statementPage(), "statement");
        screen.add(messagePage(), "message");
        pages.show(screen, "welcome");
    }

    private JPanel welcomePage() {
        JPanel p = centered();
        p.add(label("WELCOME", 34, INK, SwingConstants.CENTER));
        p.add(label("BITHM National Bank", 20, BLUE, SwingConstants.CENTER));
        p.add(Box.createVerticalStrut(26));
        p.add(label("Please insert your card to begin", 18, INK, SwingConstants.CENTER));
        p.add(Box.createVerticalStrut(25));
        p.add(action("INSERT CARD", BLUE, e -> showPage("cardSelection")));
        p.add(Box.createVerticalStrut(12));
        p.add(action("TECHNICIAN ACCESS", new Color(83, 91, 98), e -> showPage("technician")));
        return p;
    }

    private JPanel loginPage() {
        JPanel p = centered();
        JPanel header = new JPanel(new BorderLayout()); header.setMaximumSize(new Dimension(580, 42)); header.setOpaque(false);
        JLabel bankName = new JLabel("BITHM NATIONAL BANK"); bankName.setForeground(BLUE); bankName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel secure = new JLabel("●  ATM-001  •  SECURE"); secure.setForeground(TEAL); secure.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.add(bankName, BorderLayout.WEST); header.add(secure, BorderLayout.EAST); p.add(header);
        p.add(Box.createVerticalStrut(22));
        JPanel cardPanel = column(); cardPanel.setBackground(Color.WHITE); cardPanel.setMaximumSize(new Dimension(430, 290)); cardPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(189, 207, 215)), new EmptyBorder(22, 38, 20, 38)));
        cardPanel.add(label("ENTER YOUR PIN", 25, INK, SwingConstants.CENTER)); cardPanel.add(Box.createVerticalStrut(8));
        detectedCardLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); detectedCardLabel.setForeground(new Color(70, 91, 105)); detectedCardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(detectedCardLabel);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(label("Enter your 4-digit PIN", 13, INK, SwingConstants.CENTER)); cardPanel.add(Box.createVerticalStrut(7));
        JPasswordField pin = (JPasswordField) field("PIN", true);
        activePinField = pin;
        cardPanel.add(pin); cardPanel.add(Box.createVerticalStrut(9));
        cardPanel.add(label("🔒  Shield your PIN while entering it", 11, new Color(88, 112, 125), SwingConstants.CENTER));
        p.add(cardPanel); p.add(Box.createVerticalStrut(18));
        p.add(action("CONTINUE", BLUE, e -> authenticatePending(new String(pin.getPassword()))));
        p.add(Box.createVerticalStrut(10));
        p.add(action("CANCEL / EJECT CARD", new Color(90, 102, 110), e -> ejectCard()));
        p.add(Box.createVerticalGlue());
        p.add(label("🔒  Please do not remove your card until instructed", 11, new Color(88, 112, 125), SwingConstants.CENTER));
        return p;
    }

    private JPanel cardSelectionPage() {
        JPanel p = centered(); addAtmHeader(p, "INSERT DEMO CARD", "Choose the customer card to insert into this simulator."); p.add(Box.createVerticalStrut(28));
        p.add(action("ZIANA MEHNAZ RUHEE  •  •••• 4444", BLUE, e -> beginCardInsertion(customers[0]))); p.add(Box.createVerticalStrut(12));
        p.add(action("MOPARA PAIR AYAT  •  •••• 8888", TEAL, e -> beginCardInsertion(customers[1]))); p.add(Box.createVerticalStrut(12));
        p.add(action("BACK", new Color(90, 102, 110), e -> showPage("welcome"))); p.add(Box.createVerticalGlue()); addSecureFooter(p); return p;
    }

    private JPanel menuPage() {
        JPanel p = centered();
        addAtmHeader(p, "SELECT A TRANSACTION", "Welcome. Choose a service below.");
        p.add(Box.createVerticalStrut(18));
        JPanel grid = new JPanel(new GridLayout(3, 2, 14, 14)); grid.setOpaque(false); grid.setMaximumSize(new Dimension(540, 280));
        grid.add(action("BALANCE INQUIRY", BLUE, e -> showBalance()));
        grid.add(action("CASH WITHDRAWAL", BLUE, e -> showPage("withdraw")));
        grid.add(action("CASH DEPOSIT", TEAL, e -> showPage("deposit")));
        grid.add(action("FUND TRANSFER", TEAL, e -> showPage("transfer")));
        grid.add(action("MINI STATEMENT", new Color(77, 101, 118), e -> miniStatement()));
        grid.add(action("EJECT CARD", new Color(166, 76, 69), e -> ejectCard()));
        p.add(grid);
        p.add(Box.createVerticalGlue()); addSecureFooter(p);
        return p;
    }

    private JPanel amountPage(String type) {
        boolean withdrawal = type.equals("withdraw");
        JPanel p = centered();
        addAtmHeader(p, withdrawal ? "SELECT WITHDRAWAL AMOUNT" : "CASH DEPOSIT", withdrawal ? "Select a quick amount or enter another amount." : "Enter the cash amount to deposit.");
        p.add(Box.createVerticalStrut(20));
        JPanel values = new JPanel(new GridLayout(2, 2, 10, 10)); values.setOpaque(false);
        for (int value : new int[]{500, 1000, 2000, 5000}) values.add(action(money.format(value), BLUE, e -> transact(type, value)));
        p.add(values); p.add(Box.createVerticalStrut(14));
        JTextField other = field("Other amount", false); p.add(other); p.add(Box.createVerticalStrut(10));
        p.add(action(withdrawal ? "CONFIRM AMOUNT" : "OPEN DEPOSIT SLOT", TEAL, e -> {
            try { transact(type, Double.parseDouble(other.getText().trim())); }
            catch (NumberFormatException ex) { dialog("Enter a valid numeric amount."); }
        }));
        p.add(Box.createVerticalStrut(8)); p.add(action("BACK", new Color(90, 102, 110), e -> showPage("menu")));
        p.add(Box.createVerticalGlue()); addSecureFooter(p);
        return p;
    }

    private JPanel transferPage() {
        JPanel p = centered();
        addAtmHeader(p, "FUND TRANSFER", "Transfer securely to another BITHM account.");
        p.add(Box.createVerticalStrut(20));
        JTextField destination = field("Destination account number", false);
        JTextField amount = field("Amount", false);
        p.add(destination); p.add(Box.createVerticalStrut(10)); p.add(amount); p.add(Box.createVerticalStrut(14));
        p.add(action("TRANSFER NOW", TEAL, e -> transfer(destination.getText().trim(), amount.getText().trim())));
        p.add(Box.createVerticalStrut(8)); p.add(action("BACK", new Color(90, 102, 110), e -> showPage("menu")));
        p.add(Box.createVerticalGlue()); addSecureFooter(p);
        return p;
    }

    private JPanel technicianPage() {
        JPanel p = centered();
        addAtmHeader(p, "ATM SERVICE CONSOLE", "Technician: " + technician.getName());
        p.add(Box.createVerticalStrut(20));
        JPanel health = new JPanel(new GridLayout(1, 3, 8, 8)); health.setOpaque(false); health.setMaximumSize(new Dimension(540, 56));
        health.add(statusTile("CASH LEVEL", "READY", TEAL)); health.add(statusTile("NETWORK", "ONLINE", TEAL)); health.add(statusTile("PRINTER", "READY", TEAL)); p.add(health); p.add(Box.createVerticalStrut(12));
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12)); grid.setOpaque(false);
        for (String service : new String[]{"REPLENISH CASH", "UPGRADE SYSTEM", "RUN DIAGNOSTIC", "REPAIR ATM"})
            grid.add(action(service, service.contains("REPAIR") ? new Color(166, 76, 69) : BLUE, e -> runService(service)));
        p.add(grid); p.add(Box.createVerticalStrut(16));
        p.add(action("EXIT SERVICE MODE", new Color(90, 102, 110), e -> showPage("welcome")));
        return p;
    }

    private JPanel collectionPage() {
        JPanel p = centered();
        collectionHeading.setFont(new Font("Segoe UI", Font.BOLD, 27));
        collectionHeading.setForeground(INK);
        collectionHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(collectionHeading);
        p.add(Box.createVerticalStrut(14));
        p.add(label("The dispenser light is on.\nPlease take your cash before continuing.", 16, new Color(70, 91, 105), SwingConstants.CENTER));
        p.add(Box.createVerticalStrut(28));
        p.add(action("COLLECT CASH", TEAL, e -> {
            cashPort.collect(() -> offerReceipt());
        }));
        return p;
    }

    private JPanel processingPage() {
        JPanel p = centered();
        processingHeading.setFont(new Font("Segoe UI", Font.BOLD, 27)); processingHeading.setForeground(INK); processingHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        processingDetail.setFont(new Font("Segoe UI", Font.PLAIN, 17)); processingDetail.setForeground(new Color(70, 91, 105)); processingDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(processingHeading); p.add(Box.createVerticalStrut(20)); p.add(processingDetail); p.add(Box.createVerticalStrut(30));
        p.add(label("●   ●   ●", 26, TEAL, SwingConstants.CENTER));
        return p;
    }

    private JPanel receiptChoicePage() {
        JPanel p = centered();
        p.add(label("TRANSACTION COMPLETE", 27, INK, SwingConstants.CENTER)); p.add(Box.createVerticalStrut(12));
        p.add(label("Would you like a printed receipt?", 17, new Color(70, 91, 105), SwingConstants.CENTER)); p.add(Box.createVerticalStrut(28));
        p.add(action("YES, PRINT RECEIPT", TEAL, e -> printReceipt())); p.add(Box.createVerticalStrut(10));
        p.add(action("NO RECEIPT", new Color(90, 102, 110), e -> startEjection()));
        return p;
    }

    private JPanel receiptCollectionPage() {
        JPanel p = centered(); receiptHeading.setFont(new Font("Segoe UI", Font.BOLD, 27)); receiptHeading.setForeground(INK); receiptHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(receiptHeading); p.add(Box.createVerticalStrut(14));
        p.add(label("Your receipt is ready in the printer slot.", 16, new Color(70, 91, 105), SwingConstants.CENTER)); p.add(Box.createVerticalStrut(28));
        p.add(action("TAKE RECEIPT", TEAL, e -> receiptPort.collect(this::startEjection)));
        return p;
    }

    private JPanel cardCollectionPage() {
        JPanel p = centered(); p.add(label("PLEASE TAKE YOUR CARD", 27, INK, SwingConstants.CENTER)); p.add(Box.createVerticalStrut(14));
        p.add(label("Your card has been ejected from the card reader.", 16, new Color(70, 91, 105), SwingConstants.CENTER)); p.add(Box.createVerticalStrut(28));
        p.add(action("TAKE CARD", TEAL, e -> finishSession()));
        return p;
    }

    private JPanel balancePage() {
        JPanel p = centered(); addAtmHeader(p, "BALANCE INQUIRY", "Your available balance is shown below."); p.add(Box.createVerticalStrut(28));
        JPanel card = column(); card.setBackground(Color.WHITE); card.setMaximumSize(new Dimension(430, 160)); card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(189, 207, 215)), new EmptyBorder(24, 30, 24, 30)));
        card.add(label("AVAILABLE BALANCE", 13, new Color(70, 91, 105), SwingConstants.CENTER)); card.add(Box.createVerticalStrut(10));
        balanceValue.setFont(new Font("Segoe UI", Font.BOLD, 32)); balanceValue.setForeground(TEAL); balanceValue.setAlignmentX(Component.CENTER_ALIGNMENT); card.add(balanceValue);
        p.add(card); p.add(Box.createVerticalStrut(22)); p.add(action("BACK TO MENU", BLUE, e -> showPage("menu"))); p.add(Box.createVerticalGlue()); addSecureFooter(p); return p;
    }

    private JPanel statementPage() {
        JPanel p = centered(); addAtmHeader(p, "MINI STATEMENT", "Your recent account activity."); p.add(Box.createVerticalStrut(16));
        JPanel paper = new JPanel(new BorderLayout()); paper.setBackground(Color.WHITE); paper.setMaximumSize(new Dimension(500, 285)); paper.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(189, 207, 215)), new EmptyBorder(14, 20, 14, 20)));
        statementContent.setEditable(false); statementContent.setBackground(Color.WHITE); statementContent.setForeground(INK); statementContent.setFont(new Font("Monospaced", Font.PLAIN, 12)); statementContent.setLineWrap(false); statementContent.setBorder(null);
        paper.add(statementContent, BorderLayout.CENTER); p.add(paper); p.add(Box.createVerticalStrut(16));
        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0)); actions.setOpaque(false); actions.setMaximumSize(new Dimension(410, 45));
        actions.add(action("PRINT STATEMENT", TEAL, e -> receiptPort.dispense(() -> dialog("Mini statement printed. Please take it from the receipt printer."))));
        actions.add(action("BACK TO MENU", BLUE, e -> showPage("menu"))); p.add(actions); p.add(Box.createVerticalGlue()); addSecureFooter(p); return p;
    }

    private JPanel messagePage() { return centered(); }

    private void authenticatePending(String pin) {
        if (pendingCustomer == null) { dialog("No card is currently detected."); showPage("welcome"); return; }
        Customer c = pendingCustomer;
        if (atm.insertCard(c.getCard(), pin)) {
            SoundEffects.accepted();
            activeCustomer = c; activeAccount = c.getAccounts().get(0); pinAttempts = 0;
            customerLabel.setText("CARD ACTIVE"); cardPort.setStatus("CARD ACTIVE"); showPage("menu"); return;
        }
        pinAttempts++; SoundEffects.warning();
        dialog(pinAttempts >= 3 ? "Card retained after 3 incorrect PIN attempts." : "Incorrect PIN. Attempts remaining: " + (3 - pinAttempts));
        if (pinAttempts >= 3) { pendingCustomer = null; cardPort.removeItem(); cardPort.setStatus("READY"); showPage("welcome"); }
    }

    private void transact(String type, double amount) {
        if (amount <= 0) { dialog("Amount must be greater than zero."); return; }
        Transaction t = type.equals("withdraw") ? new Withdrawal(id(), amount, activeAccount) : new Deposit(id(), amount, activeAccount);
        if (!bank.verifyTransaction(t)) { dialog("Transaction rejected by bank."); return; }
        if (type.equals("withdraw") && !atm.canDispenseCash(amount)) { dialog("ATM cannot dispense this amount right now."); return; }
        boolean success = t.execute();
        if (success) {
            bank.processTransaction(t);
            if (type.equals("withdraw")) {
                atm.dispenseCash(amount);
                showProcessing("COUNTING NOTES", "Please wait while your cash is prepared…", () -> { SoundEffects.cashDispenser(); cashPort.dispense(amount, () -> {
                    collectionHeading.setText("PLEASE COLLECT YOUR CASH"); showPage("collection");
                }); });
            } else {
                showProcessing("OPENING DEPOSIT SLOT", "Place your cash in the illuminated deposit slot…", () ->
                        cashPort.acceptDeposit(amount, () -> showProcessing("COUNTING AND VALIDATING NOTES",
                                "Deposit accepted: " + money.format(amount) + "\nUpdated balance: " + money.format(activeAccount.checkBalance()), this::offerReceipt)));
            }
        }
        else dialog("Transaction could not be completed. Check your available balance.");
    }

    private void transfer(String destinationNumber, String rawAmount) {
        try {
            Account destination = null;
            for (Customer c : customers) for (Account a : c.getAccounts()) if (a.getAccountNumber().equals(destinationNumber)) destination = a;
            if (destination == null) { dialog("Destination account not found."); return; }
            double amount = Double.parseDouble(rawAmount); Transaction t = new Transfer(id(), amount, activeAccount, destination);
            if (amount <= 0 || !t.execute()) { dialog("Transfer failed. Check amount and balance."); return; }
            bank.processTransaction(t); confirmation("TRANSFER SUCCESSFUL", amount);
        } catch (NumberFormatException e) { dialog("Enter a valid numeric amount."); }
    }

    private void showBalance() { balanceValue.setText(money.format(activeAccount.checkBalance())); showPage("balance"); }
    private void miniStatement() {
        StringBuilder statement = new StringBuilder();
        statement.append("      BITHM NATIONAL BANK\n");
        statement.append("        MINI STATEMENT\n");
        statement.append("----------------------------------\n");
        statement.append("Account : ").append(activeAccount.getAccountNumber()).append("\n");
        statement.append("Card    : ").append(maskedCard(activeCustomer.getCard().getCardNumber())).append("\n");
        statement.append("----------------------------------\n");
        if (activeAccount.getHistory().isEmpty()) {
            statement.append("No recent transactions\n");
        } else {
            int start = Math.max(0, activeAccount.getHistory().size() - 5);
            for (int i = activeAccount.getHistory().size() - 1; i >= start; i--) {
                Transaction t = activeAccount.getHistory().get(i);
                String type = t.getClass().getSimpleName().replace("Inquiry", " Inquiry").toUpperCase();
                statement.append(String.format("%-19s %9.2f\n", type, t.getAmount()));
            }
        }
        statement.append("----------------------------------\n");
        statement.append(String.format("AVAILABLE BALANCE  %10.2f\n", activeAccount.checkBalance()));
        statement.append("Thank you for banking with us.");
        statementContent.setText(statement.toString());
        showPage("statement");
    }
    private void runService(String service) {
        boolean completed;
        switch (service) {
            case "REPLENISH CASH": completed = technician.performMaintenance(new Replenishment("SA-" + System.nanoTime(), "cash", 100)); break;
            case "UPGRADE SYSTEM": completed = technician.performMaintenance(new Upgrade("SA-" + System.nanoTime(), "firmware")); break;
            case "RUN DIAGNOSTIC": completed = technician.performMaintenance(new Diagnostic("SA-" + System.nanoTime(), "on-site")); break;
            case "REPAIR ATM": completed = technician.performRepair("Scheduled ATM service inspection"); break;
            default: completed = false;
        }
        dialog(service + (completed ? " completed successfully." : " could not be completed."));
    }
    private void ejectCard() { startEjection(); }
    private void confirmation(String heading, double amount) { dialog(heading + "\n\nAmount: " + money.format(amount) + "\nAvailable balance: " + money.format(activeAccount.checkBalance()) + "\n\nThank you for banking with us."); showPage("menu"); }
    private void offerReceipt() { showPage("receiptChoice"); }
    private void printReceipt() {
        showProcessing("PRINTING RECEIPT", "Your transaction details are being printed…", () -> { SoundEffects.receiptPrinter(); receiptPort.dispense(() -> {
            receiptHeading.setText("PLEASE TAKE YOUR RECEIPT"); showPage("receiptCollection");
        }); });
    }
    private void startEjection() {
        showProcessing("ENDING SESSION", "Your card is being returned…", () -> { SoundEffects.cardEject(); cardPort.eject(() -> showPage("cardCollection")); });
    }
    private void finishSession() {
        cardPort.removeItem(); activeCustomer = null; pendingCustomer = null; activeAccount = null; customerLabel.setText("READY"); cardPort.setStatus("READY"); showPage("welcome");
    }
    private void showProcessing(String heading, String detail, Runnable next) {
        processingHeading.setText(heading); processingDetail.setText(detail); showPage("processing");
        Timer wait = new Timer(1150, e -> next.run()); wait.setRepeats(false); wait.start();
    }
    private void beginCardInsertion(Customer customer) {
        customerLabel.setText("CARD DETECTED"); cardPort.setStatus("CARD DETECTED"); SoundEffects.cardReader();
        pendingCustomer = customer;
        detectedCardLabel.setText("Card detected:  " + maskedCard(customer.getCard().getCardNumber()));
        Timer detect = new Timer(400, e -> cardPort.insert(() ->
                showProcessing("READING CARD", "Please wait while your card is verified…", () -> showPage("login"))));
        detect.setRepeats(false); detect.start();
    }
    private void showPage(String name) { pages.show(screen, name); }
    private String id() { return "TXN-" + System.nanoTime(); }
    private String maskedCard(String cardNumber) { return "••••  ••••  ••••  " + cardNumber.substring(cardNumber.length() - 4); }
    private void dialog(String text) { JOptionPane.showMessageDialog(frame, text, "BITHM National Bank ATM", JOptionPane.INFORMATION_MESSAGE); }

    private JTextField field(String hint, boolean password) {
        JTextField f = password ? new JPasswordField() : new JTextField();
        f.setMaximumSize(new Dimension(350, 44)); f.setPreferredSize(new Dimension(350, 44)); f.setFont(BODY); f.setToolTipText(hint);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(142, 165, 177)), new EmptyBorder(8, 12, 8, 12)));
        return f;
    }
    private JButton action(String text, Color color, java.awt.event.ActionListener listener) {
        JButton b = new JButton(text); b.setFont(new Font("Segoe UI", Font.BOLD, 14)); b.setForeground(Color.WHITE); b.setBackground(color); b.setFocusPainted(false); b.setBorder(new EmptyBorder(12, 18, 12, 18)); b.setAlignmentX(Component.CENTER_ALIGNMENT); b.addActionListener(listener); return b;
    }
    private JPanel centered() { JPanel p = column(); p.setBackground(SCREEN); p.setBorder(new EmptyBorder(28, 80, 20, 80)); return p; }
    private JPanel column() { JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); return p; }
    private void addAtmHeader(JPanel parent, String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false); header.setMaximumSize(new Dimension(580, 42));
        JLabel bankLabel = new JLabel("BITHM NATIONAL BANK"); bankLabel.setFont(new Font("Segoe UI", Font.BOLD, 13)); bankLabel.setForeground(BLUE);
        JLabel statusLabel = new JLabel("●  ATM-001  •  ONLINE"); statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 10)); statusLabel.setForeground(TEAL);
        header.add(bankLabel, BorderLayout.WEST); header.add(statusLabel, BorderLayout.EAST); parent.add(header); parent.add(Box.createVerticalStrut(20));
        parent.add(label(title, 25, INK, SwingConstants.CENTER)); parent.add(Box.createVerticalStrut(7)); parent.add(label(subtitle, 14, new Color(70, 91, 105), SwingConstants.CENTER));
    }
    private void addSecureFooter(JPanel parent) { parent.add(label("🔒  Secure encrypted connection • Please keep your card inserted", 10, new Color(88, 112, 125), SwingConstants.CENTER)); }
    private JPanel statusTile(String title, String value, Color color) {
        JPanel tile = column(); tile.setBackground(Color.WHITE); tile.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(189, 207, 215)), new EmptyBorder(7, 8, 7, 8)));
        tile.add(label(title, 9, new Color(70, 91, 105), SwingConstants.CENTER)); tile.add(label("● " + value, 10, color, SwingConstants.CENTER)); return tile;
    }
    private JLabel label(String text, int size, Color color, int alignment) { JLabel l = new JLabel("<html>" + text.replace("\n", "<br>") + "</html>", alignment); l.setFont(new Font("Segoe UI", Font.BOLD, size)); l.setForeground(color); l.setAlignmentX(Component.CENTER_ALIGNMENT); return l; }
    private void handleKey(String value) {
        if (activePinField == null || !activePinField.isShowing()) return;
        SoundEffects.keypad();
        String current = new String(activePinField.getPassword());
        if (value.equals("C")) {
            activePinField.setText("");
        } else if (value.equals("ENTER")) {
            authenticatePending(current);
        } else if (current.length() < 4) {
            activePinField.setText(current + value);
        }
        activePinField.requestFocusInWindow();
    }

    /** Small painted hardware port used to make card, cash, and receipt actions visible. */
    private static class PortAnimation extends JPanel {
        private final String title;
        private final Color accent;
        private final String kind;
        private double progress = -1;
        private double shutterProgress;
        private double displayedAmount = 500;
        private int noteCount = 1;
        private boolean inserted;
        private String status = "READY";
        private Timer timer;

        PortAnimation(String title, Color accent, String kind) {
            this.title = title; this.accent = accent; this.kind = kind;
            int height = kind.equals("CARD") ? 210 : kind.equals("CASH") ? 110 : 120;
            setPreferredSize(new Dimension(132, height)); setMaximumSize(new Dimension(132, height));
            setOpaque(false);
        }

        void insert(Runnable done) { animate(0, 1, () -> { inserted = true; done.run(); }); }
        void eject(Runnable done) { animate(1, 0, () -> { inserted = true; done.run(); }); }
        void dispense(double amount, Runnable done) {
            displayedAmount = amount;
            noteCount = Math.max(1, Math.min(5, (int) Math.ceil(amount / 1000.0)));
            if (kind.equals("CASH")) animateShutter(0, 1, () -> animate(0, 1, done));
            else animate(0, 1, done);
        }
        void dispense(Runnable done) { dispense(500, done); }
        void acceptDeposit(double amount, Runnable done) {
            displayedAmount = amount;
            noteCount = Math.max(1, Math.min(5, (int) Math.ceil(amount / 1000.0)));
            if (kind.equals("CASH")) animateShutter(0, 1, () -> animate(1, 0, () ->
                    animateShutter(1, 0, () -> { progress = -1; repaint(); done.run(); })));
            else done.run();
        }
        void collect(Runnable done) {
            if (kind.equals("CASH")) animate(1, 0, () -> animateShutter(1, 0, () -> { progress = -1; repaint(); done.run(); }));
            else animate(1, 0, () -> { progress = -1; repaint(); done.run(); });
        }
        void removeItem() { inserted = false; progress = -1; repaint(); }
        void setStatus(String value) { status = value; repaint(); }

        private void animate(double from, double to, Runnable done) {
            if (timer != null && timer.isRunning()) timer.stop();
            progress = from;
            double step = kind.equals("CARD") ? .012 : .055;
            timer = new Timer(kind.equals("CARD") ? 20 : 18, null);
            timer.addActionListener(e -> {
                progress += to > from ? step : -step;
                boolean complete = to > from ? progress >= to : progress <= to;
                if (complete) { progress = to; timer.stop(); repaint(); done.run(); }
                else repaint();
            });
            timer.start();
        }

        private void animateShutter(double from, double to, Runnable done) {
            if (timer != null && timer.isRunning()) timer.stop();
            shutterProgress = from;
            timer = new Timer(20, null);
            timer.addActionListener(e -> {
                shutterProgress += to > from ? .035 : -.035;
                boolean complete = to > from ? shutterProgress >= to : shutterProgress <= to;
                if (complete) { shutterProgress = to; timer.stop(); repaint(); done.run(); }
                else repaint();
            });
            timer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int slotY = kind.equals("CARD") ? 92 : kind.equals("CASH") ? 44 : 38;
            int readerHeight = kind.equals("CARD") ? 108 : h - 31;
            g2.setPaint(new GradientPaint(0, 18, new Color(75, 85, 91), 0, 63, new Color(24, 29, 33)));
            g2.fillRoundRect(2, 18, w - 4, readerHeight, 7, 7);
            g2.setColor(new Color(145, 155, 160)); g2.drawRoundRect(2, 18, w - 5, readerHeight - 1, 7, 7);
            g2.setColor(new Color(134, 148, 156)); g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            int titleWidth = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (w - titleWidth) / 2, 12);
            if (kind.equals("CARD")) {
                g2.setColor(new Color(230, 240, 244)); g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                int statusWidth = g2.getFontMetrics().stringWidth(status);
                g2.drawString(status, (w - statusWidth) / 2, 79);
            }
            g2.setColor(progress >= 0 ? accent : new Color(75, 87, 93)); g2.fillOval(w - 17, 7, 7, 7);
            if (kind.equals("CARD")) {
                if (progress >= 0 || inserted) {
                    java.awt.Shape originalClip = g2.getClip();
                    g2.clipRect(0, slotY + 5, w, h - slotY - 5);
                    drawItem(g2, w);
                    g2.setClip(originalClip);
                }
                drawSlot(g2, w, slotY);
            } else if (kind.equals("CASH")) {
                if (progress >= 0) drawItem(g2, w);
                drawSlot(g2, w, slotY);
            } else {
                drawSlot(g2, w, slotY);
                if (progress >= 0) drawItem(g2, w);
            }
            g2.dispose();
        }

        private void drawSlot(Graphics2D g2, int width, int y) {
            g2.setColor(Color.BLACK); g2.fillRoundRect(16, y, width - 32, 12, 5, 5);
            g2.setColor(new Color(8, 12, 14)); g2.fillRoundRect(19, y + 3, width - 38, 5, 2, 2);
            g2.setColor(accent); g2.fillRoundRect(20, y + 4, width - 40, 2, 1, 1);
            if (kind.equals("CASH")) {
                int centre = width / 2;
                int halfGap = (int) (3 + shutterProgress * (width / 2 - 23));
                g2.setColor(new Color(96, 108, 113));
                g2.fillRoundRect(20, y + 2, centre - halfGap - 20, 7, 2, 2);
                g2.fillRoundRect(centre + halfGap, y + 2, width - 20 - (centre + halfGap), 7, 2, 2);
                g2.setColor(new Color(163, 174, 177));
                g2.drawLine(20, y + 3, centre - halfGap - 1, y + 3);
                g2.drawLine(centre + halfGap, y + 3, width - 20, y + 3);
            }
            g2.setColor(new Color(158, 175, 182)); g2.drawRoundRect(16, y, width - 33, 11, 5, 5);
        }

        private void drawItem(Graphics2D g2, int w) {
            if (kind.equals("CARD")) {
                double eased = Math.max(0, progress) * Math.max(0, progress) * (3 - 2 * Math.max(0, progress));
                int x = 37, y = (int) (112 - eased * 108), cardW = 58, cardH = 92;
                g2.setPaint(new GradientPaint(x, y, new Color(20, 113, 183), x + cardW, y + cardH, new Color(6, 42, 89)));
                g2.fillRoundRect(x, y, cardW, cardH, 8, 8);
                g2.setColor(new Color(154, 217, 239)); g2.drawRoundRect(x, y, cardW - 1, cardH - 1, 8, 8);
                g2.setColor(new Color(206, 233, 247)); g2.setFont(new Font("Segoe UI", Font.BOLD, 6)); g2.drawString("BITHM", x + 15, y + 12);
                g2.drawString("NATIONAL", x + 10, y + 19);
                g2.setColor(new Color(220, 185, 74)); g2.fillRoundRect(x + 18, y + 28, 22, 17, 3, 3);
                g2.setColor(new Color(173, 132, 42)); g2.drawLine(x + 18, y + 36, x + 40, y + 36);
                g2.drawLine(x + 29, y + 28, x + 29, y + 45);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Monospaced", Font.PLAIN, 6));
                g2.drawString("••••", x + 18, y + 58); g2.drawString("••••", x + 18, y + 66); g2.drawString("2222", x + 18, y + 74);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 5)); g2.drawString("M. P. AYAT", x + 10, y + 84);
            } else if (kind.equals("CASH")) {
                int y = (int) (23 + Math.max(0, progress) * 34), noteW = Math.min(w - 48, 150), x = (w - noteW) / 2;
                for (int i = noteCount - 1; i >= 0; i--) {
                    int offset = i * 3;
                    g2.setPaint(new GradientPaint(x, y - offset, new Color(209, 234, 183), x + noteW, y + 25 - offset, new Color(93, 156, 83)));
                    g2.fillRoundRect(x + offset, y - offset, noteW, 25, 3, 3);
                    g2.setColor(new Color(50, 112, 59)); g2.drawRoundRect(x + offset, y - offset, noteW - 1, 24, 3, 3);
                }
                g2.setColor(new Color(229, 246, 209)); g2.fillOval(w / 2 - 12, y + 3, 24, 17);
                g2.setColor(new Color(39, 104, 50)); g2.setFont(new Font("Segoe UI", Font.BOLD, 8)); g2.drawString(String.valueOf((int) displayedAmount), w / 2 - 13, y + 15);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 5)); g2.drawString("BNB • AUTHENTIC", x + 8, y + 22);
                g2.setColor(new Color(48, 104, 54)); g2.fillRect(x + noteW - 24, y + 3, 3, 18);
            } else {
                int y = (int) (24 + Math.max(0, progress) * 25), x = 28, paperW = w - 56;
                g2.setColor(new Color(252, 250, 239)); g2.fillRect(x, y, paperW, 48);
                g2.setColor(new Color(186, 183, 168)); g2.drawRect(x, y, paperW - 1, 47);
                g2.setColor(new Color(50, 50, 50)); g2.setFont(new Font("Monospaced", Font.BOLD, 6));
                g2.drawString("BITHM NATIONAL BANK", x + 4, y + 10);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 5)); g2.drawString("ATM-001  APPROVED", x + 6, y + 19);
                g2.drawString("WITHDRAWAL    500.00", x + 5, y + 27);
                g2.drawString("BALANCE       500.00", x + 5, y + 34);
                g2.drawString("THANK YOU", x + 20, y + 43);
            }
        }
    }
}
