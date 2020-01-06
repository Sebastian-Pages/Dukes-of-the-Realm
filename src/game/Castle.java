package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


public class Castle extends Sprite {
	long time;
	private String owner;
	private double gold;
	private double incomeSpeed;
	private int level;
	private double orientation;
	public boolean isReadyToAttack;
	public boolean isSelected;
	public boolean isBuildingOst;
	public int productionProgress;
	public int levelUpProgress;
	public int turnUntilDeployment;
	public Text newMessage = new Text();
	private List<Unit> reserve = new ArrayList<>();
	public List<Unit> productionQ = new ArrayList<>();
	public List<Unit> deploymentQ=new ArrayList<>();
	
	public Ost ost;

	public Castle(Pane layer, Image image, double x, double y, int health,double damage, double speed, double orientation) {
		super(layer, image, x, y);
		this.incomeSpeed=speed;
		this.gold= 0;
		this.level = 1;
		this.owner = "unowned";
		this.isReadyToAttack=true;
		this.isSelected = false;
		this.isBuildingOst = false;
		this.orientation=orientation;
		this.time=0;
		this.productionProgress=0;
		this.levelUpProgress=0;
		}

	public void setOst(Ost o){
		this.ost=o;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public double getGold() {
		return gold;
	}

	public double getIncomeSpeed() {
		return incomeSpeed;
	}

	public void setIncomeSpeed(double incomeSpeed) {
		this.incomeSpeed = incomeSpeed;
	}

	public int getLevel() {
		return level;
	}
	

	public void setLevel(int level) {
		this.level = level;
	}
	
	public void income() {
        gold += this.incomeSpeed*this.getLevel();
    }
	
	public int getReserveSize() {
		return this.reserve.size();
	}
	
	public void reserveAdd(Unit u) {
		reserve.add(u);
	}
	public Unit reservePull(int type) {
		Unit u2 = null;
		for (Unit u : reserve){
			if (u.getType()==type){
				u2 = u;	
				reserve.remove(u);
				return u2;
			}
		}
		return u2;
	}
	
	public Unit reservePull() {
		Unit u = reserve.get(getReserveSize()-1);
		reserve.remove(getReserveSize()-1);
		return u;
	}
	
	public Unit getUnit(int type) {
		Unit u2 = null;
		for (Unit u : reserve){
			if (u.getType()==type){
				u2 = u;	
				return u2;
			}
		}
		return u2;
	}
	
	public void update() {
		String selectString ="";
		if (this.isSelected)
			selectString="*";
		else
			selectString ="";
		newMessage.setText(""+this.getReserveSize()+selectString);
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
	

	public void CastleSet(int type,Image img){
		
		// On dit que 0 c'est les chateau alliés
		if (type == 0) {		
			this.setOwner("player");
			this.setIncomeSpeed(Settings.NORMAL_PRODUCTION_SPEED);
			this.setView(img,orientation);
			this.updateUI();
		}
		//1 est le type de l'ennemi
		if (type == 1) {
			this.setOwner("ennemi");
			this.setIncomeSpeed(Settings.NORMAL_PRODUCTION_SPEED);
			this.setView(img,orientation);
			this.updateUI();	
		}	
		//Chateau 3 de type neutre
		if (type == 2) {
			//change the attributes
			this.setOwner("unowned");
			this.setIncomeSpeed(Settings.NEUTRAL_PRODUCTION_SPEED);
			this.setView(img,orientation);
			this.updateUI();	
		}
	}
	

	
	public void trainUnit(){
		
		if (!this.productionQ.isEmpty()){
			this.productionProgress+=1;
			if(this.productionProgress>this.productionQ.get(0).getProductionTime()){
				this.productionProgress=0;
				Unit u = this.productionQ.get(0);
				this.productionQ.remove(0);
				this.reserve.add(u);
			}
		}
	}
	
	public int countUnits(int unitType){
		int result=0;
		if (this.getReserveSize()>0){	
			for (Unit u : reserve){
				if (u.getType()==unitType)
					result+=1;
			}
		}
		return result;				
	}

	public void setGold(double i) {
		this.gold=i;
		
	}
	
	public void takeDamage(Unit attacker) {
		if(this.getReserveSize()>0) {
			Random rnd = new Random();
			boolean attackNotDone=true;
			while(attackNotDone) {
				int type = rnd.nextInt(3);
				if(this.hasUnit(type)) {
					Unit defender = this.getUnit(type); 
					defender.damagedBy(attacker);
					attackNotDone=false;
					if(!defender.isAlive()) {
						this.reserve.remove(defender);
					}
				}
			}
		}
	}
	
	public void levelUp() {
		if(this.getLevel()<2 && this.getGold()>=this.getLevel()*Settings.LVL_UP_COST) {
			this.setGold(this.getGold()-Settings.LVL_UP_COST);
			this.levelUpProgress=1;
		}
		
	}
	
	public void upgradeCastle() {
		if(this.levelUpProgress>0) {
			this.levelUpProgress+=1;
			if(this.levelUpProgress>this.getLevel()*50+100) {
				this.levelUpProgress=0;
				this.setLevel(this.getLevel()+1);
			}
		}
		
	}

	public double[] getEntrance(){
		double[] entrance = new double[]{0,0};
		switch ((int)this.orientation) {
			case 0: //Nord
				entrance[0] = this.getCenterX()-10;entrance[1] = this.getCenterY()-15-(this.w/2)-10; //(this.w);
				return entrance;
			case 90: //Est
				entrance[0] = this.getCenterX()+15+(this.w/2)-10;entrance[1] = this.getCenterY()-10;
				return entrance;
			case 180: //South
				entrance[0] = this.getCenterX()-10;entrance[1] = this.getCenterY()+15+(this.w/2)-10;
				return entrance;
			case 270: //West
				entrance[0] = this.getCenterX()-15-(this.w/2)-10;entrance[1] = this.getCenterY()-10;
				return entrance;
		}
		return entrance;
	}
	
	
}
