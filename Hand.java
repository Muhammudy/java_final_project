import java.util.ArrayList;

public class Hand {
    private double betAmount;
    ArrayList<Card> hand;

    public Hand(double betAmount) {
        this.betAmount = betAmount;
        this.hand = new ArrayList<Card>();

    }

    public void addCard(Card card) { //added for testing, may or may not keep
        this.hand.add(card);
    }

    public int getValue(){// returns value of hand and accounts for aces
        int total = 0;
        int aces = 0;
        for(Card card : hand){
            if(card.getValue() == 11){
                aces++;
                total += card.getValue();
            }
            else{
                total += card.getValue();
            }
        }

        for(int i = 0; i < aces; ++i){
            if(total > 21){
                total -= 10;

            }
            else {break;}
        }
        return total;
    }

    public boolean isBlackjack(){
        return this.getValue() == 21;
    }

    public boolean isBusted(){
        return this.getValue() > 21;
    }

    public boolean canSplit(){
        if(hand.size() != 2){
            return false;
        }

        else if(hand.get(0).getValue() == hand.get(1).getValue()){
            return true;
        }

        else{
            return false;
        }
    }
}