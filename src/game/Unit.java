package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Unit extends Sprite{
	//int goalx;
	//int goaly;

	
	public Unit(Pane layer, Image image, double x, double y, int health,double damage, double speed) {
		super(layer, image, x, y, health, damage);
		setDy(speed);

	}
	
	public void checkRemovability() {

		if (!isAlive())
			remove();
	}
	
	public void move() {
        x += dx;
        y += dy;
    }
}
