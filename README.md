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

## 🧪 Stratégie de Validation et Qualité (27 Tests)

L'application intègre **27 tests automatisés** validant l'exhaustivité des comportements du jeu :

1. **Tests Métier & Algorithmiques (`HandTest`, `CardTest`, `DeckTest`) :**
   * Validation des scores nominaux et détection du Blackjack naturel (21 en 2 cartes).
   * Gestion avancée des As (ajustement de 11 à 1 lors de l'accumulation de plusieurs As).
   * Levée de l'exception personnalisée `EmptyDeckException` lorsque le paquet est totalement vidé.

2. **Tests de Logique & Mocking (`GameServiceTest`) :**
   * Isolation complète de la couche de persistance grâce à **Mockito**.
   * Simulation par Mock des scénarios complets de arbitrage : victoire nominale, défaite par dépassement (Bust) et égalité parfaite (Tie).
   * Vérification stricte via `verify()` que le service ordonne l'écriture et la sauvegarde du solde modifié exactement une fois.
   * Validation de la règle du casino imposant au croupier de tirer jusqu'à atteindre un score minimal de 17 (`testDealerPlayStopsAt17`).

3. **Tests d'Intégration et Persistance (`PlayerDaoTxtImplTest`) :**
   * Utilisation de l'annotation JUnit 5 `@TempDir` pour tester le cycle CRUD complet sur un système de fichiers virtuel éphémère (Sandbox), évitant d'altérer ou de polluer les données réelles du fichier `joueurs.txt`.

4. **Tests de Sérialisation Données (`PlayerTest`) :**
   * Validation de la transformation des entités en lignes CSV (`toCsvRow`).
   * Gestion et détection des anomalies de format de fichier de sauvegarde via la levée d'une `IllegalArgumentException` en cas de ligne corrompue.

---

## 🛡️ Gestion de la Robustesse et des Bugs

* **Gestion du Sabot :** Le contrôleur surveille en continu le nombre de cartes restantes. Si le sabot descend en dessous de 15 cartes, un nouveau jeu est mélangé de manière transparente, empêchant tout plantage en cours de manche.
* **Artefacts Graphiques (Ghosting) :** Pour pallier l'empilement de layouts transparents propres au moteur de rendu natif Swing, la méthode `setPlayingControlsEnabled` force un rafraîchissement complet du `contentPane` à chaque transition d'état, éliminant les résidus graphiques (dédoublement des boutons) à l'origine de la fenêtre.
* **Erreurs de Solde :** Verrouillage strict des commandes de jeu si la mise est nulle ou supérieure au solde réel du joueur.

---

## 🐛 Historique des Bugs de Développement et Résolutions

Dans le cadre de la démarche de Qualité Logicielle, voici le suivi des anomalies technologiques rencontrées lors du cycle de développement et les solutions appliquées (classées des plus complexes aux plus simples) :

### 1. Rendu Fantôme (*Ghosting*) Graphique au point (0,0) [Complexe]
* **Symptôme :** Lors du basculement d'activation des contrôles, le bouton "Distribuer" laissait une réplique visuelle figée en arrière-plan du HUD tout en haut à gauche de la fenêtre.
* **Cause :** L'optimisation native du *RepaintManager* de Swing ne redessinait pas les couches inférieures à cause de la transparence (`setOpaque(false)`) appliquée aux sous-panneaux.
* **Résolution :** Ajout d'une instruction `getContentPane().repaint()` dans la méthode de transition d'état afin de forcer un nettoyage complet de la mémoire tampon de l'écran avec l'image de fond de la table.

### 2. Décentrage et Instabilité Spatiale des Jetons [Complexe]
* **Symptôme :** La pyramide de jetons se décalait de manière asymétrique vers la gauche dès que la mise du joueur augmentait en valeur.
* **Cause :** L'utilisation de `BoxLayout` combinée à des composants extensibles modifiait dynamiquement l'alignement horizontal central dès que la largeur du composant de texte de droite variait.
* **Résolution :** Implémentation d'une structure en `BorderLayout` globale pour le panneau de contrôle inférieur et application de la technique du *Bloc Fantôme* à gauche pour faire contrepoids parfait au texte de mise à droite, stabilisant le triangle de jetons au centre géométrique exact de l'écran.

### 3. Conflit de Profondeur et Scintillement du Tapis (Z-Index) [Complexe]
* **Symptôme :** L'affichage textuel "Placez vos mises" décalait le flux des cartes distribuées vers le bas ou créait un clignotement de l'arrière-plan.
* **Cause :** Un layout linéaire classique (`BoxLayout.Y_AXIS`) interdit la superposition et force chaque élément à réclamer une zone d'affichage exclusive.
* **Résolution :** Migration de la zone centrale vers un gestionnaire de superposition `OverlayLayout` et surcharge de `isOptimizedDrawingEnabled() { return false; }` pour forcer Swing à gérer correctement la transparence des couches. Le label de feedback flotte dorénavant de manière totalement indépendante sur une couche supérieure.

### 4. Exception d'Épuisement de la Pioche (*IndexOutOfBoundsException*) [Modéré]
* **Symptôme :** Enchaîner plusieurs parties sans redémarrer le programme provoquait un crash dès que la 53ème carte devait être piochée.
* **Cause :** Absence de garde-fou algorithmique sur la méthode `drawCard()` lorsque la liste interne du `Deck` se retrouvait totalement vidée.
* **Résolution :** Encapsulation de l'erreur dans une exception métier `EmptyDeckException` et intégration d'une vérification préventive automatique réinstanciant et mélangeant un nouveau deck dès que le sabot contient moins de 15 cartes.

### 5. Fuite de Ressources (*Resource Leak*) lors de la Sauvegarde [Modéré]
* **Symptôme :** Après un test en échec, le fichier `joueurs.txt` devenait verrouillé par le système d'exploitation, interdisant toute modification ultérieure du solde.
* **Cause :** Les flux d'entrée/sortie (`FileReader` / `FileWriter`) n'étaient pas explicitement fermés en cas de levée d'exception lors du traitement.
* **Résolution :** Utilisation des méthodes de l'API moderne `java.nio.file.Files` qui gèrent de manière native la fermeture sécurisée et automatique des descripteurs de fichiers, même en cas d'interruption.

### 6. Concurrence Événementielle et Désynchronisation (Timer Swing) [Modéré]
* **Symptôme :** Cliquer frénétiquement sur "Tirer" pendant l'animation initiale distribuait trop de cartes et faussait le score de la manche.
* **Cause :** Les événements de clics sur les boutons s'empilaient et s'exécutaient dans l'EDT (*Event Dispatch Thread*) en plein milieu de l'exécution séquentielle du `javax.swing.Timer`.
* **Résolution :** Désactivation totale et immédiate de l'interactivité (`setPlayingControlsEnabled(false)`) dès le déclenchement du Timer, avec une réactivation conditionnelle uniquement en fin de script d'animation.

### 7. Goulet d'Étranglement CPU (I/O dans le Thread UI) [Modéré]
* **Symptôme :** Fortes saccades (baisse de FPS) lors du redimensionnement de la fenêtre de jeu.
* **Cause :** L'image haute définition du tapis était lue sur le disque (`ImageIO.read`) directement à l'intérieur de la méthode `paintComponent`, provoquant des centaines d'accès disques redondants par seconde.
* **Résolution :** Mise en cache de l'image en mémoire vive (RAM) lors de l'instanciation de la vue. La méthode `paintComponent` n'effectue plus qu'une simple projection mémoire.

### 8. NullPointerException (NPE) au premier lancement global [Simple]
* **Symptôme :** Plantage immédiat au tout premier démarrage de l'application sur un nouvel ordinateur.
* **Cause :** Le constructeur du DAO tentait d'ouvrir et de lire le fichier `joueurs.txt` avant même que celui-ci n'ait été créé sur le disque.
* **Résolution :** Ajout d'une vérification de sécurité simple avant la lecture : `if (!Files.exists(path)) { Files.createFile(path); }` afin d'initialiser une base de données locale vierge si nécessaire.

### 9. Cartes "Fantômes" lors du changement de manche [Simple]
* **Symptôme :** En cliquant sur "Distribuer" pour lancer une nouvelle partie, les cartes de la manche précédente restaient affichées à l'écran, superposées aux nouvelles.
* **Cause :** L'appel à `panel.removeAll()` supprime bien les composants logiques, mais ne force pas le gestionnaire d'affichage de Swing à rafraîchir l'écran physique.
* **Résolution :** Ajout des deux appels de rafraîchissement obligatoires immédiatement après le nettoyage du conteneur : `panel.revalidate(); panel.repaint();`.

### 10. Texte de score masqué ou sur fond gris opaque [Simple]
* **Symptôme :** Les labels des scores possédaient un rectangle gris opaque inesthétique tout autour du texte, masquant le feutre vert de la table.
* **Cause :** Par défaut, selon le système d'exploitation hôte (Look and Feel natif Windows/Mac), les composants `JLabel` peuvent être configurés comme opaques.
* **Résolution :** Application explicite de la propriété `label.setOpaque(false);` lors de la création de chaque étiquette de texte pour garantir une parfaite transparence sur le tapis.

### 11. Erreur d'homonymie et sensibilité à la casse (Case Sensitivity) [Simple]
* **Symptôme :** Un joueur enregistré sous le nom "Alice" perdait sa progression et voyait son solde réinitialisé s'il tapait "alice" lors de sa connexion suivante.
* **Cause :** Le moteur de recherche du DAO comparait les chaînes de caractères brutes du fichier CSV en utilisant `.equals()`, qui est strict sur la casse.
* **Résolution :** Remplacement de la condition de vérification par la méthode `.equalsIgnoreCase()`, résolvant cette friction d'expérience utilisateur.

---

## 🚀 Installation et Lancement

Ce projet utilise Maven pour la gestion des dépendances et le cycle de vie.

**Pour compiler et lancer la suite de tests automatisés :**
Résultat du code
README-v2.md generated successfully.

```bash
mvn clean test