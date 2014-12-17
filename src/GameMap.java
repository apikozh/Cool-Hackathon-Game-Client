

public class GameMap {
    private StaticObject gameMap[][];

    public GameMap(int width, int height) {
        gameMap = new StaticObject[height][width];
    }

    public int getWidth() {
        return gameMap[0].length;
    }

    public int getHeight() {
        return gameMap.length;
    }

    public StaticObject getElement(int positionX, int positionY) {
        if (positionX < 0 || positionX >= getWidth() || positionY < 0 || positionY >= getHeight()) {
            throw new IndexOutOfBoundsException();
        }
        return gameMap[positionY][positionX];
    }

    public void setElement(int positionX, int positionY, StaticObject mapObject) {
        if (positionX < 0 || positionX >= getWidth() || positionY < 0 || positionY >= getHeight()) {
            throw new IndexOutOfBoundsException();
        }
        gameMap[positionY][positionX] = mapObject;
    }
}
