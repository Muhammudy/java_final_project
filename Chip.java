public enum Chip {
    ONE(1), FIVE(5), TEN(10), TWENTYFIVE(25), HUNDRED(100);
    private final int value;
    Chip(int value) { this.value = value; }
    public int value() { return value; }
    public static int total(java.util.Map<Chip, Integer> chips) {
        return chips.entrySet().stream().mapToInt(e -> e.getKey().value * e.getValue()).sum();
    }
}
//This enum defines every chip color in the game and stores its dollar value, so elsewhere in the code 
// we can quickly ask any chip for its worth or total up a whole pile of chips into a single cash amount
