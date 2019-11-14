package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Player extends Sprite {

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;

	private Input input;
	private double speed;
	
	private double fireFrequency;
	private double lastFire = 0;
	
	public Player(Pane layer, Image image, double x, double y, int health,
			double damage, double speed, Input input) {

		super(layer, image, x, y, health, damage);

		this.speed = speed;
		this.input = input;
		setFireFrequencyMedium();

		init();
	}

	private void init() {
		// calculate movement bounds of the player ship
		// allow half of the player to be outside of the screen
		minX = 0 - getWidth() / 2.0;
		maxX = Settings.SCENE_WIDTH - getWidth() / 2.0;
		minY = 0 - getHeight() / 2.0;
		maxY = Settings.SCENE_HEIGHT - getHeight();
	}

	public void processInput() {
		// vertical direction
		if (input.isMoveUp()) {
			dy = -speed;
		} else if (input.isMoveDown()) {
			dy = speed;
		} else {
			dy = 0d;
		}

		// horizontal direction
		if (input.isMoveLeft()) {
			dx = -speed;
		} else if (input.isMoveRight()) {
			dx = speed;
		} else {
			dx = 0d;
		}

	}

	@Override
	public void move() {
		super.move();
		// ensure the player can't move outside of the screen
		checkBounds();
	}

	private void checkBounds() {
		// vertical
		y = y < minY ? minY : y;
		y = y > maxY ? maxY : y;

		// horizontal
		x = x < minX ? minX : x;
		x = x > maxX ? maxX : x;
	}

	@Override
	public void checkRemovability() {
	}

	public boolean canFire(long now) {
		return (now - lastFire >= fireFrequency);
	}

	public void fire(long now) {
		lastFire = now;
	}

	public void setFireFrequencyLow() {
		this.fireFrequency = Settings.FIRE_FREQUENCY_LOW;
	}
	
	public void setFireFrequencyMedium() {
		this.fireFrequency = Settings.FIRE_FREQUENCY_MEDIUM;
	}
	
	public void setFireFrequencyHigh() {
		this.fireFrequency = Settings.FIRE_FREQUENCY_HIGH;
	}
	

}
