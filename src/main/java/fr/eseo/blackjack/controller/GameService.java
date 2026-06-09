package fr.eseo.blackjack.controller;

import fr.eseo.blackjack.model.Deck;
import fr.eseo.blackjack.model.Hand;
import fr.eseo.blackjack.model.Player;
import fr.eseo.blackjack.model.dao.PlayerDao;

public class GameService {
    private Deck deck;
    private final Hand playerHand;
    private final Hand dealerHand;
    private final PlayerDao playerDao;
    private Player currentPlayer;

    // Injection du DAO via le constructeur
    public GameService(PlayerDao playerDao) {
        this.playerDao = playerDao;
        this.deck = new Deck();
        this.deck.shuffle();
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
    }

    public void startNewGame(String playerName) {
        // 1. On cherche le joueur dans le fichier
        this.currentPlayer = playerDao.read(playerName);

        // 2. S'il n'existe pas, on le crée avec 1000 jetons par défaut
        if (this.currentPlayer == null) {
            this.currentPlayer = new Player(playerName, 1000, 0);
            playerDao.create(this.currentPlayer);
        }
        resetRound();
    }

    public void resetRound() {
        // Si le paquet est presque vide, on en prend un nouveau
        if (deck.getRemainingCardsCount() < 15) {
            deck = new Deck();
            deck.shuffle();
        }
        playerHand.clear();
        dealerHand.clear();
    }

    public void dealInitialCards() {
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
    }

    public void playerHit() {
        playerHand.addCard(deck.drawCard());
    }

    // Résout la fin de la partie, met à jour le solde et sauvegarde dans le fichier
    public void resolveGame(int betAmount) {
        if (playerHand.isBusted()) {
            currentPlayer.setBalance(currentPlayer.getBalance() - betAmount);
        } else if (dealerHand.isBusted() || playerHand.getScore() > dealerHand.getScore()) {
            currentPlayer.setBalance(currentPlayer.getBalance() + betAmount);
            currentPlayer.addWin();
        } else if (playerHand.getScore() < dealerHand.getScore()) {
            currentPlayer.setBalance(currentPlayer.getBalance() - betAmount);
        }
        // En cas d'égalité (Tie), le solde ne bouge pas.

        // Demande au DAO d'écrire la mise à jour sur le disque
        playerDao.update(currentPlayer);
    }

    // Le croupier tire automatiquement jusqu'à atteindre au moins 17
    public void dealerPlay() {
        while (dealerHand.getScore() < 17) {
            dealerHand.addCard(deck.drawCard());
        }
    }

    public Hand getPlayerHand() { return playerHand; }
    public Hand getDealerHand() { return dealerHand; }
    public Player getCurrentPlayer() { return currentPlayer; }
}