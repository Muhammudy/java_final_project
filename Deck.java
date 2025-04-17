import java.util.Stack;
import Java.util.Collections;

public class Deck {
    Stack<Card> cards;

    private int usedCards;

    public Deck() {
        cards = new Stack<>();
        this.usedCards = 0;
        initilizeDeck(); // initilize the deck
        shuffle(); // shuffle the cards
    }

    public void initilizeDeck() {
        String[] suits = { "Hearts", "Diamonds", "Spades", "Clubs" };

        for (String suit : suits) {
            for (int number = 0; number <= 13; number++) {
                cards.push(new Card(number, suit));

            }
        }

    }

    public void shuffle() {
        Collections.shuffle(cards);
        this.usedCards = 0;
    }

    public Card drawCard() {
        if (!cards.isEmpty()) { // not empty
            usedCards++;
            return cards.pop();

        } else {
            System.out.println("No More Cards Left!");
            return null;
        }

    }

    public int cardsRemaining() {
        return cards.size();
    }

}