package fr.eseo.blackjack.model.dao;

import fr.eseo.blackjack.model.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerDaoTxtImpl implements PlayerDao {

    private final Path filePath;

    public PlayerDaoTxtImpl(String fileName) {
        this.filePath = Paths.get(fileName);
        try {
            // Crée le fichier s'il n'existe pas encore
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.err.println("Erreur critique : Impossible de créer le fichier de sauvegarde.");
        }
    }

    @Override
    public void create(Player player) {
        if (read(player.getName()) != null) {
            throw new IllegalArgumentException("Un joueur avec ce nom existe déjà.");
        }
        try {
            String line = player.toCsvRow() + System.lineSeparator();
            Files.writeString(filePath, line, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    @Override
    public Player read(String name) {
        return readAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Player> readAll() {
        List<Player> players = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    players.add(Player.fromCsvRow(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture : " + e.getMessage());
        }
        return players;
    }

    @Override
    public void update(Player player) {
        List<Player> players = readAll();
        boolean updated = false;

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equalsIgnoreCase(player.getName())) {
                players.set(i, player); // On remplace par la nouvelle version (ex : nouveau solde).
                updated = true;
                break;
            }
        }

        if (updated) {
            rewriteFile(players);
        }
    }

    @Override
    public void delete(String name) {
        List<Player> players = readAll();
        boolean removed = players.removeIf(p -> p.getName().equalsIgnoreCase(name));

        if (removed) {
            rewriteFile(players);
        }
    }

    // Méthode interne pour écraser le fichier avec la liste mise à jour (utile pour update et delete)
    private void rewriteFile(List<Player> players) {
        try {
            List<String> lines = new ArrayList<>();
            for (Player p : players) {
                lines.add(p.toCsvRow());
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            System.err.println("Erreur lors de la réécriture du fichier : " + e.getMessage());
        }
    }
}