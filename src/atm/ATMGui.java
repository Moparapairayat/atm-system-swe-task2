package atm;

import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/** Complete visual ATM kiosk interface with modern card-like UI buttons and vector icons. */
public class ATMGui {
    private static final Color SCREEN = new Color(232, 243, 246);
    private static final Color BLUE = new Color(18, 92, 155);
    private static final Color TEAL = new Color(0, 138, 130);
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

    // Dynamic Labels & Components
    private final JLabel collectionHeading = new JLabel("", SwingConstants.CENTER);
    private final JLabel noteBreakdownLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel processingHeading = new JLabel("", SwingConstants.CENTER);
    private final JLabel processingDetail = new JLabel("", SwingConstants.CENTER);
    private final JLabel receiptHeading = new JLabel("", SwingConstants.CENTER);
    private final JLabel detectedCardLabel = new JLabel("", SwingConstants.CENTER);
    private final PinDotsPanel pinDotsIndicator = new PinDotsPanel();
    private final JLabel pinAttemptsLabel = new JLabel("Security: 3 attempts allowed  |  Shield your PIN", SwingConstants.CENTER);
    private final JLabel menuUserGreeting = new JLabel("", SwingConstants.CENTER);
    private final JLabel withdrawBalanceInfo = new JLabel("", SwingConstants.CENTER);
    private final JLabel depositBalanceInfo = new JLabel("", SwingConstants.CENTER);
    private final JLabel balanceAccountNum = new JLabel("", SwingConstants.CENTER);
    private final JLabel balanceAccountType = new JLabel("", SwingConstants.CENTER);
    private final JLabel balanceAmountBig = new JLabel("", SwingConstants.CENTER);
    private final JTextArea statementContent = new JTextArea();
    private final JPanel accountSelectionOptions = column();
    private final JTextField transferDestField = new JTextField();
    private final JTextField transferAmountField = new JTextField();
    private final JLabel transferAccountInfoLabel = new JLabel("", SwingConstants.CENTER);
    private final JPanel transferSuggestionsPanel = new JPanel();
    private final JLabel liveClockLabel = new JLabel("", SwingConstants.RIGHT);
    private final JProgressBar vaultProgressBar = new JProgressBar(0, 100);
    private final JLabel vaultStatusLabel = new JLabel("$45,000 / $50,000 (90% Loaded)", SwingConstants.CENTER);
    private double currentVaultCash = 45000.0;
    private boolean isPinVisible = false;
    private String currentPage = "welcome";

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  |  hh:mm:ss a");
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
        startLiveClock();
        buildWindow();
    }

    public void show() { frame.setVisible(true); }

    private void startLiveClock() {
        updateClock();
        Timer clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
    }

    private void updateClock() {
        liveClockLabel.setText(LocalDateTime.now().format(timeFormat));
    }

    private void buildWindow() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1080, 860));
        frame.setSize(1220, 910);
        frame.setLocationRelativeTo(null);
        JPanel background = new JPanel(new GridLayout(1, 1));
        background.setBackground(new Color(18, 27, 37));
        background.setBorder(new EmptyBorder(12, 35, 12, 35));
        background.add(machine());
        frame.setContentPane(background);
    }

    private JPanel machine() {
        JPanel machine = new JPanel(new BorderLayout(14, 10));
        machine.setBackground(new Color(48, 57, 64));
        machine.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(143, 157, 164), 3), new EmptyBorder(10, 20, 12, 20)));
        machine.add(fascia(), BorderLayout.NORTH);
        machine.add(leftPanel(), BorderLayout.WEST);

        screen.setBackground(SCREEN);
        screen.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(10, 15, 19), 10), BorderFactory.createLineBorder(new Color(80, 95, 102), 2)),
                new EmptyBorder(16, 24, 16, 24)));
        addPages();

        machine.add(fdkScreenHousing(), BorderLayout.CENTER);
        machine.add(keypad(), BorderLayout.EAST);
        machine.add(bottomBay(), BorderLayout.SOUTH);
        return machine;
    }

    /** Hardware Screen Housing with 8 Tactile Vector FDK Side Buttons. */
    private JPanel fdkScreenHousing() {
        JPanel container = new JPanel(new BorderLayout(8, 0));
        container.setOpaque(false);

        // Left FDK Column
        JPanel leftFdk = column();
        leftFdk.setOpaque(false);
        leftFdk.setPreferredSize(new Dimension(52, 0));
        leftFdk.add(Box.createVerticalGlue());
        for (int i = 1; i <= 4; i++) {
            final int index = i;
            leftFdk.add(new FdkHardwareButton(true, "FDK " + i, e -> handleFdkClick(index)));
            if (i < 4) leftFdk.add(Box.createVerticalStrut(40));
        }
        leftFdk.add(Box.createVerticalGlue());

        // Right FDK Column
        JPanel rightFdk = column();
        rightFdk.setOpaque(false);
        rightFdk.setPreferredSize(new Dimension(52, 0));
        rightFdk.add(Box.createVerticalGlue());
        for (int i = 5; i <= 8; i++) {
            final int index = i;
            rightFdk.add(new FdkHardwareButton(false, "FDK " + i, e -> handleFdkClick(index)));
            if (i < 8) rightFdk.add(Box.createVerticalStrut(40));
        }
        rightFdk.add(Box.createVerticalGlue());

        container.add(leftFdk, BorderLayout.WEST);
        container.add(screen, BorderLayout.CENTER);
        container.add(rightFdk, BorderLayout.EAST);
        return container;
    }

    private void handleFdkClick(int fdkIndex) {
        SoundEffects.keypad();
        if ("menu".equals(currentPage)) {
            switch (fdkIndex) {
                case 1: showWithdrawPage(); break;
                case 2: showTransferPage(); break;
                case 3: miniStatement(); break;
                case 5: showDepositPage(); break;
                case 6: showBalance(); break;
                case 7: ejectCard(); break;
                default: break;
            }
        }
    }

    private JPanel fascia() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(8, 55, 94));
        p.setBorder(new EmptyBorder(10, 22, 10, 22));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JLabel brand = new JLabel("BITHM NATIONAL BANK");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel branch = new JLabel("|  CHATTOGRAM TERMINAL 001");
        branch.setForeground(new Color(160, 205, 235));
        branch.setFont(new Font("Segoe UI", Font.BOLD, 11));
        left.add(brand);
        left.add(branch);

        liveClockLabel.setForeground(new Color(180, 230, 255));
        liveClockLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        p.add(left, BorderLayout.WEST);
        p.add(liveClockLabel, BorderLayout.EAST);
        return p;
    }

    private JPanel bottomBay() {
        JPanel p = new JPanel(new BorderLayout(20, 0)); p.setBackground(new Color(38, 45, 50)); p.setBorder(new EmptyBorder(6, 175, 0, 175));
        JPanel cash = hardwareBay("CASH DISPENSER", new Color(45, 174, 135), cashPort, 280);
        JPanel receipt = hardwareBay("RECEIPT PRINTER", new Color(231, 201, 101), receiptPort, 120);
        p.add(cash, BorderLayout.CENTER); p.add(receipt, BorderLayout.EAST); return p;
    }

    private JPanel hardwareBay(String name, Color light, PortAnimation port, int width) {
        JPanel p = column(); p.setBackground(new Color(24, 29, 33)); p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(105, 118, 123)), new EmptyBorder(5, 12, 7, 12)));
        p.add(label(name + "  [READY]", 9, light, SwingConstants.CENTER));
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
        JLabel light = label("[CONTACTLESS READY]", 10, new Color(91, 221, 144), SwingConstants.CENTER);
        light.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(light);
        left.add(Box.createVerticalGlue());
        cardPort.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(cardPort);
        return left;
    }

    private JPanel keypad() {
        JPanel p = new JPanel(new GridLayout(4, 3, 7, 7));
        p.setBackground(new Color(32, 38, 43));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 102, 110), 2), new EmptyBorder(16, 12, 16, 12)));
        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "ENTER"};
        for (String k : keys) {
            Color color = k.equals("ENTER") ? TEAL : k.equals("C") ? new Color(176, 68, 60) : new Color(64, 76, 85);
            p.add(keyBtn(k, color));
        }
        JPanel holder = column();
        holder.setPreferredSize(new Dimension(200, 0));
        holder.setBackground(new Color(43, 50, 56));
        JLabel keypadTitle = label("PIN KEYPAD", 13, Color.WHITE, SwingConstants.CENTER);
        keypadTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        holder.add(keypadTitle);
        holder.add(Box.createVerticalStrut(12));
        holder.add(p);
        holder.add(Box.createVerticalGlue());
        holder.add(label("HELP / ACCESSIBILITY", 9, new Color(183, 195, 202), SwingConstants.CENTER));
        return holder;
    }

    private JButton keyBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, text.length() > 1 ? 12 : 17));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(110, 124, 133)), new EmptyBorder(10, 8, 10, 8)));
        b.addActionListener(e -> handleKey(text));
        return b;
    }

    private void addPages() {
        screen.add(welcomePage(), "welcome");
        screen.add(loginPage(), "login");
        screen.add(cardSelectionPage(), "cardSelection");
        screen.add(accountSelectionPage(), "accountSelection");
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
        showPage("welcome");
    }

    // SCREEN 1: Welcome Page
    private JPanel welcomePage() {
        JPanel p = centered();
        p.setBorder(new EmptyBorder(12, 40, 12, 40));

        JPanel emblemPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        emblemPanel.setOpaque(false);
        JLabel logo = new JLabel(new BankLogoIcon());
        JLabel bankTitle = new JLabel("BITHM NATIONAL BANK");
        bankTitle.setFont(new Font("Segoe UI", Font.BOLD, 25));
        bankTitle.setForeground(BLUE);
        emblemPanel.add(logo);
        emblemPanel.add(bankTitle);
        p.add(emblemPanel);

        p.add(Box.createVerticalStrut(4));
        JLabel tagline = new JLabel("24/7 Smart ATM & Digital Banking Kiosk  |  Chattogram Terminal 001", SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tagline.setForeground(new Color(88, 112, 125));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(tagline);

        p.add(Box.createVerticalStrut(6));
        JPanel statusPill = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        statusPill.setOpaque(false);
        JLabel statusDot = new JLabel("ATM ONLINE  |  DISPENSER READY  |  EMV CHIP SECURED");
        statusDot.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusDot.setForeground(new Color(12, 138, 98));
        statusPill.add(statusDot);
        p.add(statusPill);

        p.add(Box.createVerticalStrut(10));

        JPanel cardBox = column();
        cardBox.setBackground(Color.WHITE);
        cardBox.setMaximumSize(new Dimension(480, 220));
        cardBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 207, 215), 1),
                new EmptyBorder(14, 28, 14, 28)));

        JLabel welcomeHeading = new JLabel("WELCOME TO BITHM BANK", SwingConstants.CENTER);
        welcomeHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeHeading.setForeground(INK);
        welcomeHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardBox.add(welcomeHeading);

        cardBox.add(Box.createVerticalStrut(4));
        JLabel instruction = new JLabel("Please insert your debit card to initiate a secure session", SwingConstants.CENTER);
        instruction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instruction.setForeground(new Color(80, 95, 105));
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardBox.add(instruction);

        cardBox.add(Box.createVerticalStrut(12));
        JButton insertBtn = action("INSERT DEBIT CARD", BLUE, e -> showPage("cardSelection"));
        insertBtn.setPreferredSize(new Dimension(360, 42));
        insertBtn.setMaximumSize(new Dimension(360, 42));
        cardBox.add(insertBtn);

        cardBox.add(Box.createVerticalStrut(8));
        JButton techBtn = action("TECHNICIAN SERVICE CONSOLE", new Color(74, 85, 94), e -> showPage("technician"));
        techBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        techBtn.setPreferredSize(new Dimension(360, 34));
        techBtn.setMaximumSize(new Dimension(360, 34));
        cardBox.add(techBtn);

        p.add(cardBox);
        p.add(Box.createVerticalStrut(10));

        JPanel logosBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        logosBar.setOpaque(false);
        logosBar.add(new CardBrandBadge("VISA"));
        logosBar.add(new CardBrandBadge("MASTERCARD"));
        logosBar.add(new CardBrandBadge("UNIONPAY"));
        logosBar.add(new CardBrandBadge("NPSB"));
        logosBar.add(new CardBrandBadge("EMV"));
        p.add(logosBar);

        p.add(Box.createVerticalStrut(8));
        JLabel securityNote = new JLabel("Protected with 256-Bit SSL & EMV Hardware Security", SwingConstants.CENTER);
        securityNote.setFont(new Font("Segoe UI", Font.BOLD, 10));
        securityNote.setForeground(new Color(90, 110, 120));
        securityNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(securityNote);

        p.add(Box.createVerticalGlue());
        JLabel devCredit = new JLabel("BITHM College Of Professionals  |  Student Developer: Mopara Pair Ayat", SwingConstants.CENTER);
        devCredit.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        devCredit.setForeground(new Color(120, 138, 148));
        devCredit.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(devCredit);

        return p;
    }

    // SCREEN 2: Card Selection Page
    private JPanel cardSelectionPage() {
        JPanel p = centered();
        p.setBorder(new EmptyBorder(12, 25, 12, 25));
        addAtmHeader(p, "SELECT & INSERT CARD", "Click on any debit card below to insert it into the ATM slot.");
        p.add(Box.createVerticalStrut(14));

        JPanel cardsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 0));
        cardsContainer.setOpaque(false);

        // Card 1: Ziana Mehnaz Ruhee
        JPanel card1Wrapper = column();
        card1Wrapper.setOpaque(false);
        card1Wrapper.add(new VisualDebitCard(customers[0], "BLUE", "1234", "08/29", "PLATINUM DEBIT", () -> beginCardInsertion(customers[0])));
        card1Wrapper.add(Box.createVerticalStrut(8));
        JLabel pinLabel1 = new JLabel("DEMO PIN: 1234  |  CLICK CARD TO INSERT", SwingConstants.CENTER);
        pinLabel1.setFont(new Font("Segoe UI", Font.BOLD, 10));
        pinLabel1.setForeground(BLUE);
        pinLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        card1Wrapper.add(pinLabel1);
        cardsContainer.add(card1Wrapper);

        // Card 2: Mopara Pair Ayat
        JPanel card2Wrapper = column();
        card2Wrapper.setOpaque(false);
        card2Wrapper.add(new VisualDebitCard(customers[1], "GOLD", "4321", "12/30", "GOLD VIP DEBIT", () -> beginCardInsertion(customers[1])));
        card2Wrapper.add(Box.createVerticalStrut(8));
        JLabel pinLabel2 = new JLabel("DEMO PIN: 4321  |  CLICK CARD TO INSERT", SwingConstants.CENTER);
        pinLabel2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        pinLabel2.setForeground(new Color(175, 120, 20));
        pinLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        card2Wrapper.add(pinLabel2);
        cardsContainer.add(card2Wrapper);

        p.add(cardsContainer);
        p.add(Box.createVerticalStrut(14));

        p.add(action("BACK TO WELCOME", new Color(90, 102, 110), e -> showPage("welcome")));
        p.add(Box.createVerticalGlue());
        addSecureFooter(p);
        return p;
    }

    // SCREEN 3: PIN Entry Page
    private JPanel loginPage() {
        JPanel p = centered();
        addAtmHeader(p, "ENTER YOUR PIN", "Please input your 4-digit security PIN to access your account.");
        p.add(Box.createVerticalStrut(14));

        JPanel cardPanel = column();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setMaximumSize(new Dimension(460, 280));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 207, 215)), new EmptyBorder(16, 28, 16, 28)));

        detectedCardLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detectedCardLabel.setForeground(BLUE);
        detectedCardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(detectedCardLabel);
        cardPanel.add(Box.createVerticalStrut(12));

        pinDotsIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(pinDotsIndicator);
        cardPanel.add(Box.createVerticalStrut(10));

        JPanel pinInputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        pinInputRow.setOpaque(false);

        JPasswordField pin = (JPasswordField) field("PIN", true);
        activePinField = pin;
        pin.setHorizontalAlignment(JTextField.CENTER);
        pin.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pin.setPreferredSize(new Dimension(180, 40));
        pin.setMaximumSize(new Dimension(180, 40));
        pin.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                pinDotsIndicator.setFilledCount(new String(pin.getPassword()).length());
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    authenticatePending(new String(pin.getPassword()));
                }
            }
        });
        pinInputRow.add(pin);

        JButton toggleEyeBtn = new JButton("SHOW PIN");
        toggleEyeBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        toggleEyeBtn.setForeground(BLUE);
        toggleEyeBtn.setBackground(new Color(232, 243, 246));
        toggleEyeBtn.setFocusPainted(false);
        toggleEyeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleEyeBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 195, 220)),
                new EmptyBorder(8, 10, 8, 10)));
        toggleEyeBtn.addActionListener(e -> {
            isPinVisible = !isPinVisible;
            if (isPinVisible) {
                pin.setEchoChar((char) 0);
                toggleEyeBtn.setText("HIDE PIN");
                toggleEyeBtn.setForeground(new Color(180, 50, 40));
            } else {
                pin.setEchoChar('*');
                toggleEyeBtn.setText("SHOW PIN");
                toggleEyeBtn.setForeground(BLUE);
            }
            pin.requestFocusInWindow();
        });
        pinInputRow.add(toggleEyeBtn);
        cardPanel.add(pinInputRow);

        cardPanel.add(Box.createVerticalStrut(10));
        pinAttemptsLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pinAttemptsLabel.setForeground(new Color(110, 130, 140));
        pinAttemptsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(pinAttemptsLabel);

        p.add(cardPanel);
        p.add(Box.createVerticalStrut(14));

        JPanel actBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actBox.setOpaque(false);
        JButton continueBtn = action("SUBMIT PIN  |  ENTER", BLUE, e -> authenticatePending(new String(pin.getPassword())));
        continueBtn.setPreferredSize(new Dimension(210, 40));
        JButton cancelBtn = action("CANCEL & EJECT", new Color(90, 102, 110), e -> ejectCard());
        cancelBtn.setPreferredSize(new Dimension(160, 40));
        actBox.add(continueBtn);
        actBox.add(cancelBtn);
        p.add(actBox);

        p.add(Box.createVerticalGlue());
        addSecureFooter(p);
        return p;
    }

    // SCREEN 4: Main Menu Page with Modern Card-Like Transaction Tiles
    private JPanel menuPage() {
        JPanel p = centered();
        p.setBorder(new EmptyBorder(10, 20, 10, 20));

        menuUserGreeting.setFont(new Font("Segoe UI", Font.BOLD, 15));
        menuUserGreeting.setForeground(BLUE);
        menuUserGreeting.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(menuUserGreeting);
        p.add(Box.createVerticalStrut(4));

        JLabel selectPrompt = new JLabel("SELECT TRANSACTION SERVICE", SwingConstants.CENTER);
        selectPrompt.setFont(new Font("Segoe UI", Font.BOLD, 22));
        selectPrompt.setForeground(INK);
        selectPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(selectPrompt);

        p.add(Box.createVerticalStrut(14));

        JPanel grid = new JPanel(new GridLayout(3, 2, 20, 16));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(630, 275));

        // Row 1
        grid.add(new MenuOptionCard("CASH WITHDRAWAL", "Fast cash & custom amounts", new Color(16, 78, 138), new Color(10, 48, 88), "WITHDRAW", true, e -> showWithdrawPage()));
        grid.add(new MenuOptionCard("CASH DEPOSIT", "Instant account deposit", new Color(0, 130, 120), new Color(0, 85, 78), "DEPOSIT", false, e -> showDepositPage()));

        // Row 2
        grid.add(new MenuOptionCard("FUND TRANSFER", "Transfer to BITHM accounts", new Color(0, 130, 120), new Color(0, 85, 78), "TRANSFER", true, e -> showTransferPage()));
        grid.add(new MenuOptionCard("BALANCE INQUIRY", "Check available funds", new Color(16, 78, 138), new Color(10, 48, 88), "BALANCE", false, e -> showBalance()));

        // Row 3
        grid.add(new MenuOptionCard("MINI STATEMENT", "Recent transaction ledger", new Color(52, 75, 95), new Color(32, 48, 62), "STATEMENT", true, e -> miniStatement()));
        grid.add(new MenuOptionCard("EJECT CARD", "Finish & collect debit card", new Color(175, 58, 48), new Color(120, 32, 25), "EJECT", false, e -> ejectCard()));

        p.add(grid);
        p.add(Box.createVerticalGlue());
        addSecureFooter(p);
        return p;
    }

    // SCREEN 5: Amount Page
    private JPanel amountPage(String type) {
        boolean withdrawal = type.equals("withdraw");
        JPanel p = centered();
        p.setBorder(new EmptyBorder(12, 30, 12, 30));
        addAtmHeader(p, withdrawal ? "CASH WITHDRAWAL" : "CASH DEPOSIT",
                withdrawal ? "Select quick amount or enter custom sum below." : "Enter the amount to insert into deposit slot.");
        p.add(Box.createVerticalStrut(8));

        JLabel balIndicator = withdrawal ? withdrawBalanceInfo : depositBalanceInfo;
        balIndicator.setFont(new Font("Segoe UI", Font.BOLD, 13));
        balIndicator.setForeground(BLUE);
        balIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(balIndicator);
        p.add(Box.createVerticalStrut(10));

        JPanel values = new JPanel(new GridLayout(2, 3, 10, 10));
        values.setOpaque(false);
        values.setMaximumSize(new Dimension(540, 95));
        for (int value : new int[]{100, 500, 1000, 2000, 3000, 5000}) {
            values.add(action(money.format(value), BLUE, e -> transact(type, value)));
        }
        p.add(values);
        p.add(Box.createVerticalStrut(12));

        JPanel customBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        customBox.setOpaque(false);
        JLabel customAmtLabel = new JLabel("Custom Amount ($):");
        customAmtLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        customAmtLabel.setForeground(INK);
        customBox.add(customAmtLabel);

        JTextField other = field("Custom sum", false);
        other.setPreferredSize(new Dimension(160, 38));
        other.setMaximumSize(new Dimension(160, 38));
        customBox.add(other);

        JButton confirmBtn = action(withdrawal ? "CONFIRM" : "DEPOSIT", TEAL, e -> {
            try { transact(type, Double.parseDouble(other.getText().trim())); }
            catch (NumberFormatException ex) { dialog("Enter a valid numeric amount."); }
        });
        confirmBtn.setPreferredSize(new Dimension(120, 38));
        customBox.add(confirmBtn);
        p.add(customBox);

        p.add(Box.createVerticalStrut(8));
        JLabel denomNote = new JLabel("Available Notes in Dispenser: $50, $100, $500, $1000", SwingConstants.CENTER);
        denomNote.setFont(new Font("Segoe UI", Font.BOLD, 11));
        denomNote.setForeground(new Color(88, 112, 125));
        denomNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(denomNote);

        p.add(Box.createVerticalStrut(8));
        p.add(action("BACK TO MAIN MENU", new Color(90, 102, 110), e -> showPage("menu")));
        p.add(Box.createVerticalGlue());
        addSecureFooter(p);
        return p;
    }

    private void showWithdrawPage() {
        if (activeAccount != null) {
            withdrawBalanceInfo.setText("Active Account: " + activeAccount.getAccountNumber() + "  |  Available Balance: " + money.format(activeAccount.checkBalance()));
        }
        showPage("withdraw");
    }

    private void showDepositPage() {
        if (activeAccount != null) {
            depositBalanceInfo.setText("Active Account: " + activeAccount.getAccountNumber() + "  |  Available Balance: " + money.format(activeAccount.checkBalance()));
        }
        showPage("deposit");
    }

    private static String getNoteBreakdown(double amount) {
        int amt = (int) amount;
        int thousands = amt / 1000; amt %= 1000;
        int fiveHundreds = amt / 500; amt %= 500;
        int hundreds = amt / 100; amt %= 100;
        int fifties = amt / 50; amt %= 50;

        StringBuilder sb = new StringBuilder();
        int totalNotes = thousands + fiveHundreds + hundreds + fifties;
        if (thousands > 0) sb.append(thousands).append(" x $1000  ");
        if (fiveHundreds > 0) sb.append(fiveHundreds).append(" x $500  ");
        if (hundreds > 0) sb.append(hundreds).append(" x $100  ");
        if (fifties > 0) sb.append(fifties).append(" x $50  ");
        if (amt > 0) sb.append(amt).append(" x $1 Change  ");
        sb.append("(Total: ").append(totalNotes).append(" Note").append(totalNotes > 1 ? "s)" : ")");
        return sb.toString();
    }

    // SCREEN 6: Collection Page
    private JPanel collectionPage() {
        JPanel p = centered();
        collectionHeading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        collectionHeading.setForeground(INK);
        collectionHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(collectionHeading);
        p.add(Box.createVerticalStrut(8));

        noteBreakdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        noteBreakdownLabel.setForeground(BLUE);
        noteBreakdownLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(noteBreakdownLabel);

        p.add(Box.createVerticalStrut(12));
        p.add(label("The cash dispenser slot is illuminated below.\nPlease take your banknotes.", 15, new Color(70, 91, 105), SwingConstants.CENTER));
        p.add(Box.createVerticalStrut(24));
        p.add(action("COLLECT CASH", TEAL, e -> cashPort.collect(this::offerReceipt)));
        p.add(Box.createVerticalGlue());
        addSecureFooter(p);
        return p;
    }

    private JPanel balancePage() {
        JPanel p = centered();
        p.setBorder(new EmptyBorder(14, 40, 14, 40));
        addAtmHeader(p, "BALANCE INQUIRY", "Detailed breakdown of your current linked account.");
        p.add(Box.createVerticalStrut(14));

        JPanel card = column();
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(480, 205));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 207, 215)), new EmptyBorder(16, 26, 16, 26)));

        balanceAccountNum.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balanceAccountNum.setForeground(BLUE);
        balanceAccountNum.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(balanceAccountNum);

        balanceAccountType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        balanceAccountType.setForeground(new Color(100, 118, 128));
        balanceAccountType.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(balanceAccountType);
        card.add(Box.createVerticalStrut(10));

        JLabel availTitle = new JLabel("AVAILABLE BALANCE", SwingConstants.CENTER);
        availTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        availTitle.setForeground(new Color(88, 112, 125));
        availTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(availTitle);

        balanceAmountBig.setFont(new Font("Segoe UI", Font.BOLD, 36));
        balanceAmountBig.setForeground(TEAL);
        balanceAmountBig.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(balanceAmountBig);

        card.add(Box.createVerticalStrut(8));
        JLabel limitNote = new JLabel("Daily ATM Withdrawal Limit: $10,000.00  |  Status: ACTIVE", SwingConstants.CENTER);
        limitNote.setFont(new Font("Segoe UI", Font.BOLD, 10));
        limitNote.setForeground(new Color(40, 130, 90));
        limitNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(limitNote);

        p.add(card);
        p.add(Box.createVerticalStrut(16));

        JPanel actRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actRow.setOpaque(false);
        JButton wBtn = action("WITHDRAW CASH", BLUE, e -> showWithdrawPage());
        JButton pBtn = action("PRINT RECEIPT", TEAL, e -> printReceipt());
        JButton bBtn = action("MAIN MENU", new Color(90, 102, 110), e -> showPage("menu"));
        actRow.add(wBtn);
        actRow.add(pBtn);
        actRow.add(bBtn);
        p.add(actRow);

        p.add(Box.createVerticalGlue());
        addSecureFooter(p);
        return p;
    }

    private JPanel statementPage() {
        JPanel p = centered();
        p.setBorder(new EmptyBorder(12, 35, 12, 35));
        addAtmHeader(p, "MINI STATEMENT", "Your recent transaction history and activity log.");
        p.add(Box.createVerticalStrut(10));

        JPanel paper = new JPanel(new BorderLayout());
        paper.setBackground(Color.WHITE);
        paper.setMaximumSize(new Dimension(520, 275));
        paper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 207, 215)), new EmptyBorder(14, 22, 14, 22)));

        statementContent.setEditable(false);
        statementContent.setBackground(Color.WHITE);
        statementContent.setForeground(INK);
        statementContent.setFont(new Font("Monospaced", Font.BOLD, 12));
        statementContent.setLineWrap(false);
        statementContent.setBorder(null);
        paper.add(statementContent, BorderLayout.CENTER);
        p.add(paper);

        p.add(Box.createVerticalStrut(12));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        actions.setOpaque(false);
        actions.add(action("PRINT PAPER STATEMENT", TEAL, e -> receiptPort.dispense(() -> dialog("Mini statement printed. Please take it from the receipt printer."))));
        actions.add(action("BACK TO MAIN MENU", BLUE, e -> showPage("menu")));
        p.add(actions);

        p.add(Box.createVerticalGlue());
        addSecureFooter(p);
        return p;
    }

    private JPanel technicianPage() {
        JPanel p = centered();
        p.setBorder(new EmptyBorder(12, 35, 12, 35));
        addAtmHeader(p, "ATM SERVICE & MAINTENANCE", "Technician: " + technician.getName());
        p.add(Box.createVerticalStrut(12));

        JPanel vaultCard = column();
        vaultCard.setBackground(Color.WHITE);
        vaultCard.setMaximumSize(new Dimension(540, 95));
        vaultCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 207, 215)), new EmptyBorder(10, 18, 10, 18)));

        JLabel vaultTitle = new JLabel("ATM CASH VAULT CAPACITY", SwingConstants.CENTER);
        vaultTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        vaultTitle.setForeground(new Color(70, 91, 105));
        vaultTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        vaultCard.add(vaultTitle);
        vaultCard.add(Box.createVerticalStrut(6));

        vaultProgressBar.setValue((int) ((currentVaultCash / 50000.0) * 100));
        vaultProgressBar.setStringPainted(true);
        vaultProgressBar.setForeground(TEAL);
        vaultProgressBar.setPreferredSize(new Dimension(480, 20));
        vaultProgressBar.setMaximumSize(new Dimension(480, 20));
        vaultCard.add(vaultProgressBar);
        vaultCard.add(Box.createVerticalStrut(6));

        vaultStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        vaultStatusLabel.setForeground(INK);
        vaultStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        vaultCard.add(vaultStatusLabel);

        p.add(vaultCard);
        p.add(Box.createVerticalStrut(10));

        JPanel health = new JPanel(new GridLayout(1, 4, 8, 8));
        health.setOpaque(false);
        health.setMaximumSize(new Dimension(540, 52));
        health.add(statusTile("DISPENSER", "100% OK", TEAL));
        health.add(statusTile("PAPER ROLL", "88% OK", TEAL));
        health.add(statusTile("NETWORK", "14ms ONLINE", TEAL));
        health.add(statusTile("CARD SLOT", "SECURE", BLUE));
        p.add(health);

        p.add(Box.createVerticalStrut(10));

        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 10));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(540, 95));
        grid.add(action("REPLENISH VAULT ($50,000)", TEAL, e -> {
            currentVaultCash = 50000.0;
            vaultProgressBar.setValue(100);
            vaultStatusLabel.setText("$50,000 / $50,000 (100% Full)");
            runService("REPLENISH CASH");
        }));
        grid.add(action("UPGRADE SYSTEM FIRMWARE", BLUE, e -> runService("UPGRADE SYSTEM")));
        grid.add(action("RUN HARDWARE DIAGNOSTIC", BLUE, e -> runService("RUN DIAGNOSTIC")));
        grid.add(action("INSPECT & REPAIR ATM", new Color(166, 76, 69), e -> runService("REPAIR ATM")));
        p.add(grid);

        p.add(Box.createVerticalStrut(12));
        p.add(action("EXIT SERVICE CONSOLE", new Color(90, 102, 110), e -> showPage("welcome")));
        p.add(Box.createVerticalGlue());
        addSecureFooter(p);
        return p;
    }

    private JPanel transferPage() {
        JPanel p = centered();
        addAtmHeader(p, "FUND TRANSFER", "Transfer funds securely to another BITHM account.");
        p.add(Box.createVerticalStrut(10));

        JPanel formCard = column();
        formCard.setBackground(Color.WHITE);
        formCard.setMaximumSize(new Dimension(500, 320));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 207, 215), 1),
                new EmptyBorder(14, 24, 14, 24)));

        transferAccountInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        transferAccountInfoLabel.setForeground(BLUE);
        transferAccountInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(transferAccountInfoLabel);
        formCard.add(Box.createVerticalStrut(10));

        JLabel destLabel = new JLabel("Recipient / Destination Account Number:");
        destLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        destLabel.setForeground(INK);
        destLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(destLabel);
        formCard.add(Box.createVerticalStrut(4));

        transferDestField.setFont(BODY);
        transferDestField.setPreferredSize(new Dimension(360, 36));
        transferDestField.setMaximumSize(new Dimension(360, 36));
        transferDestField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(142, 165, 177)),
                new EmptyBorder(6, 10, 6, 10)));
        formCard.add(transferDestField);
        formCard.add(Box.createVerticalStrut(6));

        transferSuggestionsPanel.setOpaque(false);
        transferSuggestionsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 6, 2));
        transferSuggestionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(transferSuggestionsPanel);
        formCard.add(Box.createVerticalStrut(8));

        JLabel amtLabel = new JLabel("Transfer Amount ($):");
        amtLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        amtLabel.setForeground(INK);
        amtLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(amtLabel);
        formCard.add(Box.createVerticalStrut(4));

        transferAmountField.setFont(BODY);
        transferAmountField.setPreferredSize(new Dimension(360, 36));
        transferAmountField.setMaximumSize(new Dimension(360, 36));
        transferAmountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(142, 165, 177)),
                new EmptyBorder(6, 10, 6, 10)));
        formCard.add(transferAmountField);
        formCard.add(Box.createVerticalStrut(6));

        JPanel quickAmts = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 6, 0));
        quickAmts.setOpaque(false);
        for (int qVal : new int[]{50, 100, 200, 500}) {
            JButton qBtn = new JButton("$" + qVal);
            qBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            qBtn.setForeground(INK);
            qBtn.setBackground(new Color(240, 244, 248));
            qBtn.setFocusPainted(false);
            qBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            qBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 207, 215)),
                    new EmptyBorder(3, 8, 3, 8)));
            qBtn.addActionListener(ev -> transferAmountField.setText(String.valueOf(qVal)));
            quickAmts.add(qBtn);
        }
        formCard.add(quickAmts);

        p.add(formCard);
        p.add(Box.createVerticalStrut(12));

        p.add(action("TRANSFER NOW", TEAL, e -> transfer(transferDestField.getText().trim(), transferAmountField.getText().trim())));
        p.add(Box.createVerticalStrut(6));
        p.add(action("BACK", new Color(90, 102, 110), e -> showPage("menu")));
        p.add(Box.createVerticalGlue());
        addSecureFooter(p);
        return p;
    }

    private JPanel accountSelectionPage() {
        JPanel p = centered();
        addAtmHeader(p, "SELECT AN ACCOUNT", "Choose the account for this ATM session.");
        p.add(Box.createVerticalStrut(24));
        accountSelectionOptions.setOpaque(false);
        accountSelectionOptions.setMaximumSize(new Dimension(460, 220));
        p.add(accountSelectionOptions);
        p.add(Box.createVerticalStrut(12));
        p.add(action("CANCEL / EJECT CARD", new Color(90, 102, 110), e -> ejectCard()));
        p.add(Box.createVerticalGlue());
        addSecureFooter(p);
        return p;
    }

    private JPanel processingPage() {
        JPanel p = centered();
        processingHeading.setFont(new Font("Segoe UI", Font.BOLD, 26)); processingHeading.setForeground(INK); processingHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        processingDetail.setFont(new Font("Segoe UI", Font.PLAIN, 16)); processingDetail.setForeground(new Color(70, 91, 105)); processingDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(processingHeading); p.add(Box.createVerticalStrut(18)); p.add(processingDetail); p.add(Box.createVerticalStrut(26));
        p.add(label("PROCESSING TRANSACTION", 16, TEAL, SwingConstants.CENTER));
        return p;
    }

    private JPanel receiptChoicePage() {
        JPanel p = centered();
        p.add(label("TRANSACTION COMPLETE", 26, INK, SwingConstants.CENTER)); p.add(Box.createVerticalStrut(12));
        p.add(label("Would you like a printed receipt?", 16, new Color(70, 91, 105), SwingConstants.CENTER)); p.add(Box.createVerticalStrut(26));
        p.add(action("YES, PRINT RECEIPT", TEAL, e -> printReceipt())); p.add(Box.createVerticalStrut(10));
        p.add(action("NO RECEIPT", new Color(90, 102, 110), e -> startEjection()));
        return p;
    }

    private JPanel receiptCollectionPage() {
        JPanel p = centered(); receiptHeading.setFont(new Font("Segoe UI", Font.BOLD, 26)); receiptHeading.setForeground(INK); receiptHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(receiptHeading); p.add(Box.createVerticalStrut(14));
        p.add(label("Your receipt is ready in the printer slot.", 16, new Color(70, 91, 105), SwingConstants.CENTER)); p.add(Box.createVerticalStrut(26));
        p.add(action("TAKE RECEIPT", TEAL, e -> receiptPort.collect(this::startEjection)));
        return p;
    }

    private JPanel cardCollectionPage() {
        JPanel p = centered(); p.add(label("PLEASE TAKE YOUR CARD", 26, INK, SwingConstants.CENTER)); p.add(Box.createVerticalStrut(14));
        p.add(label("Your card has been ejected from the card reader.", 16, new Color(70, 91, 105), SwingConstants.CENTER)); p.add(Box.createVerticalStrut(26));
        p.add(action("TAKE CARD", TEAL, e -> finishSession()));
        return p;
    }

    private JPanel messagePage() { return centered(); }

    private void authenticatePending(String pin) {
        if (pendingCustomer == null) { dialog("No card is currently detected."); showPage("welcome"); return; }
        Customer c = pendingCustomer;
        if (atm.insertCard(c.getCard(), pin)) {
            SoundEffects.accepted();
            activeCustomer = c;
            activeAccount = null;
            pinAttempts = 0;
            pinAttemptsLabel.setText("Security: Verified Successfully");
            pinAttemptsLabel.setForeground(new Color(30, 140, 80));
            customerLabel.setText("CARD ACTIVE");
            cardPort.setStatus("CARD ACTIVE");
            showAccountSelection();
            return;
        }
        pinAttempts++; SoundEffects.warning();
        int left = 3 - pinAttempts;
        if (left > 0) {
            pinAttemptsLabel.setText("Incorrect PIN! Attempts remaining: " + left + " of 3");
            pinAttemptsLabel.setForeground(new Color(200, 40, 40));
            dialog("Incorrect PIN. Attempts remaining: " + left);
            if (activePinField != null) {
                activePinField.setText("");
                pinDotsIndicator.setFilledCount(0);
            }
        } else {
            dialog("Card retained after 3 incorrect PIN attempts for your security.");
            pendingCustomer = null;
            cardPort.removeItem();
            cardPort.setStatus("READY");
            showPage("welcome");
        }
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
                currentVaultCash = Math.max(0, currentVaultCash - amount);
                vaultProgressBar.setValue((int) ((currentVaultCash / 50000.0) * 100));
                vaultStatusLabel.setText(money.format(currentVaultCash) + " / $50,000 (" + vaultProgressBar.getValue() + "% Loaded)");
                String breakdown = getNoteBreakdown(amount);
                noteBreakdownLabel.setText("Dispensed Notes: " + breakdown);
                showProcessing("COUNTING BANKNOTES", "Preparing notes: " + breakdown + "...", () -> {
                    SoundEffects.cashDispenser();
                    cashPort.dispense(amount, () -> {
                        collectionHeading.setText("PLEASE COLLECT YOUR CASH");
                        showPage("collection");
                    });
                });
            } else {
                currentVaultCash += amount;
                vaultProgressBar.setValue((int) ((currentVaultCash / 50000.0) * 100));
                vaultStatusLabel.setText(money.format(currentVaultCash) + " / $50,000 (" + vaultProgressBar.getValue() + "% Loaded)");
                showProcessing("OPENING DEPOSIT SLOT", "Place your cash in the illuminated deposit slot...", () ->
                        cashPort.acceptDeposit(amount, () -> showProcessing("COUNTING AND VALIDATING NOTES",
                                "Deposit accepted: " + money.format(amount) + "\nUpdated balance: " + money.format(activeAccount.checkBalance()), this::offerReceipt)));
            }
        }
        else dialog("Transaction could not be completed. Check your available balance.");
    }

    private void transfer(String destinationNumber, String rawAmount) {
        try {
            if (destinationNumber.isEmpty() || rawAmount.isEmpty()) {
                dialog("Please enter both recipient account number and amount.");
                return;
            }
            if (activeAccount != null && destinationNumber.equalsIgnoreCase(activeAccount.getAccountNumber())) {
                dialog("Cannot transfer funds to the same account.");
                return;
            }
            Account destination = null;
            for (Customer c : customers) {
                for (Account a : c.getAccounts()) {
                    if (a.getAccountNumber().equalsIgnoreCase(destinationNumber)) {
                        destination = a;
                    }
                }
            }
            if (destination == null) {
                dialog("Destination account not found. Please check account number.");
                return;
            }
            double amount = Double.parseDouble(rawAmount);
            Transaction t = new Transfer(id(), amount, activeAccount, destination);
            if (amount <= 0 || !t.execute()) {
                dialog("Transfer failed. Please check amount and available balance.");
                return;
            }
            bank.processTransaction(t);
            confirmation("TRANSFER SUCCESSFUL", amount);
        } catch (NumberFormatException e) {
            dialog("Please enter a valid numeric amount.");
        }
    }

    private void showTransferPage() {
        if (activeAccount != null) {
            transferAccountInfoLabel.setText("From: " + activeAccount.getAccountNumber() + "  |  Available Balance: " + money.format(activeAccount.checkBalance()));
        }
        transferDestField.setText("");
        transferAmountField.setText("");

        transferSuggestionsPanel.removeAll();
        JLabel hintLabel = new JLabel("Demo Accounts: ");
        hintLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        hintLabel.setForeground(new Color(88, 112, 125));
        transferSuggestionsPanel.add(hintLabel);

        for (Customer c : customers) {
            for (Account a : c.getAccounts()) {
                if (activeAccount != null && a.getAccountNumber().equalsIgnoreCase(activeAccount.getAccountNumber())) {
                    continue;
                }
                JButton accBtn = new JButton(a.getAccountNumber() + " (" + c.getName().split(" ")[0] + ")");
                accBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                accBtn.setForeground(BLUE);
                accBtn.setBackground(new Color(232, 243, 246));
                accBtn.setFocusPainted(false);
                accBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                accBtn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(160, 195, 220)),
                        new EmptyBorder(3, 7, 3, 7)));
                accBtn.addActionListener(ev -> transferDestField.setText(a.getAccountNumber()));
                transferSuggestionsPanel.add(accBtn);
            }
        }
        transferSuggestionsPanel.revalidate();
        transferSuggestionsPanel.repaint();
        showPage("transfer");
    }

    private void showBalance() {
        if (activeAccount != null) {
            balanceAccountNum.setText("ACCOUNT NUMBER: " + activeAccount.getAccountNumber());
            balanceAccountType.setText("Account Category: Savings / Standard Current Account");
            balanceAmountBig.setText(money.format(activeAccount.checkBalance()));
        }
        showPage("balance");
    }

    private void showAccountSelection() {
        accountSelectionOptions.removeAll();
        if (activeCustomer == null || activeCustomer.getAccounts().isEmpty()) {
            dialog("No account is linked to this card.");
            ejectCard();
            return;
        }
        int index = 1;
        for (Account account : activeCustomer.getAccounts()) {
            String text = "ACCOUNT " + index++ + "  |  " + account.getAccountNumber() + "  (Balance: " + money.format(account.checkBalance()) + ")";
            accountSelectionOptions.add(action(text, index == 2 ? BLUE : TEAL, e -> {
                activeAccount = account;
                menuUserGreeting.setText("Welcome back, " + activeCustomer.getName() + "  |  Account: " + account.getAccountNumber());
                showPage("menu");
            }));
            accountSelectionOptions.add(Box.createVerticalStrut(12));
        }
        accountSelectionOptions.revalidate();
        accountSelectionOptions.repaint();
        showPage("accountSelection");
    }

    private void miniStatement() {
        StringBuilder statement = new StringBuilder();
        statement.append("==============================================\n");
        statement.append("           BITHM NATIONAL BANK ATM           \n");
        statement.append("             MINI ACCOUNT STATEMENT          \n");
        statement.append("==============================================\n");
        statement.append("Date/Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy  hh:mm a"))).append("\n");
        statement.append("Terminal : ATM-001 | CHATTOGRAM TERMINAL\n");
        statement.append("Customer : ").append(activeCustomer.getName().toUpperCase()).append("\n");
        statement.append("Account  : ").append(activeAccount.getAccountNumber()).append("\n");
        statement.append("Card     : ").append(maskedCard(activeCustomer.getCard().getCardNumber())).append("\n");
        statement.append("----------------------------------------------\n");
        statement.append(String.format("%-18s %-12s %14s\n", "TRANSACTION", "TYPE", "AMOUNT ($)"));
        statement.append("----------------------------------------------\n");
        if (activeAccount.getHistory().isEmpty()) {
            statement.append("        No recent transactions found          \n");
        } else {
            int start = Math.max(0, activeAccount.getHistory().size() - 5);
            for (int i = activeAccount.getHistory().size() - 1; i >= start; i--) {
                Transaction t = activeAccount.getHistory().get(i);
                String typeName = t.getClass().getSimpleName().replace("Inquiry", " Inq").toUpperCase();
                String sign = typeName.contains("DEPOSIT") ? "+" : typeName.contains("WITHDRAW") ? "-" : " ";
                statement.append(String.format("%-18s %-10s %11s%6.2f\n", t.getTransactionID(), typeName, sign, t.getAmount()));
            }
        }
        statement.append("----------------------------------------------\n");
        statement.append(String.format("AVAILABLE BALANCE:             %15s\n", money.format(activeAccount.checkBalance())));
        statement.append("==============================================\n");
        statement.append("    Thank you for banking with BITHM Bank     \n");
        statement.append("==============================================\n");
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
        showProcessing("PRINTING RECEIPT", "Your transaction details are being printed...", () -> { SoundEffects.receiptPrinter(); receiptPort.dispense(() -> {
            receiptHeading.setText("PLEASE TAKE YOUR RECEIPT"); showPage("receiptCollection");
        }); });
    }
    private void startEjection() {
        showProcessing("ENDING SESSION", "Your card is being returned...", () -> { SoundEffects.cardEject(); cardPort.eject(() -> showPage("cardCollection")); });
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
        pinAttempts = 0;
        isPinVisible = false;
        pinAttemptsLabel.setText("Security: 3 attempts allowed  |  Shield your PIN");
        pinAttemptsLabel.setForeground(new Color(110, 130, 140));
        pinDotsIndicator.setFilledCount(0);
        if (activePinField != null) {
            activePinField.setText("");
            activePinField.setEchoChar('*');
        }
        cardPort.setCardDetails(customer.getName(), customer.getCard().getCardNumber());
        detectedCardLabel.setText("Card Inserted: " + customer.getName() + "  (" + maskedCard(customer.getCard().getCardNumber()) + ")");
        Timer detect = new Timer(400, e -> cardPort.insert(() ->
                showProcessing("READING CARD", "Please wait while your card is verified...", () -> showPage("login"))));
        detect.setRepeats(false); detect.start();
    }
    private void showPage(String name) {
        currentPage = name;
        pages.show(screen, name);
    }
    private String id() { return "TXN-" + System.nanoTime(); }
    private String maskedCard(String cardNumber) { return "****  ****  ****  " + cardNumber.substring(cardNumber.length() - 4); }
    private void dialog(String text) { JOptionPane.showMessageDialog(frame, text, "BITHM National Bank ATM", JOptionPane.INFORMATION_MESSAGE); }

    private JTextField field(String hint, boolean password) {
        JTextField f = password ? new JPasswordField() : new JTextField();
        f.setMaximumSize(new Dimension(350, 44)); f.setPreferredSize(new Dimension(350, 44)); f.setFont(BODY); f.setToolTipText(hint);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(142, 165, 177)), new EmptyBorder(8, 12, 8, 12)));
        return f;
    }
    private JButton action(String text, Color color, java.awt.event.ActionListener listener) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(12, 18, 12, 18));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addActionListener(listener);
        return b;
    }
    private JPanel centered() { JPanel p = column(); p.setBackground(SCREEN); p.setBorder(new EmptyBorder(20, 30, 16, 30)); return p; }
    private JPanel column() { JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); return p; }
    private void addAtmHeader(JPanel parent, String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false); header.setMaximumSize(new Dimension(580, 40));
        JLabel bankLabel = new JLabel("BITHM NATIONAL BANK"); bankLabel.setFont(new Font("Segoe UI", Font.BOLD, 13)); bankLabel.setForeground(BLUE);
        JLabel statusLabel = new JLabel("ATM-001  |  ONLINE"); statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 10)); statusLabel.setForeground(TEAL);
        header.add(bankLabel, BorderLayout.WEST); header.add(statusLabel, BorderLayout.EAST); parent.add(header); parent.add(Box.createVerticalStrut(12));
        parent.add(label(title, 23, INK, SwingConstants.CENTER)); parent.add(Box.createVerticalStrut(5)); parent.add(label(subtitle, 13, new Color(70, 91, 105), SwingConstants.CENTER));
    }
    private void addSecureFooter(JPanel parent) { parent.add(label("Secure 256-Bit encrypted connection | Keep your card inserted", 10, new Color(88, 112, 125), SwingConstants.CENTER)); }
    private JPanel statusTile(String title, String value, Color color) {
        JPanel tile = column(); tile.setBackground(Color.WHITE); tile.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(189, 207, 215)), new EmptyBorder(7, 8, 7, 8)));
        tile.add(label(title, 9, new Color(70, 91, 105), SwingConstants.CENTER)); tile.add(label(value, 10, color, SwingConstants.CENTER)); return tile;
    }
    private JLabel label(String text, int size, Color color, int alignment) { JLabel l = new JLabel("<html>" + text.replace("\n", "<br>") + "</html>", alignment); l.setFont(new Font("Segoe UI", Font.BOLD, size)); l.setForeground(color); l.setAlignmentX(Component.CENTER_ALIGNMENT); return l; }

    /** Modern Touchscreen Menu Option Card with vector icon badge and explanatory subtitle. */
    @SuppressWarnings("serial")
    private static class MenuOptionCard extends JButton {
        private final String title;
        private final String subtitle;
        private final Color primaryColor;
        private final Color accentColor;
        private final String iconType;
        private final boolean arrowOnLeft;

        MenuOptionCard(String title, String subtitle, Color primaryColor, Color accentColor, String iconType, boolean arrowOnLeft, java.awt.event.ActionListener listener) {
            super();
            this.title = title;
            this.subtitle = subtitle;
            this.primaryColor = primaryColor;
            this.accentColor = accentColor;
            this.iconType = iconType;
            this.arrowOnLeft = arrowOnLeft;

            setPreferredSize(new Dimension(295, 74));
            setMinimumSize(new Dimension(295, 74));
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setBorder(new EmptyBorder(6, 10, 6, 10));
            addActionListener(listener);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            boolean pressed = getModel().isPressed();
            boolean hover = getModel().isRollover();

            int yOff = pressed ? 2 : 0;

            // Subtle Drop Shadow
            g2.setColor(new Color(0, 0, 0, hover ? 50 : 25));
            g2.fillRoundRect(3, yOff + 4, w - 6, h - 6, 14, 14);

            // Card Gradient Body
            Color top = pressed ? primaryColor.darker() : hover ? primaryColor.brighter() : primaryColor;
            Color bot = pressed ? accentColor.darker() : hover ? accentColor : accentColor.darker();
            g2.setPaint(new GradientPaint(0, yOff, top, w, h + yOff, bot));
            g2.fillRoundRect(2, yOff + 1, w - 4, h - 4, 12, 12);

            // Top Sheen Glass highlight
            g2.setColor(new Color(255, 255, 255, hover ? 38 : 20));
            g2.fillRoundRect(3, yOff + 2, w - 6, (h - 4) / 2, 10, 10);

            // Card Outline
            g2.setColor(hover ? Color.WHITE : new Color(255, 255, 255, 90));
            g2.setStroke(new BasicStroke(hover ? 1.8f : 1.1f));
            g2.drawRoundRect(2, yOff + 1, w - 4, h - 4, 12, 12);

            // Icon Badge Box
            int iconBoxX = arrowOnLeft ? 12 : w - 46;
            int iconBoxY = yOff + (h - 34) / 2;

            g2.setColor(new Color(255, 255, 255, hover ? 55 : 35));
            g2.fillRoundRect(iconBoxX, iconBoxY, 34, 34, 8, 8);
            g2.setColor(new Color(255, 255, 255, hover ? 160 : 100));
            g2.drawRoundRect(iconBoxX, iconBoxY, 34, 34, 8, 8);

            // Draw Vector Icon Inside Badge
            drawOptionIcon(g2, iconBoxX + 17, iconBoxY + 17, iconType);

            // Text Labels
            int textX = arrowOnLeft ? 54 : 14;
            int titleY = yOff + 31;
            int subY = yOff + 49;

            // Title
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString(title, textX, titleY);

            // Subtitle
            g2.setColor(new Color(215, 235, 250, 220));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString(subtitle, textX, subY);

            // Tactile FDK Direction Indicator Indicator Triangle
            g2.setColor(hover ? Color.WHITE : new Color(255, 255, 255, 180));
            int cy = yOff + h / 2, s = 4;
            if (arrowOnLeft) {
                int[] px = {5, 2, 5};
                int[] py = {cy - s, cy, cy + s};
                g2.fillPolygon(px, py, 3);
            } else {
                int[] px = {w - 5, w - 2, w - 5};
                int[] py = {cy - s, cy, cy + s};
                g2.fillPolygon(px, py, 3);
            }

            g2.dispose();
        }

        private void drawOptionIcon(Graphics2D g2, int cx, int cy, String type) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));

            if (type.equals("WITHDRAW")) {
                // Banknote outline
                g2.drawRoundRect(cx - 9, cy - 8, 18, 11, 2, 2);
                g2.fillOval(cx - 2, cy - 4, 4, 3);
                // Downward Arrow
                g2.drawLine(cx, cy + 4, cx, cy + 9);
                g2.fillPolygon(new int[]{cx - 3, cx, cx + 3}, new int[]{cy + 6, cy + 10, cy + 6}, 3);
            } else if (type.equals("DEPOSIT")) {
                // Banknote outline
                g2.drawRoundRect(cx - 9, cy - 4, 18, 11, 2, 2);
                g2.fillOval(cx - 2, cy, 4, 3);
                // Upward Arrow
                g2.drawLine(cx, cy - 5, cx, cy - 9);
                g2.fillPolygon(new int[]{cx - 3, cx, cx + 3}, new int[]{cy - 6, cy - 10, cy - 6}, 3);
            } else if (type.equals("TRANSFER")) {
                // Reciprocal Transfer Arrows
                g2.drawLine(cx - 7, cy - 3, cx + 7, cy - 3);
                g2.fillPolygon(new int[]{cx + 4, cx + 8, cx + 4}, new int[]{cy - 6, cy - 3, cy}, 3);
                g2.drawLine(cx + 7, cy + 4, cx - 7, cy + 4);
                g2.fillPolygon(new int[]{cx - 4, cx - 8, cx - 4}, new int[]{cy + 1, cy + 4, cy + 7}, 3);
            } else if (type.equals("BALANCE")) {
                // Card Balance Icon
                g2.drawRoundRect(cx - 8, cy - 6, 16, 12, 3, 3);
                g2.fillRect(cx - 8, cy - 2, 16, 3);
                g2.fillOval(cx + 2, cy + 2, 3, 3);
            } else if (type.equals("STATEMENT")) {
                // Document / Ledger Sheet
                g2.drawRoundRect(cx - 6, cy - 8, 12, 16, 2, 2);
                g2.drawLine(cx - 3, cy - 4, cx + 3, cy - 4);
                g2.drawLine(cx - 3, cy, cx + 3, cy);
                g2.drawLine(cx - 3, cy + 4, cx + 1, cy + 4);
            } else if (type.equals("EJECT")) {
                // Eject / Exit Icon
                g2.drawRoundRect(cx - 8, cy - 4, 16, 11, 2, 2);
                g2.drawLine(cx - 5, cy + 10, cx + 5, cy + 10);
                g2.fillPolygon(new int[]{cx - 4, cx, cx + 4}, new int[]{cy - 5, cy - 9, cy - 5}, 3);
            }
        }
    }

    /** Tactile Hardware FDK Key with Pure Vector Arrow Polygon. */
    @SuppressWarnings("serial")
    private static class FdkHardwareButton extends JButton {
        private final boolean pointRight;

        FdkHardwareButton(boolean pointRight, String tooltip, java.awt.event.ActionListener listener) {
            this.pointRight = pointRight;
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setToolTipText(tooltip);
            setPreferredSize(new Dimension(46, 38));
            setMaximumSize(new Dimension(46, 38));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            addActionListener(listener);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            boolean pressed = getModel().isPressed();
            boolean hover = getModel().isRollover();

            Color top = pressed ? new Color(35, 42, 48) : hover ? new Color(75, 88, 98) : new Color(55, 66, 74);
            Color bot = pressed ? new Color(25, 30, 35) : hover ? new Color(45, 55, 62) : new Color(38, 46, 52);
            g2.setPaint(new GradientPaint(0, 0, top, 0, h, bot));
            g2.fillRoundRect(2, 2, w - 4, h - 4, 8, 8);

            g2.setColor(hover ? new Color(180, 210, 230) : new Color(110, 125, 135));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(2, 2, w - 4, h - 4, 8, 8);

            // Vector Triangle Arrow Polygon
            g2.setColor(hover ? Color.WHITE : new Color(210, 230, 245));
            int cx = w / 2, cy = h / 2, s = 5;
            if (pointRight) {
                int[] px = {cx - s, cx + s, cx - s};
                int[] py = {cy - s, cy, cy + s};
                g2.fillPolygon(px, py, 3);
            } else {
                int[] px = {cx + s, cx - s, cx + s};
                int[] py = {cy - s, cy, cy + s};
                g2.fillPolygon(px, py, 3);
            }

            g2.dispose();
        }
    }

    /** Custom interactive PIN indicator dots widget (4 circles). */
    @SuppressWarnings("serial")
    private static class PinDotsPanel extends JPanel {
        private int filledCount = 0;

        PinDotsPanel() {
            setPreferredSize(new Dimension(160, 24));
            setMaximumSize(new Dimension(160, 24));
            setOpaque(false);
        }

        void setFilledCount(int count) {
            this.filledCount = Math.max(0, Math.min(4, count));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int startX = 22, spacing = 32, radius = 14;
            for (int i = 0; i < 4; i++) {
                int x = startX + i * spacing;
                if (i < filledCount) {
                    g2.setColor(TEAL);
                    g2.fillOval(x, 4, radius, radius);
                    g2.setColor(new Color(10, 80, 75));
                    g2.drawOval(x, 4, radius, radius);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x, 4, radius, radius);
                    g2.setColor(new Color(160, 180, 190));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(x, 4, radius, radius);
                }
            }
            g2.dispose();
        }
    }

    private static final class BankLogoIcon implements Icon {
        @Override public int getIconWidth() { return 36; }
        @Override public int getIconHeight() { return 36; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(20, 103, 166));
            g2.fillRoundRect(x + 2, y + 2, 32, 32, 8, 8);
            g2.setColor(new Color(180, 225, 245));
            g2.drawRoundRect(x + 2, y + 2, 32, 32, 8, 8);
            g2.setColor(Color.WHITE);
            int[] px = {x + 6, x + 18, x + 30};
            int[] py = {y + 13, y + 7, y + 13};
            g2.fillPolygon(px, py, 3);
            g2.fillRect(x + 8, y + 14, 20, 2);
            g2.fillRect(x + 10, y + 18, 3, 9);
            g2.fillRect(x + 15, y + 18, 3, 9);
            g2.fillRect(x + 20, y + 18, 3, 9);
            g2.fillRect(x + 25, y + 18, 3, 9);
            g2.fillRect(x + 7, y + 28, 22, 3);
            g2.dispose();
        }
    }

    /** Realistic interactive visual debit card component. */
    @SuppressWarnings("serial")
    private static class VisualDebitCard extends JPanel {
        private final Customer customer;
        private final String theme; // "BLUE" or "GOLD"
        private final String expiry;
        private final String cardType;
        private boolean isHovered = false;

        VisualDebitCard(Customer customer, String theme, String pinHint, String expiry, String cardType, Runnable onClick) {
            this.customer = customer;
            this.theme = theme;
            this.expiry = expiry;
            this.cardType = cardType;

            setPreferredSize(new Dimension(270, 168));
            setMaximumSize(new Dimension(270, 168));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setOpaque(false);
            setToolTipText("Click to insert " + customer.getName() + "'s card (PIN: " + pinHint + ")");

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { isHovered = false; repaint(); }
                @Override public void mouseClicked(java.awt.event.MouseEvent e) { onClick.run(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int yOffset = isHovered ? 0 : 2;

            g2.setColor(new Color(0, 0, 0, isHovered ? 60 : 35));
            g2.fillRoundRect(3, yOffset + 5, w - 6, h - 8, 16, 16);

            if (theme.equals("BLUE")) {
                g2.setPaint(new GradientPaint(0, yOffset, new Color(14, 42, 85), w, h + yOffset, new Color(6, 20, 44)));
            } else {
                g2.setPaint(new GradientPaint(0, yOffset, new Color(50, 38, 14), w, h + yOffset, new Color(18, 14, 5)));
            }
            g2.fillRoundRect(2, yOffset, w - 4, h - 6, 14, 14);

            g2.setColor(new Color(255, 255, 255, isHovered ? 26 : 14));
            g2.fillArc(-w / 2, yOffset - 30, w * 2, h + 20, 190, 70);

            if (isHovered) {
                g2.setColor(theme.equals("BLUE") ? new Color(120, 195, 255) : new Color(255, 215, 100));
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(theme.equals("BLUE") ? new Color(45, 95, 155) : new Color(140, 105, 38));
                g2.setStroke(new BasicStroke(1.2f));
            }
            g2.drawRoundRect(2, yOffset, w - 4, h - 6, 14, 14);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.drawString("BITHM NATIONAL BANK", 14, yOffset + 18);

            g2.setColor(theme.equals("BLUE") ? new Color(140, 210, 255) : new Color(255, 210, 100));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
            int typeW = g2.getFontMetrics().stringWidth(cardType);
            g2.drawString(cardType, w - typeW - 14, yOffset + 18);

            int chipX = 14, chipY = yOffset + 30, chipW = 26, chipH = 20;
            g2.setPaint(new GradientPaint(chipX, chipY, new Color(245, 210, 95), chipX + chipW, chipY + chipH, new Color(185, 140, 30)));
            g2.fillRoundRect(chipX, chipY, chipW, chipH, 4, 4);
            g2.setColor(new Color(130, 95, 20));
            g2.drawRoundRect(chipX, chipY, chipW, chipH, 4, 4);
            g2.drawLine(chipX, chipY + 10, chipX + chipW, chipY + 10);
            g2.drawLine(chipX + 9, chipY, chipX + 9, chipY + chipH);
            g2.drawLine(chipX + 17, chipY, chipX + 17, chipY + chipH);

            g2.setColor(new Color(255, 255, 255, 180));
            g2.setStroke(new BasicStroke(1.3f));
            g2.drawArc(chipX + 32, chipY + 3, 7, 13, -45, 90);
            g2.drawArc(chipX + 36, chipY + 1, 11, 17, -45, 90);
            g2.drawArc(chipX + 40, chipY - 1, 15, 21, -45, 90);

            String num = customer.getCard().getCardNumber();
            String formattedNum = num.length() >= 16 ?
                    num.substring(0, 4) + "  " + num.substring(5, 9) + "  " + num.substring(10, 14) + "  " + num.substring(15) : num;
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString(formattedNum, 14, yOffset + 78);

            g2.setColor(new Color(200, 220, 235));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 7));
            g2.drawString("VALID THRU", 14, yOffset + 98);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            g2.drawString(expiry, 64, yOffset + 99);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.drawString(customer.getName().toUpperCase(), 14, yOffset + 120);

            if (theme.equals("BLUE")) {
                g2.setColor(new Color(247, 182, 0));
                g2.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 13));
                g2.drawString("VISA", w - 46, yOffset + 122);
            } else {
                int mx = w - 42, my = yOffset + 110, mr = 13;
                g2.setColor(new Color(235, 0, 27));
                g2.fillOval(mx, my, mr, mr);
                g2.setColor(new Color(247, 158, 27));
                g2.fillOval(mx + 9, my, mr, mr);
            }

            if (isHovered) {
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(w / 2 - 58, yOffset + 136, 116, 16, 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
                g2.drawString("CLICK TO INSERT CARD", w / 2 - 50, yOffset + 148);
            }

            g2.dispose();
        }
    }

    /** Authentic custom painted card brand badge component. */
    @SuppressWarnings("serial")
    private static class CardBrandBadge extends JPanel {
        private final String brand;

        CardBrandBadge(String brand) {
            this.brand = brand;
            int width = brand.equals("NPSB") ? 82 : brand.equals("MASTERCARD") ? 64 : 58;
            setPreferredSize(new Dimension(width, 28));
            setMaximumSize(new Dimension(width, 28));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, w - 2, h - 2, 6, 6);
            g2.setColor(new Color(185, 200, 210));
            g2.drawRoundRect(1, 1, w - 3, h - 3, 6, 6);

            if (brand.equals("VISA")) {
                g2.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 14));
                g2.setColor(new Color(26, 31, 113));
                g2.drawString("VISA", 12, 19);
                g2.setColor(new Color(247, 182, 0));
                int[] vx = {12, 17, 12};
                int[] vy = {9, 9, 13};
                g2.fillPolygon(vx, vy, 3);
            } else if (brand.equals("MASTERCARD")) {
                int cx = 15, cy = 6, r = 16;
                g2.setColor(new Color(235, 0, 27));
                g2.fillOval(cx, cy, r, r);
                g2.setColor(new Color(247, 158, 27));
                g2.fillOval(cx + 12, cy, r, r);
                g2.setColor(new Color(255, 95, 0, 210));
                java.awt.geom.Area a1 = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx, cy, r, r));
                java.awt.geom.Area a2 = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx + 12, cy, r, r));
                a1.intersect(a2);
                g2.fill(a1);
            } else if (brand.equals("UNIONPAY")) {
                int startX = 6;
                g2.setColor(new Color(226, 26, 34));
                g2.fillRoundRect(startX, 6, 8, 16, 3, 3);
                g2.setColor(new Color(0, 44, 108));
                g2.fillRoundRect(startX + 6, 6, 8, 16, 3, 3);
                g2.setColor(new Color(0, 121, 52));
                g2.fillRoundRect(startX + 12, 6, 8, 16, 3, 3);
                g2.setColor(new Color(0, 44, 108));
                g2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 9));
                g2.drawString("Union", startX + 22, 13);
                g2.setColor(new Color(226, 26, 34));
                g2.drawString("Pay", startX + 22, 22);
            } else if (brand.equals("NPSB")) {
                g2.setColor(new Color(0, 106, 78));
                g2.fillRoundRect(4, 5, 34, 18, 4, 4);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.drawString("NPSB", 7, 18);

                g2.setColor(new Color(227, 27, 35));
                g2.fillRoundRect(41, 5, 37, 18, 4, 4);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
                g2.drawString("Q-Cash", 43, 17);
            } else if (brand.equals("EMV")) {
                g2.setColor(new Color(225, 180, 60));
                g2.fillRoundRect(6, 6, 17, 16, 3, 3);
                g2.setColor(new Color(160, 120, 30));
                g2.drawRoundRect(6, 6, 17, 16, 3, 3);
                g2.drawLine(6, 14, 23, 14);
                g2.drawLine(14, 6, 14, 22);

                g2.setColor(new Color(30, 80, 120));
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawArc(24, 7, 10, 14, -45, 90);
                g2.drawArc(28, 5, 14, 18, -45, 90);
                g2.drawArc(32, 3, 18, 22, -45, 90);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 7));
                g2.drawString("CHIP", 37, 24);
            }
            g2.dispose();
        }
    }

    private void handleKey(String value) {
        if (activePinField == null || !activePinField.isShowing()) return;
        SoundEffects.keypad();
        String current = new String(activePinField.getPassword());
        if (value.equals("C")) {
            activePinField.setText("");
            pinDotsIndicator.setFilledCount(0);
        } else if (value.equals("ENTER")) {
            authenticatePending(current);
        } else if (current.length() < 4) {
            String updated = current + value;
            activePinField.setText(updated);
            pinDotsIndicator.setFilledCount(updated.length());
        }
        activePinField.requestFocusInWindow();
    }

    /** Small painted hardware port used to make card, cash, and receipt actions visible. */
    @SuppressWarnings("serial")
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
        private String cardHolder = "CARD HOLDER";
        private String cardLastFour = "0000";
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
        void setCardDetails(String holderName, String cardNumber) {
            cardHolder = abbreviatedCardHolder(holderName);
            cardLastFour = cardNumber.substring(Math.max(0, cardNumber.length() - 4));
            repaint();
        }
        private String abbreviatedCardHolder(String holderName) {
            String[] parts = holderName.trim().split("\\s+");
            if (parts.length < 2) return holderName.toUpperCase();
            StringBuilder abbreviated = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                abbreviated.append(Character.toUpperCase(parts[i].charAt(0))).append(". ");
            }
            return abbreviated.append(parts[parts.length - 1].toUpperCase()).toString();
        }

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
                int cardW = 68;
                int x = (w - cardW) / 2, y = (int) (102 - eased * 98);
                java.awt.geom.AffineTransform originalTransform = g2.getTransform();
                double scale = cardW / 58.0;
                g2.translate(x, y); g2.scale(scale, scale);
                g2.setPaint(new GradientPaint(0, 0, new Color(20, 113, 183), 58, 92, new Color(6, 42, 89)));
                g2.fillRoundRect(0, 0, 58, 92, 8, 8);
                g2.setColor(new Color(154, 217, 239)); g2.drawRoundRect(0, 0, 57, 91, 8, 8);
                g2.setColor(new Color(206, 233, 247)); g2.setFont(new Font("Segoe UI", Font.BOLD, 6)); g2.drawString("BITHM", 15, 12);
                g2.drawString("NATIONAL", 10, 19);
                g2.setColor(new Color(220, 185, 74)); g2.fillRoundRect(18, 28, 22, 17, 3, 3);
                g2.setColor(new Color(173, 132, 42)); g2.drawLine(18, 36, 40, 36);
                g2.drawLine(29, 28, 29, 45);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Monospaced", Font.PLAIN, 6));
                g2.drawString("****", 18, 58); g2.drawString("****", 18, 66); g2.drawString(cardLastFour, 18, 74);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 4));
                int holderWidth = g2.getFontMetrics().stringWidth(cardHolder);
                g2.drawString(cardHolder, (58 - holderWidth) / 2, 84);
                g2.setTransform(originalTransform);
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
                g2.setFont(new Font("Monospaced", Font.PLAIN, 5)); g2.drawString("BNB * AUTHENTIC", x + 8, y + 22);
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
