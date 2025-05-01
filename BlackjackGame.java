import java.util.Map;
import java.util.function.Consumer;
import java.io.Serializable;

import java.util.ArrayList;

public class BlackjackGame implements Serializable {

    private final Deck deck = new Deck();
    private final Player player = new Player(500);
    private final Dealer dealer = new Dealer();

    private transient Consumer<String> notifier = s -> {
    };

    public void setNotifier(Consumer<String> n) {
        notifier = n;
    }

    public Hand playerHand() {
        return player.getHands().isEmpty() ? null : player.getHands().get(0);
    }

    public Hand dealerHand() {
        return dealer.getHands().isEmpty() ? null : dealer.getHands().get(0);
    }

    public int bankroll() {
        return player.bankroll();
    }

    public double balance() {
        return player.bankroll();
    }

    public boolean startRound(double cashBet) {
        int bet = (int) cashBet;
        if (bet <= 0 || bet > bankroll())
            return false;

        if (!player.takeBet(Map.of(Chip.ONE, bet)))
            return false;

        player.getHands().clear();
        dealer.getHands().clear();
        if (deck.getUsedCardsNum() > 35)
            deck.shuffle();

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
        if (playerHand().isBusted())
            stand();
    }

    public void stand() {
        Hand p = playerHand(), d = dealerHand();
        int bet = p.bet();

        boolean playerBJ = p.isBlackjack();
        boolean dealerBJ = d.isBlackjack();

        // Handle Blackjacks before dealer plays
        if (playerBJ || dealerBJ) {
            if (playerBJ && dealerBJ) {
                // Push
                player.payout(bet);
            } else if (playerBJ) {
                // Player wins with 3:2 payout
                player.payout(bet + (int) (1.5 * bet));
            }
            notifier.accept("END");
            checkBankroll();
            return;
        }

        // No Blackjack — continue with dealer play
        dealer.playTurn(deck);

        if (!p.isBusted()) {
            if (d.isBusted() || p.getValue() > d.getValue()) {
                player.payout(2 * bet);
            } else if (p.getValue() == d.getValue()) {
                player.payout(bet);
            }
        }

        notifier.accept("END");
        checkBankroll();
    }

    private void checkBankroll() {
        if (bankroll() <= 0) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null, "You're out of money, back to the Lobby!");
            });
            resetGame();
        }
    }

    public void resetGame() {
        notifier.accept("RESET");

    }

    public String outcomeString() {
        Hand p = playerHand(), d = dealerHand();
        int bet = p.bet();
        if (p.isBusted())
            return "You bust  -$" + bet;
        if (d.isBusted())
            return "Dealer busts! +$" + bet;
        if (p.getValue() > d.getValue())
            return "You win   +$" + bet;
        if (p.getValue() < d.getValue())
            return "Dealer wins -$" + bet;
        return "Push – bet returned";
    }

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

    public String shuffle() {
        return deck.shuffle();
    }
}
