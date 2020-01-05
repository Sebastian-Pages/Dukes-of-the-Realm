package game;
//this is a test

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class Main extends Application {

    /**
     * DECLARATION DES VARIABLES GLOBALES
     **/

    private Random rnd = new Random();

    private Pane playfieldLayer;
    private Pane menuLayer;

    private Image playerImage;
    //private Image enemyImage;
    private Image missileImage;

    //castle images
    private Image castleImage;
    private Image castleImageBlue;
    private Image castleImageBlueS;
    private Image castleImageRed;
    private Image castleImageRedS;
    private Image unitImageR;
    private Image grassImage;
    private Image targetImage;
    private Image backgroundImage;
    private Image classiqueImage;
    private Image iavsiaImage;

    //Units images
    private Image unitImage;

    //Selected castles
    private List<Castle> selected = new ArrayList<>();

    //private List<Enemy> enemies = new ArrayList<>();
    //private List<Missile> missiles = new ArrayList<>();
    private List<Castle> castles = new ArrayList<>();
    private List<Unit> units = new ArrayList<>();
    private List<Ost> osts = new ArrayList<>();
    private List<Button> buttons = new ArrayList<>();
    private List<Decoration> targets = new ArrayList<>();
    private List<Decoration> trainingQ = new ArrayList<>();
    public double orientationA[] = {0, 90, 180, 270};

    private Text infoMessage = new Text();
    private Text newMessage = new Text();
    private int scoreValue = 0;
    private boolean collision = false;
    private boolean needsAIGoalplayer = true;
    private boolean needsAIGoalennemi = true;
    private boolean pauseState = false;
    private boolean playerIsIA = false;
    private Castle aiGoalennemi;
    private Castle aiGoalplayer;
    long timestamp;
    private HBox statusBar;
    private int hboxState;
    public int global;

    private Scene scene;
    private Input input;
    private AnimationTimer gameLoop;

    Group root;

    @Override
    public void start(Stage primaryStage) {

        root = new Group();
        scene = new Scene(root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT + Settings.STATUS_BAR_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // create layers
        playfieldLayer = new Pane();
        menuLayer = new Pane();
        root.getChildren().add(playfieldLayer);

        /**INITIALISATION DU JEU**/
        //loadGame();
        loadMenu();

        /**DEBUT DE LA LOOP**/
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                processInput(input, now);
                if (global == 1) {
                    loadGame();
                    global = 2;
                }
                if (global == 2) {

                    //update army count
                    //updateUnitsCount(false);

                    // movement
                    checkUnitCollision();
                    units.forEach(sprite -> sprite.move());

                    // AI
                    AI("ennemi");
                    if (playerIsIA == true)
                        AI("player");


                    checkSieges();
                    //generate gold income
                    castles.forEach(sprite -> sprite.income());
                    // update sprites in scene

                    units.forEach(sprite -> sprite.updateUI());
                    castles.forEach(sprite -> sprite.trainUnit());

                    // check if sprite can be removed

                    //castles.forEach(sprite -> sprite.checkRemovability());
                    units.forEach(sprite -> sprite.checkRemovability());

                    // remove removables from list, layer, etc
                    removeSprites(castles);
                    removeSprites(units);
                    removeSprites(targets);

                    // update score, health, etc
                    updateText();
                    castles.forEach(castle -> castle.update());
                    checkIfGameOver();
                }
            }

            private void processInput(Input input, long now) {
                if (input.isExit()) {
                    Platform.exit();
                    System.exit(0);
                }
            }

        };

        gameLoop.start();
    }

    private void loadGame() {
        castleImage = new Image(getClass().getResource("/images/neutral_castle.png").toExternalForm(), 100, 100, true, true);
        castleImageBlue = new Image(getClass().getResource("/images/blue_castle.png").toExternalForm(), 100, 100, true, true);
        castleImageBlueS = new Image(getClass().getResource("/images/blue_castle_selected.png").toExternalForm(), 100, 100, true, true);
        castleImageRed = new Image(getClass().getResource("/images/red_castle.png").toExternalForm(), 100, 100, true, true);
        castleImageRedS = new Image(getClass().getResource("/images/red_castle_selected.png").toExternalForm(), 100, 100, true, true);
        unitImage = new Image(getClass().getResource("/images/blue_castle_selected.png").toExternalForm(), 20, 20, true, true);
        unitImageR = new Image(getClass().getResource("/images/red_castle_selected.png").toExternalForm(), 20, 20, true, true);
        targetImage = new Image(getClass().getResource("/images/target.png").toExternalForm(), 100, 100, true, true);
        grassImage = new Image(getClass().getResource("/images/grass.png").toExternalForm(), Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT, false, true);
        input = new Input(scene);
        input.addListeners();

        createStatusBar();

        //Initialize pause input
        scene.setOnKeyTyped(ke -> {
            if (input.isPaused()) {
                if (pauseState) {
                    gameLoop.start();
                    pauseState = false;
                } else {
                    gameLoop.stop();
                    pauseState = true;
                }
            }
        });

        //Initialize map
        Decoration grass = new Decoration(playfieldLayer, grassImage, 0, 0);
        grass.getView().setOnMousePressed(e -> {
            for (Castle c3 : selected) {
                c3.isSelected = false;
            }
            setStatusBar(statusBar, Settings.STATE_UNSELECTED);
            selected.clear();
            e.consume();
        });
        spawnCastles();
    }

    private void loadMenu() {
        classiqueImage = new Image(getClass().getResource("/images/classique.png").toExternalForm(), 350, 120, false, true);
        iavsiaImage = new Image(getClass().getResource("/images/iavsia.png").toExternalForm(), 350, 120, false, false);
        backgroundImage = new Image(getClass().getResource("/images/grass.png").toExternalForm(), Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT + Settings.STATUS_BAR_HEIGHT, false, true);
        input = new Input(scene);
        input.addListeners();

        /**
         String bip = "/images/music.mp3";
         Media hit = new Media(new File(bip).toURI().toString());
         MediaPlayer mediaPlayer = new MediaPlayer(hit);
         mediaPlayer.play();**/

        Decoration menuBackground = new Decoration(playfieldLayer, backgroundImage, 0, 0);
        Decoration classique = new Decoration(playfieldLayer, classiqueImage, (Settings.SCENE_WIDTH / 2) - 175, (Settings.SCENE_HEIGHT / 2) - 150);
        Decoration iavsia = new Decoration(playfieldLayer, iavsiaImage, (Settings.SCENE_WIDTH / 2) - 175, (Settings.SCENE_HEIGHT / 2) + 150);
        classique.getView().setOnMousePressed(e -> {
            global = 1;
            e.consume();
        });
        iavsia.getView().setOnMousePressed(e -> {
            global = 1;
            playerIsIA = true;
            e.consume();
        });
    }


    public void createStatusBar() {
        statusBar = new HBox();
        setStatusBar(statusBar, Settings.STATE_INIT);

        statusBar.relocate(0, Settings.SCENE_HEIGHT);
        statusBar.setPrefSize(Settings.SCENE_WIDTH, Settings.STATUS_BAR_HEIGHT);
        root.getChildren().add(statusBar);
    }

    //GESTION DES SOUS-MENUS
    public void setStatusBar(HBox sb, int state) {
        switch (state) {
            case Settings.STATE_INIT:
                hboxState = Settings.STATE_INIT;
                infoMessage.setText("Click on a castle");
                sb.getChildren().addAll(infoMessage);
                sb.getStyleClass().add("statusBar");
                sb.setSpacing(15);
                break;

            case Settings.STATE_UNSELECTED:
                hboxState = Settings.STATE_UNSELECTED;
                for (Button b : buttons) {
                    sb.getChildren().remove(b);
                }
                infoMessage.setText("Click on a castle");
                break;

            case Settings.STATE_FIRST:
                hboxState = Settings.STATE_FIRST;
                for (Button b : buttons) {
                    sb.getChildren().remove(b);
                }
                Castle c = selected.get(0);
                infoMessage.setText("Castle: owner: " + c.getOwner() + "\n	    Gold:   " + Math.round(c.getGold()) + "\n	    Level:  " + c.getLevel());


                Button trainButton = new Button("Train");
                buttons.add(trainButton);
                sb.getChildren().addAll(trainButton);


                Button sendButton = new Button("Send");
                buttons.add(sendButton);
                sb.getChildren().addAll(sendButton);

                Button upgradeButton = new Button("Upgrade");
                buttons.add(upgradeButton);
                sb.getChildren().addAll(upgradeButton);

                //gérer les actions
                trainButton.setOnAction(e -> {
                    setStatusBar(sb, Settings.STATE_TRAIN);
                    e.consume();
                });
                sendButton.setOnAction(e -> {
                    setStatusBar(sb, Settings.STATE_SEND);
                    e.consume();
                });
                upgradeButton.setOnAction(e -> {
                    setStatusBar(sb, Settings.STATE_UPGRADE);
                    e.consume();
                });

                break;

            case Settings.STATE_INFO:
                hboxState = Settings.STATE_FIRST;
                for (Button b : buttons) {
                    sb.getChildren().remove(b);
                }
                c = selected.get(0);
                infoMessage.setText("Castle: owner: " + c.getOwner() + "\n	    Gold:   " + Math.round(c.getGold()) + "\n	    Level:  " + c.getLevel());
                break;

            case Settings.STATE_TRAIN:
                hboxState = Settings.STATE_TRAIN;
                for (Button b : buttons) {
                    sb.getChildren().remove(b);
                }

                Button returnButton = new Button("<-");
                sb.getChildren().addAll(returnButton);
                buttons.add(returnButton);
                returnButton.setOnAction(e -> {
                    setStatusBar(sb, Settings.STATE_FIRST);
                    e.consume();
                });

                Button piquierButton = new Button("Piquier 100£");
                sb.getChildren().addAll(piquierButton);
                buttons.add(piquierButton);
                c = selected.get(0);
                piquierButton.setOnAction(e -> {
                    buyUnit(c, 0, Settings.PIKEMAN_COST);
                    e.consume();
                });
                Button chevalierButton = new Button("Chevalier 500£");
                sb.getChildren().addAll(chevalierButton);
                buttons.add(chevalierButton);
                chevalierButton.setOnAction(e -> {
                    buyUnit(c, 1, Settings.KNIGHT_COST);
                    e.consume();
                });

                Button onagreButton = new Button("Onagre 900£");
                sb.getChildren().addAll(onagreButton);
                buttons.add(onagreButton);
                onagreButton.setOnAction(e -> {
                    buyUnit(c, 2, Settings.ONAGER_COST);
                    e.consume();
                });

                break;

            case Settings.STATE_SEND:
                hboxState = Settings.STATE_SEND;
                for (Button b : buttons) {
                    sb.getChildren().remove(b);
                }
                c = selected.get(0);
                Button returnButton2 = new Button("<-");
                sb.getChildren().addAll(returnButton2);
                buttons.add(returnButton2);
                returnButton2.setOnAction(e -> {
                    setStatusBar(sb, Settings.STATE_FIRST);
                    e.consume();
                });
                int unitCount = c.countUnits(Settings.PIKEMAN_TYPE);
                Button piquierButton2 = new Button("Piquier: " + unitCount);
                sb.getChildren().addAll(piquierButton2);
                piquierButton2.setOnAction(e -> {
                    reserveToOst(c, Settings.PIKEMAN_TYPE);
                    setStatusBar(sb, Settings.STATE_SEND);
                    e.consume();
                });
                buttons.add(piquierButton2);

                Button chevalierButton2 = new Button("Chevalier");
                sb.getChildren().addAll(chevalierButton2);
                buttons.add(chevalierButton2);
                chevalierButton2.setOnAction(e -> {
                    reserveToOst(c, Settings.KNIGHT_TYPE);
                    e.consume();
                });

                Button onagreButton2 = new Button("Onagre");
                sb.getChildren().addAll(onagreButton2);
                buttons.add(onagreButton2);
                onagreButton2.setOnAction(e -> {
                    reserveToOst(c, Settings.ONAGER_TYPE);
                    e.consume();
                });

                Button sendOstButton = new Button("Send OST");
                sb.getChildren().addAll(sendOstButton);
                sendOstButton.setOnAction(e -> {
                    selectTarget(c);
                    e.consume();
                });
                buttons.add(sendOstButton);

                break;

            case Settings.STATE_UPGRADE:
                hboxState = Settings.STATE_UPGRADE;
                for (Button b : buttons) {
                    sb.getChildren().remove(b);
                }
                c = selected.get(0);
                Button returnButton3 = new Button("<-");
                sb.getChildren().addAll(returnButton3);
                buttons.add(returnButton3);
                returnButton3.setOnAction(e -> {
                    setStatusBar(sb, Settings.STATE_FIRST);
                    e.consume();
                });
                Button levelUp = new Button("Level Up");
                sb.getChildren().addAll(levelUp);
                levelUp.setOnAction(e -> {
                    c.levelUp();
                    e.consume();
                });
                buttons.add(levelUp);

                Button mercenary = new Button("Mercenaire");
                sb.getChildren().addAll(mercenary);
                buttons.add(mercenary);
                mercenary.setOnAction(e -> {
                    //reserveToOst(c,Settings.KNIGHT_TYPE);
                    e.consume();
                });
        }
    }

    void displayQ(Castle c) {
        for (Unit u : c.productionQ) {
            Image img = unitImage;
            if (u.type == 0)
                img = unitImage;

            int x = c.productionQ.indexOf(u);
            Decoration un = new Decoration(playfieldLayer, img, x * 21 + 10, 770);
            trainingQ.add(un);
        }

    }


    /**
     * INITIALISE LES CHATEAUX
     **/
    private void spawnCastles() {
        boolean placed_well = true;


        /**ON GENERE N CHATEAU AU DEBUT DE LA PARTIE**/
        while ((castles.size() < Settings.NUMBER_OF_CASTLES)) {
            ListIterator<Castle> it = castles.listIterator();
            double speed = 0;
            double x = rnd.nextDouble() * (Settings.SCENE_WIDTH - castleImage.getWidth());
            double y = rnd.nextDouble() * (Settings.SCENE_HEIGHT - castleImage.getHeight());
            int rndOrientationIndex = rnd.nextInt(4);
            double orientation = orientationA[rndOrientationIndex];
            Castle castle = new Castle(playfieldLayer, castleImage, x, y, 1, 1, speed, orientation);

            castle.getView().setOnMousePressed(e -> {
                infoMessage.setText("Castle [ owner: " + castle.getOwner() + "\n	    Gold:   " + Math.round(castle.getGold()) + " | Level: " + castle.getLevel() + " ]");
                manageSelectedCastles(castle);
                e.consume();
            });

            //parcours des chateaux pour éviter les collisions et une distance min entre eux
            while (it.hasNext()) {
                Castle c = it.next();
                double deltaX = Math.abs(castle.getCenterX()-c.getCenterX());
                double deltaY = Math.abs(castle.getCenterY()-c.getCenterY());

                if (castle.collidesWith(c) || (deltaX<120) || (deltaY<120)) {
                    placed_well = false;
                }
            }

            if (placed_well) {
                castle.CastleSet(2, castleImage);
                castle.time = System.currentTimeMillis();
                castle.setGold(1000);
                setOnClickBehaviour(castle);
                castles.remove(castle);

                //pour tous les chateau faire une HBOX
                HBox unitCount = new HBox();
                castle.newMessage.setText("0");
                unitCount.getChildren().addAll(castle.newMessage);
                unitCount.getStyleClass().add("uc");
                unitCount.relocate(castle.getX(), castle.getY());
                unitCount.setPrefSize(10, 10);
                root.getChildren().add(unitCount);
                castles.add(castle);
            } else {
                castle.removeFromLayer();
                castle.remove();
            }
            placed_well = true;
            System.out.println(""+castles.size());
        }
        // pick 1 starting castles
        Castle castle_1 = castles.get(2);
        castles.remove(2);
        castle_1.CastleSet(0, castleImageBlue);
        setOnClickBehaviour(castle_1);
        castles.add(2, castle_1);

        // pick ennemy starting castles
        Castle castle_2 = castles.get(3);
        castles.remove(3);
        castle_2.CastleSet(1, castleImageRed);
        setOnClickBehaviour(castle_2);
        castles.add(3, castle_2);

        for (Castle castle : castles) {
            for (int i = 0; i < 4; i++) {
                buyUnit(castle, 0, 100);
            }

        }


    }

    /**
     * private void updateUnitsCount(boolean bool){
     * if(bool) {
     * for (Castle c : castles) {
     * if (c.getGold()>10) {
     * <p>
     * if (c.getOwner()=="player") {
     * Unit u = new Unit(playfieldLayer,unitImage, c.getCenterX(), c.getCenterY(), 1, 1, 1);
     * u.owner = "player";
     * double temp=c.getGold();
     * c.setUnitProduction(temp-10);
     * c.reserveAdd(u);
     * u.removeFromLayer();
     * }
     * if(c.getOwner()=="ennemi") {
     * Unit u = new Unit(playfieldLayer,unitImageR, c.getCenterX(), c.getCenterY(), 1, 1, 1);
     * u.owner="ennemi";
     * double temp=c.getGold();
     * c.setUnitProduction(temp-10);
     * c.reserveAdd(u);
     * u.removeFromLayer();
     * }
     * if(c.getOwner()=="unowned") {
     * Unit u = new Unit(playfieldLayer,unitImageR, c.getCenterX(), c.getCenterY(), 1, 1, 1);
     * u.owner="unowned";
     * double temp=c.getGold();
     * c.setUnitProduction(temp-10);
     * c.reserveAdd(u);
     * u.removeFromLayer();
     * }
     * }
     * }
     * }
     * <p>
     * }
     **/

    private void buyUnit(Castle c, int type, int cost) {
        if (c.getGold() > cost) {
            String owner = c.getOwner();
            //System.out.println(""+owner);
            Unit u = null;
            Image img = unitImage;
            double offset = img.getWidth()/2;
            if (c.getOwner() == "player")
                img = unitImage;
            if (c.getOwner() == "ennemi")
                img = unitImageR;
            if (c.getOwner() == "unowned")
                img = unitImageR;

            if (type == Settings.PIKEMAN_TYPE) {
                u = new Pikeman(playfieldLayer, unitImage, c.getCenterX()-offset, c.getCenterY()-offset, owner);
            }
            if (type == Settings.KNIGHT_TYPE) {
                u = new Knight(playfieldLayer, unitImage, c.getCenterX()-offset, c.getCenterY()-offset, owner);
            }
            if (type == Settings.ONAGER_TYPE) {
                u = new Onager(playfieldLayer, unitImage, c.getCenterX()-offset, c.getCenterY()-offset, owner);
            }

            double temp = c.getGold();
            c.setGold(temp - u.getCost());
            c.productionQ.add(u);
            //System.out.println(u+" added to prodQ");
            u.removeFromLayer();
        }
    }


    private void removeSprites(List<? extends Sprite> spriteList) {
        Iterator<? extends Sprite> iter = spriteList.iterator();
        while (iter.hasNext()) {
            Sprite sprite = iter.next();

            if (sprite.isRemovable()) {
                // remove from layer
                sprite.removeFromLayer();
                // remove from list
                iter.remove();
            }
        }
    }

    /**
     * private void checkCollisions() {
     * collision = false;
     * <p>
     * <p>
     * <p>
     * for (Enemy enemy : enemies) {
     * for (Missile missile : missiles) {
     * if (missile.collidesWith(enemy)) {
     * enemy.damagedBy(missile);
     * missile.remove();
     * collision = true;
     * scoreValue += 10 + (Settings.SCENE_HEIGHT - player.getY()) / 10;
     * }
     * }
     * <p>
     * if (player.collidesWith(enemy)) {
     * collision = true;
     * enemy.remove();
     * player.damagedBy(enemy);
     * if (player.getHealth() < 1)
     * gameOver();
     * }
     * }
     * }
     **/
    private void checkOrders() {
        if (selected.size() > 1) {
            //System.out.println(selected.get(0).getOwner() + (selected.get(1).getOwner()));
            if (
                    (selected.get(0).getOwner() == "player" && selected.get(1) != selected.get(0) /**&&( selected.get(1).getOwner()=="unowned")||(selected.get(1).getOwner()=="ennemi")**/) &&
                            (selected.get(0).getReserveSize() > 0) && (selected.get(0).isReadyToAttack)) {
                Castle c = selected.get(0);
                Castle d = selected.get(1);
                /**
                 System.out.println(selected.get(0).getOwner() +" attacks -> "+ (selected.get(1).getOwner()));
                 Unit u = c.reservePull();
                 //set destination of unit
                 u.setGoalx(d.getCenterX());
                 u.setGoaly(d.getCenterY());

                 u.addToLayer();
                 units.add(u);
                 if (c.getReserveSize()==0)
                 selected.clear();**/

                //sendOstAI(c,d);
                selected.clear();
            }

        }

    }


    private void reserveToOst(Castle c, int unitType) {
        if (c.hasUnit(unitType)) {
            String mystring = " ";
            Image im = unitImage;

            if (c.getOwner() == "player") {
                im = unitImageR;
                mystring = "player";
            }
            if (c.getOwner() == "ennemi") {
                im = unitImageR;
                mystring = "ennemi";
            }
            if (!c.isBuildingOst) {
                Ost o = new Ost();
                o.owner = mystring;
                c.setOst(o);
                c.isBuildingOst = true;
                osts.add(o);
            }
            Unit u = c.reservePull(unitType);
            c.ost.reserveAdd(u);
            //u.remove();
            //u.removeFromLayer();
        }
    }

    /**
     * private void sendOstAI(Castle source,Castle dest) {
     * <p>
     * //on veux au moins 1 unité dans l'ost
     * if (source.getReserveSize()>0) {
     * String mystring=" ";
     * Image im=unitImage;
     * <p>
     * if (source.getOwner()=="player") {
     * im = unitImage;
     * mystring="player";
     * }
     * if(source.getOwner()=="ennemi") {
     * im = unitImageR;
     * mystring="ennemi";
     * }
     * Ost o = new Ost(playfieldLayer,im, source.getCenterX(), source.getCenterY(), 1, 1, 1);
     * o.owner = mystring;
     * <p>
     * //pour chaque unité dans le chateau, l'ajouter à l'ost
     * while (source.getReserveSize()>0) {
     * Unit u = source.reservePull(0);
     * o.reserveAdd(u);
     * }
     * o.setGoalx(dest.getCenterX());
     * o.setGoaly(dest.getCenterY());
     * //System.out.println("ost: "+ o+" size: "+o.getReserveSize());
     * osts.add(o);
     * //System.out.println("ost[]: "+ osts.get(0));
     * }
     * <p>
     * <p>
     * if(selected.size()>1) {
     * //System.out.println(selected.get(0).getOwner() + (selected.get(1).getOwner()));
     * if(
     * (selected.get(0).getOwner()=="player" && selected.get(1)!=selected.get(0) &&( selected.get(1).getOwner()=="unowned")||(selected.get(1).getOwner()=="ennemi"))&&
     * (selected.get(0).getReserveSize()>0) &&(selected.get(0).isReadyToAttack))
     * {
     * Castle c=selected.get(0);
     * Castle d=selected.get(1);
     * <p>
     * System.out.println(selected.get(0).getOwner() +" attacks -> "+ (selected.get(1).getOwner()));
     * Unit u = c.reservePull(0);
     * <p>
     * u.addToLayer();
     * units.add(u);
     * if (c.getReserveSize()==0)
     * selected.clear();
     * //gameOver();
     * }
     * <p>
     * }
     * <p>
     * }
     **/
    private void selectTarget(Castle c) {
        for (Castle targetc : castles) {
            if (targetc != c) {
                Decoration target = new Decoration(playfieldLayer, targetImage, targetc.getX(), targetc.getY());
                targets.add(target);
                target.getView().setOnMousePressed(e -> {
                    sendOst(c, targetc);
                    //System.out.println("DEBUG: ");
                    e.consume();
                });
            }
        }
    }

    private void sendOst(Castle c, Castle d) {

        Ost o = c.ost;
        o.setSpeed(o.getOstSpeed());
        for (Unit u : o.reserve) {
            u.setSpeed(o.getSpeed()); // techniquement on a plus besoin de �a car chaque unit est ind�pendante
            u.setGoalx(d.getCenterX()-10);
            u.setGoaly(d.getCenterY()-10);
            u.addToPath(c.getEntrance());
            u.addToPath(d.getEntrance());
            double[] doubleArray = new double[]{ d.getCenterX()-10,d.getCenterY()-10};
            u.addToPath(doubleArray);
            //System.out.println("Path: "+u.path.get(0)[0] +","+u.path.get(0)[1]);
            u.isNotAtDoor = false;
            units.add(u);
            //System.out.println("u.x "+u.goalx+"u.goalx "+u.getGoalx());
            //System.out.println("added to list");
            if (!playfieldLayer.getChildren().contains(u.imageView)) {
                u.addToLayer();
            }
        }
        //System.out.println("DEBUG: "+"ost size: "+c.ost.getReserveSize());
        o.reserve.clear();
        c.isBuildingOst = false;
        if(c.getOwner()=="player")
        	targets.forEach(sprite -> sprite.remove());

    }

    private void checkSieges() {
        List<Unit> unitsToDelete = new ArrayList<>();
        for (Unit u : units) {
            // La condition est comme ça pour laisser de la marge d'erreur et ne pas rater la collision
            if (
                    ((int) u.getGoalx() - 5 < (int) u.getX()) &&
                            ((int) u.getX() < (int) u.getGoalx() + 5) &&
                            ((int) u.getGoaly() - 5 < (int) u.getY()) &&
                            ((int) u.getY() < (int) u.getGoaly() + 5)) {
                for (Castle c : castles) {
                    if (u.collidesWith(c)) {
                        //System.out.println("collision");
                        if (c.getReserveSize() > 0) {
                            //System.out.println("c " + c.getOwner() + " u " + u.owner );
                            if (c.getOwner() == u.owner) { //m�me owner -> ajout � la garnison
                                c.reserveAdd(u);

                            } else { // attaque
                                //System.out.println("attaque");
                                c.takeDamage(u);
                                unitsToDelete.add(u);
                            }
                        } else {
                            int type = 0;
                            Image im = castleImage;
                            if (u.owner == "player") {
                                type = 0;
                                im = castleImageBlue;
                                needsAIGoalplayer = true;
                            }

                            if (u.owner == "ennemi") {
                                type = 1;
                                im = castleImageRed;
                                needsAIGoalennemi = true;
                            }
                            c.CastleSet(type, im);
                            setOnClickBehaviour(c);
                            c.reserveAdd(u);
                            u.path.clear();
                        }
                        //u.remove();
                        unitsToDelete.add(u);
                    }
                }

            }
            //u.removeFromLayer();
        }

        units.removeAll(unitsToDelete);
        unitsToDelete.forEach(sprite -> sprite.removeFromLayer());
        unitsToDelete.clear();
    }

    private void checkUnitCollision() {
        for (Unit u : units) {
            if (u.path.size()<2)
                u.isNotAtDoor=false;
            for (Castle c : castles) {
                if (u.collidesWith(c)&&u.isNotAtDoor) {
                    u.isColliding=true;
                    //calculate new coordinates
                    double[] p= new double[]{ u.getX(),u.getY()};
                    double[] p2 =new double[]{ u.getX(),u.getY()};

                    double deltaX =c.getCenterX() - u.getX()+10;
                    double deltaY =u.getY()+10-c.getCenterY() ;

                    //hitting north border
                    if(u.movingS) {
                        p[0] -= 100;
                        p[1] -= 4;
                        u.setY(u.getY()-4);
                        p2[0]=p[0];
                        p2[1]=p[1]+130;
                        u.path.add(0,p2);
                        System.out.println("DEBUG : N Border | X: "+ (60-deltaX));
                    }
                    if(u.movingN) {
                        p[0] += 100;
                        p[1] += 4;
                        u.setY(u.getY()+4);
                        p2[0]=p[0];
                        p2[1]=p[1]-130;
                        u.path.add(0,p2);
                        System.out.println("DEBUG : S Border | X: "+ (60-deltaX));
                    }
                    if(u.movingE) { // -> [
                        p[0] -= 4;
                        p[1] += 100;
                        u.setX(u.getX()-4);
                        System.out.println("DEBUG : W Border | Y: "+ (60-deltaY));
                    }
                    if(u.movingW) {
                        p[0] += 4;
                        p[1] -= 100;
                        u.setX(u.getX()+4);
                        System.out.println("DEBUG : E Border | Y: "+ (60-deltaY));
                    }
                    System.out.println("DEBUG : Point added");
                    u.path.add(0,p);

                }
            }
            u.isColliding=false;
        }
    }

    // ne marche pas lorqu'il reste des chateaux neutre
    private void checkIfGameOver() {
        boolean areAllOwnedByTheSame = true;
        String s = castles.get(2).getOwner();
        for (Castle c : castles) {
            if (c.getOwner() != s && c.getOwner() != "unowned") {
                areAllOwnedByTheSame = false;
            }
        }
        if (areAllOwnedByTheSame)
            gameOver();
    }

    private void gameOver() {
        HBox hbox = new HBox();
        hbox.setPrefSize(Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);
        hbox.getStyleClass().add("message");
        Text message = new Text();
        message.getStyleClass().add("message");
        message.setText("Game over");
        hbox.getChildren().add(message);
        root.getChildren().add(hbox);
        gameLoop.stop();
    }

    private void AI(String team) {

        //trouve un premier objectif
        if (needsAIGoalplayer | needsAIGoalennemi) {
            //System.out.println("DEBUG 1");
            for (Castle c : castles) {
                if ((c.getOwner() != team)) {
                    if (team == "ennemi") {
                        aiGoalennemi = c;
                        needsAIGoalennemi = false;
                    }
                    if (team == "player") {
                        aiGoalplayer = c;
                        needsAIGoalplayer = false;
                    }
                }
            }
        }

        //Attaquer aiGoal
        for (Castle c2 : castles) {
            if (c2.getOwner() == team) {
                buyUnit(c2, 0, 100);
                if (c2.getReserveSize() > 0) {
                    long now = System.currentTimeMillis();
                    if (now - c2.time > 2000) {
                        c2.time = now;
                        for (int i = 0; i < c2.getReserveSize(); i++) {
                            reserveToOst(c2, Settings.PIKEMAN_TYPE);
                        }
                        if (team == "ennemi")
                            sendOst(c2, aiGoalennemi);
                        if (team == "player") {
                            sendOst(c2, aiGoalplayer);
                        }
                    }
                }
            }
        }
    }

    private void updateText() {
        if (selected.size() > 0) {
            Castle c = selected.get(0);
            for (Decoration d : trainingQ) {
                d.removeFromLayer();
                d.remove();
            }
            displayQ(c);
            infoMessage.setText("Castle: owner: " + c.getOwner() + "\n	    Gold:   " + Math.round(c.getGold()) + "\n	    Level:  " + c.getLevel());
        } else
            setStatusBar(statusBar, Settings.STATE_UNSELECTED);
        //setStatusBar(statusBar,hboxState); // à debug
    }


    //on peut revoie la séléection et changer les views pour voir ce qui est sélectionner
    private void manageSelectedCastles(Castle c) {
        /**
         if (selected.size()>=2) {
         Castle temp = selected.get(1);
         selected.clear();
         selected.add(temp);
         selected.add(c);
         c.isSelected=true;
         for (Castle cas : castles) {
         if (cas != c)
         cas.isSelected = false;
         }
         }
         else {
         selected.add(c);
         c.isSelected=true;
         for (Castle cas : castles) {
         if (cas != c)
         cas.isSelected = false;
         }
         }**/
        selected.clear();
        selected.add(c);
        if (c.getOwner() == "player")
            setStatusBar(statusBar, Settings.STATE_FIRST);
        else
            setStatusBar(statusBar, Settings.STATE_INFO);
    }

    public void setOnClickBehaviour(Castle c) {
        c.getView().setOnMousePressed(e -> {
            //scoreMessage.setText("Castle: owner: "+c.getOwner()+"\n	    Units:  "+Math.round(c.getReserveSize())+"\n	    Level:  "+c.getLevel());
            manageSelectedCastles(c);
            e.consume();
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}