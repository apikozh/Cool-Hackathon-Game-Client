/**
 * Created by Victor Dichko on 23.11.14.
 */
public class UnitAction {
    public static final int MOVE_NONE = 0;
    public static final int MOVE_RIGHT = 1;
    public static final int MOVE_DOWN = 2;
    public static final int MOVE_LEFT = 3;
    public static final int MOVE_UP = 4;

    public static final int ROT_NONE = 0;
    public static final int ROT_LEFT = -1;
    public static final int ROT_RIGHT = 1;

    private int movement;
    private int rotation;
    private boolean shoot;
    private int weapon;

    public int getWeapon() {
        return weapon;
    }

    public void setWeapon(int weapon) {
        this.weapon = weapon;
    }

    public boolean isShooting() {
        return shoot;
    }

    public void setShooting(boolean value) {
        this.shoot = value;
    }

    public int getMovement() {
        return movement;
    }

    public int getRotation() {
        return rotation;
    }

    public void setMovement(int movement) {
        this.movement = movement;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public UnitAction() {
        this.weapon = -1;
		//this.movement = MOVE_NONE;
		//this.rotation = ROT_NONE;
		//this.shoot = false;
    }
}
