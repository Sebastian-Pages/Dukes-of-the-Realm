package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Missile extends Sprite {

	public Missile(Pane layer, Image image, double x, double y, double damage, double speed) {
		super(layer, image, x, y, 0, 1);
		setDy(-speed);
	}

	@Override
	public void checkRemovability() {

		if (getY() < 0) {
			remove();
		}
	}
}
