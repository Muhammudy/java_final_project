import java.util.ArrayList;

public class Player {
    private double balance;
    private ArrayList<Hand> hands;

    public Player(double balance) {
        this.balance = balance;
        hands = new ArrayList<>();
    }

    public void placeBet(double amount) {
    }

    public void hit(Hand hand, Deck deck) {
        hand.addCard(deck.drawCard());
    }

    public void stand(Hand hand) {
    }

    public void split(Hand hand) {
    }

    public void doubleDown(Hand hand, Deck deck) {
    }

    public void placeInsurance() {
    }

    public double getBalance() {
        return balance;
    }

    public void adjustBalance(double delta) {
        balance += delta;
    }

    public ArrayList<Hand> getHands() {
        return hands;
    }
}
