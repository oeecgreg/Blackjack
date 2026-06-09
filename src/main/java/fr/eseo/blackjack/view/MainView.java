package fr.eseo.blackjack.view;

import fr.eseo.blackjack.controller.GameService;
import fr.eseo.blackjack.exception.EmptyDeckException;
import fr.eseo.blackjack.model.Card;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URL;

public class MainView extends JFrame {
    private final GameService gameService;
    private int accumulatedBet = 0;

    // Composants graphiques du HUD (Bandeau haut)
    private JLabel lblNameBadge;
    private JLabel lblBalanceBadge;
    private JLabel lblWinsBadge;

    // Zone de jeu
    private JPanel pnlDealerCards, pnlPlayerCards;
    private JLabel lblDealerScore, lblPlayerScore;
    private JLabel lblFeedback;

    // Composants de contrôle
    private JLabel lblCurrentBet;
    private JButton btnDeal, btnHit, btnStand;

    // Nos jetons personnalisés
    private CasinoChip chip10, chip50, chip100;

    private boolean isBettingPhase = true;

    private Font cinzelFont;
    private Font montserratFont;

    // Image de fond de la table
    private Image tableBackgroundImage;

    private Font loadCustomFont(String path, float size) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (Exception e) {
            System.err.println("Erreur chargement police : " + path + " - Retour au mode par défaut.");
            return new Font("SansSerif", Font.PLAIN, (int) size);
        }
    }

    public MainView(GameService gameService) {
        this.gameService = gameService;

        cinzelFont = loadCustomFont("/fonts/Cinzel/static/Cinzel-Bold.ttf", 42f);
        montserratFont = loadCustomFont("/fonts/Montserrat/static/Montserrat-SemiBold.ttf", 18f);

        URL imgURL = getClass().getResource("/images/table_blackjack.png");
        if (imgURL != null) {
            tableBackgroundImage = new ImageIcon(imgURL).getImage();
        } else {
            System.err.println("Attention: Image de la table introuvable.");
        }

        initUI();
        refreshInfoPanel();
    }

    private void initUI() {
        setTitle("Blackjack - ESEO L3");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Le Panneau Racine (Image de fond) ---
        JPanel pnlRoot = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (tableBackgroundImage != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(tableBackgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(16, 89, 42));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        setContentPane(pnlRoot);

        // --- Panel NORD : HUD ---
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(new EmptyBorder(10, 10, 0, 10));

        lblNameBadge = createHUDBadge("Joueur : -", new Color(20, 20, 20, 160));
        lblBalanceBadge = createHUDBadge("Solde : - €", new Color(20, 20, 20, 160));
        lblWinsBadge = createHUDBadge("Victoires : -", new Color(20, 20, 20, 160));

        pnlInfo.add(lblNameBadge);
        pnlInfo.add(lblBalanceBadge);
        pnlInfo.add(lblWinsBadge);
        pnlRoot.add(pnlInfo, BorderLayout.NORTH);

        // --- Panel CENTRE : Superposition (OverlayLayout) ---
        JPanel pnlCenterOverlay = new JPanel() {
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        pnlCenterOverlay.setLayout(new OverlayLayout(pnlCenterOverlay));
        pnlCenterOverlay.setOpaque(false);

        // COUCHE 1 (Dessus) : Le Label Feedback flottant
        JPanel pnlFeedbackWrapper = new JPanel(new GridBagLayout());
        pnlFeedbackWrapper.setOpaque(false);

        lblFeedback = new JLabel("Placez vos mises", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 20, 20, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(212, 175, 55));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblFeedback.setForeground(new Color(255, 215, 0));
        lblFeedback.setFont(cinzelFont.deriveFont(42f));
        lblFeedback.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        GridBagConstraints gbcFb = new GridBagConstraints();
        gbcFb.insets = new Insets(0, 0, 50, 0);
        pnlFeedbackWrapper.add(lblFeedback, gbcFb);

        // COUCHE 2 (Dessous) : La zone des Cartes et des Scores
        JPanel pnlCardsArea = new JPanel();
        pnlCardsArea.setOpaque(false);
        pnlCardsArea.setLayout(new BoxLayout(pnlCardsArea, BoxLayout.Y_AXIS));

        pnlDealerCards = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlDealerCards.setOpaque(false);
        pnlPlayerCards = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlPlayerCards.setOpaque(false);

        lblDealerScore = new JLabel(" ", SwingConstants.CENTER);
        lblDealerScore.setForeground(Color.WHITE);
        lblDealerScore.setFont(montserratFont.deriveFont(24f));
        lblDealerScore.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPlayerScore = new JLabel(" ", SwingConstants.CENTER);
        lblPlayerScore.setForeground(Color.WHITE);
        lblPlayerScore.setFont(montserratFont.deriveFont(24f));
        lblPlayerScore.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlCardsArea.add(Box.createRigidArea(new Dimension(0, 120)));
        pnlCardsArea.add(lblDealerScore);
        pnlCardsArea.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlCardsArea.add(pnlDealerCards);

        pnlCardsArea.add(Box.createVerticalGlue());

        pnlCardsArea.add(pnlPlayerCards);
        pnlCardsArea.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlCardsArea.add(lblPlayerScore);
        pnlCardsArea.add(Box.createRigidArea(new Dimension(0, 50)));

        pnlCenterOverlay.add(pnlFeedbackWrapper);
        pnlCenterOverlay.add(pnlCardsArea);

        pnlRoot.add(pnlCenterOverlay, BorderLayout.CENTER);

        // --- Panel SUD : La zone de paris ---
        JPanel pnlBettingArea = new JPanel();
        pnlBettingArea.setLayout(new BoxLayout(pnlBettingArea, BoxLayout.X_AXIS));
        pnlBettingArea.setOpaque(false);

        Dimension sideDim = new Dimension(350, 150);

        // BLOC GAUCHE (Jetons)
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setOpaque(false);
        pnlLeft.setPreferredSize(sideDim);
        pnlLeft.setMinimumSize(sideDim);
        pnlLeft.setMaximumSize(sideDim);

        JPanel pnlTriangle = new JPanel(null);
        pnlTriangle.setOpaque(false);
        pnlTriangle.setPreferredSize(new Dimension(170, 150));

        chip10 = new CasinoChip(10, new Color(41, 128, 185));
        chip50 = new CasinoChip(50, new Color(192, 57, 43));
        chip100 = new CasinoChip(100, new Color(44, 62, 80));

        chip50.setBounds(45, 0, 80, 80);
        chip10.setBounds(0, 65, 80, 80);
        chip100.setBounds(90, 65, 80, 80);

        pnlTriangle.add(chip50);
        pnlTriangle.add(chip10);
        pnlTriangle.add(chip100);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.anchor = GridBagConstraints.EAST;
        gbcLeft.weightx = 1.0;
        gbcLeft.insets = new Insets(0, 0, 0, 40);
        pnlLeft.add(pnlTriangle, gbcLeft);

        // BARRE CENTRALE
        JPanel separator = new JPanel();
        separator.setPreferredSize(new Dimension(3, 110));
        separator.setMaximumSize(new Dimension(3, 110));
        separator.setBackground(new Color(255, 255, 255, 128));
        separator.setAlignmentY(Component.CENTER_ALIGNMENT);

        // BLOC DROIT (Texte Mise)
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setOpaque(false);
        pnlRight.setPreferredSize(sideDim);
        pnlRight.setMinimumSize(sideDim);
        pnlRight.setMaximumSize(sideDim);

        lblCurrentBet = new JLabel("Mise : 0 €", SwingConstants.LEFT);
        lblCurrentBet.setForeground(Color.WHITE);
        lblCurrentBet.setFont(montserratFont.deriveFont(24f));

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.anchor = GridBagConstraints.WEST;
        gbcRight.weightx = 1.0;
        gbcRight.insets = new Insets(0, 40, 0, 0);
        pnlRight.add(lblCurrentBet, gbcRight);

        pnlBettingArea.add(Box.createHorizontalGlue());
        pnlBettingArea.add(pnlLeft);
        pnlBettingArea.add(separator);
        pnlBettingArea.add(pnlRight);
        pnlBettingArea.add(Box.createHorizontalGlue());

        // --- Panel SUD : La zone des boutons ---
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlActions.setOpaque(false);
        btnDeal = createStyledButton("Distribuer", new Color(39, 174, 96));
        btnHit = createStyledButton("Tirer", Color.DARK_GRAY);
        btnStand = createStyledButton("Rester", Color.DARK_GRAY);
        pnlActions.add(btnDeal); pnlActions.add(btnHit); pnlActions.add(btnStand);


        // =====================================================================
        // SOLUTION DÉFINITIVE POUR LE CENTRAGE : BorderLayout
        // =====================================================================
        // Le vgap (30) permet d'espacer la zone des jetons et la zone des boutons.
        JPanel pnlControls = new JPanel(new BorderLayout(0, 30));
        pnlControls.setOpaque(false);
        pnlControls.setBorder(new EmptyBorder(10, 20, 40, 20));

        // pnlBettingArea prend la place centrale
        pnlControls.add(pnlBettingArea, BorderLayout.CENTER);

        // pnlActions est forcé en bas. BorderLayout.SOUTH force le conteneur
        // à prendre 100% de la largeur de l'écran. Le FlowLayout.CENTER des
        // boutons calculera donc le centre parfait de l'écran !
        pnlControls.add(pnlActions, BorderLayout.SOUTH);

        pnlRoot.add(pnlControls, BorderLayout.SOUTH);
        // =====================================================================


        // --- ECOUTEURS DES JETONS ---
        MouseAdapter chipListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isBettingPhase) return;
                CasinoChip clickedChip = (CasinoChip) e.getSource();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    addBet(clickedChip.getValue());
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    removeBet(clickedChip.getValue());
                }
            }
        };

        chip10.addMouseListener(chipListener);
        chip50.addMouseListener(chipListener);
        chip100.addMouseListener(chipListener);

        setPlayingControlsEnabled(false);

        // --- ECOUTEURS DU JEU ---
        btnDeal.addActionListener(e -> startAnimatedRound());
        btnHit.addActionListener(e -> handlePlayerHit());
        btnStand.addActionListener(e -> startDealerAnimation());
    }

    // --- METHODES UTILITAIRES ---

    private JLabel createHUDBadge(String text, Color bgColor) {
        JLabel label = new JLabel(text);
        label.setFont(montserratFont.deriveFont(20f));
        label.setForeground(Color.WHITE);
        label.setBackground(bgColor);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 175, 55, 120), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return label;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (!isEnabled()) {
                    g2.setColor(new Color(60, 60, 60));
                } else if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else {
                    g2.setColor(bgColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(montserratFont.deriveFont(24f));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        return btn;
    }

    private void addBet(int amount) {
        if (accumulatedBet + amount <= gameService.getCurrentPlayer().getBalance()) {
            accumulatedBet += amount;
            updateBetDisplay();
            lblFeedback.setText(" ");
            lblFeedback.setVisible(false);
        } else {
            lblFeedback.setVisible(true);
            lblFeedback.setText("Solde insuffisant !");
            lblFeedback.setForeground(Color.RED);
        }
    }

    private void removeBet(int amount) {
        if (accumulatedBet - amount >= 0) {
            accumulatedBet -= amount;
        } else {
            accumulatedBet = 0;
        }
        updateBetDisplay();
        lblFeedback.setText(" ");
        lblFeedback.setVisible(false);
    }

    private void updateBetDisplay() {
        lblCurrentBet.setText("Mise : " + accumulatedBet + " €");
    }

    private ImageIcon getCardImage(Card card) {
        String fileName = card.getRank() + "_of_" + card.getSuit() + ".png";
        URL imgURL = getClass().getResource("/images/cards/" + fileName);
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage().getScaledInstance(140, 200, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }

    private void addCardToPanel(JPanel panel, Card c) {
        ImageIcon icon = getCardImage(c);
        if (icon != null) panel.add(new JLabel(icon));
        else panel.add(new JLabel("[" + c.toString() + "]"));
        panel.revalidate(); panel.repaint();
    }

    private void setPlayingControlsEnabled(boolean enabled) {
        btnHit.setEnabled(enabled); btnStand.setEnabled(enabled);
        btnDeal.setEnabled(!enabled);
        isBettingPhase = !enabled;

        chip10.setEnabled(isBettingPhase); chip50.setEnabled(isBettingPhase); chip100.setEnabled(isBettingPhase);
        chip10.repaint(); chip50.repaint(); chip100.repaint();

        if (getContentPane() != null) {
            getContentPane().repaint();
        }
    }

    // --- LOGIQUE D'ANIMATION ---

    private void startAnimatedRound() {
        if (accumulatedBet <= 0) {
            lblFeedback.setVisible(true);
            lblFeedback.setForeground(Color.RED);
            lblFeedback.setText("Placez une mise !");
            return;
        }
        try {
            gameService.resetRound();
            gameService.dealInitialCards();
        } catch (EmptyDeckException e) {
            lblFeedback.setVisible(true);
            lblFeedback.setText("Paquet vide, relancez !");
            gameService.resetRound();
            return;
        }

        setPlayingControlsEnabled(true);
        btnHit.setEnabled(false); btnStand.setEnabled(false);

        lblFeedback.setVisible(false);
        pnlPlayerCards.removeAll(); pnlDealerCards.removeAll();
        pnlPlayerCards.revalidate(); pnlPlayerCards.repaint();
        pnlDealerCards.revalidate(); pnlDealerCards.repaint();
        lblPlayerScore.setText("Score : ?"); lblDealerScore.setText("Score : ?");

        java.util.List<Card> pCards = gameService.getPlayerHand().getCards();
        java.util.List<Card> dCards = gameService.getDealerHand().getCards();

        Timer dealTimer = new Timer(300, null);
        dealTimer.addActionListener(new java.awt.event.ActionListener() {
            int step = 0;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (step == 0) addCardToPanel(pnlPlayerCards, pCards.get(0));
                else if (step == 1) addCardToPanel(pnlDealerCards, dCards.get(0));
                else if (step == 2) {
                    addCardToPanel(pnlPlayerCards, pCards.get(1));
                    lblPlayerScore.setText("Score : " + gameService.getPlayerHand().getScore());
                }
                else if (step == 3) {
                    addCardToPanel(pnlDealerCards, dCards.get(1));
                    lblDealerScore.setText("Score : " + gameService.getDealerHand().getScore());
                }
                else {
                    dealTimer.stop();
                    checkInitialBlackjack();
                }
                step++;
            }
        });
        dealTimer.start();
    }

    private void handlePlayerHit() {
        try {
            gameService.playerHit();
            Card newCard = gameService.getPlayerHand().getCards().get(gameService.getPlayerHand().getCards().size() - 1);
            addCardToPanel(pnlPlayerCards, newCard);
            lblPlayerScore.setText("Score : " + gameService.getPlayerHand().getScore());

            if (gameService.getPlayerHand().isBusted()) {
                gameService.resolveGame(accumulatedBet);
                endRound("BUST ! Vous avez dépassé 21.");
            }
        } catch (EmptyDeckException ex) {
            lblFeedback.setVisible(true);
            lblFeedback.setText("Mélange du paquet...");
            gameService.resetRound();
        }
    }

    private void checkInitialBlackjack() {
        if (gameService.getPlayerHand().isBlackjack()) {
            gameService.resolveGame(accumulatedBet);
            endRound("BLACKJACK ! Vous gagnez !");
        } else {
            btnHit.setEnabled(true); btnStand.setEnabled(true);
        }
    }

    private void startDealerAnimation() {
        btnHit.setEnabled(false); btnStand.setEnabled(false);
        gameService.dealerPlay();
        gameService.resolveGame(accumulatedBet);

        java.util.List<Card> dCards = gameService.getDealerHand().getCards();

        Timer dealerTimer = new Timer(600, null);
        dealerTimer.addActionListener(new java.awt.event.ActionListener() {
            int index = 2;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (index < dCards.size()) {
                    addCardToPanel(pnlDealerCards, dCards.get(index));
                    index++;
                } else {
                    dealerTimer.stop();
                    lblDealerScore.setText("Score : " + gameService.getDealerHand().getScore());
                    evaluateWinnerUI();
                }
            }
        });
        dealerTimer.start();
    }

    private void evaluateWinnerUI() {
        int pScore = gameService.getPlayerHand().getScore();
        int dScore = gameService.getDealerHand().getScore();

        if (gameService.getDealerHand().isBusted()) endRound("Le Croupier saute. VICTOIRE !");
        else if (pScore > dScore) endRound("C'est gagné !");
        else if (pScore < dScore) endRound("Le Croupier gagne.");
        else endRound("Égalité !");
    }

    private void endRound(String message) {
        lblFeedback.setVisible(true);
        lblFeedback.setForeground(new Color(255, 215, 0));
        lblFeedback.setText(message);

        refreshInfoPanel();
        accumulatedBet = 0;
        updateBetDisplay();

        setPlayingControlsEnabled(false);
    }

    private void refreshInfoPanel() {
        if (gameService.getCurrentPlayer() != null) {
            lblNameBadge.setText(String.format("Joueur : %s", gameService.getCurrentPlayer().getName()));
            lblBalanceBadge.setText(String.format("Solde : %d €", gameService.getCurrentPlayer().getBalance()));
            lblWinsBadge.setText(String.format("Victoires : %d", gameService.getCurrentPlayer().getWins()));
        }
    }

    // =========================================================================
    // CLASSE INTERNE : Le composant Jeton Personnalisé (CasinoChip)
    // =========================================================================
    class CasinoChip extends JComponent {
        private int value;
        private Color mainColor;

        public CasinoChip(int value, Color mainColor) {
            this.value = value;
            this.mainColor = mainColor;
            setPreferredSize(new Dimension(80, 80));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText("Clic Gauche : + " + value + " €  |  Clic Droit : - " + value + " €");
        }

        public int getValue() {
            return value;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int d = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - d) / 2;
            int y = (getHeight() - d) / 2;

            if (!isEnabled()) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            }

            g2d.setColor(Color.WHITE);
            g2d.fillOval(x, y, d, d);

            g2d.setColor(mainColor);
            g2d.fillOval(x + 4, y + 4, d - 8, d - 8);

            g2d.setColor(Color.WHITE);
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0));
            g2d.drawOval(x + 8, y + 8, d - 16, d - 16);
            g2d.setStroke(oldStroke);

            g2d.setColor(Color.WHITE);
            g2d.setFont(montserratFont.deriveFont(24f));
            String txt = String.valueOf(value);
            FontMetrics fm = g2d.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(txt)) / 2;
            int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(txt, tx, ty);
        }
    }
}