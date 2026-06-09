package fr.eseo.blackjack.view;

import fr.eseo.blackjack.controller.GameService;
import fr.eseo.blackjack.model.dao.PlayerDao;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.JFrame;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test structurel pour {@link MainView}.
 */
public class MainViewTest {

    @Test
    public void testViewInitializationProperties() {
        // 1. On crée un faux DAO "à la volée" avec Mockito
        PlayerDao dummyDao = Mockito.mock(PlayerDao.class);

        // 2. On passe ce faux DAO au service pour respecter le nouveau constructeur
        GameService mockService = new GameService(dummyDao);

        MainView view = new MainView(mockService);

        // Vérification des propriétés de la fenêtre Swing
        assertEquals("Blackjack - ESEO L3", view.getTitle(), "Le titre de la fenêtre est incorrect.");
        assertEquals(JFrame.EXIT_ON_CLOSE, view.getDefaultCloseOperation(), "L'opération de fermeture doit quitter l'application.");
        assertFalse(view.isVisible(), "La vue ne doit pas être visible tant que le Main ne l'a pas décidé.");
    }
}