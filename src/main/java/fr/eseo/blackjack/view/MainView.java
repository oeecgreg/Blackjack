package fr.eseo.blackjack.view;

import fr.eseo.blackjack.controller.GameService;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private final GameService gameService;
    private JLabel infoLabel;

    public MainView(GameService gameService) {
        this.gameService = gameService;
        initUI();
    }

    private void initUI() {
        setTitle("Blackjack - ESEO L1");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        infoLabel = new JLabel("Bienvenue au Blackjack !", SwingConstants.CENTER);
        add(infoLabel, BorderLayout.CENTER);

        JButton drawButton = new JButton("Piocher une carte");
        drawButton.addActionListener(e -> {
            // La vue appelle le service (MVC)
            String cardInfo = gameService.drawInitialCard();
            infoLabel.setText(cardInfo);
        });

        add(drawButton, BorderLayout.SOUTH);
    }
}