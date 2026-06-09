# Blackjack - Projet Java Swing (ESEO - L3)

Ce projet est une implémentation du jeu de Blackjack en Java, avec une interface graphique Swing, réalisée dans le cadre du module de Qualité Logicielle (ESEO).

L'objectif principal de ce projet est de démontrer l'application stricte de l'architecture MVC, la mise en place d'une persistance des données, et une couverture de code rigoureuse via JUnit 5 et Mockito.

---

## 🏗️ Architecture du Projet

Le projet applique rigoureusement l'architecture **MVC (Modèle-Vue-Contrôleur)** afin de découpler totalement la logique métier de l'affichage Swing, garantissant ainsi la testabilité de l'application.

* **Inversion de Contrôle & Injection de Dépendances :** Le `GameService` ne crée pas son propre système d'accès aux données. Il reçoit une abstraction (`PlayerDao`) par son constructeur, ce qui permet d'injecter une implémentation de production (Fichier texte) ou un simulacre (Mock Mockito) pour les tests unitaires.
* **Calcul Métier Isolé :** La classe `Hand` embarque la logique complexe du calcul des scores (gestion de l'As valant 11 ou 1 selon le contexte) sans aucune dépendance visuelle.
* **Persistance par Patron DAO :** Implémentation complète d'un cycle CRUD (Create, Read, Update, Delete) stocké au format CSV au sein d'un fichier plat `joueurs.txt`.

---

## 🎨 Interface Graphique (UI/UX Moderne)

L'interface graphique a été entièrement optimisée pour offrir une expérience immersive typée "Casino" en plein écran :

* **HUD Élégant (Tableau de bord) :** Affichage du profil du joueur sous forme de badges translucides avec liserés dorés (Style HUD moderne).
* **Mises Tactiles en Triangle :** Les jetons (10€, 50€, 100€) sont dessinés vectoriellement (`Graphics2D`) et disposés en pyramide réaliste. Un clic gauche ajoute à la mise, un clic droit la diminue.
* **Centrage Absolu :** Utilisation combinée de `BorderLayout` et `GridBagLayout` pour garantir que la barre de mise reste l'axe de symétrie horizontal parfait en bas de la fenêtre.
* **Feedback Flottant en Couches :** Utilisation d'un `OverlayLayout` pour permettre au panneau d'annonces (utilisant la police *Cinzel*) de flotter en couche supérieure au-dessus du tapis de cartes sans perturber le placement général.

---

## 🧪 Stratégie de Validation et Qualité (19 Tests)

L'application intègre **19 tests automatisés** validant l'exhaustivité des comportements du jeu :

1. **Tests Métier & Algorithmiques (`HandTest`, `CardTest`, `DeckTest`) :**
    * Validation des scores nominaux et détection du Blackjack naturel (21 en 2 cartes).
    * Gestion avancée des As (ajustement de 11 à 1 lors de l'accumulation de plusieurs As).
    * Levée de l'exception personnalisée `EmptyDeckException` lorsque le paquet est totalement vidé.

2. **Tests de Logique & Mocking (`GameServiceTest`) :**
    * Isolation complète de la couche de persistance grâce à **Mockito**.
    * Simulation par Mock des scénarios de victoire, de défaite (Bust) et d'égalité (Tie), en vérifiant avec `verify()` que le DAO enregistre correctement le solde modifié exactement une fois.

3. **Tests d'Intégration et Persistance (`PlayerDaoTxtImplTest`) :**
    * Utilisation de l'annotation JUnit 5 `@TempDir` pour tester le CRUD sur un système de fichiers virtuel éphémère (Sandbox), évitant d'altérer les données réelles du fichier `joueurs.txt`.

---

## 🛡️ Gestion de la Robustesse et des Bugs

* **Gestion du Sabot :** Le contrôleur surveille en continu le nombre de cartes restantes. Si le sabot descend en dessous de 15 cartes, un nouveau jeu est mélangé de manière transparente, empêchant tout plantage en cours de manche.
* **Artefacts Graphiques (Ghosting) :** Pour pallier l'empilement de layouts transparents propres au moteur de rendu natif Swing, la méthode `setPlayingControlsEnabled` force un rafraîchissement complet du `contentPane` à chaque transition d'état, éliminant les résidus graphiques (dédoublement des boutons) à l'origine de la fenêtre.
* **Erreurs de Solde :** Verrouillage strict des commandes de jeu si la mise est nulle ou supérieure au solde réel du joueur.

---

## 🚀 Installation et Lancement

Ce projet utilise Maven pour la gestion des dépendances et le cycle de vie.

**Pour compiler et lancer la suite de tests automatisés :**
```bash
mvn clean test