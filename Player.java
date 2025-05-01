import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.io.Serializable;

public class Player implements Serializable {
    private final EnumMap<Chip, Integer> chips = new EnumMap<>(Chip.class);
    private final ArrayList<Hand> hands = new ArrayList<>();

    public Player(int buyInDollars) {
        addChips(buyInDollars);
    }

    // For compatibility with GUI code that uses balance as double
    public double getBalance() {
        return bankroll();
    }

    public void setBalance(double balance) {
        chips.clear();
        addChips((int) balance);
    }

    public void adjustBalance(double delta) {
        // Not used with chips, but provided for compatibility
        addChips((int) delta);
    }

    /* ---------- chip helpers ---------- */

    public int bankroll() {
        return Chip.total(chips);
    }

    public void addChips(int dollars) {
        for (Chip c : Chip.values()) {
            int n = dollars / c.value();
            chips.merge(c, n, Integer::sum);
            dollars -= n * c.value();
        }
    }

    /** Remove the chips listed in wager; return false if player is short. */
    public boolean takeBet(Map<Chip, Integer> wager) {
        for (var e : wager.entrySet())
            if (chips.getOrDefault(e.getKey(), 0) < e.getValue()) return false;
        wager.forEach((c, n) -> chips.merge(c, -n, Integer::sum));
        return true;
    }

    /** Positive dollars → winnings (or returned bet). */
    public void payout(int dollars) {
        addChips(dollars);
    }

    /* ---------- helpers the game/GUI already use ---------- */

    public void hit(Hand h, Deck d) {
        h.addCard(d.drawCard());
    }

    public ArrayList<Hand> getHands() {
        return hands;
    }

    public void resetBankroll(int buyIn) {
        chips.clear();
        addChips(buyIn);
    }
}
