package fr.eseo.blackjack.model;

import fr.eseo.blackjack.exception.EmptyDeckException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    @Test
    public void testDeckInitialization() {
        Deck deck = new Deck();
        assertEquals(52, deck.getRemainingCardsCount(), "Un nouveau deck doit contenir 52 cartes.");
    }

    @Test
    public void testDrawCardReducesDeckSize() {
        Deck deck = new Deck();
        deck.drawCard();
        assertEquals(51, deck.getRemainingCardsCount(),"Piocher une carte doit réduire la taille du deck de 1.");
    }

    @Test
    public void testDrawFromEmptyDeckThrowsException() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }

        // La 53ᵉ pioche doit lever notre exception personnalisée
        assertThrows(EmptyDeckException.class, deck::drawCard, "Piocher dans un paquet vide doit lever une EmptyDeckException.");
    }
}
