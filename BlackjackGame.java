import java.util.Map;
import java.util.function.Consumer;
import java.io.Serializable;

import java.util.ArrayList;

public class BlackjackGame implements Serializable {

    private final Deck   deck   = new Deck();
    private final Player player = new Player(500);   // $500 buy-in
    private final Dealer dealer = new Dealer();

    private transient Consumer<String> notifier = s -> {};

    public void setNotifier(Consumer<String> n) { notifier = n; }

    public Hand playerHand() { return player.getHands().isEmpty() ? null : player.getHands().get(0); }
    public Hand dealerHand() { return dealer.getHands().isEmpty() ? null : dealer.getHands().get(0); }

    public int bankroll() { return player.bankroll(); }
    public double balance() { return player.bankroll(); } // for GUI compatibility

    /* ---------- round flow ---------- */

    public boolean startRound(double cashBet) {
        int bet = (int) cashBet;
        if (bet <= 0 || bet > bankroll()) return false;

        // bet = pile of $1 chips for now
        if (!player.takeBet(Map.of(Chip.ONE, bet))) return false;

        player.getHands().clear();
        dealer.getHands().clear();
        if (deck.getUsedCardsNum() > 35) deck.shuffle();

        Hand p = new Hand(bet);
        Hand d = new Hand(0);
        player.getHands().add(p);
        dealer.getHands().add(d);

        for (int i = 0; i < 2; i++) {
            p.addCard(deck.drawCard());
            d.addCard(deck.drawCard());
        }
        notifier.accept("NEW");
        return true;
    }

    public void hit() {
        player.hit(playerHand(), deck);
        notifier.accept("HIT");
        if (playerHand().isBusted()) stand();   // auto-stand on bust
    }

    public void stand() {
        dealer.playTurn(deck);

        Hand p = playerHand(), d = dealerHand();
        int bet = p.bet();

        if (!p.isBusted()) {
            if (d.isBusted() || p.getValue() > d.getValue())
                player.payout(2 * bet);            // win
            else if (p.getValue() == d.getValue())
                player.payout(bet);                // push
        }
        notifier.accept("END");

        // Check if bankroll is zero and reset game if so
        if (bankroll() == 0) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null, "You're out of money!");
            });
            resetGame();
        }
    }

    public void resetGame() {
        player.resetBankroll(500);     // fresh buy-in
        player.getHands().clear();
        dealer.getHands().clear();
        deck.shuffle();
    }

    public String outcomeString() {
        Hand p = playerHand(), d = dealerHand();
        int bet = p.bet();
        if (p.isBusted())                return "You bust  -$" + bet;
        if (d.isBusted())                return "Dealer busts! +$" + bet;
        if (p.getValue() > d.getValue()) return "You win   +$" + bet;
        if (p.getValue() < d.getValue()) return "Dealer wins -$" + bet;
        return "Push – bet returned";
    }

    // --- Methods for GUI save/load support ---

    public void setBalance(double balance) {
        player.resetBankroll((int) balance);
    }

    public void setPlayerHand(Hand h) {
        player.getHands().clear();
        player.getHands().add(h);
    }

    public void setDealerHand(Hand h) {
        dealer.getHands().clear();
        dealer.getHands().add(h);
    }

    public void setDeck(Deck d) {
        deck.copyFrom(d);
    }

    public Deck deck() {
        return this.deck;
    }
}

