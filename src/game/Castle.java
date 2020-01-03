package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


public class Castle extends Sprite {
	long time;
	private String owner;
	private double gold;
	private double productionSpeed;
	private int level;
	private double orientation;
	public boolean isReadyToAttack;
	public boolean isSelected;
	public boolean isBuildingOst;
	public double productionProgress;
	public Text newMessage = new Text();
	private List<Unit> reserve = new ArrayList<>();
	public List<Unit> productionQ = new ArrayList<>();
	
	public Ost ost;
	//private Unitproduction up = new Unitproduction();
	//private Order order = new Order();
	//private String orientation;
	
	public Castle(Pane layer, Image image, double x, double y, int health,double damage, double speed, double orientation) {
		super(layer, image, x, y);
		this.productionSpeed=speed;
		this.gold= 0;
		this.level = 1;
		this.owner = "unowned";
		this.isReadyToAttack=true;
		this.isSelected = false;
		this.isBuildingOst = false;
		this.orientation=orientation;
		time=0;
		productionProgress=0;
		}

	public void setOst(Ost o){
		this.ost=o;
	}
	@Override
	public void checkRemovability() {
		if(false)
			remove();
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

	public void setUnitProduction(double UnitProduction) {
		this.gold = UnitProduction;
	}

	public double getProductionSpeed() {
		return productionSpeed;
	}

	public void setProductionSpeed(double productionSpeed) {
		this.productionSpeed = productionSpeed;
	}

	public int getLevel() {
		return level;
	}
	

	public void setLevel(int level) {
		this.level = level;
	}
	
	public void updateUI() {
        gold += this.productionSpeed;
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
			this.setProductionSpeed(Settings.NORMAL_PRODUCTION_SPEED);
			this.setView(img,orientation);
			this.updateUI();
		}
		//1 est le type de l'ennemi
		if (type == 1) {
			this.setOwner("ennemi");
			this.setProductionSpeed(Settings.NORMAL_PRODUCTION_SPEED);
			this.setView(img,orientation);
			this.updateUI();	
		}	
		//Chateau 3 de type neutre
		if (type == 2) {
			//change the attributes
			this.setOwner("unowned");
			this.setProductionSpeed(Settings.NEUTRAL_PRODUCTION_SPEED);
			this.setView(img,orientation);
			this.updateUI();	
		}
	}
	

	
	public void trainUnit(){
		
		if (!this.productionQ.isEmpty()){
			this.productionProgress+=0.1;
			if (this.productionProgress > Settings.PIKEMAN_PRODUCTION_TIME/8){
				this.productionProgress=0;
				Unit u = this.productionQ.get(0);
				this.productionQ.remove(0);
				this.reserve.add(u);
				System.out.println("owner :"+u.owner);
				//System.out.println("prodQ: "+this.productionQ.size());
				//System.out.println("reserve: "+this.reserve.size());
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
					System.out.println("attaque done");
					if(!defender.isAlive()) {
						this.reserve.remove(defender);
					}
				}
			}
		}
	}
	
	private void levelUp() {
		if(this.getLevel()<2 && this.getGold()>=this.getLevel()*Settings.LVL_UP_COST) {
			this.setLevel(this.getLevel()+1);
		}
		
	}
	
	
}
