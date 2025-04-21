import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand;
    private double bet;

    public Hand(double bet) {
        this.bet = bet;
        hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public ArrayList<Card> getCards() {
        return hand;
    }

    public double getBet() {
        return bet;
    }

    public int getValue() {
        int total = 0, aces = 0;
        for (Card c : hand) {
            int v = c.getValue();
            total += v;
            if (v == 11) aces++;
        }
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    public boolean isBlackjack() {
        return hand.size() == 2 && getValue() == 21;
    }

    public boolean isBusted() {
        return getValue() > 21;
    }

    public boolean canSplit() {
        return hand.size() == 2 && hand.get(0).getValue() == hand.get(1).getValue();
    }

    @Override
    public String toString() {
        return hand.toString();
    }
}
