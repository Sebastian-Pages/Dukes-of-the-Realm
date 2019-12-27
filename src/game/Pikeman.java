package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Pikeman extends Unit {
	
	public Pikeman(Pane layer, Image image, double x, double y,String owner) {
		super(layer, image, x, y,owner);
		this.health=Settings.PIKEMAN_HEALTH;
		this.damage=Settings.PIKEMAN_DAMAGE;
		this.type=Settings.PIKEMAN_TYPE;
		this.cost=Settings.PIKEMAN_COST;
		this.speed=Settings.PIKEMAN_SPEED;
		this.owner=owner;
	}
		
}
