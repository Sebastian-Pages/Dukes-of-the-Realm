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

/**
 * The main application class to run the game
 *
 */
public class Main extends Application {

    /**
     * DECLARATION DES VARIABLES GLOBALES
     **/

    private Random rnd = new Random();

    private Pane playfieldLayer;
    private Pane menuLayer;

    //castle images
    private Image castleImage;
    private Image castleImageBlue;
    private Image castleImageBlueS;
    private Image castleImageRed;
    private Image pikeman_red;
    private Image pikeman_blue;
    private Image knight_red;
    private Image knight_blue;
    private Image onager_red;
    private Image onager_blue;
    private Image grassImage;
    private Image targetImage;
    private Image backgroundImage;
    private Image classiqueImage;
    private Image iavsiaImage;

    //Units images
    private Image unitImage;

    //Selected castles
    private List<Castle> selected = new ArrayList<>();

    private List<Castle> castles = new ArrayList<>();
    private List<Unit> units = new ArrayList<>();
    private List<Ost> osts = new ArrayList<>();
    private List<Button> buttons = new ArrayList<>();
    private List<Decoration> targets = new ArrayList<>();
    private List<Decoration> trainingQ = new ArrayList<>();
    public double orientationA[] = {0, 90, 180, 270};

    private Text infoMessage = new Text();
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
    public int distanceMargin=10;

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

        //INITIALISATION DU JEU
        loadMenu();

        //DEBUT DE LA LOOP
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                processInput(input, now);
                if (global == 1) {
                    loadGame();
                    global = 2;
                }
                if (global == 2) {

                	//Sending ost's units with delay
                	for(Castle c : castles) {
                    	deploy(c);
                    }
                    
                    // movement
                    pathfindingCollision();
                    units.forEach(sprite -> sprite.move());

                    // AI
                    AI("ennemi");
                    if (playerIsIA == true)
                        AI("player");

                    //Resolving attacks
                    checkSieges();
                    
                    //generate gold income
                    castles.forEach(sprite -> sprite.income());
                    
                    // update sprites in scene
                    units.forEach(sprite -> sprite.updateUI());
                    castles.forEach(sprite -> sprite.trainUnit());
                    castles.forEach(sprite ->sprite.upgradeCastle());
                    
                    // check if units can be removed
                    units.forEach(sprite -> sprite.checkRemovability());

                    // remove removables from list, layer, etc
                    removeSprites(castles);
                    removeSprites(units);
                    removeSprites(targets);

                    //update information bar
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

    /**
     * Load images and input listeners
     */
    private void loadGame() {
        castleImage = new Image(getClass().getResource("/images/neutral_castle.png").toExternalForm(), Settings.CASTLE_SIZE, Settings.CASTLE_SIZE, true, true);
        castleImageBlue = new Image(getClass().getResource("/images/blue_castle.png").toExternalForm(), Settings.CASTLE_SIZE, Settings.CASTLE_SIZE, true, true);
        castleImageBlueS = new Image(getClass().getResource("/images/blue_castle_selected.png").toExternalForm(), Settings.CASTLE_SIZE, Settings.CASTLE_SIZE, true, true);
        castleImageRed = new Image(getClass().getResource("/images/red_castle.png").toExternalForm(), Settings.CASTLE_SIZE, Settings.CASTLE_SIZE, true, true);
        unitImage = new Image(getClass().getResource("/images/pikeman_blue.png").toExternalForm(), Settings.UNIT_SIZE, Settings.UNIT_SIZE, true, true);
        pikeman_blue = new Image(getClass().getResource("/images/pikeman_blue.png").toExternalForm(), Settings.UNIT_SIZE, Settings.UNIT_SIZE, true, true);
        pikeman_red = new Image(getClass().getResource("/images/pikeman_red.png").toExternalForm(), Settings.UNIT_SIZE, Settings.UNIT_SIZE, true, true);
        knight_blue = new Image(getClass().getResource("/images/knight_blue.png").toExternalForm(), Settings.UNIT_SIZE, Settings.UNIT_SIZE, true, true);
        knight_red = new Image(getClass().getResource("/images/knight_red.png").toExternalForm(), Settings.UNIT_SIZE, Settings.UNIT_SIZE, true, true);
        onager_blue = new Image(getClass().getResource("/images/onager_blue.png").toExternalForm(), Settings.UNIT_SIZE, Settings.UNIT_SIZE, true, true);
        onager_red = new Image(getClass().getResource("/images/onager_red.png").toExternalForm(), Settings.UNIT_SIZE, Settings.UNIT_SIZE, true, true);
        targetImage = new Image(getClass().getResource("/images/target.png").toExternalForm(), Settings.CASTLE_SIZE, Settings.CASTLE_SIZE, true, true);
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

    /**
     * Load starting menu
     */
    private void loadMenu() {
        classiqueImage = new Image(getClass().getResource("/images/classique.png").toExternalForm(), 350, 120, false, true);
        iavsiaImage = new Image(getClass().getResource("/images/iavsia.png").toExternalForm(), 350, 120, false, false);
        backgroundImage = new Image(getClass().getResource("/images/grass.png").toExternalForm(), Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT + Settings.STATUS_BAR_HEIGHT, false, true);
        input = new Input(scene);
        input.addListeners();
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


    /**
     * Create the information bar
     */
    public void createStatusBar() {
        statusBar = new HBox();
        setStatusBar(statusBar, Settings.STATE_INIT);

        statusBar.relocate(0, Settings.SCENE_HEIGHT);
        statusBar.setPrefSize(Settings.SCENE_WIDTH, Settings.STATUS_BAR_HEIGHT);
        root.getChildren().add(statusBar);
    }


    /**
     * Set the different bar configurations with buttons
     * 
     * @param sb 
     * 			the bar where to put the buttons and informations
     * @param state
     * 			the type of button and informations we want on the bar
     *
     */
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

                Button piquierButton = new Button("Pikeman: "+Settings.PIKEMAN_COST);
                sb.getChildren().addAll(piquierButton);
                buttons.add(piquierButton);
                c = selected.get(0);
                piquierButton.setOnAction(e -> {
                    buyUnit(c, Settings.PIKEMAN_TYPE, Settings.PIKEMAN_COST);
                    e.consume();
                });
                Button chevalierButton = new Button("Knight: "+Settings.KNIGHT_COST);
                sb.getChildren().addAll(chevalierButton);
                buttons.add(chevalierButton);
                chevalierButton.setOnAction(e -> {
                    buyUnit(c, Settings.KNIGHT_TYPE, Settings.KNIGHT_COST);
                    e.consume();
                });

                Button onagreButton = new Button("Onager: "+Settings.ONAGER_COST);
                sb.getChildren().addAll(onagreButton);
                buttons.add(onagreButton);
                onagreButton.setOnAction(e -> {
                    buyUnit(c, Settings.ONAGER_TYPE, Settings.ONAGER_COST);
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
                int pikemanCount = c.countUnits(Settings.PIKEMAN_TYPE);
                Button piquierButton2 = new Button("Pikeman: " + pikemanCount);
                sb.getChildren().addAll(piquierButton2);
                piquierButton2.setOnAction(e -> {
                    reserveToOst(c, Settings.PIKEMAN_TYPE);
                    setStatusBar(sb, Settings.STATE_SEND);
                    e.consume();
                });
                buttons.add(piquierButton2);

                int knightCount = c.countUnits(Settings.KNIGHT_TYPE);
                Button chevalierButton2 = new Button("Knight: "+knightCount);
                sb.getChildren().addAll(chevalierButton2);
                buttons.add(chevalierButton2);
                chevalierButton2.setOnAction(e -> {
                    reserveToOst(c, Settings.KNIGHT_TYPE);
                    setStatusBar(sb, Settings.STATE_SEND);
                    e.consume();
                });

                int onagerCount = c.countUnits(Settings.ONAGER_TYPE);
                Button onagreButton2 = new Button("Onager: "+onagerCount);
                sb.getChildren().addAll(onagreButton2);
                buttons.add(onagreButton2);
                onagreButton2.setOnAction(e -> {
                    reserveToOst(c, Settings.ONAGER_TYPE);
                    setStatusBar(sb, Settings.STATE_SEND);
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
        }
    }

    /**
     * Displays the castle's production queue
     * @param c
     * 		The castle 
     */
    void displayQ(Castle c) {
        for (Unit u : c.productionQ) {
            Image img=pikeman_blue;
            if (u.type == Settings.PIKEMAN_TYPE)
                img = pikeman_blue;
            if(u.type==Settings.KNIGHT_TYPE)
            	img=knight_blue;
            if(u.type==Settings.ONAGER_TYPE)
            	img=onager_blue;

            int x = c.productionQ.indexOf(u);
            Decoration un = new Decoration(playfieldLayer, img, x * 21 + 10, 770);
            trainingQ.add(un);
        }

    }


    /**
     * Initialize and place castles on the map
     **/
    private void spawnCastles() {
        boolean placed_well = true;


        //Generate Settings.NUMBER_OF_CASTLES castles at the beginning of the  game
        int count = 0;
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

            //check if castles collide
            while (it.hasNext()) {
                Castle c = it.next();
                double deltaX = Math.abs(castle.getCenterX()-c.getCenterX());
                double deltaY = Math.abs(castle.getCenterY()-c.getCenterY());

                if (castle.collidesWith(c) || (deltaX<120-count) || (deltaY<120-count) || castle.getCenterX()<100 || castle.getCenterX()>Settings.SCENE_WIDTH-100 || castle.getCenterY()<100 || castle.getCenterX()>Settings.SCENE_HEIGHT-100  ) {
                    placed_well = false;
                    if(count<50)
                    	count+= 1;
                }
            }

            if (placed_well) {
                castle.CastleSet(2, castleImage);
                castle.time = System.currentTimeMillis();
                castle.setGold(1000);
                setOnClickBehaviour(castle);
                castles.remove(castle);

                //making a unit count on castles
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
        }
        // pick 1 starting castles
        Castle castle_1 = castles.get(2);
        castles.remove(2);
        castle_1.CastleSet(0, castleImageBlue);
        setOnClickBehaviour(castle_1);
        castles.add(2, castle_1);

        // pick enemy starting castles
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
     * Buy unit in a castle and put it in the castle's production queue
     * @param c
     * 		The castle
     * @param type
     * 			The unit type
     * @param cost
     * 			The unit's cost			
     */
    private void buyUnit(Castle c, int type, int cost) {
        if (c.getGold() > cost) {
            String owner = c.getOwner();
            Unit u = null;
            Image img = unitImage;
            double offset = img.getWidth()/2;
            if (c.getOwner() == "unowned")
                img = unitImage;

            if (type == Settings.PIKEMAN_TYPE) {
                if (c.getOwner() == "player")
                    img = pikeman_blue;
                if (c.getOwner() == "ennemi")
                    img = pikeman_red;
                u = new Pikeman(playfieldLayer, img, c.getCenterX()-offset, c.getCenterY()-offset, owner);
            }
            if (type == Settings.KNIGHT_TYPE) {
                if (c.getOwner() == "player")
                    img = knight_blue;
                if (c.getOwner() == "ennemi")
                    img = knight_red;
                u = new Knight(playfieldLayer, img, c.getCenterX()-offset, c.getCenterY()-offset, owner);
            }
            if (type == Settings.ONAGER_TYPE) {
                if (c.getOwner() == "player")
                    img = onager_blue;
                if (c.getOwner() == "ennemi")
                    img = onager_red;
                u = new Onager(playfieldLayer, img, c.getCenterX()-offset, c.getCenterY()-offset, owner);
            }

            double temp = c.getGold();
            c.setGold(temp - u.getCost());
            c.productionQ.add(u);
            u.removeFromLayer();
        }
    }


    /**
     * Removes sprite from the screen
     * 
     * @param spriteList 
     * 				The list of sprite to remove
     */
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
     * Pull units from the castle reserve to the ost
     * @param c
     * 		the castle from which to pull units
     * @param unitType
     * 			the type of unit to pull
     */
    private void reserveToOst(Castle c, int unitType) {
        if (c.hasUnit(unitType)) {
            String mystring = " ";
            Image im = unitImage;

            if (c.getOwner() == "player") {
                //im = unitImageR;
                mystring = "player";
            }
            if (c.getOwner() == "ennemi") {
                //im = unitImageR;
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
        }
    }

    /**
     * Send ost to the selected castle
     * @param c
     * 		the attacking castle
     * @see #sendOst(Castle c,Castle d)
     */
    private void selectTarget(Castle c) {
        for (Castle targetc : castles) {
            if (targetc != c) {
                Decoration target = new Decoration(playfieldLayer, targetImage, targetc.getX(), targetc.getY());
                targets.add(target);
                target.getView().setOnMousePressed(e -> {
                    sendOst(c, targetc);
                    e.consume();
                });
            }
        }
    }

    /**
     * Initialize the ost's unit's destination and put them in the deployment queue.
     * 
     * @param c
     * 		the attacking castle
     * @param d
     * 		destination castle
     * @see #deploy(Castle)
     */
    private void sendOst(Castle c, Castle d) {
        Ost o = c.ost;
        o.setSpeed(o.getOstSpeed());
        for (Unit u : o.reserve) {
            u.path.clear();
        	u.setSpeed(o.getSpeed());
            u.setGoalx(d.getCenterX()-10);
            u.setGoaly(d.getCenterY()-10);
            u.addToPath(c.getEntrance());
            u.addToPath(d.getEntrance());
            double[] doubleArray = new double[]{ d.getCenterX()-10,d.getCenterY()-10};
            u.addToPath(doubleArray);
            u.isNotAtDoor = false;
            c.deploymentQ.add(u);
        }
        o.reserve.clear();
        c.isBuildingOst = false;
        if(c.getOwner()=="player")
        	targets.forEach(sprite -> sprite.remove());

    }
    
    /**
     * Put units on the map every turnUntilDeployment
     * @param c 
     * 		the castle from which to deploy units
     * @see Settings#TURN_UNTIL_DEPLOYMENT
     */
    private void deploy(Castle c) {
    	if(c.turnUntilDeployment==0 && c.deploymentQ.size()>0) {
    		Unit u = c.deploymentQ.get(0);
    		units.add(u);
    		u.addToLayer();
    		c.deploymentQ.remove(u);
    		c.turnUntilDeployment=Settings.TURN_UNTIL_DEPLOYMENT;
    	}
    	if(c.turnUntilDeployment>0) {
    		c.turnUntilDeployment--;
    	}
    }
    
    /**
     * Resolve all the attacks on the map on a turn when units collide with castles
     * 
     * @see Castle#takeDamage(Unit)
     *
     */
    private void checkSieges() {
        List<Unit> unitsToDelete = new ArrayList<>();
        for (Unit u : units) {
            //Check unit collision with destination castle with a little margin of error
            if (
                    ((int) u.getGoalx() - distanceMargin < (int) u.getX()) &&
                            ((int) u.getX() < (int) u.getGoalx() + distanceMargin) &&
                            ((int) u.getGoaly() - distanceMargin < (int) u.getY()) &&
                            ((int) u.getY() < (int) u.getGoaly() + distanceMargin)) {
                for (Castle c : castles) {
                    if (u.collidesWith(c)) {
                        if (c.getReserveSize() > 0) {
                            if (c.getOwner() == u.owner) { //same owner -> add to castle
                                c.reserveAdd(u);

                            } else { // attack
                                c.takeDamage(u);
                                unitsToDelete.add(u);
                            }
                        } 
                        
                        else { //Conquer the castle
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
                        unitsToDelete.add(u);
                    }
                }
            }
        }

        units.removeAll(unitsToDelete); //removing all units from the map
        unitsToDelete.forEach(sprite -> sprite.removeFromLayer());
        unitsToDelete.clear();
    }

    /**
     * Make the unit go around castles when they collide
     */
    private void pathfindingCollision() {
        for (Unit u : units) {
            if (u.path.size()<2)
                u.isNotAtDoor=false;
            for (Castle c : castles) {
                if (u.collidesWith(c)&&u.isNotAtDoor) {
                    u.isColliding=true;
                    //calculate new coordinates
                    double[] p= new double[]{ u.getX(),u.getY()};
                    double[] p2 =new double[]{ u.getX(),u.getY()};

                    //hitting north border
                    if(u.movingS) {
                        p[0] -= Settings.CASTLE_SIZE;
                        p[1] -= 4;
                        u.setY(u.getY()-4);
                        p2[0]=p[0];
                        p2[1]=p[1]+(Settings.CASTLE_SIZE+30);
                        u.path.add(0,p2);
                    }
                    if(u.movingN) {
                        p[0] += Settings.CASTLE_SIZE;
                        p[1] += 4;
                        u.setY(u.getY()+4);
                        p2[0]=p[0];
                        p2[1]=p[1]-(Settings.CASTLE_SIZE+30);
                        u.path.add(0,p2);
                    }
                    if(u.movingE) { // -> [
                        p[0] -= 4;
                        p[1] += Settings.CASTLE_SIZE;
                        u.setX(u.getX()-4);
                    }
                    if(u.movingW) {
                        p[0] += 4;
                        p[1] -= Settings.CASTLE_SIZE;
                        u.setX(u.getX()+4);
                    }
                    u.path.add(0,p);

                }
            }
            u.isColliding=false;
        }
    }

    /**
     * Check if a player has at least a unit or a castle on the map.
     */
    private void checkIfGameOver() {
        boolean castleRemainingBlue=false;
        boolean castleRemainingRed=false;
        for (Castle c : castles) {
        	if (c.getOwner()=="player") {
                castleRemainingBlue=true;
            }
            if(c.getOwner()=="ennemi") {
            	castleRemainingRed=true;
            }
        }

        boolean unitRemainingBlue =false;
        boolean unitRemainingRed=false;
        if(units.size()>0) {
        	for(Unit u : units) {
                if (u.getOwner() =="player") {
                    unitRemainingBlue = true;
                }
                else {
                	unitRemainingRed=true;
                }
        	}
        }
        if(!castleRemainingBlue && !unitRemainingBlue)
        	gameOver();
        if(!castleRemainingRed &&!unitRemainingRed)
        	gameOver();  
    }

    /**
     * End the game loop
     */
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

    /**
     * AI behaviour
     * @param team
     */
    private void AI(String team) {

        //trouve un premier objectif
        if (needsAIGoalplayer | needsAIGoalennemi) {
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
                    if (now - c2.time > 5000) {
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

    /**
     * update castle's information bar
     */
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
    }


    private void manageSelectedCastles(Castle c) {
        selected.clear();
        selected.add(c);
        if (c.getOwner() == "player")
            setStatusBar(statusBar, Settings.STATE_FIRST);
        else
            setStatusBar(statusBar, Settings.STATE_INFO);
    }

    public void setOnClickBehaviour(Castle c) {
        c.getView().setOnMousePressed(e -> {
            manageSelectedCastles(c);
            e.consume();
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}