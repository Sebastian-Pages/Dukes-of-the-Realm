package game;
//this is a test

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class Main extends Application {
	private Random rnd = new Random();

	private Pane playfieldLayer;

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
	
	//Units images
	private Image unitImage;
	
	//Selected castles
	private List<Castle> selected = new ArrayList<>();;

	private Player player;
	private List<Enemy> enemies = new ArrayList<>();
	private List<Missile> missiles = new ArrayList<>();
	private List<Castle> castles = new ArrayList<>();
	private List<Unit> units= new ArrayList<>();

	private Text scoreMessage = new Text();
	private Text newMessage = new Text();
	private int scoreValue = 0;
	private boolean collision = false;
	private boolean test = true;
	//private boolean pauseState = false;
	private Castle aiGoal;

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
		root.getChildren().add(playfieldLayer);
		
		loadGame();
		
		gameLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				processInput(input, now);

				// player input
				player.processInput();

				// add random enemies
				//spawnEnemies(true);
				
				//update army count
				updateUnitsCount(true);

				// movement
				player.move();
				enemies.forEach(sprite -> sprite.move());
				missiles.forEach(sprite -> sprite.move());
				units.forEach(sprite -> sprite.move());
				
				// AI
				AI();

				// check collisions
				checkCollisions();
				checkOrders();
				checkSiege();
				
				// update sprites in scene
				player.updateUI();
				enemies.forEach(sprite -> sprite.updateUI());
				missiles.forEach(sprite -> sprite.updateUI());
				castles.forEach(sprite -> sprite.updateUI());
				units.forEach(sprite -> sprite.updateUI());


				// check if sprite can be removed
				enemies.forEach(sprite -> sprite.checkRemovability());
				missiles.forEach(sprite -> sprite.checkRemovability());
				castles.forEach(sprite -> sprite.checkRemovability());
				units.forEach(sprite -> sprite.checkRemovability());

				// remove removables from list, layer, etc
				removeSprites(enemies);
				removeSprites(missiles);
				removeSprites(castles);
				removeSprites(units);

				// update score, health, etc
				update();	
				castles.forEach(castle -> castle.update());
				checkIfGameOver();
			}

			private void processInput(Input input, long now) {
				if (input.isExit()) {
					Platform.exit();
					System.exit(0);
				} else if (input.isFire()) {
					fire(now);
				} 
			}

		};
		
		gameLoop.start();
	}

	private void loadGame() {
		playerImage = new Image(getClass().getResource("/images/alien.png").toExternalForm(), 100, 100, true, true);
		//enemyImage = new Image(getClass().getResource("/images/enemy.png").toExternalForm(), 50, 50, true, true);
		missileImage = new Image(getClass().getResource("/images/pinapple.png").toExternalForm(), 20, 20, true, true);
		castleImage = new Image(getClass().getResource("/images/neutral_castle.png").toExternalForm(), 100, 100, true, true);	
		castleImageBlue = new Image(getClass().getResource("/images/blue_castle.png").toExternalForm(), 100, 100, true, true);
		castleImageBlueS = new Image(getClass().getResource("/images/blue_castle_selected.png").toExternalForm(), 100, 100, true, true);
		castleImageRed = new Image(getClass().getResource("/images/red_castle.png").toExternalForm(), 100, 100, true, true);
		castleImageRedS = new Image(getClass().getResource("/images/red_castle_selected.png").toExternalForm(), 100, 100, true, true);
		unitImage = new Image(getClass().getResource("/images/blue_castle_selected.png").toExternalForm(), 20, 20, true, true);
		unitImageR = new Image(getClass().getResource("/images/red_castle_selected.png").toExternalForm(), 20, 20, true, true);
		input = new Input(scene);
		input.addListeners();

		createPlayer();
		player.removeFromLayer();
		createStatusBar();
		
		//Initialize map
		spawnCastles(5);
		/**
		//scene.setOnMousePressed(e -> {
			
			
			player.setX(e.getX() - (player.getWidth() / 2));
			player.setY(e.getY() - (player.getHeight() / 2));
			
			
			****** pause function *****
			if (pauseState) {
				gameLoop.start();
				pauseState = false;
			}
				
			else {
				gameLoop.stop();
				pauseState = true;
			}
			***************************
				
		} ) ; 
		**/
	}


	public void createStatusBar() {
		HBox statusBar = new HBox();
		scoreMessage.setText("Click on one of your castles to see more information");
		statusBar.getChildren().addAll(scoreMessage);
		statusBar.getStyleClass().add("statusBar");
		statusBar.relocate(0, Settings.SCENE_HEIGHT);
		statusBar.setPrefSize(Settings.SCENE_WIDTH, Settings.STATUS_BAR_HEIGHT);
		root.getChildren().add(statusBar);
	}

	private void createPlayer() {
		double x = (Settings.SCENE_WIDTH - playerImage.getWidth()) / 2.0;
		double y = Settings.SCENE_HEIGHT * 0.7;
		player = new Player(playfieldLayer, playerImage, x, y, Settings.PLAYER_HEALTH, Settings.PLAYER_DAMAGE,
				Settings.PLAYER_SPEED, input);
		
		
		player.getView().setOnMousePressed(e -> {
			System.out.println("Click on player");
			e.consume();
		});
		
		player.getView().setOnContextMenuRequested(e -> {
			ContextMenu contextMenu = new ContextMenu();
			MenuItem low = new MenuItem("Slow");
			MenuItem medium= new MenuItem("Regular");
			MenuItem high= new MenuItem("Fast");
			low.setOnAction(evt -> player.setFireFrequencyLow());
			medium.setOnAction(evt -> player.setFireFrequencyMedium());
			high.setOnAction(evt -> player.setFireFrequencyHigh());
			contextMenu.getItems().addAll(low, medium, high);
			contextMenu.show(player.getView(), e.getScreenX(), e.getScreenY());
		});
	}
	/**
	private void spawnEnemies(boolean random) {
		if (random && rnd.nextInt(Settings.ENEMY_SPAWN_RANDOMNESS) != 0) {
			return;
		}
		double speed = rnd.nextDouble() * 3 + 1.0;
		double x = rnd.nextDouble() * (Settings.SCENE_WIDTH - enemyImage.getWidth());
		double y = -enemyImage.getHeight();
		Enemy enemy = new Enemy(playfieldLayer, enemyImage, x, y, 1, 1, speed);
		enemies.add(enemy);
	}
	**/
	
	private void spawnCastles(int nb_castles) {
		boolean placed_well = true;
		
		
		// add 5 neutral castles
		while ((castles.size()<nb_castles) ){
			ListIterator<Castle> it = castles.listIterator();
			double speed = 0;
			double x = rnd.nextDouble() * (Settings.SCENE_WIDTH - castleImage.getWidth());
			double y = rnd.nextDouble() * (Settings.SCENE_HEIGHT- castleImage.getHeight());
			Castle castle = new Castle(playfieldLayer, castleImage, x, y, 1, 1, speed);
			
			castle.getView().setOnMousePressed(e -> {
				scoreMessage.setText("Castle [ owner: "+castle.getOwner()+" | units: "+Math.round(castle.getReserveSize())+" | Level: "+castle.getLevel()+" ]");
				//System.out.println("Castle [ owner: "+castle.getOwner()+" | units: "+Math.round(castle.getReserveSize())+" | Level: "+castle.getLevel()+" ]");
				manageSelectedCastles(castle);
				System.out.println("Selection: "+selected.get(0));
				e.consume();
			});
			
			//parcours des chateaux pour éviter les collisions et une distance min entre eux			
			while(it.hasNext()){
		    	 Castle c = it.next();
		    	 if (castle.collidesWith(c)) {
						placed_well = false;
					}
		    }	
			
			if (placed_well){
				Castle castle2 = CastleSet(castle,2);
				castles.add(castle2);
				castles.remove(castle);
			}
			else {
				castle.removeFromLayer();
				castle.remove();
			}
			placed_well = true;
		}	
		// pick 1 starting castles 
		Castle castle_1=castles.get(2);
		castles.remove(2);
		castle_1 = CastleSet(castle_1 ,0);
		castles.add(2, castle_1);
		
		// pick ennemy starting castles 
		Castle castle_2=castles.get(3);
		castles.remove(3);
		castle_2 = CastleSet(castle_2 ,1);
		castles.add(3, castle_2);
		
		
	}
	
	private void updateUnitsCount(boolean bool){
		if(bool) {
			for (Castle c : castles) {
				if (c.getUnitProduction()>10) {

					if (c.getOwner()=="player") {
						Unit u = new Unit(playfieldLayer,unitImage, c.getCenterX(), c.getCenterY(), 1, 1, 1);
						u.owner = "player";	
						double temp=c.getUnitProduction();
						c.setUnitProduction(temp-10);
						c.reserveAdd(u);
						u.removeFromLayer();
					}
					if(c.getOwner()=="ennemi") {
						Unit u = new Unit(playfieldLayer,unitImageR, c.getCenterX(), c.getCenterY(), 1, 1, 1);
						u.owner="ennemi";
						double temp=c.getUnitProduction();
						c.setUnitProduction(temp-10);
						c.reserveAdd(u);
						u.removeFromLayer();
					}
					if(c.getOwner()=="unowned") {
						Unit u = new Unit(playfieldLayer,unitImageR, c.getCenterX(), c.getCenterY(), 1, 1, 1);
						u.owner="unowned";
						double temp=c.getUnitProduction();
						c.setUnitProduction(temp-10);
						c.reserveAdd(u);
						u.removeFromLayer();
					}
				}			
			}
		}
		
	}
	

	private void fire(long now) {
		if (player.canFire(now)) {
			Missile missile = new Missile(playfieldLayer, missileImage, player.getCenterX(), player.getY(),
					Settings.MISSILE_DAMAGE, Settings.MISSILE_SPEED);
			missiles.add(missile);
			player.fire(now);
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

	private void checkCollisions() {
		collision = false;
		
		
				
		for (Enemy enemy : enemies) {
			for (Missile missile : missiles) {
				if (missile.collidesWith(enemy)) {
					enemy.damagedBy(missile);
					missile.remove();
					collision = true;
					scoreValue += 10 + (Settings.SCENE_HEIGHT - player.getY()) / 10;
				}
			}

			if (player.collidesWith(enemy)) {
				collision = true;
				enemy.remove();
				player.damagedBy(enemy);
				if (player.getHealth() < 1)
					gameOver();
			}
		}
	}
	private void checkOrders() {		
		// checkForAttackOrder (je met dans une fonction bien rangé quand ça marchera)
		if(selected.size()>1) {
			//System.out.println(selected.get(0).getOwner() + (selected.get(1).getOwner()));
			
			
			if(
			(selected.get(0).getOwner()=="player" &&( selected.get(1).getOwner()=="unowned")||(selected.get(1).getOwner()=="ennemi"))&&
			(selected.get(0).getReserveSize()>0) &&(selected.get(0).isReadyToAttack))
			{
				Castle c=selected.get(0);
				Castle d=selected.get(1);

				System.out.println(selected.get(0).getOwner() +" attacks -> "+ (selected.get(1).getOwner()));
				Unit u = c.reservePull();
				
				//set destination of unit
				u.setGoalx(d.getCenterX());
				u.setGoaly(d.getCenterY());
				
				u.addToLayer();
				units.add(u);
				if (c.getReserveSize()==0)
					selected.clear();
				//gameOver();
			}
			
		}

	}
	private void checkSiege() {
		for (Unit u : units) {
			
			// La condition est comme ça pour laisser de la marge d'erreur et ne pas rater la collision
			if(
			( (int)u.getGoalx()-5 < (int)u.getX() )&&
			( (int)u.getX() < (int)u.getGoalx()+5 )&&
			( (int)u.getGoaly()-5 < (int)u.getY() )&&
			( (int)u.getY() < (int)u.getGoaly()+5 ) ) 
			{
				
				for (Castle c : castles) {
					
					if (u.collidesWith(c)) {
						
						u.remove();
						
						/// ne pas mettre cette ligne. On met son attribut removable à"true" ensuite la fct remove sprite le retire de la lsite
						//units.remove(u);
						
						//removSrpite le fait deja
						//u.removeFromLayer();
						
						
						if (c.getReserveSize()>0) {
							
							if (c.getOwner()==u.owner) {
								c.reserveAdd(u);
							}
							else {
								Unit t =c.reservePull();
								t.remove();
							}
								
							
						}
						else {
							int type =0;
							if (u.owner=="player")
								type = 0;
							if (u.owner=="ennemi")
								type = 1;	;
								test=true;
							c = CastleSet(c ,type);
						}
					}		
				}
			}
		}
	}

	// ne marche pas lorqu'il reste des chateaux neutre
	private void checkIfGameOver() {
		boolean areAllOwnedByTheSame = true;
		String s = castles.get(0).getOwner();
		for (Castle c : castles) {
			if (c.getOwner() != s ) {
				areAllOwnedByTheSame=false;
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
	
	private void AI() {
		
		//trouve un premier objectif
		if (test){
			System.out.println("DEBUG 1");
			for (Castle c : castles) {
				if ((c.getOwner()!="ennemi")&&(test)) {
					aiGoal = c;
					System.out.println("DEBUG "+aiGoal);
					System.out.println("DEBUG size: "+castles.size());
					test=false;
				}
			}
		}
		
		//trouver nouvo objectif si aiGoal est "ennemi"
		if (!false) {
			for (Castle c : castles) {
				if ((c.getOwner()!="ennemi")&&(test)) {
					aiGoal = c;
					System.out.println("DEBUG "+aiGoal);
					System.out.println("DEBUG size: "+castles.size());
					test=false;
				}
			}
		
		//Attaquer aiGoal
		for (Castle c2 : castles) {
			if (c2.getOwner()=="ennemi") {
				if (c2.getReserveSize()>0) {
					Unit u = c2.reservePull();
					//set destination of unit
					u.setGoalx(aiGoal.getCenterX());
					u.setGoaly(aiGoal.getCenterY());
					u.addToLayer();
					units.add(u);
				}
			}
		}
		}
	
		
		/**&& (aiGoal.getOwner()!="ennemi")
		Unit u = c.reservePull();
		
		//set destination of unit
		u.setGoalx(d.getCenterX());
		u.setGoaly(d.getCenterY());
		
		u.addToLayer();
		units.add(u);
		if (c.getReserveSize()==0)
			selected.clear();**/
	}

	private void update() {
		if (collision) {
			scoreMessage.setText("Score : " + scoreValue + "          Life : " + player.getHealth());
		}
		newMessage.setText(""+castles.get(0).getReserveSize());
	}
	
	
	//on peut revoie la séléection et changer les views pour voir ce qui est sélectionner
	private void manageSelectedCastles(Castle c){
		if (selected.size()>2) {
			Castle temp = selected.get(1);
			selected.clear();
			selected.add(temp);
			selected.add(c);
		}
		else {
			selected.add(c);
		}
	}
	
	private Castle CastleSet(Castle c , int type){
		
		// On dit que 0 c'est les chateau alliés
		if (type == 0) {
			
			//change the attributes
			c.setOwner("player");
			c.setProductionSpeed(0.2);
			c.setView(castleImageBlue);
			c.updateUI();
			
			//change On Click behaviour
			c.getView().setOnMousePressed(e -> {
				scoreMessage.setText("Castle [ owner: "+c.getOwner()+" | units: "+Math.round(c.getReserveSize())+" | Level: "+c.getLevel()+" ]");
				manageSelectedCastles(c);
				e.consume();
			});

		}
		//1 est le type de l'ennemi
		if (type == 1) {
			//change the attributes
			c.setOwner("ennemi");
			c.setProductionSpeed(0.2);
			c.setView(castleImageRed);
			c.updateUI();	
			
			//change On Click behaviour
			c.getView().setOnMousePressed(e -> {
				scoreMessage.setText("Castle [ owner: "+c.getOwner()+" | units: "+Math.round(c.getReserveSize())+" | Level: "+c.getLevel()+" ]");
				manageSelectedCastles(c);
				e.consume();
			});
		}
		
		//Chateau 3 de type neutre
		if (type == 2) {
			//change the attributes
			c.setOwner("unowned");
			c.setProductionSpeed(0.1);
			c.setView(castleImage);
			c.updateUI();	
			
			//change On Click behaviour
			c.getView().setOnMousePressed(e -> {
				scoreMessage.setText("Castle [ owner: "+c.getOwner()+" | units: "+Math.round(c.getReserveSize())+" | Level: "+c.getLevel()+" ]");
				manageSelectedCastles(c);
				e.consume();
			});
			
			//pour tous les chateau faire une HBOX
			HBox unitCount = new HBox();
			c.newMessage.setText("0");
			unitCount.getChildren().addAll(c.newMessage);
			unitCount.getStyleClass().add("uc");
			unitCount.relocate(c.getX(),c.getY() );
			unitCount.setPrefSize(10,10);
			root.getChildren().add(unitCount);
			castles.add(c);
		}
		
			
		
		
		return c;
	}

	public static void main(String[] args) {
		launch(args);
	}

}