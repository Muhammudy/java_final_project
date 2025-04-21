import java.util.function.Consumer;

/** Pure game logic. No Swing imports, no printing. */
public class BlackjackGame {

    private final Deck   deck   = new Deck();
    private final Player player = new Player(500);
    private final Dealer dealer = new Dealer();

    private Consumer<String> notifier = s -> {};
    public  void setNotifier(Consumer<String> n) { notifier = n; }

    public Hand  playerHand() {
        return player.getHands().isEmpty() ? null : player.getHands().get(0);
    }
    public Hand  dealerHand() {
        return dealer.getHands().isEmpty() ? null : dealer.getHands().get(0);
    }
    public double balance()   { return player.getBalance(); }

    public boolean startRound(double bet) {
        if (bet <= 0 || bet > player.getBalance()) return false;

        player.getHands().clear();
        dealer.getHands().clear();
        if (deck.getUsedCardsNum() > 35) deck.shuffle();

        Hand p = new Hand(bet);
        Hand d = new Hand(0);

        player.adjustBalance(-bet);
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
        if (playerHand().isBusted()) stand();   
    }

    public void stand() {
        dealer.playTurn(deck);
        Hand p = playerHand(), d = dealerHand();
        double bet = p.getBet();

        if (p.isBusted())               {/* lose, bet already deducted */}
        else if (d.isBusted())          player.adjustBalance(2 * bet);
        else if (p.getValue() > d.getValue()) player.adjustBalance(2 * bet);
        else if (p.getValue() == d.getValue()) player.adjustBalance(bet);

        notifier.accept("END");
    }

    public String outcomeString() {
        Hand p = playerHand(), d = dealerHand();
        double bet = p.getBet();
        if (p.isBusted())                 return "You bust! -$" + bet;
        if (d.isBusted())                 return "Dealer busts! +$" + (2 * bet);
        if (p.getValue() > d.getValue())  return "You win! +$" + (2 * bet);
        if (p.getValue() < d.getValue())  return "Dealer wins! -$" + bet;
        return "Push. Bet returned.";
    }
}
