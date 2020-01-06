# Dukes-of-the-Realm
Ce projet est un jeu vidéo en java réalisé dans le cadre du cours de programmation objet.


###Mode d'emploi
Le menu principal vous permet de choisir entre le mode de jeu Classique ou un
mode IA vs IA. Dans le mode Classique, vous commencez la partie avec un chateau
sous votre contrôle. Cliquer sur un chateau vous donne les informations le concernant.
Cliquer sur un chateau de votre équipe vous donne en plus accès à un menu pour effectuer
des actions.[Train] : permettant de sélectionner les unité à produire.
[Send] : permettant de composer un ost avec les unités de la réserve et de choisir
le chateau à attaquer. [Upgrade] : permettant d'augmenter le niveau d'un chateau et ainsi
augmenter sa vitesse de production d'or.
Vous Gagnez la partie lorsque l'ennemi n'as plus de chateau ou d'unités sur la carte.

##Règle du jeu complète
Après avoir sélectionné un mode de jeu, la partie commence. la carte est générée avec 5
chateaux à des positions aléatoires. Un chateau bleu vous appartenant, un rouge appartenant
à l'IA et 3 blancs qui sont neutres et ne font que se défendre. Chaque chateau génère passivement de l'or dans sa réserve vous permettant de faire des achats de soldats dans le menu.

Appuyez sur "Train" puis sur l'unité de votre choix pour lancer sa production.
Un indicateur visuel en bas à gauche de l'écran vous montre les unités dans votre
file de production. Une fois entrainée, l'unité rejoint votre réserve et incrémente
le compteur de soldat du chateau. Les unités dans la réserve défendent le chateau. Appuyez
sur "Send" pour choisir les unités à envoyer. Appuyez sur le bouton correspondant à
l'unité de votre choix. Elle est placée dans l'ost du chateau. Une fois l'ost complété,
appuyez sur "Send OST". Une cible apparaît sur les chateaux. Choisissez la destination
de l'ost. Si le chateau est allié, l'ost va rejoindre la réserve du chateau. Si le
chateau est ennemi, les unités vont soustraire la valeur de leur attaque à celle de la
vie d'une unité dans la réserve du chateau en défense.

Voici un tableau détaillé des unités du jeu:

|                   | Pikeman       | Knight | Onagre  |
| :-----------:     |:-------------:| :-----:| :------:|
| Attaque           | 1             | 3      | 10      |
| Vie               | 1             | 2      | 5       |
| Coût              | 100           | 200    | 300     |
| Temps de Production| 30           | 100    | 200     |


##Fonctionnalités supplémentaires
* Barre de production des unités
* Affichage visuel de la cible d'envoi d'un OST
* Compteur des unités d'un chateau sur la carte
* Compteur de chaque type d'unité dans la reserve dans le menu Send
* Mode de jeu IA VS IA et mode de jeu Classique (Joueur VS IA)
* IA (Envoi de piquiers sur une même cible à intervalles réguliers, si plusieurs chateaux ils attaquent la même cible)
* Fichier Settings permettant de modifier les valeurs du jeu


##Fonctionnalités non implémentées
* Sauvegarde et chargement du jeu

##Bugs
* Pathfinding déffectueux lorsque la vitesse des unités est trop importante
* Compteur des unités dans le chateau qui ne se met pas correctement à jour
