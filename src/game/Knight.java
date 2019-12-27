package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Knight extends Unit {
	
	public Knight(Pane layer, Image image, double x, double y, String owner) {
		super(layer, image, x, y,owner);
		this.health=Settings.KNIGHT_HEALTH;
		this.damage=Settings.KNIGHT_DAMAGE;
		this.type=Settings.KNIGHT_TYPE;
		this.cost=Settings.KNIGHT_COST;
		this.speed=Settings.KNIGHT_SPEED;
		this.owner=owner;
	}
		
}
