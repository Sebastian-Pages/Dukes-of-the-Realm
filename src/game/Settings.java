package game;

public class Settings {

	public static final double SCENE_WIDTH = 1000;
    public static final double SCENE_HEIGHT = 800;
	public static final double STATUS_BAR_HEIGHT = 80;
	
	public static final int NUMBER_OF_CASTLES = 5;
	public static final double NEUTRAL_PRODUCTION_SPEED = 0.1;
	public static final double NORMAL_PRODUCTION_SPEED = 0.2;
	public static final int NEUTRAL_MAX_UNIT = 20;
	public static final int NORMAL_MAX_UNIT = 50;
	
	public static final int PIQUIER_COST= 100;
	public static final int PIQUIER_PRODUCTION_TIME= 100;
	public static final int PIQUIER_SPEED= 1;
	public static final int PIQUIER_HEALTH= 1;
	public static final int PIQUIER_DAMAGE= 1;
	
    public static final double PLAYER_SPEED = 4.0;
    public static final int    PLAYER_HEALTH = 3;
    public static final double PLAYER_DAMAGE = 1;

    public static final double MISSILE_SPEED = 4.0;
    public static final int    MISSILE_HEALTH = 0;
    public static final double MISSILE_DAMAGE = 1.0;

    public static final int ENEMY_SPAWN_RANDOMNESS = 100;
    
    public static final int FIRE_FREQUENCY_LOW = 1000 * 1000 * 1000; // 1 second in nanoseconds
    public static final int FIRE_FREQUENCY_MEDIUM = 500 * 1000 * 1000; // 0.5 second in nanoseconds
    public static final int FIRE_FREQUENCY_HIGH = 100 * 1000 * 1000; // 0.1 second in nanoseconds
   
    public static final int STATE_INIT=0;
    public static final int STATE_FIRST=1;
    public static final int STATE_TRAIN=2;
    public static final int STATE_SEND=3;
}
