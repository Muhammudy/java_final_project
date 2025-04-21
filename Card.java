public class Card {
    private int number;
    private String suit;

    public Card(int number, String suit) {
        this.number = number;
        this.suit = suit;
    }

    public int getValue() {
        if (number == 1) return 11;         
        if (number >= 10) return 10;          
        return number;
    }

    public int getNumber() {
        return number;
    }

    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return number + " of " + suit;
    }
}
