import java.util.function.Consumer;

public class BlackjackGame {

    private final Deck deck = new Deck();
    private final Player player = new Player(500);
    private final Dealer dealer = new Dealer();

    private Consumer<String> notifier = s -> {
    };

    public void setNotifier(Consumer<String> n) {
        notifier = n;
    }

    public Hand playerHand() {
        if (player.getHands().isEmpty()) {
            return null;
        } else {
            return player.getHands().get(0);
        }

    }

    public Hand dealerHand() {
        if (dealer.getHands().isEmpty()) {
            return null;
        } else {
            return dealer.getHands().get(0);
        }

    }

    public double balance() {
        return player.getBalance();
    }

    public boolean startRound(double bet) {
        if (bet <= 0 || bet > player.getBalance()) {
            return false;
        }
        player.getHands().clear();
        dealer.getHands().clear();
        if (deck.getUsedCardsNum() > 35) {
            deck.shuffle();
        }

        Hand p = new Hand(bet); // player
        Hand d = new Hand(0); // dealer

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
        if (playerHand().isBusted()) {
            stand();
        }
    }

    public void stand() {

        dealer.playTurn(deck);

        Hand playerHand = playerHand();
        Hand dealerHand = dealerHand();

        double bet = playerHand.getBet();

        if (playerHand.isBusted()) {
            // already subtracted

        } else if (dealerHand.isBusted()) {
            player.adjustBalance(2 * bet);
        } else if (playerHand.getValue() > dealerHand.getValue()) {
            player.adjustBalance(2 * bet);
        } else if (playerHand.getValue() == dealerHand.getValue()) {
            player.adjustBalance(bet);
        }

        notifier.accept("END");
    }

    public String outcomeString() {
        Hand playerHand = playerHand();
        Hand dealerHand = dealerHand();
        double bet = playerHand.getBet();

        if (playerHand.isBusted()) {
            return "You bust! -$" + bet;
        }
        if (dealerHand.isBusted()) {
            return "Dealer busts! +$" + (2 * bet);
        }
        if (playerHand.getValue() > dealerHand.getValue()) {
            return "You win! +$" + (2 * bet);
        }
        if (playerHand.getValue() < dealerHand.getValue()) {
            return "Dealer wins! -$" + bet;
        }

        return "Push. Bet returned.";
    }

}
