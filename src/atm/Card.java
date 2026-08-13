package atm;

import java.time.LocalDate;

/**
 * <<entity>> Card
 * Physical ATM card presented by a Customer during a session.
 */
public class Card {
    private String cardNumber;
    private LocalDate expiryDate;
    private String pin;

    public Card(String cardNumber, LocalDate expiryDate, String pin) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.pin = pin;
    }

    /** + validate(inputPin: String) : boolean */
    public boolean validate(String inputPin) {
        return this.pin != null && this.pin.equals(inputPin);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}
