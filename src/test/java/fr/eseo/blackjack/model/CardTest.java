package fr.eseo.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @Test
    public void testCardValueNumber() {
        Card card = new Card(Suit.HEARTS, Rank.SEVEN);
        assertEquals(7, card.getValue(), "Un 7 doit valoir 7 points.");
    }

    @Test
    public void testCardValueFace() {
        Card card = new Card(Suit.SPADES, Rank.KING);
        assertEquals(10, card.getValue(), "Une figure (Roi) doit valoir 10 points.");
    }

    @Test
    public void testCardValueAce() {
        Card card = new Card(Suit.DIAMONDS, Rank.ACE);
        assertEquals(11, card.getValue(), "L'As doit valoir 11 points par défaut.");
    }

    @Test
    public void testToString() {
        Card card = new Card(Suit.CLUBS, Rank.ACE);
        assertEquals("ACE of CLUBS", card.toString(), "L'affichage de la carte doit être correct.");
    }
}