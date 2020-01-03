package game;

public class Settings {

	public static final double SCENE_WIDTH = 1400;
    public static final double SCENE_HEIGHT = 900;
	public static final double STATUS_BAR_HEIGHT = 80;
	
	public static final int NUMBER_OF_CASTLES = 5;
	public static final double NEUTRAL_PRODUCTION_SPEED = 0.1;
	public static final double NORMAL_PRODUCTION_SPEED = 0.2;
	public static final int NEUTRAL_MAX_UNIT = 20;
	public static final int NORMAL_MAX_UNIT = 50;
	
	public static final int PIKEMAN_TYPE= 0;
	public static final int PIKEMAN_COST= 100;
	public static final int PIKEMAN_PRODUCTION_TIME= 10;
	public static final int PIKEMAN_SPEED= 1;
	public static final int PIKEMAN_HEALTH= 1;
	public static final int PIKEMAN_DAMAGE= 1;
	
	public static final int KNIGHT_TYPE= 1;
	public static final int KNIGHT_COST= 150;
	public static final int KNIGHT_PRODUCTION_TIME= 100;
	public static final int KNIGHT_SPEED= 1;
	public static final int KNIGHT_HEALTH= 3;
	public static final int KNIGHT_DAMAGE= 5;
	
	public static final int ONAGER_TYPE= 2;
	public static final int ONAGER_COST= 200;
	public static final int ONAGER_PRODUCTION_TIME= 100;
	public static final int ONAGER_SPEED= 1;
	public static final int ONAGER_HEALTH= 5;
	public static final int ONAGER_DAMAGE= 10;
	
	public static final int LVL_UP_COST=1000;
	
    public static final int STATE_INIT=0;
    public static final int STATE_FIRST=1;
    public static final int STATE_TRAIN=2;
    public static final int STATE_SEND=3;
    public static final int STATE_UNSELECTED=4;
    public static final int STATE_INFO=5;
	public static final int STATE_UPGRADE=6;
}
