package fr.eseo.blackjack.view;

import fr.eseo.blackjack.controller.GameService;
import org.junit.jupiter.api.Test;

import javax.swing.JFrame;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test structurel pour {@link MainView}.
 * Note : Les tests d'interface graphique (GUI) purs avec JUnit sont limités.
 * On se concentre ici sur la vérification de l'initialisation de la fenêtre.
 */
public class MainViewTest {

    /**
     * Vérifie que la fenêtre s'initialise avec les bons paramètres
     * sans déclencher d'erreur de thread ou d'affichage.
     */
    @Test
    public void testViewInitializationProperties() {
        GameService mockService = new GameService(); // On fournit les dépendances requises
        MainView view = new MainView(mockService);

        // Vérification des propriétés de la fenêtre Swing
        assertEquals("Blackjack - ESEO L1", view.getTitle(), "Le titre de la fenêtre est incorrect.");
        assertEquals(JFrame.EXIT_ON_CLOSE, view.getDefaultCloseOperation(), "L'opération de fermeture doit quitter l'application.");
        assertFalse(view.isVisible(), "La vue ne doit pas être visible tant que le Main ne l'a pas décidé.");
    }
}