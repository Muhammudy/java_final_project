public class Dealer extends Player {
    public Dealer() {
        super(0);
    }

    public void playTurn(Deck deck) {
        Hand hand = getHands().get(0);
        while (hand.getValue() < 17) {
            hand.addCard(deck.drawCard());
        }
    }
}
