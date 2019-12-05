package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Onager extends Unit {
	
	public Onager(Pane layer, Image image, double x, double y, int health,double damage, double speed) {
		super(layer, image, x, y,health,damage,speed);
		this.health=Settings.ONAGER_HEALTH;
		this.damage=Settings.ONAGER_DAMAGE;
		this.type=Settings.ONAGER_TYPE;
		this.cost=Settings.ONAGER_COST;
	}
		
}
