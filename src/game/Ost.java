package game;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
public class Ost extends Unit{
	double goalx;
	double goaly;
	String owner;
	private List<Unit> reserve = new ArrayList<>();
	
	public Ost(Pane layer, Image image, double x, double y, int health,double damage, double speed) {
		super(layer, image, x, y,health,damage,speed);
		setDy(speed);
		setDx(speed);

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
	
	public void checkRemovability() {

		if (false)		
			remove();
	}
	
	public void move() {
		if (this.x < this.goalx)
			x += dx;
		
		if (this.x > this.goalx)
			x -= dx;
		if (this.y < this.goaly)
			y += dy;
		
		if (this.y > this.goaly)
			y -= dy;
    }
	
	public int getReserveSize() {
		return this.reserve.size();
	}
	
	public void reserveAdd(Unit u) {
		reserve.add(u);
	}
	public Unit reservePull() {
		Unit u = reserve.get(getReserveSize()-1);
		reserve.remove(getReserveSize()-1);
		return u;
	}
}
