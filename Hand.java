import java.util.ArrayList;

public class Hand {
    private double betAmount;
    ArrayList<Card> hand;

    public Hand(double betAmount) {
        this.betAmount = betAmount;
        this.hand = new ArrayList<Card>();

    }

}