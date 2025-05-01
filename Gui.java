import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Gui extends JFrame implements Serializable {

    private static final Color FELT = new Color(0, 102, 0);
    private static final int W = 150, H = 200;

    private final BlackjackGame game = new BlackjackGame();
    private final Map<String, ImageIcon> imgs = loadImages();

    private final JPanel dealerPanel = new JPanel();
    private final JPanel playerPanel = new JPanel();

    private final JPanel startPanel = new BackgroundPanel();

    private final JButton loadGameBtn = new JButton("Load Previous Game");
    private final File saveFile = new File("game.ser");
    private final JButton newGameBtn = new JButton("Start a New Game");
    private final JLabel titleLabel = new JLabel();

    private final JLabel balanceLbl = new JLabel();
    private final JTextField betFld = new JTextField("50", 4);
    private final JButton dealBtn = new JButton("Deal");
    private final JButton hitBtn = new JButton("Hit");
    private final JButton standBtn = new JButton("Stand");
    private final JButton saveState = new JButton("Save");
    private final JButton clearBtn = new JButton("Clear Bet");
    private final ArrayList<JButton> chipButtons = new ArrayList<>();

    private Font customFont;

    public Gui() {
        super("Blackjack");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        try {
            customFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("font/BLACKJAR.TTF")).deriveFont(70f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            customFont = new Font("Serif", Font.PLAIN, 18); // fallback font
        }

        // Configure panels
        dealerPanel.setBorder(BorderFactory.createTitledBorder("Dealer"));
        playerPanel.setBorder(BorderFactory.createTitledBorder("You"));
        dealerPanel.setBackground(FELT);
        playerPanel.setBackground(FELT);

        if (!saveFile.exists()) {
            loadGameBtn.setEnabled(false);
        } else {
            loadGameBtn.setEnabled(true);
        }

        // Start screen layout
        titleLabel.setText("Welcome To Blackjack!");
        titleLabel.setFont(customFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        startPanel.setLayout(new BorderLayout());
        startPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(newGameBtn);
        buttonPanel.add(loadGameBtn);
        startPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add start screen first
        add(startPanel, BorderLayout.CENTER);

        // Action listeners for start screen
        newGameBtn.addActionListener(e -> startNewGame());
        loadGameBtn.addActionListener(e -> loadGame());

        setVisible(true);
    }

    private void startNewGame() {
        remove(startPanel);
        add(dealerPanel, BorderLayout.NORTH);
        add(playerPanel, BorderLayout.CENTER);
        add(buildControls(), BorderLayout.SOUTH);

        game.setNotifier(evt -> refresh(evt));
        refresh("INIT");
    }

    private void loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("game.ser"))) {
            BlackjackGame loadedGame = (BlackjackGame) in.readObject();
            this.game.setBalance(loadedGame.balance());
            this.game.setPlayerHand(loadedGame.playerHand());
            this.game.setDealerHand(loadedGame.dealerHand());
            this.game.setDeck(loadedGame.deck());
            remove(startPanel);
            add(dealerPanel, BorderLayout.NORTH);
            add(playerPanel, BorderLayout.CENTER);
            add(buildControls(), BorderLayout.SOUTH);
            game.setNotifier(evt -> refresh(evt));

            refresh("INIT");
            JOptionPane.showMessageDialog(this, "Game loaded successfully!");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load game.");
        }
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
        p.add(saveState);

        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);
        saveState.setEnabled(false);

        clearBtn.addActionListener(e -> betFld.setText("0"));

        dealBtn.addActionListener(e -> {
            if (game.startRound(Double.parseDouble(betFld.getText()))) {
                toggleChipButtons(false);
                dealBtn.setEnabled(false);
                hitBtn.setEnabled(true);
                standBtn.setEnabled(true);
                saveState.setEnabled(false);
            }
        });

        saveState.addActionListener(e -> {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("game.ser"))) {
                out.writeObject(game);
                JOptionPane.showMessageDialog(this, "Game saved successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save game");
            }
        });

        hitBtn.addActionListener(e -> game.hit());
        standBtn.addActionListener(e -> game.stand());

        return p;
    }

    private void refresh(String evt) {
        dealerPanel.removeAll();
        playerPanel.removeAll();

        drawHand(game.dealerHand(), dealerPanel, !evt.equals("END"));
        drawHand(game.playerHand(), playerPanel, false);

        balanceLbl.setText("Balance: $" + String.format("%.2f", game.balance()));

        dealerPanel.revalidate();
        dealerPanel.repaint();
        playerPanel.revalidate();
        playerPanel.repaint();

        if (evt.equals("END")) {
            hitBtn.setEnabled(false);
            standBtn.setEnabled(false);
            dealBtn.setEnabled(true);
            saveState.setEnabled(true);
            toggleChipButtons(true);
            JOptionPane.showMessageDialog(this, game.outcomeString());
        }
    }

    private void drawHand(Hand hand, JPanel p, boolean hideFirst) {
        if (hand == null || hand.getCards() == null) return;

        p.removeAll();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        /* ---------- row of card images ---------- */
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));  // ← centred
        int i = 0;
        for (Card c : hand.getCards()) {
            ImageIcon ico =
                (i == 0 && hideFirst) ? imgs.get("BACK") : imgs.get(key(c));
            row.add(new JLabel(ico));
            i++;
        }
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);                     // ← centre in column
        p.add(row);

        /* ---------- total shown underneath ---------- */
        if (!hideFirst) {
            JLabel totalLbl = new JLabel(String.valueOf(hand.getValue()));
            totalLbl.setFont(totalLbl.getFont().deriveFont(Font.BOLD, 18f));
            totalLbl.setForeground(Color.WHITE);
            totalLbl.setAlignmentX(Component.CENTER_ALIGNMENT);            // ← centre in column
            p.add(totalLbl);
        }
    }


    private String key(Card c) {
        int n = c.getNumber();
        String r = switch (n) {
            case 1 -> "A";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> String.valueOf(n);
        };
        return r + "-" + c.getSuit().charAt(0);
    }

    private ImageIcon icon(String path) {
        Image img = new ImageIcon(path).getImage().getScaledInstance(W, H, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private Map<String, ImageIcon> loadImages() {
        Map<String, ImageIcon> m = new HashMap<>();
        String base = "cards/";
        String[] s = { "C", "D", "H", "S" };
        String[] r = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
        for (String su : s)
            for (String ra : r)
                m.put(ra + "-" + su, icon(base + ra + "-" + su + ".png"));
        m.put("BACK", icon(base + "BACK.png"));
        return m;
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

    private void toggleChipButtons(boolean enabled) {
        chipButtons.forEach(b -> b.setEnabled(enabled));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gui::new);
    }
}


