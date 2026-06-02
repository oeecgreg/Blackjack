package fr.eseo.blackjack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Classe de test pour le point d'entrée de l'application {@link Main}.
 */
public class MainTest {

    /**
     * Vérifie que la méthode main s'exécute sans lever d'exceptions inattendues.
     * Cela garantit que le chaînage MVC (création du Service, injection dans la Vue)
     * se déroule correctement au lancement.
     */
    @Test
    public void testApplicationLaunchesWithoutCrashing() {
        // assertDoesNotThrow vérifie que le bloc de code s'exécute sans faire planter le test
        assertDoesNotThrow(() ->
                Main.main(new String[]{}), "Le lancement de l'application via le Main ne doit lever aucune exception.");
    }
}