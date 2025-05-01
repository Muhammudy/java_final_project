import java.util.Stack;
import java.util.Collections;
import java.io.Serializable;

public class Deck implements Serializable {
    private Stack<Card> cards;
    private int usedCardsNum;

    public Deck() {
        cards = new Stack<>();
        usedCardsNum = 0;
        String[] suits = { "Hearts", "Diamonds", "Spades", "Clubs" };
        for (String s : suits)
            for (int n = 1; n <= 13; n++)
                cards.push(new Card(n, s));
        shuffle();
    }

    public String shuffle() {
        Collections.shuffle(cards);
        usedCardsNum = 0;

        return "Deck shuffled successfully";
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            System.out.println("Deck is empty. Creating a new deck.");
            this.cards = new Stack<>();
            String[] suits = { "Hearts", "Diamonds", "Spades", "Clubs" };
            for (String s : suits) {
                for (int n = 1; n <= 13; n++) {
                    cards.push(new Card(n, s));
                }
            }
            shuffle();
        }

        usedCardsNum++;
        return cards.pop();
    }

    public int getUsedCardsNum() {
        return usedCardsNum;
    }

    public void copyFrom(Deck other) {

        this.cards = new Stack<>();
        this.cards.addAll(other.cards);

        this.usedCardsNum = other.usedCardsNum;
    }
}
