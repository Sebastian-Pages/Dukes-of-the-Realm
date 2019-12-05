package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Onager extends Unit {
	
	public Onager(Pane layer, Image image, double x, double y,String owner) {
		super(layer, image, x, y,owner);
		this.health=Settings.ONAGER_HEALTH;
		this.damage=Settings.ONAGER_DAMAGE;
		this.type=Settings.ONAGER_TYPE;
		this.cost=Settings.ONAGER_COST;
	}
		
}
