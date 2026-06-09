package fr.eseo.blackjack.model;

public class Player {
    private String name;
    private int balance;
    private int wins;

    public Player(String name, int balance, int wins) {
        this.name = name;
        this.balance = balance;
        this.wins = wins;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void addWin() {
        this.wins++;
    }

    // Format utile pour l'écriture dans le fichier texte (format CSV)
    public String toCsvRow() {
        return name + "," + balance + "," + wins;
    }

    // Méthode utilitaire pour recréer un joueur depuis une ligne CSV
    public static Player fromCsvRow(String csvRow) {
        String[] parts = csvRow.split(",");
        if (parts.length == 3) {
            return new Player(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }
        throw new IllegalArgumentException("Format de ligne invalide : " + csvRow);
    }
}