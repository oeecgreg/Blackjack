package fr.eseo.blackjack.controller;

import fr.eseo.blackjack.exception.EmptyDeckException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour le {@link GameService}.
 * Vérifie l'orchestration de la logique métier (le paquet de cartes).
 */
public class GameServiceTest {

    private GameService gameService;

    @BeforeEach
    public void setUp() {
        // On recrée un service neuf avant chaque test
        gameService = new GameService();
    }

    /**
     * Vérifie que la méthode drawInitialCard renvoie bien une chaîne de caractères formatée
     * contenant les informations d'une carte valide.
     */
    @Test
    public void testDrawInitialCardReturnsValidString() {
        String result = gameService.drawInitialCard();
        assertNotNull(result, "Le résultat ne doit pas être nul.");
        assertTrue(result.startsWith("Première carte : "), "Le formatage de la chaîne est incorrect.");
    }

    /**
     * Vérifie la robustesse du service lorsqu'il épuise le paquet.
     * Le service doit laisser remonter l'EmptyDeckException du Modèle.
     */
    @Test
    public void testDrawCardExhaustsDeckAndThrowsException() {
        // On vide le paquet de ses 52 cartes
        for (int i = 0; i < 52; i++) {
            gameService.drawInitialCard();
        }

        // La 53ᵉ tentative doit lever l'exception
        assertThrows(EmptyDeckException.class, () -> gameService.drawInitialCard(),
                "Le service doit propager l'EmptyDeckException quand le paquet est vide.");
    }
}