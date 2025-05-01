import java.util.Stack;
import java.util.Collections;
import java.io.Serializable;
// ‼  ArrayList import is no longer needed, so delete it.

public class Deck implements Serializable {
    private Stack<Card> cards;
    private int usedCardsNum;          // ← this is the only counter field

    public Deck() {
        cards = new Stack<>();
        usedCardsNum = 0;
        String[] suits = { "Hearts", "Diamonds", "Spades", "Clubs" };
        for (String s : suits)
            for (int n = 1; n <= 13; n++)
                cards.push(new Card(n, s));
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
        usedCardsNum = 0;
    }

    public Card drawCard() {
        if (cards.isEmpty()) return null;
        usedCardsNum++;
        return cards.pop();
    }

    public int getUsedCardsNum() { return usedCardsNum; }

    /** Replace this deck’s content with another deck’s content. */
    public void copyFrom(Deck other) {
        // clone the stack so we don’t share the same object
        this.cards = new Stack<>();
        this.cards.addAll(other.cards);

        // copy the counter
        this.usedCardsNum = other.usedCardsNum;
    }
}
