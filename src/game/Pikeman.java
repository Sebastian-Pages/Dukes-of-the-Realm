package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Pikeman extends Unit {
	
	public Pikeman(Pane layer, Image image, double x, double y, int health,double damage, double speed) {
		super(layer, image, x, y,health,damage,speed);
		this.health=Settings.PIKEMAN_HEALTH;
		this.damage=Settings.PIKEMAN_DAMAGE;
		this.type=Settings.PIKEMAN_TYPE;
		this.cost=Settings.PIKEMAN_COST;
	}
		
}
