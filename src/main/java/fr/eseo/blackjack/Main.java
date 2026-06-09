package fr.eseo.blackjack;

import fr.eseo.blackjack.controller.GameService;
import fr.eseo.blackjack.model.dao.PlayerDao;
import fr.eseo.blackjack.model.dao.PlayerDaoTxtImpl;
import fr.eseo.blackjack.view.MainView;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlayerDao playerDao = new PlayerDaoTxtImpl("joueurs.txt");
            GameService service = new GameService(playerDao);

            // Demande le nom du joueur au lancement
            String playerName = JOptionPane.showInputDialog(null, "Entrez votre nom de joueur :", "Connexion", JOptionPane.QUESTION_MESSAGE);

            // Si l'utilisateur annule, on ferme
            if (playerName == null || playerName.trim().isEmpty()) {
                System.exit(0);
            }

            service.startNewGame(playerName.trim());

            MainView view = new MainView(service);
            view.setVisible(true);
        });
    }
}