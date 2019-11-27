package game;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class Castle extends Sprite {
	private double maxY;
	
	private String owner;
	private double unitProduction;
	private double productionSpeed;
	private int level;
	public boolean isReadyToAttack;
	public boolean isSelected;
	public Text newMessage = new Text();
	private List<Unit> reserve = new ArrayList<>();
	//private Unitproduction up = new Unitproduction();
	//private Order order = new Order();
	//private String orientation;
	
	public Castle(Pane layer, Image image, double x, double y, int health,double damage, double speed) {
		super(layer, image, x, y, health, damage);
		setDy(0);
		setDx(0);
		this.productionSpeed=speed;
		maxY = Settings.SCENE_HEIGHT - image.getHeight();
		this.unitProduction= 0;
		this.level = 1;
		this.owner = "unowned";
		this.isReadyToAttack=true;
		this.isSelected = false;
		}

	@Override
	public void checkRemovability() {

		if (getY() > maxY || !isAlive())
			remove();
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public double getUnitProduction() {
		return unitProduction;
	}

	public void setUnitProduction(double UnitProduction) {
		this.unitProduction = UnitProduction;
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
        unitProduction += this.productionSpeed;
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
	public void update() {
		String selectString ="";
		if (this.isSelected)
			selectString="*";
		else
			selectString ="";
		newMessage.setText(""+this.getReserveSize()+selectString);
	}
	
	public void CastleSet(int type,Image img){
		
		// On dit que 0 c'est les chateau alliés
		if (type == 0) {		
			this.setOwner("player");
			this.setProductionSpeed(Settings.NORMAL_PRODUCTION_SPEED);
			this.setView(img);
			this.updateUI();
		}
		//1 est le type de l'ennemi
		if (type == 1) {
			this.setOwner("ennemi");
			this.setProductionSpeed(Settings.NORMAL_PRODUCTION_SPEED);
			this.setView(img);
			this.updateUI();	
		}	
		//Chateau 3 de type neutre
		if (type == 2) {
			//change the attributes
			this.setOwner("unowned");
			this.setProductionSpeed(Settings.NEUTRAL_PRODUCTION_SPEED);
			this.setView(img);
			this.updateUI();	
		}
	}
	
}
