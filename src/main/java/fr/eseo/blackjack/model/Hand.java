package fr.eseo.blackjack.model;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private final List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (card != null) {
            cards.add(card);
        }
    }

    public int getScore() {
        int score = 0;
        int acesCount = 0;

        // Étape 1 : On additionne tout, et on compte les As
        for (Card card : cards) {
            score += card.getValue();
            if (card.getRank() == Rank.ACE) {
                acesCount++;
            }
        }

        // Étape 2 : On ajuste la valeur des As (de 11 à 1) si on dépasse 21
        while (score > 21 && acesCount > 0) {
            score -= 10; // C'est l'équivalent de passer un As de 11 à 1
            acesCount--;
        }

        return score;
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards); // Retourne une copie pour protéger la liste interne
    }

    public void clear() {
        cards.clear();
    }

    // Le Blackjack naturel est exactement 21 points avec seulement les 2 premières cartes
    public boolean isBlackjack() {
        return cards.size() == 2 && getScore() == 21;
    }

    // Savoir si la main a "brûlé" (dépassé 21)
    public boolean isBusted() {
        return getScore() > 21;
    }
}