# Dukes-of-the-Realm
Ce projet est un jeu vidéo en java réalisé lors d'un cours de programmation objet.


###Mode d'emploi
Le menu principal vour permez de choisir entre le mode de jeu Classique ou un 
mode IA vs IADans le mode Classique, vous commencé la partie avec un chateau 
sous votre controle. Cliquer sur un chateau vous donne les informations le concernant.
Cliquer sur un chateau de votre équipe bous donne en plus accès à un menu pour effectuer 
les actions suivantes.[Train] : permettant de sélectionner les unité à produire.
[Send] : permettant de composer un ost avec les unité de la réserve et de choisir 
le chateau à attaquer. [Upgrade] : permettant d'augmenter le niveau d'un chateau et ainsi 
augmenter la vitesse de produciton d'or.
Vous Gagné la partie lorsque l'ennemi n'as plus de chateau ou d'unité sur la carte.

##Règle du jeu complète
Après avoir sélectionné un mode de jeu, la partie commence. la carte est généré avec 5 
chateaux à une position aléatoire. Un chateau bleu vous appartenant, un rouge appartenant
à l'IA et 3 blancs qui ne combattent pas. Votre chateau génère passivement de l'or vous 
permettant de faire des achats dans le menu. 

Appuyez sur les "Train" puis sur l'unité de votre choix pour lancer sa production. 
Un indicateur visuel en bas à gauche de l'écran pour montre les unités dans votre 
file de production. Une fois entrainé, l'unité rejoins votre réserve et incrémente 
le compteur du chateau. les unités dans la réserve défendent le chateau. Appuyez 
sur "Send" pour choisir les unité à envoyer. Appuyer sur le bouton corespondant à 
l'unité de votre choix. Elle est placé dans l'ost du chateau. Une fois l'ost complété, 
appuyé sur "Send OST". Une cible apparait sur les chateaux. Choisissez la destination 
de l'ost. SI le chateau est allié, l'ost vas rejoindre la réserve du chateau. Si le 
chateau est ennemi, Les unité vont soustraire la valeur de leur attaque à celle de la 
vie d'une unité dans la réserve du chateau en défense.

```java
public int getGold(){
    return this.gold;
}
```

| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |