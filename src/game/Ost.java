package game;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
public class Ost extends Unit{
	double goalx;
	double goaly;
	String owner;
	public List<Unit> reserve = new ArrayList<>();
	
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
			x += speed;
		
		if (this.x > this.goalx)
			x -= speed;
		if (this.y < this.goaly)
			y += speed;
		
		if (this.y > this.goaly)
			y -= speed;
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
	public boolean hasUnit(int unitType){
		
		if (this.getReserveSize()>0){
			boolean result=false;
			for (Unit u : reserve){
				if (u.getType()==unitType)
					result=true;
			}
			return result;
		}	
		else
			return false;				
	}
	public int countUnits(int unitType){
		// gérer le cas sans ost
		int result=0;
		if (this.getReserveSize()>0){	
			for (Unit u : reserve){
				if (u.getType()==unitType)
					result+=1;
			}
		}
		return result;				
	}
	
	public double getOstSpeed() {
		double s=0;
		if (this.getReserveSize()>0){
			s=1000;
			for (Unit u : reserve){
				if (u.getSpeed()<s)
					s=u.getSpeed();
			}
		}
		return s;
	}
	
	public void setSpeed(double speed){
		this.speed=speed;
	}
}

