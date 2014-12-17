
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Andrew
 */

abstract class GameObject {
	int health;

	//public GameObject(int health) {
	//	this.health = health;
	//}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public abstract void drawTo(Graphics g, int scrollX, int scrollY, int size);
	
}

abstract class StaticObject extends GameObject {
	int x, y;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
} 

abstract class DynamicObject extends GameObject {
	public final static int ANGLE_RIGHT = 0;
	public final static int ANGLE_DOWN  = 1;
	public final static int ANGLE_LEFT  = 2;
	public final static int ANGLE_UP	= 3;

	int id;
	int x, y;
	int angle;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}
	
	
} 

class Bonus extends StaticObject {
	public static final int MEDIKIT = 1;
	
	int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	@Override
	public void drawTo(Graphics g, int scrollX, int scrollY, int size) {
		int sX = (x-scrollX)*size;
		int sY = (y-scrollY)*size;
		Graphics2D g2d = (Graphics2D)g;

		switch (type) {
			case 1: g2d.setColor(new Color(0, 230, 0)); break;
			case 2: g2d.setColor(new Color(170, 170, 255)); break;
			case 3: g2d.setColor(Color.ORANGE); break;
			default: g2d.setColor(Color.MAGENTA); break;
		}
		g2d.fill(new Rectangle2D.Double(sX, sY, size-1, size-1));
		
		g2d.setColor(Color.GRAY);
		g2d.draw(new Rectangle2D.Double(sX, sY, size-1, size-1));

		if (type > 0) {
			g2d.setColor(Color.GRAY);
			g.drawLine(sX, sY, sX+size-1, sY+size-1);
			g.drawLine(sX+size-1, sY, sX, sY+size-1);
		}else{
			g2d.setColor(Color.BLACK);
			FontMetrics fm = g2d.getFontMetrics();
			g2d.drawString("?", sX + size/2.f - fm.stringWidth("?")/2.f, sY + size/2.f + fm.getHeight()/4.f);
		}
		
	}
	
}

class Wall extends StaticObject {
	private static Color[] states = {new Color(230, 230, 230), 
			new Color(130, 130, 130), new Color(100, 100, 100), Color.DARK_GRAY};
	private byte[] bitMap = {(byte)3, (byte)3, (byte)3, (byte)3, 
					(byte)3, (byte)3, (byte)3, (byte)3, 
					(byte)3};/*, (byte)3, (byte)3, (byte)3, 
					(byte)3, (byte)3, (byte)3, (byte)3};*/
	private int bitMapWeight = 3 * 9;//16;

	public void updateBitMap(int health) {
		int newBMW = 3 * 9/*16*/ * health / 400;
		Random random = new Random();
		while (bitMapWeight != newBMW) {
			int index = random.nextInt(9/*16*/);
			if (bitMapWeight > newBMW && bitMap[index] > 0) {
				bitMap[index]--;
				bitMapWeight--;
			}else if (bitMapWeight < newBMW && bitMap[index] < 3) {
				bitMap[index]++;
				bitMapWeight++;
			}
		}
	}
	
	@Override
	public void setHealth(int health) {
		super.setHealth(health);
		updateBitMap(health);
	}
	
	@Override
	public void drawTo(Graphics g, int scrollX, int scrollY, int size) {
		int sX = (x-scrollX)*size;
		int sY = (y-scrollY)*size;
		Graphics2D g2d = (Graphics2D)g;
		//g.setColor(Color.DARK_GRAY);
		//g.fillRect(sX, sY, size, size);
		float bitSize = size/3.f;
		for (int x=0; x<3; x++) {
			for (int y=0; y<3; y++) {
				if (bitMap[x*3+y] > 0) {
					g2d.setColor(states[bitMap[x*3+y]]);
					g2d.fill(new Rectangle2D.Float(sX+x*bitSize, sY+y*bitSize, bitSize, bitSize));
				}
			}
		}
	}
}

/*class Player {
	int id;

}*/

class Unit extends DynamicObject {
	String name;
	int armor;
	int lives;
	int score;
	int kills;
	Team team;
	
	boolean myUnit;

	boolean armorChanged;
	boolean healthChanged;
	boolean livesChanged;

	@Override
	public void setHealth(int health) {
		if (this.health != health) {
			this.health = health;
			setHealthChanged(true);
		}
	}

	public boolean isHealthChanged() {
		return healthChanged;
	}

	public void setHealthChanged(boolean healthChanged) {
		this.healthChanged = healthChanged;
	}
	
	public boolean isMyUnit() {
		return myUnit;
	}

	public void setMyUnit(boolean myUnit) {
		this.myUnit = myUnit;
	}

	public int getArmor() {
		return armor;
	}

	public void setArmor(int armor) {
		if (this.armor != armor) {
			this.armor = armor;
			setArmorChanged(true);
		}
	}

	public boolean isArmorChanged() {
		return armorChanged;
	}

	public void setArmorChanged(boolean armorChanged) {
		this.armorChanged = armorChanged;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		if (this.lives != lives) {
			this.lives = lives;
			setLivesChanged(true);
		}
	}

	public boolean isLivesChanged() {
		return livesChanged;
	}

	public void setLivesChanged(boolean livesChanged) {
		this.livesChanged = livesChanged;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public boolean isAlive() {
		return lives > 0 && health > 0;
	}
	
	@Override
	public void drawTo(Graphics g, int scrollX, int scrollY, int size) {
		int sX = (x-scrollX)*size;
		int sY = (y-scrollY)*size;
		Graphics2D g2d = (Graphics2D)g;
		
		Shape bodyShape = new Ellipse2D.Double(sX, sY, size-1, size-1);
		Shape bodyShapeSolid = new Ellipse2D.Double(sX+1, sY+1, size-2, size-2);
		
		if (isMyUnit())
			g2d.setColor(new Color(150, 150, 150));
		else
			g2d.setColor(new Color(210, 210, 210));
		g2d.fill(bodyShapeSolid);
	
		//g2d.setStroke(new BasicStroke(2));
		double midX = sX+(size-1)/2.0, midY = sY+(size-1)/2.0, rad = (size-1)/2.0+1;
		double fPosX = midX, fPosY = midY;
		double angle = 0;
		if (getArmor() > 0)
			rad += 2;
		switch (getAngle()) {
			case ANGLE_RIGHT:
				fPosX = midX+rad;
				fPosY = midY;
				angle = 0;
				break;
			case ANGLE_DOWN:
				fPosX = midX;
				fPosY = midY+rad;
				angle = -90;
				break;
			case ANGLE_LEFT:
				fPosX = midX-rad;
				fPosY = midY;
				angle = -180;
				break;
			case ANGLE_UP:
				fPosX = midX;
				fPosY = midY-rad;
				angle = -270;
				break;
				
		}
		
		//Shape heathShape = new Arc2D.Double(sX, sY, size-1, size-1, angle, 360.0*getHealth()/100, Arc2D.Double.PIE);
		Shape heathShape = new Arc2D.Double(sX+1, sY+1, size-2, size-2, angle, -360.0*getHealth()/100, Arc2D.Double.PIE);
		if (isMyUnit())
			g2d.setColor(Color.WHITE);
		else
			g2d.setColor(new Color(255, 100, 100));
		g2d.fill(heathShape);
		//g2d.setColor(Color.GRAY);
		g2d.draw(heathShape);
		
		//g2d.setStroke(new BasicStroke(1));
		g2d.setColor(Color.BLACK);
		g2d.draw(bodyShape);

		Shape dShape = new Arc2D.Double(sX-1+0.5, sY-1+0.5, size+1, size+1, angle, -360.0*getArmor()/100, Arc2D.Double.OPEN);
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(Color.BLUE);
		g2d.draw(dShape);
		g2d.setStroke(new BasicStroke(1));
		
		g2d.setColor(Color.BLACK);
		g2d.draw(new Line2D.Double(midX, midY, fPosX, fPosY));
		g2d.setColor(new Color(180, 0, 0));
		g2d.draw(new Rectangle2D.Double(fPosX-1, fPosY-1, 2, 2));
	}

	public void drawNameTo(Graphics g, int scrollX, int scrollY, int size) {
		int sX = (x-scrollX)*size;
		int sY = (y-scrollY)*size;
		Graphics2D g2d = (Graphics2D)g;

		FontMetrics metrics = g2d.getFontMetrics();

		//TextLayout tl = new TextLayout(name, g2d.getFont(), g2d.getFontRenderContext());
		/*Shape outline = tl.getOutline(null);
		//g2d.drawString(name, sX - metrics.stringWidth(name)/2.f + size/2.f, sY - 3);
		g2d.translate(200, 200);
		g2d.setColor(new Color(255, 255, 255, 128));
		g2d.draw(outline);
		g2d.setColor(new Color(0, 0, 0, 128));
		g2d.fill(outline);
		g2d.translate(-200, -200);*/

		Font font = g2d.getFont().deriveFont(size);
		GlyphVector gv = font.createGlyphVector(g2d.getFontRenderContext(), name);
		Shape ol = gv.getOutline(sX - metrics.stringWidth(name)/2.f + size/2.f, sY - size/2.f);
		g2d.setStroke(new BasicStroke(2.f));
		g2d.setColor(new Color(255, 255, 255, 127));
		g2d.draw(ol);
		g2d.setColor(new Color(255, 0, 0, 127));
		// Color(0, 170, 0, 127) G
		// Color(0, 0, 255, 127) B
		// Color(255, 0, 0, 127) R
		// Color(100, 100, 0, 127) Y
		// Color(0, 150, 150, 127) T
		// Color(150, 0, 150, 127) P
		g2d.fill(ol);
		g2d.setStroke(new BasicStroke(1.f));
		
		// sX + size/2.0 - size, sY - size*2/8.0, size*2, size/8.0
		/*g2d.setColor(new Color(0, 255, 0, 127));
		g2d.fill(new Rectangle2D.Double(sX - size*2/4.0, 
				sY + size/2.0 - size*4/5.0 + (100-getHealth())/100.0*size*8/5.0, 
				size/4.0, 
				getHealth()/100.0*size*8/5.0));
		g2d.setColor(new Color(0, 0, 0, 127));
		g2d.draw(new Rectangle2D.Double(sX - size*2/4.0, sY + size/2.0 - size*4/5.0, size/4.0, size*8/5.0));
		//g2d.draw(new Rectangle2D.Double(sX + size/2.0 - size, sY - size*2/8.0, size*2, size/8.0));
*/
		//g2d.setColor(new Color(0, 0, 0, 128));
		//tl.draw(g2d, sX - metrics.stringWidth(name)/2.f + size/2.f, sY - 3);
		//g2d.setColor(new Color(0, 0, 0, 255));
		//g2d.drawString(name, sX - metrics.stringWidth(name)/2.f + size/2.f, sY - 3);
	}

}

class Bullet extends DynamicObject {
	int type;
	int delay;
	int leftDelay;
	boolean firstIteration = true;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
		this.leftDelay = delay;
	}

	public int getLeftDelay() {
		return leftDelay;
	}

	public void setLeftDelay(int leftDelay) {
		this.leftDelay = leftDelay;
	}
	
	public boolean fly() {
		if (firstIteration)
			firstIteration = false;
        else if (leftDelay == 0) {
            leftDelay = delay;
            switch (angle) {
                case ANGLE_UP:
                    y--;
                    break;
                case ANGLE_LEFT:
                    x--;
                    break;
                case ANGLE_RIGHT:
                    x++;
                    break;
                case ANGLE_DOWN:
                    y++;
                    break;
            }
            return true;
        }else{
            leftDelay--;
        }
        return false;
    }
    
    @Override
    public void drawTo(Graphics g, int scrollX, int scrollY, int size) {
        int sX = (x-scrollX)*size;
        int sY = (y-scrollY)*size;
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.red);
        double midX = sX + (size-1)/2.0, midY = sY + (size-1)/2.0, rad = size/3.1;
        g2d.fill(new Ellipse2D.Double(midX - rad, midY - rad, rad*2-1, rad*2-1));
    }
}

class Team {
    String name;
    ArrayList<Unit> units = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }
    
    public void addUnit(Unit unit) {
            units.add(unit);
    }

    public void removeUnit(Unit unit) {
            units.remove(unit);
    }
}

class Weapon {
    String name;
    int bulletType;
    int bulletsNumber;
    int bulletsInMagazine;
    int bulletReloadTime;
    int magazineReloadTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBulletType() {
        return bulletType;
    }

    public void setBulletType(int bulletType) {
        this.bulletType = bulletType;
    }

    public int getBulletsNumber() {
        return bulletsNumber;
    }

    public void setBulletsNumber(int bulletsNumber) {
        this.bulletsNumber = bulletsNumber;
    }
	
    public boolean hasBulletsInBag() {
        return bulletsNumber == -1 || bulletsNumber > 0;
    }

    public boolean hasBulletsInMagazine() {
        return bulletsInMagazine == -1 || bulletsInMagazine > 0;
    }

    public boolean hasBullets() {
        return hasBulletsInBag() || hasBulletsInMagazine();
    }

	public int getBulletsInMagazine() {
		return bulletsInMagazine;
	}

	public void setBulletsInMagazine(int bulletsInMagazine) {
		this.bulletsInMagazine = bulletsInMagazine;
	}

	public int getBulletReloadTime() {
		return bulletReloadTime;
	}

	public void setBulletReloadTime(int bulletReloadTime) {
		this.bulletReloadTime = bulletReloadTime;
	}

	public int getMagazineReloadTime() {
		return magazineReloadTime;
	}

	public void setMagazineReloadTime(int magazineReloadTime) {
		this.magazineReloadTime = magazineReloadTime;
	}

    
}

public class GameInfo {
    PlayerInfo playerInfo;
    int playerId;
    String playerName;
    int playerTeam = -1;
    
    /*ArrayList<Wall> walls = new ArrayList<>();
    ArrayList<Bonus> bonuses = new ArrayList<>();
    ArrayList<Unit> units = new ArrayList<>();
    ArrayList<Unit> deadUnits = new ArrayList<>();
    ArrayList<Bullet> bullets = new ArrayList<>();
    */
    
    GameMap map;    
    ArrayList<StaticObject> mapObjects = new ArrayList<>();
    ArrayList<Team> teams = new ArrayList<>();
    ArrayList<Weapon> weapons = new ArrayList<>();
    LinkedHashMap<Integer, Unit> units = new LinkedHashMap<>(20);
    LinkedHashMap<Integer, Unit> players = new LinkedHashMap<>(20);
    LinkedHashMap<Integer, Bullet> bullets = new LinkedHashMap<>(1000);
    //Wall[] walls = new Wall[1000];
    //Bonus[] bonuses = new Bonus[1000];
    //Unit[] units = new Unit[100];
    //Unit[] deadUnits = new Unit[100];
    //Bullet[] bullets = new Bullet[1000];
    //int wallsCount, bonusesCount, unitsCount, deadUnitsCount, bulletsCount;
    
    IUpdatesListener updatesListener;

    public Weapon findWeapon(String name) {
        for (Weapon w : weapons) {
            if (w.getName().equals(name))
                return w;
        }
        return null;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerTeam() {
        return playerTeam;
    }

    public void setPlayerTeam(int playerTeam) {
        this.playerTeam = playerTeam;
    }
    
    public /*synchronized*/ PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public /*synchronized*/ void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }
    
    public ArrayList<StaticObject> getMapObjects() {
        return mapObjects;
    }
    
    public LinkedHashMap<Integer, Unit> getUnits() {
        return units;
    }

    public LinkedHashMap<Integer, Unit> getPlayers() {
        return players;
    }

    public LinkedHashMap<Integer, Bullet> getBullets() {
        return bullets;
    }

    /*
    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public ArrayList<Bonus> getBonuses() {
        return bonuses;
    }

    /*public ArrayList<Unit> getUnits() {
        return units;
    }

    public ArrayList<Unit> getDeadUnits() {
        return deadUnits;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }*/

    /*public Wall getWall(int index) {
        if (walls[index] == null)
            walls[index] = new Wall();
        return walls[index];
    }
        
    public Bonus getBonus(int index) {
        if (bonuses[index] == null)
            bonuses[index] = new Bonus();
        return bonuses[index];
    }
        
    public Unit getUnit(int index) {
        if (units[index] == null)
            units[index] = new Unit();
        return units[index];
    }

    public Unit getDeadUnit(int index) {
        if (deadUnits[index] == null)
            deadUnits[index] = new Unit();
        return deadUnits[index];
    }

    public Bullet getBullet(int index) {
        if (bullets[index] == null)
            bullets[index] = new Bullet();
        return bullets[index];
    }
    
    /*public int getWallsCount() {
        return wallsCount;
    }

    public void setWallsCount(int wallsCount) {
        this.wallsCount = wallsCount;
    }

    public int getBonusesCount() {
        return bonusesCount;
    }

    public void setBonusesCount(int bonusesCount) {
        this.bonusesCount = bonusesCount;
    }

    public int getUnitsCount() {
        return unitsCount;
    }

    public void setUnitsCount(int unitsCount) {
        this.unitsCount = unitsCount;
    }

    public int getDeadUnitsCount() {
        return deadUnitsCount;
    }

    public void setDeadUnitsCount(int deadUnitsCount) {
        this.deadUnitsCount = deadUnitsCount;
    }

    public int getBulletsCount() {
        return bulletsCount;
    }

    public void setBulletsCount(int bulletsCount) {
        this.bulletsCount = bulletsCount;
    }
    */
    
    public ArrayList<Team> getTeams() {
        return teams;
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    public int getWidth() {
        return map.getWidth();
    }

    public int getHeight() {
        return map.getHeight();
    }

    public synchronized void setMapSize(int width, int height) {
        map = new GameMap(width, height);
    }

    public GameMap getMap() {
        return map;
    }
    
    public IUpdatesListener getUpdatesListener() {
        return updatesListener;
    }

    public void setUpdatesListener(IUpdatesListener updatesListener) {
        this.updatesListener = updatesListener;
    }
    
    public void notifyUpdatesListener(UpdateType type) {
        if (updatesListener != null)
            updatesListener.infoUpdated(type);
    }
    
    public synchronized boolean processBullets() {
        boolean hasChanges = false;

        for (Bullet bullet : bullets.values()) {
            if (bullet.fly()) {
                hasChanges = true;
            }
        }
        
        return hasChanges;
    }
    
    public synchronized void drawTo(Graphics g, int scrollX, int scrollY, int size) {
        scrollX = scrollX - 1;
        scrollY = scrollY - 1;
        
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        //g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        //g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        g2d.setBackground(new Color(230, 230, 230));
        g2d.clearRect(size-1, size-1, size*map.getWidth()+1, size*map.getHeight()+1);
        //g2d.fill(new Rectangle2D.Double(size-1, size-1, size*map.getWidth()+1, size*map.getHeight()+1));
        g2d.setColor(Color.black);
        g2d.draw(new Rectangle2D.Double(size-1, size-1, size*map.getWidth()+1, size*map.getHeight()+1));
        //g.drawRect(size-1, size-1, size*width+1, size*height+1);

        /*for (int i=0; i<wallsCount; i++) {
            walls[i].drawTo(g, scrollX, scrollY, size);
        }
        for (int i=0; i<bonusesCount; i++) {
            bonuses[i].drawTo(g, scrollX, scrollY, size);
        }
        for (int i=0; i<unitsCount; i++) {
            units[i].drawTo(g, scrollX, scrollY, size);
        }
        for (int i=0; i<bulletsCount; i++) {
            bullets[i].drawTo(g, scrollX, scrollY, size);
        }
        */
        for (Bullet obj : bullets.values()) {
            obj.drawTo(g, scrollX, scrollY, size);
        }
        for (StaticObject obj : mapObjects) {
            obj.drawTo(g, scrollX, scrollY, size);
        }
        for (Unit obj : units.values()) {
            if (!obj.isMyUnit())
                obj.drawTo(g, scrollX, scrollY, size);
        }
        if (playerInfo != null && playerInfo.isAlive())
            playerInfo.drawTo(g, scrollX, scrollY, size);
    
        for (Unit obj : units.values()) {
            obj.drawNameTo(g, scrollX, scrollY, size);
        }
        
    }
    
}
