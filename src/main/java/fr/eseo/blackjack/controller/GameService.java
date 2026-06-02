package fr.eseo.blackjack.controller;

import fr.eseo.blackjack.model.Deck;

public class GameService {
    private Deck deck;

    public GameService() {
        this.deck = new Deck();
        this.deck.shuffle();
    }

    // Méthode métier basique pour prouver que le service fonctionne
    public String drawInitialCard() {
        return "Première carte : " + deck.drawCard().toString();
    }
}