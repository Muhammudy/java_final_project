import java.io.Serializable;
import java.util.ArrayList;

public class Hand implements Serializable {
    private final ArrayList<Card> cards = new ArrayList<>();
    private final int wager;

    public Hand(int wager) {
        this.wager = wager;
    }

    public int bet() {
        return wager;
    }

    public void addCard(Card card) {
        if (card != null) {
            cards.add(card);
        } else {
            System.out.println("Attempted to add a null card to the hand");
        }
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public int getValue() {
        int total = 0, aces = 0;
        for (Card c : cards) {
            int v = c.getValue();
            total += v;
            if (v == 11)
                aces++;
        }
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    public boolean isBusted() {
        return getValue() > 21;
    }

    public boolean canSplit() {
        return cards.size() == 2 && cards.get(0).getValue() == cards.get(1).getValue();
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}
