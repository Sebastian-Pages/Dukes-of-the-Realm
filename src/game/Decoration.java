package game;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Decoration extends Sprite {

	public Decoration(Pane layer, Image image, double x, double y, int health, double damage) {
		super(layer, image, x, y, health, damage);

	}

	@Override
	public void checkRemovability() {

		if (this.getHealth()<1)
			remove();
	}

}
