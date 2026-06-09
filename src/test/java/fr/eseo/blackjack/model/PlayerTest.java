package fr.eseo.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    public void testToCsvRow() {
        Player p = new Player("Bob", 500, 1);
        assertEquals("Bob,500,1", p.toCsvRow(), "La chaîne générée doit être au format CSV.");
    }

    @Test
    public void testFromCsvRowValid() {
        Player p = Player.fromCsvRow("Diana,2500,10");
        assertEquals("Diana", p.getName());
        assertEquals(2500, p.getBalance());
        assertEquals(10, p.getWins());
    }

    @Test
    public void testFromCsvRowInvalidThrowsException() {
        // Cas limite : Une ligne de sauvegarde corrompue (il manque le nombre de victoires)
        assertThrows(IllegalArgumentException.class, () -> Player.fromCsvRow("Diana,2500"),
                "Une ligne CSV invalide doit déclencher une IllegalArgumentException.");
    }
}