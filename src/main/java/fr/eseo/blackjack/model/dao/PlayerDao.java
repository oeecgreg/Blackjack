package fr.eseo.blackjack.model.dao;

import fr.eseo.blackjack.model.Player;

import java.util.List;

/**
 * Interface définissant les opérations CRUD pour l'entité Player.
 */
public interface PlayerDao {
    // Create
    void create(Player player);

    // Read
    Player read(String name);
    List<Player> readAll();

    // Update
    void update(Player player);

    // Delete
    void delete(String name);
}