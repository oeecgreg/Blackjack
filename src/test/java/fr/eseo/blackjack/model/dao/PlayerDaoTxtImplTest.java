package fr.eseo.blackjack.model.dao;

import fr.eseo.blackjack.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerDaoTxtImplTest {

    // JUnit 5 crée un dossier temporaire et le nettoie après les tests
    @TempDir
    Path tempDir;

    private PlayerDaoTxtImpl dao;

    @BeforeEach
    public void setUp() {
        // On crée un fichier fictif spécifiquement pour ces tests
        Path tempFile = tempDir.resolve("test_joueurs.txt");
        dao = new PlayerDaoTxtImpl(tempFile.toString());
    }

    @Test
    public void testCreateAndReadPlayer() {
        Player p = new Player("Charlie", 1200, 2);
        dao.create(p);

        Player readPlayer = dao.read("Charlie");
        assertNotNull(readPlayer, "Le joueur fraîchement créé devrait être trouvé.");
        assertEquals("Charlie", readPlayer.getName());
        assertEquals(1200, readPlayer.getBalance());
    }

    @Test
    public void testUpdatePlayer() {
        dao.create(new Player("Charlie", 1200, 2));

        // Charlie gagne et son solde augmente
        Player updatedPlayer = new Player("Charlie", 1500, 3);
        dao.update(updatedPlayer);

        Player readPlayer = dao.read("Charlie");
        assertEquals(1500, readPlayer.getBalance(), "Le solde doit avoir été mis à jour.");
        assertEquals(3, readPlayer.getWins(), "Le nombre de victoires doit avoir été mis à jour.");
    }
}