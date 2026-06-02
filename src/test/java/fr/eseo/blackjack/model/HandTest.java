package fr.eseo.blackjack.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HandTest {

    private Hand hand;

    @BeforeEach
    public void setUp() {
        hand = new Hand(); // Une main toute neuve avant chaque test
    }

    @Test
    public void testEmptyHandScoreIsZero() {
        assertEquals(0, hand.getScore(), "Une main vide doit valoir 0.");
    }

    @Test
    public void testScoreWithoutAces() {
        hand.addCard(new Card(Suit.HEARTS, Rank.TEN));
        hand.addCard(new Card(Suit.SPADES, Rank.SEVEN));
        assertEquals(17, hand.getScore(), "10 + 7 doit faire 17.");
    }

    @Test
    public void testScoreWithOneAceNotBusting() {
        hand.addCard(new Card(Suit.CLUBS, Rank.ACE));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.NINE));
        assertEquals(20, hand.getScore(), "As (11) + 9 doit faire 20.");
    }

    @Test
    public void testScoreWithOneAceBusting() {
        hand.addCard(new Card(Suit.CLUBS, Rank.ACE));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.NINE));
        hand.addCard(new Card(Suit.HEARTS, Rank.FIVE));
        // 11 + 9 + 5 = 25 -> l'As devient 1 -> 1 + 9 + 5 = 15
        assertEquals(15, hand.getScore(), "Un As (devenant 1) + 9 + 5 doit faire 15.");
    }

    @Test
    public void testScoreWithMultipleAces() {
        hand.addCard(new Card(Suit.CLUBS, Rank.ACE));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.ACE));
        hand.addCard(new Card(Suit.HEARTS, Rank.NINE));
        // 11 + 11 + 9 = 31 -> 1er As devient 1 (21) -> le compte est bon.
        assertEquals(21, hand.getScore(), "As (1) + As (11) + 9 doit faire 21.");
    }

    @Test
    public void testIsBlackjack() {
        hand.addCard(new Card(Suit.SPADES, Rank.ACE));
        hand.addCard(new Card(Suit.HEARTS, Rank.KING));
        assertTrue(hand.isBlackjack(), "Un As et une figure (2 cartes) doivent faire Blackjack.");
    }

    @Test
    public void testIsNotBlackjackWith21ButThreeCards() {
        hand.addCard(new Card(Suit.SPADES, Rank.SEVEN));
        hand.addCard(new Card(Suit.HEARTS, Rank.SEVEN));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.SEVEN));
        assertEquals(21, hand.getScore());
        assertFalse(hand.isBlackjack(), "21 points en 3 cartes n'est pas un Blackjack naturel.");
    }

    @Test
    public void testIsBusted() {
        hand.addCard(new Card(Suit.SPADES, Rank.KING));
        hand.addCard(new Card(Suit.HEARTS, Rank.QUEEN));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.TWO));
        assertTrue(hand.isBusted(), "Un score > 21 doit être considéré comme 'busted'.");
    }
}