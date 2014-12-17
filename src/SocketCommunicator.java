
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Andrew
 */
public class SocketCommunicator extends Thread implements Closeable {
    
    Socket socket;
//    String hostname;
//    int port;
    GameInfo game;
    PrintWriter out;
    BufferedReader in;
    boolean ready = false;
    
    public SocketCommunicator(Socket socket, GameInfo game) {
//        this.hostname = hostname;
//        this.port = port;
        this.game = game;
        this.socket = socket;
        
        start();
    }

    public boolean isReady() {
        return ready;
    }
    
    @Override
    public void close() {
        if (in != null) {
            try {
                in.close();
                in = null;
            } catch (IOException ex) {
                Logger.getLogger(SocketCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (out != null) {
            out.close();
            out = null;
        }
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException ex) {
                Logger.getLogger(SocketCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void sendCommand(String command) {
        if (out !=null) {
            out.println(command);
        }
    }
    
    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out.print("I wanna play! (protoV2)\n");
            out.flush();
            
            String inputLine = in.readLine();
            System.out.println("SERVER: " + inputLine);
            if (inputLine == null || !inputLine.equals("Welcome to Cool Hackathon!")) {
                //out.print("Bad_answer!\n");
                //out.flush();
                System.out.println("Bad magic sentence from server!");
                return;
            }
            
            ready = true;
            
            PlayerInfo playerInfo;
            
            // TODO: replace "true" to connection alive condition
            while (true) {
                inputLine = in.readLine();
                if (inputLine == null)
                    continue;
                try (Scanner cmdScanner = new Scanner(inputLine)) {
                    if (!cmdScanner.hasNext())
                        continue;
                    String cmd = cmdScanner.next();
                    //System.out.println("CLIENT: " + cmd + " ");

                    switch (cmd) {

                        case "map":
                            if (cmdScanner.hasNextInt()) {
                                int width = cmdScanner.nextInt();
                                if (cmdScanner.hasNextInt()) {
                                    int height = cmdScanner.nextInt();
                                    game.setMapSize(width, height);
                                }
                            }
                            break;

                        case "teams":
                            if (cmdScanner.hasNextInt()) {
                                int num = cmdScanner.nextInt();
                                synchronized (game) {
                                    // TODO: Clear reliative map info
                                    game.getTeams().clear();
                                    for (int i=0; i<num; i++) {
                                        inputLine = in.readLine();
                                        if (inputLine == null)
                                            break;
                                        
                                        Team team = new Team();
                                        try (Scanner scanner = new Scanner(inputLine)) {
                                            if (!scanner.hasNext()) break;
                                            team.setName(scanner.next());
                                        }
                                        game.getTeams().add(team);
                                    }
                                }
                            }
                            break;

                        case "weapons":
                            if (cmdScanner.hasNextInt()) {
                                int num = cmdScanner.nextInt();
                                synchronized (game) {
                                    game.getWeapons().clear();
                                    for (int i=0; i<num; i++) {
                                        inputLine = in.readLine();
                                        if (inputLine == null)
                                            break;
                                        
                                        Weapon weapon = new Weapon();
                                        try (Scanner scanner = new Scanner(inputLine)) {
                                            if (!scanner.hasNext()) break;
                                            weapon.setName(scanner.next());

                                            if (!scanner.hasNextInt()) break;
                                            weapon.setBulletReloadTime(scanner.nextInt());

                                            if (!scanner.hasNextInt()) break;
                                            weapon.setMagazineReloadTime(scanner.nextInt());

                                            // TODO: other weapon params
                                        }
                                        game.getWeapons().add(weapon);
                                    }
                                }
                            }
                            break;

                        case "your_id":
                            if (cmdScanner.hasNextInt()) {
                                game.setPlayerId(cmdScanner.nextInt());
                            }
                            break;

                        case "begin#map_data":
                            boolean mapChanged = false;
                            synchronized (game) {
                                do {
                                    inputLine = in.readLine();
                                    if (inputLine == null)
                                        break;
//                                    if (!inputLine.equals("end#map_data"))
//                                        System.out.println(inputLine);
                                    try (Scanner mapScanner = new Scanner(inputLine)) {
                                        if (!mapScanner.hasNext())
                                            continue;
                                        cmd = mapScanner.next();

                                        switch (cmd) {

                                            case "reset":
                                                mapChanged = true;
                                                // Clear all map data
                                                game.getMapObjects().clear();
                                                game.getUnits().clear();
                                                game.getPlayers().clear();
                                                game.getBullets().clear();
                                                break;

                                            case "list#static_objs":
                                                mapChanged = true;
                                                if (mapScanner.hasNextInt()) {
                                                    int num = mapScanner.nextInt();
                                                    for (int i=0; i<num; i++) {
                                                        inputLine = in.readLine();
                                                        if (inputLine == null) break;
                                                        //System.out.println(inputLine);
                                                        try (Scanner scanner = new Scanner(inputLine)) {
                                                            // TODO: String to Int
                                                            if (!scanner.hasNext()) break;
                                                            cmd = scanner.next();
                                                            if (!scanner.hasNextInt()) break;
                                                            int x = scanner.nextInt();
                                                            if (!scanner.hasNextInt()) break;
                                                            int y = scanner.nextInt();
                                                            int val;
                                                            StaticObject obj = null;
                                                            switch (cmd) {
                                                                case "a":
                                                                    if (!scanner.hasNext()) break;
                                                                    cmd = scanner.next();
                                                                    if (!scanner.hasNextInt()) break;
                                                                    val = scanner.nextInt();
                                                                    if (cmd.equals("w")) {
                                                                        obj = new Wall();
                                                                        obj.setHealth(val);
                                                                    }else if (cmd.equals("b")) {
                                                                        obj = new Bonus();
                                                                        ((Bonus)obj).setType(val);
                                                                    }
                                                                    obj.setX(x);
                                                                    obj.setY(y);
                                                                    game.getMapObjects().add(obj);
                                                                    game.getMap().setElement(x, y, obj);
                                                                    break;
                                                                case "c":
                                                                    if (!scanner.hasNextInt()) break;
                                                                    val = scanner.nextInt();
                                                                    obj = game.getMap().getElement(x, y);
                                                                    if (obj == null) {
                                                                    }else if (obj instanceof Wall) {
                                                                        obj.setHealth(val);
                                                                    }else if (obj instanceof Bonus) {
                                                                        ((Bonus)obj).setType(val);
                                                                    }
                                                                    break;
                                                                case "r":
                                                                    obj = game.getMap().getElement(x, y);
                                                                    game.getMapObjects().remove(obj);
                                                                    game.getMap().setElement(x, y, null);
                                                                    break;
                                                            }
                                                                                                                    }
                                                    }
                                                }
                                                break;
                                            
                                            case "begin#dyn_objs":
                                                mapChanged = true;
                                                do {
                                                    inputLine = in.readLine();
                                                    if (inputLine == null) break;
                                                    //if (!inputLine.equals("end#dyn_objs"))
                                                    //    System.out.println(inputLine);
                                                    try (Scanner scanner = new Scanner(inputLine)) {
                                                        if (!scanner.hasNext()) continue;
                                                        cmd = scanner.next();
                                                        if (!scanner.hasNextInt()) continue;
                                                        int id = scanner.nextInt();
                                                        if (!scanner.hasNext()) continue;
                                                        String type = scanner.next();
                                                        switch (type) {
                                                            case "u":
                                                                if (cmd.equals("r")) {
                                                                    //game.getUnits().get(id).setHealth(0);
                                                                    game.getUnits().remove(id);
                                                                    if (game.getPlayerId() == id && game.getPlayerInfo() != null)
                                                                        game.getPlayerInfo().setHealth(0);
                                                                }else{
                                                                    Unit unit;
                                                                    if (cmd.equals("a")) {
                                                                        unit = game.getPlayers().get(id);
                                                                        game.getUnits().put(id, unit);
                                                                    }else{
                                                                        unit = game.getUnits().get(id);
                                                                    }

                                                                    if (!scanner.hasNextInt()) break;
                                                                    unit.setX(scanner.nextInt());
                                                                    
                                                                    if (!scanner.hasNextInt()) break;
                                                                    unit.setY(scanner.nextInt());

                                                                    if (!scanner.hasNextInt()) break;
                                                                    unit.setAngle(scanner.nextInt());

                                                                    if (!scanner.hasNextInt()) break;
                                                                    unit.setHealth(scanner.nextInt());

                                                                    if (!scanner.hasNextInt()) break;
                                                                    unit.setArmor(scanner.nextInt());
                                                                }
                                                                break;
                                                            case "p":
                                                                if (cmd.equals("r")) {
                                                                    game.getPlayers().remove(id);
                                                                    if (game.getPlayerId() == id) {
                                                                        game.setPlayerInfo(null);
                                                                        game.notifyUpdatesListener(UpdateType.UPD_PLAYER_INFO);
                                                                    }
                                                                }else{
                                                                    Unit player;
                                                                    if (cmd.equals("a")) {
                                                                        if (game.getPlayerId() == id)
                                                                            player = game.getPlayerInfo();
                                                                        else
                                                                            player = new Unit();
                                                                        player.setId(id);
                                                                        game.getPlayers().put(id, player);
                                                                    }else{
                                                                        player = game.getPlayers().get(id);
                                                                    }

                                                                    if (!scanner.hasNext()) break;
                                                                    player.setName(scanner.next());
                                                                    
                                                                    if (!scanner.hasNextInt()) break;
                                                                    int teamId = scanner.nextInt();
                                                                    if (teamId == -1) {
                                                                        player.setTeam(null);
                                                                    }else if (teamId < game.getTeams().size()) {
                                                                        player.setTeam(game.getTeams().get(teamId));
                                                                    }

                                                                    if (!scanner.hasNextInt()) break;
                                                                    player.setScore(scanner.nextInt());

                                                                    if (!scanner.hasNextInt()) break;
                                                                    player.setKills(scanner.nextInt());
                                                                }
                                                                break;
                                                            case "s":
                                                                if (cmd.equals("c")) {
                                                                    Unit player = game.getPlayers().get(id);

                                                                    if (!scanner.hasNextInt()) break;
                                                                    player.setScore(scanner.nextInt());

                                                                    if (!scanner.hasNextInt()) break;
                                                                    player.setKills(scanner.nextInt());
                                                                }
                                                                break;
                                                            case "n":
                                                                if (cmd.equals("c")) {
                                                                    Unit player = game.getPlayers().get(id);

                                                                    if (!scanner.hasNext()) break;
                                                                    player.setName(scanner.next());
                                                                }
                                                                break;
                                                            case "t":
                                                                if (cmd.equals("c")) {
                                                                    Unit player = game.getPlayers().get(id);

                                                                    if (!scanner.hasNextInt()) break;
                                                                    int teamId = scanner.nextInt();
                                                                    if (teamId == -1) {
                                                                        player.setTeam(null);
                                                                    }else if (teamId < game.getTeams().size()) {
                                                                        player.setTeam(game.getTeams().get(teamId));
                                                                    }
                                                                }
                                                                break;
                                                            case "b":
                                                                if (cmd.equals("r")) {
                                                                    game.getBullets().remove(id);
                                                                }else{
                                                                    Bullet bullet;
                                                                    if (cmd.equals("a")) {
                                                                        bullet = new Bullet();
                                                                        bullet.setId(id);
                                                                        game.getBullets().put(id, bullet);
                                                                    }else{
                                                                        bullet = game.getBullets().get(id);
                                                                    }

                                                                    if (!scanner.hasNextInt()) break;
                                                                    bullet.setX(scanner.nextInt());
                                                                    
                                                                    if (!scanner.hasNextInt()) break;
                                                                    bullet.setY(scanner.nextInt());

                                                                    if (!scanner.hasNextInt()) break;
                                                                    bullet.setAngle(scanner.nextInt());

                                                                    if (!scanner.hasNextInt()) break;
                                                                    bullet.setType(scanner.nextInt());

                                                                    if (!scanner.hasNextInt()) break;
                                                                    bullet.setDelay(scanner.nextInt());
                                                                }
                                                                break;
                                                        }
                                                    }
                                                } while(!cmd.equals("end#dyn_objs"));
                                                
                                                break;
                                            
                                            /*
                                            case "walls":
                                                if (mapScanner.hasNextInt()) {
                                                    int num = mapScanner.nextInt();
                                                    //game.getWalls().clear();
                                                    game.setWallsCount(num);
                                                    for (int i=0; i<num; i++) {
                                                        inputLine = in.readLine();
                                                        if (inputLine == null) break;
                                                        Wall wall = game.getWall(i);
                                                        try (Scanner scanner = new Scanner(inputLine)) {
                                                            if (!scanner.hasNextInt()) break;
                                                            wall.setX(scanner.nextInt());
                                                            
                                                            if (!scanner.hasNextInt()) break;
                                                            wall.setY(scanner.nextInt());
                                                            
                                                            if (!scanner.hasNextInt()) break;
                                                            wall.setHealth(scanner.nextInt());
                                                        }
                                                    }
                                                }
                                                break;

                                            case "players":
                                                if (mapScanner.hasNextInt()) {
                                                    int num = mapScanner.nextInt();
                                                    //game.getUnits().clear();
                                                    game.setUnitsCount(num);
                                                    for (int i=0; i<num; i++) {
                                                        inputLine = in.readLine();
                                                        if (inputLine == null) break;
                                                        Unit unit = game.getUnit(i);
                                                        try (Scanner scanner = new Scanner(inputLine)) {
                                                            if (!scanner.hasNextInt()) break;
                                                            unit.setX(scanner.nextInt());

                                                            if (!scanner.hasNextInt()) break;
                                                            unit.setY(scanner.nextInt());

                                                            if (!scanner.hasNextInt()) break;
                                                            unit.setAngle(scanner.nextInt());

                                                            if (!scanner.hasNextInt()) break;
                                                            unit.setHealth(scanner.nextInt());

                                                            if (!scanner.hasNext()) break;
                                                            unit.setName(scanner.next());

                                                            if (!scanner.hasNextInt()) break;
                                                            int teamId = scanner.nextInt();
                                                            if (teamId >= 0 && teamId < game.getTeams().size())
                                                                unit.setTeam(game.getTeams().get(teamId));

                                                            if (!scanner.hasNextInt()) break;
                                                            unit.setScore(scanner.nextInt());
                                                        }
                                                        //game.getUnits().add(unit);
                                                        String playerName = game.getPlayerName();
                                                        PlayerInfo pi = game.getPlayerInfo();
                                                        if (pi != null && playerName != null && 
                                                                !playerName.isEmpty() && 
                                                                !unit.getName().equals("null") && 
                                                                playerName.equals(unit.getName())) 
                                                        {
                                                            unit.setMyUnit(true);
                                                            game.playerInfo.setUnit(unit);
                                                        }else
                                                            unit.setMyUnit(false);
                                                    }
                                                }
                                                break;

                                            case "dead_players":
                                                if (mapScanner.hasNextInt()) {
                                                    int num = mapScanner.nextInt();
                                                    //game.getDeadUnits().clear();
                                                    game.setDeadUnitsCount(num);
                                                    for (int i=0; i<num; i++) {
                                                        inputLine = in.readLine();
                                                        if (inputLine == null) break;
                                                        Unit unit = game.getDeadUnit(i);
                                                        try (Scanner scanner = new Scanner(inputLine)) {
                                                            if (!scanner.hasNext()) break;
                                                            unit.setName(scanner.next());

                                                            if (!scanner.hasNextInt()) break;
                                                            int teamId = scanner.nextInt();
                                                            if (teamId >= 0 && teamId < game.getTeams().size())
                                                                unit.setTeam(game.getTeams().get(teamId));

                                                            if (!scanner.hasNextInt()) break;
                                                            unit.setScore(scanner.nextInt());
                                                        }
                                                        //game.getDeadUnits().add(unit);
                                                        String playerName = game.getPlayerName();
                                                        PlayerInfo pi = game.getPlayerInfo();
                                                        if (pi != null && playerName != null && 
                                                                !playerName.isEmpty() && 
                                                                !unit.getName().equals("null") && 
                                                                playerName.equals(unit.getName())) 
                                                        {
                                                            game.playerInfo.setUnit(unit);
                                                        }
                                                    }
                                                }
                                                break;

                                            case "shells":
                                                if (mapScanner.hasNextInt()) {
                                                    int num = mapScanner.nextInt();
                                                    //game.getBullets().clear();
                                                    game.setBulletsCount(num);
                                                    for (int i=0; i<num; i++) {
                                                        inputLine = in.readLine();
                                                        if (inputLine == null) break;
                                                        Bullet bullet = game.getBullet(i);
                                                        try (Scanner scanner = new Scanner(inputLine)) {
                                                            if (!scanner.hasNextInt()) break;
                                                            bullet.setX(scanner.nextInt());

                                                            if (!scanner.hasNextInt()) break;
                                                            bullet.setY(scanner.nextInt());

                                                            if (!scanner.hasNextInt()) break;
                                                            bullet.setAngle(scanner.nextInt());

                                                            if (!scanner.hasNextInt()) break;
                                                            bullet.setType(scanner.nextInt());
                                                        }
                                                        //game.getBullets().add(bullet);
                                                    }
                                                }
                                                break;

                                            case "bonuses":
                                                if (mapScanner.hasNextInt()) {
                                                    int num = mapScanner.nextInt();
                                                    //game.getBonuses().clear();
                                                    game.setBonusesCount(num);
                                                    for (int i=0; i<num; i++) {
                                                        inputLine = in.readLine();
                                                        if (inputLine == null) break;
                                                        Bonus bonus = game.getBonus(i);
                                                        try (Scanner scanner = new Scanner(inputLine)) {
                                                            if (!scanner.hasNextInt()) break;
                                                            bonus.setX(scanner.nextInt());

                                                            if (!scanner.hasNextInt()) break;
                                                            bonus.setY(scanner.nextInt());

                                                            if (!scanner.hasNextInt()) break;
                                                            bonus.setType(scanner.nextInt());
                                                        }
                                                        //game.getBonuses().add(bonus);
                                                    }
                                                }
                                                break;*/

                                        }
                                    }
                                } while(!cmd.equals("end#map_data"));
                            }
                            
                            if (game.getPlayerInfo() != null) {
                                game.getPlayerInfo().processActions();
                                if (game.getPlayerInfo().isChanged()) {
                                    game.notifyUpdatesListener(UpdateType.UPD_PLAYER_INFO);
                                    game.getPlayerInfo().clearChanges();
                                }
                            }
                            
                            if (game.processBullets() || mapChanged) {
                                game.notifyUpdatesListener(UpdateType.UPD_MAP_INFO);
                            }

                            game.notifyUpdatesListener(UpdateType.UPD_ITERATION);
                            
                            break;

                        case "begin#player_data":
                            playerInfo = game.getPlayerInfo();
                            if (playerInfo == null) {
                                playerInfo = new PlayerInfo();
                            }
                            do {
                                inputLine = in.readLine();
                                if (inputLine == null)
                                    continue;
                                System.out.println(inputLine);
                                try (Scanner pinfoScanner = new Scanner(inputLine)) {
                                    if (!pinfoScanner.hasNext())
                                        continue;
                                    cmd = pinfoScanner.next();

                                    switch (cmd) {

                                        case "reset":
                                            if (!playerInfo.getWeapons().isEmpty()) {
                                                playerInfo.getWeapons().clear();
                                                playerInfo.setWeapon(-1);
                                            }
                                            playerInfo.setWeaponsChanged(true);
                                            playerInfo.setLivesChanged(true);
                                            playerInfo.setHealthChanged(true);
                                            playerInfo.setWeaponChanged(true);
                                            playerInfo.setReloadChanged(true);    
                                            break;

                                        /*case "id":
                                            if (pinfoScanner.hasNextInt()) {
                                                playerInfo.setId(pinfoScanner.nextInt());
                                            }
                                            break;*/

                                        case "lives":
                                            if (pinfoScanner.hasNextInt()) {
                                                playerInfo.setLives(pinfoScanner.nextInt());
                                            }
                                            break;

                                        case "weapon":
                                            if (pinfoScanner.hasNextInt()) {
                                                playerInfo.setWeapon(pinfoScanner.nextInt());
                                            }
                                            break;

                                        case "reload":
                                            if (pinfoScanner.hasNextInt()) {
                                                playerInfo.setReload(pinfoScanner.nextInt());
                                            }
                                            break;

                                        case "list#weapons":
                                            if (pinfoScanner.hasNextInt()) {
                                                int num = pinfoScanner.nextInt();
                                                playerInfo.setWeaponsChanged(true);
                                                for (int i=0; i<num; i++) {
                                                    inputLine = in.readLine();
                                                    if (inputLine == null) break;
                                                    try (Scanner scanner = new Scanner(inputLine)) {
                                                        if (!scanner.hasNext()) break;
                                                        cmd = scanner.next();

                                                        if (!scanner.hasNext()) break;
                                                        String name = scanner.next();
                                                        
                                                        Weapon weapon;
                                                        if (cmd.equals("r")) {
                                                            weapon = playerInfo.findWeapon(name);
                                                            playerInfo.getWeapons().remove(weapon);
                                                        }else{
                                                            if (cmd.equals("a")) {
                                                                weapon = game.findWeapon(name);
                                                                playerInfo.getWeapons().add(weapon);
                                                            }else{
                                                                weapon = playerInfo.findWeapon(name);
                                                            }
                                                            
                                                            if (!scanner.hasNextInt()) break;
                                                            weapon.setBulletsNumber(scanner.nextInt());
                                                            
                                                            if (!scanner.hasNextInt()) break;
                                                            weapon.setBulletsInMagazine(scanner.nextInt());
                                                        }
                                                    }
                                                }
                                            }
                                            break;

                                    }
                                }
                            } while(!cmd.equals("end#player_data"));
                            
                            game.setPlayerInfo(playerInfo);
                            /*if (playerInfo.isChanged()) {
                                game.notifyUpdatesListener(UpdateType.UPD_PLAYER_INFO);
                            }*/
                            break;

                        default:;
                    }
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(SocketCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Couldn't get I/O for the connection to: " + socket.getInetAddress().toString());
        } finally {
            close();
            System.out.println("Disconnected!");
            System.exit(0);
        }
    }
    
}
