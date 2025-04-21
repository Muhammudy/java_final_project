import javax.swing.*;
import java.awt.*;
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
    private final JTextField betFld   = new JTextField("50", 4);
    private final JButton dealBtn  = new JButton("Deal");
    private final JButton hitBtn   = new JButton("Hit");
    private final JButton standBtn = new JButton("Stand");

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
        p.add(dealBtn);
        p.add(hitBtn);
        p.add(standBtn);
        p.add(balanceLbl);

        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);

        dealBtn.addActionListener(e -> {
            if (game.startRound(Double.parseDouble(betFld.getText()))) {
                hitBtn.setEnabled(true);
                standBtn.setEnabled(true);
                dealBtn.setEnabled(false);
            }
        });
        hitBtn  .addActionListener(e -> game.hit());
        standBtn.addActionListener(e -> game.stand());
        return p;
    }

    private void refresh(String evt) {
        dealerPanel.removeAll();
        playerPanel.removeAll();

        drawHand(game.dealerHand(), dealerPanel, !evt.equals("END"));
        drawHand(game.playerHand(), playerPanel, false);

        balanceLbl.setText("Balance: $" + String.format("%.2f", game.balance()));

        dealerPanel.revalidate(); dealerPanel.repaint();
        playerPanel.revalidate(); playerPanel.repaint();

        if (evt.equals("END")) {
            hitBtn.setEnabled(false);
            standBtn.setEnabled(false);
            dealBtn.setEnabled(true);
            JOptionPane.showMessageDialog(this, game.outcomeString());
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
