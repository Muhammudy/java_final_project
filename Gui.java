import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Gui extends JFrame {

    private static final Color FELT = new Color(0, 102, 0);
    private static final int   W = 80, H = 115;

    private final BlackjackGame game = new BlackjackGame();

    private final Map<String, ImageIcon> imgs = loadImages();

    private final JPanel dealerPanel = new JPanel();
    private final JPanel playerPanel = new JPanel();

    private final JLabel  balanceLbl = new JLabel();
    private final JTextField betFld   = new JTextField("0", 4);
    private final JButton dealBtn  = new JButton("Deal");
    private final JButton hitBtn   = new JButton("Hit");
    private final JButton standBtn = new JButton("Stand");
    private final JButton clearBtn = new JButton("Clear");

    // List to hold chip buttons for easy enable/disable
    private final java.util.List<JButton> chipButtons = new ArrayList<>();

    public Gui() {
        super("Blackjack");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        dealerPanel.setBorder(BorderFactory.createTitledBorder("Dealer"));
        playerPanel.setBorder(BorderFactory.createTitledBorder("You"));
        dealerPanel.setBackground(FELT);
        playerPanel.setBackground(FELT);

        add(dealerPanel, BorderLayout.NORTH);
        add(playerPanel, BorderLayout.CENTER);
        add(buildControls(), BorderLayout.SOUTH);

        game.setNotifier(evt -> refresh(evt));  
        refresh("INIT");
        setVisible(true);
    }

    private JPanel buildControls() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        p.add(new JLabel("Bet $"));
        p.add(betFld);

        // Add chip buttons
        addChipButton(p, "ChipYellow1.png", 1);
        addChipButton(p, "ChipRed5.png", 5);
        addChipButton(p, "ChipBlue10.png", 10);
        addChipButton(p, "ChipGreen25.png", 25);
        addChipButton(p, "BlackChip100.png", 100);

        p.add(clearBtn);
        p.add(dealBtn);
        p.add(hitBtn);
        p.add(standBtn);
        p.add(balanceLbl);

        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);

        clearBtn.addActionListener(e -> betFld.setText("0"));

        dealBtn.addActionListener(this::dealAction);
        hitBtn.addActionListener(e -> game.hit());
        standBtn.addActionListener(e -> game.stand());

        return p;
    }

    private void addChipButton(JPanel parent, String fileName, int value) {
        Image img = new ImageIcon("cards/" + fileName).getImage()
                .getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        JButton btn = new JButton(new ImageIcon(img));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setToolTipText("Add $" + value);
        btn.addActionListener(e -> incrementBet(value));
        parent.add(btn);
        chipButtons.add(btn);
    }

    private void incrementBet(int dollars) {
        betFld.setText(String.valueOf(parseBet() + dollars));
    }

    private int parseBet() {
        try { return Integer.parseInt(betFld.getText().trim()); }
        catch (NumberFormatException ex) { return 0; }
    }

    private void dealAction(ActionEvent e) {
        int wager = parseBet();
        if (game.startRound(wager)) {
            toggleChipButtons(false);
            dealBtn.setEnabled(false);
            hitBtn.setEnabled(true);
            standBtn.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid bet or insufficient bankroll.");
        }
    }

    private void toggleChipButtons(boolean enabled) {
        chipButtons.forEach(b -> b.setEnabled(enabled));
    }

    private void refresh(String evt) {
        dealerPanel.removeAll();
        playerPanel.removeAll();

        drawHand(game.dealerHand(), dealerPanel, !evt.equals("END"));
        drawHand(game.playerHand(), playerPanel, false);

        balanceLbl.setText("Bankroll: $" + game.bankroll());

        dealerPanel.revalidate(); dealerPanel.repaint();
        playerPanel.revalidate(); playerPanel.repaint();

        if (evt.equals("END")) {
            hitBtn.setEnabled(false);
            standBtn.setEnabled(false);
            dealBtn.setEnabled(true);
            toggleChipButtons(true);
            JOptionPane.showMessageDialog(this, game.outcomeString());
            betFld.setText("0");
        }
    }

    private void drawHand(Hand h, JPanel p, boolean hideFirst) {
        if (h == null) return;
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        int i = 0;
        for (Card c : h.getCards()) {
            ImageIcon ico = (i == 0 && hideFirst) ? imgs.get("BACK") : imgs.get(key(c));
            p.add(new JLabel(ico));
            i++;
        }
    }

    private String key(Card c) {
        int n = c.getNumber();
        String r = switch (n) {
            case 1 -> "A"; case 11 -> "J"; case 12 -> "Q"; case 13 -> "K";
            default -> String.valueOf(n);
        };
        return r + "-" + c.getSuit().charAt(0);
    }

    private ImageIcon icon(String path) {
        Image img = new ImageIcon(path).getImage()
                .getScaledInstance(W, H, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private Map<String, ImageIcon> loadImages() {
        Map<String, ImageIcon> m = new HashMap<>();
        String base = "cards/";
        String[] s = {"C", "D", "H", "S"};
        String[] r = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        for (String su : s)
            for (String ra : r)
                m.put(ra + "-" + su, icon(base + ra + "-" + su + ".png"));
        m.put("BACK", icon(base + "BACK.png"));
        return m;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gui::new);
    }
}
