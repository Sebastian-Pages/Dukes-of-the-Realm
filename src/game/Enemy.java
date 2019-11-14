package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Enemy extends Sprite {

	private double maxY;
	
	public Enemy(Pane layer, Image image, double x, double y, int health,
			double damage, double speed) {
		super(layer, image, x, y, health, damage);
		setDy(speed);
		maxY = Settings.SCENE_HEIGHT - image.getHeight();
	}

	@Override
	public void checkRemovability() {

		if (getY() > maxY || !isAlive())
			remove();
	}
}
