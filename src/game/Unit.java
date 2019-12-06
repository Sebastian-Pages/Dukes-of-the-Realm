package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

abstract public class Unit extends Sprite{
	protected String owner;
    protected double dx;
    protected double dy;
    protected double speed;
    protected int health;
    protected int damage; 
	protected int type;
	protected int cost;
	private Image image;



	public Unit(Pane layer, Image image, double x, double y, int health,double damage, double speed) {
		super(layer, image, x, y);
		setDy(speed);
		setDx(speed);
		this.type=0;

	}
	public Unit(Pane layer, Image image, double x, double y,String owner) {
		super(layer, image, x, y);
		setDy(speed);
		setDx(speed);
		this.type=0;
		this.owner=owner;
		this.image=image;

	}
	public Unit(Unit u) {		
		this(u.layer,u.image, u.x, u.y,u.owner);
	}
	public void checkRemovability() {

		if (!isAlive())
			remove();
	}
    
	public void move() {
        x += speed;
        y += speed;
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

    public void setDamage(int damage) {
    	this.damage = damage;
    }
    
    public double getType() {
    	return type;
    }
    public void setType(int type) {
    	this.type = type;
    }
    public double getCost() {
    	return cost;
    }
    public void setCost(int cost) {
    	this.cost = cost;
    }
	public double getSpeed() {
		return speed;
	}
    

 
}