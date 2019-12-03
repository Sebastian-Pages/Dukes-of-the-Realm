package game;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Decoration extends Sprite {

	public Decoration(Pane layer, Image image, double x, double y, int health, double damage) {
		super(layer, image, x, y);

	}

	@Override
	public void checkRemovability() {

		if (false)
			remove();
	}

}
