package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Knight extends Unit {
	
	public Knight(Pane layer, Image image, double x, double y, int health,double damage, double speed) {
		super(layer, image, x, y,health,damage,speed);
		this.health=Settings.KNIGHT_HEALTH;
		this.damage=Settings.KNIGHT_DAMAGE;
	}
		
}
