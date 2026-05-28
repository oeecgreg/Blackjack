package fr.eseo.blackjack.model;

import fr.eseo.blackjack.exception.EmptyDeckException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
    }

    private void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new EmptyDeckException("Impossible de piocher : le paquet est vide.");
        }
        // On retire et on renvoie la carte au sommet du paquet (index 0).
        return cards.remove(0);
    }

    public int getRemainingCardsCount() {
        return cards.size();
    }
}