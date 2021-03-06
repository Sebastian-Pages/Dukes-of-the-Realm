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
		this.speed=Settings.ONAGER_SPEED;
		this.owner=owner;
	}

	@Override
	public double getProductionTime() {
		// TODO Auto-generated method stub
		return Settings.ONAGER_PRODUCTION_TIME;
	}
		
}
