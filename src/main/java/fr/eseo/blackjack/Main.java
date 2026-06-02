package fr.eseo.blackjack;

import fr.eseo.blackjack.controller.GameService;
import fr.eseo.blackjack.view.MainView;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Initialisation du Service (Modèle / Logique)
            GameService service = new GameService();

            // 2. Initialisation de la Vue en lui passant le Service
            MainView view = new MainView(service);
            view.setVisible(true);
        });
    }
}