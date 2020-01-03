package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

abstract public class Unit extends Sprite {
    public boolean isNotAtDoor = false;
    protected String owner;
    protected double dx;
    protected double dy;
    protected double speed;
    protected int health;
    protected int damage;
    protected int type;
    protected int cost;
    private Image image;
    protected double goalx;
    protected double goaly;
    protected List<double[]> path = new ArrayList<>();
    protected boolean movingN = false;
    protected boolean movingE = false;
    protected boolean movingS = false;
    protected boolean movingW = false;
    protected boolean isColliding = false;


    public Unit(Pane layer, Image image, double x, double y, int health, double damage, double speed) {
        super(layer, image, x, y);
        setDy(speed);
        setDx(speed);
        this.type = 0;

    }

    public Unit(Pane layer, Image image, double x, double y, String owner) {
        super(layer, image, x, y);
        setDy(speed);
        setDx(speed);
        this.type = 0;
        this.owner = owner;
        this.image = image;

    }

    public Unit(Unit u) {
        this(u.layer, u.image, u.x, u.y, u.owner);
    }

    public void checkRemovability() {

        if (!isAlive())
            remove();
    }

    public void move() {
        if (((int) this.path.get(0)[0] - 3 < (int) this.x) &&
                ((int) this.x < (int) this.path.get(0)[0] + 3) &&
                ((int) this.path.get(0)[1] - 3 < (int) this.y) &&
                ((int) this.y < (int) this.path.get(0)[1] + 3)) {
            this.path.remove(0);
            this.isNotAtDoor = true;
        }
        movingN = false;
        movingE = false;
        movingS = false;
        movingW = false;
		boolean isMoving=false;
        if ((this.x < this.path.get(0)[0]-1)&&!isMoving){
            x += speed;
            this.movingE = true;
			isMoving=true;
        }
        if ((this.x > this.path.get(0)[0]+1)&&!isMoving) {
            x -= speed;
            this.movingW = true;
			isMoving=true;
        }
        if ((this.y < this.path.get(0)[1]-1)&&!isMoving) {
            y += speed;
            this.movingS = true;
			isMoving=true;
        }
        if ((this.y > this.path.get(0)[1]+1)&&!isMoving) {
            y -= speed;
            this.movingN = true;
			isMoving=true;
        }
		isMoving=false;
    }

    public void addToPath(double tab[]) {
        path.add(tab);
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

    public double getGoalx() {
        return goalx;
    }

    public void setGoalx(double goalx) {
        this.goalx = goalx;
    }

    public double getGoaly() {
        return goaly;
    }

    public void setGoaly(double goaly) {
        this.goaly = goaly;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    abstract public double getProductionTime();


}