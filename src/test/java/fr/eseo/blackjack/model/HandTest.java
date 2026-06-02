package fr.eseo.blackjack.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour la classe {@link Hand}.
 * Elle vérifie la logique de calcul des scores d'une main au Blackjack,
 * en s'assurant particulièrement de la bonne gestion de la valeur changeante de l'As (1 ou 11)
 * et de la bonne détection des états de victoire ou d'échec (Blackjack ou Bust).
 */
public class HandTest {

    private Hand hand;

    /**
     * Initialise l'environnement de test avant l'exécution de chaque méthode de test.
     * Cette méthode garantit qu'une main vide et neuve est utilisée pour chaque scénario,
     * évitant ainsi que les tests n'interfèrent les uns avec les autres.
     */
    @BeforeEach
    public void setUp() {
        hand = new Hand();
    }

    /**
     * Teste qu'une main fraîchement instanciée et sans carte possède un score initial de 0.
     */
    @Test
    public void testEmptyHandScoreIsZero() {
        assertEquals(0, hand.getScore(), "Une main vide doit valoir 0.");
    }

    /**
     * Teste le calcul du score pour une main classique ne contenant aucun As.
     * Vérifie que l'addition standard de la valeur des cartes est correcte.
     */
    @Test
    public void testScoreWithoutAces() {
        hand.addCard(new Card(Suit.HEARTS, Rank.TEN));
        hand.addCard(new Card(Suit.SPADES, Rank.SEVEN));
        assertEquals(17, hand.getScore(), "10 + 7 doit faire 17.");
    }

    /**
     * Teste le calcul du score lorsqu'un As est présent et que sa valeur forte (11)
     * ne fait pas dépasser le score total limite de 21.
     */
    @Test
    public void testScoreWithOneAceNotBusting() {
        hand.addCard(new Card(Suit.CLUBS, Rank.ACE));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.NINE));
        assertEquals(20, hand.getScore(), "As (11) + 9 doit faire 20.");
    }

    /**
     * Teste la réduction automatique de la valeur de l'As (passage de 11 à 1)
     * lorsque le score total dépasserait 21 si l'As conservait sa valeur forte.
     */
    @Test
    public void testScoreWithOneAceBusting() {
        hand.addCard(new Card(Suit.CLUBS, Rank.ACE));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.NINE));
        hand.addCard(new Card(Suit.HEARTS, Rank.FIVE));
        // 11 + 9 + 5 = 25 -> l'As devient 1 -> 1 + 9 + 5 = 15
        assertEquals(15, hand.getScore(), "Un As (devenant 1) + 9 + 5 doit faire 15.");
    }

    /**
     * Teste le calcul du score complexe avec plusieurs As en main.
     * Vérifie que seul le nombre strictement nécessaire d'As est réduit à 1
     * pour éviter de dépasser 21, tandis que le dernier As conserve la valeur de 11 si possible.
     */
    @Test
    public void testScoreWithMultipleAces() {
        hand.addCard(new Card(Suit.CLUBS, Rank.ACE));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.ACE));
        hand.addCard(new Card(Suit.HEARTS, Rank.NINE));
        // 11 + 11 + 9 = 31 -> 1er As devient 1 (21) -> le compte est bon.
        assertEquals(21, hand.getScore(), "As (1) + As (11) + 9 doit faire 21.");
    }

    /**
     * Teste la détection valide d'un Blackjack naturel.
     * Un Blackjack doit valoir exactement 21 points et être composé de strictement deux cartes.
     */
    @Test
    public void testIsBlackjack() {
        hand.addCard(new Card(Suit.SPADES, Rank.ACE));
        hand.addCard(new Card(Suit.HEARTS, Rank.KING));
        assertTrue(hand.isBlackjack(), "Un As et une figure (2 cartes) doivent faire Blackjack.");
    }

    /**
     * Teste la distinction entre un score de 21 et un Blackjack naturel.
     * Vérifie qu'un score de 21 obtenu avec trois cartes (ou plus) n'est pas
     * considéré comme un Blackjack (qui paie 3:2).
     */
    @Test
    public void testIsNotBlackjackWith21ButThreeCards() {
        hand.addCard(new Card(Suit.SPADES, Rank.SEVEN));
        hand.addCard(new Card(Suit.HEARTS, Rank.SEVEN));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.SEVEN));
        assertEquals(21, hand.getScore());
        assertFalse(hand.isBlackjack(), "21 points en 3 cartes n'est pas un Blackjack naturel.");
    }

    /**
     * Teste la détection d'une main "brûlée" (Bust).
     * Vérifie que la méthode isBusted() renvoie bien 'true' dès que le score dépasse strictement 21.
     */
    @Test
    public void testIsBusted() {
        hand.addCard(new Card(Suit.SPADES, Rank.KING));
        hand.addCard(new Card(Suit.HEARTS, Rank.QUEEN));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.TWO));
        assertTrue(hand.isBusted(), "Un score > 21 doit être considéré comme 'busted'.");
    }
}