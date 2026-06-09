package fr.eseo.blackjack.controller;

import fr.eseo.blackjack.model.Card;
import fr.eseo.blackjack.model.Player;
import fr.eseo.blackjack.model.Rank;
import fr.eseo.blackjack.model.Suit;
import fr.eseo.blackjack.model.dao.PlayerDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Active l'intégration de Mockito avec JUnit 5
@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    // On crée un faux DAO ("bouchon" ou "mock")
    @Mock
    private PlayerDao playerDaoMock;

    private GameService gameService;

    @BeforeEach
    public void setUp() {
        // On injecte le faux DAO dans notre vrai Service
        gameService = new GameService(playerDaoMock);
    }

    @Test
    public void testStartNewGameCreatesPlayerIfNotFound() {
        // Règle du Mock : Si le service demande le joueur "Bob", renvoie "null"
        when(playerDaoMock.read("Bob")).thenReturn(null);

        gameService.startNewGame("Bob");

        // Vérifications :
        assertNotNull(gameService.getCurrentPlayer(), "Le joueur devrait être créé.");
        assertEquals("Bob", gameService.getCurrentPlayer().getName());

        // Mockito vérifie que la méthode 'create' du DAO a bien été appelée exactement 1 fois
        verify(playerDaoMock, times(1)).create(any(Player.class));
    }

    @Test
    public void testResolveGamePlayerWinsUpdatesDao() {
        // Règle du Mock : Si on cherche "Alice", on renvoie ce faux joueur
        Player mockPlayer = new Player("Alice", 1000, 5);
        when(playerDaoMock.read("Alice")).thenReturn(mockPlayer);

        gameService.startNewGame("Alice");

        // Manipulation manuelle des mains pour forcer un scénario de victoire (Joueur: 20, Croupier: 18)
        gameService.getPlayerHand().addCard(new Card(Suit.HEARTS, Rank.TEN));
        gameService.getPlayerHand().addCard(new Card(Suit.SPADES, Rank.TEN));

        gameService.getDealerHand().addCard(new Card(Suit.CLUBS, Rank.TEN));
        gameService.getDealerHand().addCard(new Card(Suit.DIAMONDS, Rank.EIGHT));

        // On déclenche la résolution avec une mise de 100
        gameService.resolveGame(100);

        // 1. On vérifie la logique métier (le solde augmente)
        assertEquals(1100, mockPlayer.getBalance(), "Le joueur a gagné, son solde doit augmenter.");
        assertEquals(6, mockPlayer.getWins(), "Le compteur de victoires doit être incrémenté.");

        // 2. On vérifie la persistance avec Mockito (le service a-t-il pensé à sauvegarder ?)
        verify(playerDaoMock, times(1)).update(mockPlayer);
    }
}