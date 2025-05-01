import java.io.Serializable;

public class Card implements Serializable {

    private final int number;   // 1-13  (Ace..King)
    private final String suit;  // "Hearts", "Diamonds", ...

    public Card(int number, String suit) {
        this.number = number;
        this.suit   = suit;
    }

    public int getNumber() { return number; }
    public String getSuit() { return suit; }

    @Override
    public String toString() { return number + " of " + suit; }

    public java.awt.Image getImage() {
        String rank = switch (number) {
            case 1  -> "A";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> String.valueOf(number);
        };
        char s = suit.charAt(0);                 // H, D, S, C
        String path = "cards/" + rank + "-" + s + ".png";

        // one-time scale to 80×115 for consistent size
        return new javax.swing.ImageIcon(path)
                 .getImage()
                 .getScaledInstance(80, 115, java.awt.Image.SCALE_SMOOTH);
    }

    public int getValue() {
        if (number == 1) {
            return 11;
        } else if (number >= 10) {
            return 10;
        } else {
            return number;
        }
    }
}
