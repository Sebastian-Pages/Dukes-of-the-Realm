package game;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
public class Ost {
	String owner;
	public List<Unit> reserve = new ArrayList<>();
	double speed;
	public Ost() {
		this.speed=0;
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
	
	public double getSpeed() {
		return speed;
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

