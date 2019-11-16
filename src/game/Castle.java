package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Castle extends Sprite {
	private double maxY;
	
	private String owner;
	private int tresor;
	private int level;
	//private List<Unit> reserve = new ArrayList<>();
	//private Unitproduction up = new Unitproduction();
	//private Order order = new Order();
	//private String orientation;
	
	public Castle(Pane layer, Image image, double x, double y, int health,double damage, double speed) {
		super(layer, image, x, y, health, damage);
		setDy(speed);
		maxY = Settings.SCENE_HEIGHT - image.getHeight();
		this.tresor= 0;
		this.level = 1;
		this.owner = "player1";
		}

	@Override
	public void checkRemovability() {

		if (getY() > maxY || !isAlive())
			remove();
	}
}
