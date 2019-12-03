package game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Unit extends Sprite{
	String owner;
    protected double dx;
    protected double dy;

    protected int health;
    protected double damage;
    protected double speed;
	



	public Unit(Pane layer, Image image, double x, double y, int health,double damage, double speed) {
		super(layer, image, x, y);
		setDy(speed);
		setDx(speed);

	}
	
	public void checkRemovability() {

		if (!isAlive())
			remove();
	}
    
	public void move() {
        x += dx;
        y += dy;
    }
	
    public boolean isAlive() {
        return health > 0;
    }
    
    public void damagedBy(Unit unit) {
        health -= unit.getDamage();
    }

public double getDx() {
    return dx;
}

public void setDx(double dx) {
    this.dx = dx;
}

public double getDy() {
    return dy;
}

public void setDy(double dy) {
    this.dy = dy;
}

public int getHealth() {
    return health;
}

public double getDamage() {
    return damage;
}

public void setDamage(double damage) {
    this.damage = damage;
}
}