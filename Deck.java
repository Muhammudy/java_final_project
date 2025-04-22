import java.util.Stack;
import java.util.Collections;

public class Deck {
    private Stack<Card> cards;
    private int usedCardsNum;

    public Deck() {
        cards = new Stack<>();
        usedCardsNum = 0;
        String[] suits = { "Hearts", "Diamonds", "Spades", "Clubs" };
        for (String s : suits) {
            for (int n = 1; n <= 13; n++) {
                cards.push(new Card(n, s));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
        usedCardsNum = 0;
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        usedCardsNum++;
        return cards.pop();
    }

    public int getUsedCardsNum() {
        return usedCardsNum;
    }
}
