
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Andrew
 */
public class PlayerInfo extends Unit {
    int reload;
    int maxReload;
    ArrayList<Weapon> weapons = new ArrayList<>();
    int weapon = -1;

    boolean reloadChanged;
    boolean weaponsChanged;
    boolean weaponChanged;
    
    public PlayerInfo() {
        setMyUnit(true);
    }
    
    public int getMaxReload() {
        return maxReload;
    }

	public void setMaxReload(int maxReload) {
		this.maxReload = maxReload;
	}

	public int getReload() {
		return reload;
	}

	public void setReload(int reload) {
		if (this.reload != reload) {
			this.reload = reload;
			setReloadChanged(true);
			
			if (reload > 0 && weapon != -1) {
				Weapon w = weapons.get(weapon);
				if (reload > w.getBulletReloadTime())
					setMaxReload(w.getMagazineReloadTime());
				else
					setMaxReload(w.getBulletReloadTime());
			}
		}
	}

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }
    
    public Weapon findWeapon(String name) {
        for (Weapon w : weapons) {
            if (w.getName().equals(name))
                return w;
        }
        return null;
    }

    public int getWeapon() {
        return weapon;
    }

    public void setWeapon(int weapon) {
        if (this.weapon != weapon) {
            this.weapon = weapon;
            setWeaponChanged(true);
        }
    }

    /*
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        if (this.unit != unit) {
            if (this.unit != null)
                this.unit.setMyUnit(false);
            if (unit != null)
                unit.setMyUnit(true);
            
            this.unit = unit;
            setUnitChanged(true);
        }
    }

    public Unit getPlayer() {
        return player;
    }

    public void setPlayer(Unit player) {
        this.player = player;
    }*/

    public boolean isReloadChanged() {
        return reloadChanged;
    }

    public void setReloadChanged(boolean reloadChanged) {
        this.reloadChanged = reloadChanged;
    }

/*    
    public boolean isIdChanged() {
        return idChanged;
    }

    public void setIdChanged(boolean idChanged) {
        this.idChanged = idChanged;
    }
*/
    public boolean isWeaponsChanged() {
        return weaponsChanged;
    }

    public void setWeaponsChanged(boolean weaponsChanged) {
        this.weaponsChanged = weaponsChanged;
    }

    public boolean isWeaponChanged() {
        return weaponChanged;
    }

    public void setWeaponChanged(boolean weaponChanged) {
        this.weaponChanged = weaponChanged;
    }
/*
    public boolean isUnitChanged() {
        return unitChanged;
    }

    public void setUnitChanged(boolean unitChanged) {
        this.unitChanged = unitChanged;
    }
    
    public boolean isHealthChanged() {
        return unit == null ? false : unit.healthChanged;
    }

    public void setHealthChanged(boolean healthChanged) {
        if (unit != null)
            unit.setHealthChanged(healthChanged);
    }    
*/    
    public boolean isChanged() {
        return isLivesChanged() || isReloadChanged() || isWeaponChanged() || 
                isWeaponsChanged() || isHealthChanged() || isArmorChanged();
    }
    
    public void clearChanges() {
        setLivesChanged(false);
        setHealthChanged(false);
        setWeaponsChanged(false);
        setWeaponChanged(false);
        setReloadChanged(false);
        setArmorChanged(false);
    }
    
    public void processActions() {
        if (!reloadChanged && reload > 0 && weapon != -1 && weapons.get(weapon).hasBullets()) {
            reload--;
            reloadChanged = true;
        }
    }
    
}
