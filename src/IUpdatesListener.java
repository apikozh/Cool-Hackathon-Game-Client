/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Andrew
 */

enum UpdateType {
    UPD_MAP_SIZE,
    UPD_TEAMS,
    UPD_WEAPONS,
    
    UPD_MAP_INFO,
    UPD_ITERATION,
    UPD_PLAYER_INFO,
    UPD_PLAYER_NAME

}

public interface IUpdatesListener {
    public void infoUpdated(UpdateType type);
}
