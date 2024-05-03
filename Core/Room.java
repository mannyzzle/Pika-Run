package byow.Core;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

/**
 * The Room class handles room and hallway generation for the game world.
 */
public class Room {
    private static int WIDTH;
    private static int HEIGHT;
    private static long SEED;
    private static Random RANDOM;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int ONEHUNDRED = 100;
    private static final int NEGATIVETHREE = -3;

    private static HashMap<Integer, ArrayList<CharacterPlacement>> TILES;
    private static ArrayList<ArrayList<Integer>> ROOMS;
    private static ArrayList<Integer> SIZES;

    /**
     * Checks if a room has been fully constructed.
     *
     * @param roomNum The index of the room to check.
     * @return true if the room has been fully constructed, false otherwise.
     */
    private static boolean verification(int roomNum) {
        ArrayList<Integer> sizes = ROOMS.get(roomNum);
        return sizes.get(2).equals(0);
    }
    /**
     * Checks if the last constructed area was vertical.
     *
     * @return true if the last area was vertical, false otherwise.
     */
    private static boolean wasVertical() {
        int curr1 = horizontalSize(ID);
        int curr2 = verticalSize(ID);
        return curr2 >= curr1;
    }
    /**
     * Returns the position of a room based on its index.
     *
     * @param roomNum The index of the room.
     * @return The position of the room.
     */
    private static CharacterPlacement getPosition(int roomNum) {
        ArrayList<Integer> size = ROOMS.get(roomNum);
        int x = size.get(THREE);
        int y = size.get(FOUR);
        return new CharacterPlacement(x, y);
    }
    /**
     * Returns the horizontal size of a room.
     *
     * @param num The index of the room.
     * @return The horizontal size of the room.
     */
    private static int horizontalSize(int num) {
        ArrayList<Integer> size = ROOMS.get(num);
        return size.get(0);
    }
    /**
     * Returns the vertical size of a room.
     *
     * @param num The index of the room.
     * @return The vertical size of the room.
     */
    private static int verticalSize(int num) {
        ArrayList<Integer> size = ROOMS.get(num);
        return size.get(1);
    }

    private static ArrayList<CharacterPlacement> WALL;
    private static ArrayList<CharacterPlacement> WALLS;
    private static ArrayList<CharacterPlacement> REMOVE;
    private static int TOTAL;
    private static int ID;
    private static CharacterPlacement latestPOSITION;
    private static boolean VERIFICATION;
    private static boolean capacity;

    /**
     * Returns the index of the closest fully constructed area.
     *
     * @return The index of the closest fully constructed area, or 0 if none found.
     */
    private static int closest() {
        int closestArea = 0;

        for (int id = ID; id >= 0; id--) {
            ArrayList<Integer> sizes = ROOMS.get(id);
            if (sizes.get(2) == 0) {
                closestArea = id;
            }
            if (closestArea != 0) {
                return closestArea;
            }
        }
        return closestArea;
    }


    /**
     * Constructs a new Room object with the specified seed, width, and height.
     *
     * @param seed The random seed for world generation.
     * @param x    The width of the game world.
     * @param y    The height of the game world.
     */
    public Room(long seed, int x, int y) {
        WIDTH = x;
        HEIGHT = y;
        TOTAL = 0;
        ID = 0;
        SEED = seed;
        TILES = new HashMap<>();
        ROOMS = new ArrayList<>();
        RANDOM = new Random(SEED);
        SIZES = new ArrayList<>(FIVE);
        REMOVE = new ArrayList<>();
        VERIFICATION = true;
        capacity = false;
        WALL = new ArrayList<CharacterPlacement>();
        WALLS = new ArrayList<CharacterPlacement>();
    }

    private static class CharacterPlacement {
        int x;
        int y;

        /**
         * Represents a character placement with coordinates (x, y).
         */
        CharacterPlacement(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Moves the character placement by a specified amount.
         *
         * @param dx The change in x-coordinate.
         * @param dy The change in y-coordinate.
         * @return The new character placement.
         */
        public CharacterPlacement moving(int dx, int dy) {
            return new CharacterPlacement(this.x + dx, this.y + dy);
        }

        /**
         * Compares this character placement to another object for equality.
         *
         * @param input The object to compare to.
         * @return true if the objects are equal, false otherwise.
         */
        public boolean equals(Object input) {
            if (input == null) {
                return false;
            }

            if (input.getClass() != this.getClass()) {
                return false;
            }

            final CharacterPlacement second = (CharacterPlacement) input;
            if (this.x != second.x) {
                return false;
            }

            if (this.y != second.y) {
                return false;
            }

            return true;
        }

        /**
         * Generates a hash code for this character placement.
         *
         * @return The hash code.
         */
        public int hashCode() {
            return (this.x * ONEHUNDRED + this.y);
        }
    }

    /**
     * Fills the given 2D tile array with empty tiles.
     *
     * @param t The 2D tile array to be filled.
     */
    public static void emptyBoard(TETile[][] t) {
        int x = t[0].length;
        int y = t.length;

        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j += 1) {
                t[i][j] = Tileset.MY_NOTHING;
            }
        }
    }

    /**
     * Creates a room at the given character placement in the game world.
     *
     * @param t   The 2D tile array representing the game world.
     * @param c   The character placement of the room.
     * @param x   The width of the room.
     * @param y   The height of the room.
     * @param path The path of the room's connection.
     */
    private static void createA(TETile[][] t, CharacterPlacement c, int x, int y, String path) {
        boolean verify = false;

        if (path == null) {
            verify = true;
        } else if (path.equals("up") || path.equals("right")) {
            verify = up(c, x, y);
        } else if (path.equals("down")) {
            verify  = down(c, x, y);
        } else if (path.equals("left")) {
            verify = left(c, x, y);
        }

        if (!verify) {
            roomCreatorA(t, c, x, y, path);
            VERIFICATION = true;
        } else {
            VERIFICATION = false;
        }
    }

    /**
     * Checks if it is possible to create a room by moving upward from the given character placement.
     *
     * @param c The starting character placement.
     * @param x The width of the room.
     * @param y The height of the room.
     * @return true if a room can be created, false otherwise.
     */
    private static boolean up(CharacterPlacement c, int x, int y) {
        boolean barrier = !(c.x + x < WIDTH && c.y + y < HEIGHT);
        boolean sameArea = false;

        if (WALLS == null) {
            return barrier;
        }
        for (int i = 1; i < x; i++) {
            for (int j = 1; j < y; j++) {
                CharacterPlacement latest = c.moving(i, j);
                if (WALLS.contains(latest)) {
                    sameArea = true;
                    break;
                }
            }
            if (sameArea) {
                break;
            }
        }
        return (barrier || sameArea);
    }

    /**
     * Checks if it is possible to create a room by moving downward from the given character placement.
     *
     * @param c The starting character placement.
     * @param x The width of the room.
     * @param y The height of the room.
     * @return true if a room can be created, false otherwise.
     */
    private static boolean down(CharacterPlacement c, int x, int y) {
        ArrayList<CharacterPlacement> allWall = WALLS;
        boolean barrier = !(c.x + x < WIDTH && c.y - y > 0);
        boolean sameArea = false;

        for (int i = 0; i < x; i++) {
            for (int j = 1; j < y; j++) {
                CharacterPlacement latest = c.moving(i, -j);
                if (WALLS.contains(latest)) {

                    sameArea = true;
                    break;
                }
            }
            if (sameArea) {
                break;
            }
        }
        return (barrier || sameArea);
    }

    /**
     * Checks if it is possible to create a room by moving left from the given character placement.
     *
     * @param c The starting character placement.
     * @param x The width of the room.
     * @param y The height of the room.
     * @return true if a room can be created, false otherwise.
     */
    private static boolean left(CharacterPlacement c, int x, int y) {
        boolean barrier = !(c.x - x > 0 && c.y + y < HEIGHT);
        boolean sameArea = false;

        for (int i = 1; i < x; i++) {
            for (int j = 0; j < y; j++) {
                CharacterPlacement latest = c.moving(-i, j);
                if (WALLS.contains(latest)) {
                    sameArea = true;
                    break;
                }
            }
            if (sameArea) {
                break;
            }
        }
        return (barrier || sameArea);
    }

    /**
     * Creates a room in the game world at the given character placement.
     *
     * @param t The 2D tile array representing the game world.
     * @param c The character placement of the room.
     */
    public static void roomCreator(TETile[][] t, CharacterPlacement c) {
        String path;
        if (wasVertical()) {
            path = vertical(t, c);
        } else {
            path = horizontal(t, c);
        }
        int x = RANDOM.nextInt(FIVE) + FOUR;
        int y = RANDOM.nextInt(FIVE) + FOUR;
        createA(t, c, x, y, path);
        if (VERIFICATION) {
            ID += 1;
            tileRemover();
            restoreParameter(x, y, c, 0);
        } else {
            ID = closest();
            CharacterPlacement position = getPosition(ID);
            CharacterPlacement latest = newHallWay(position);
            hallWayCreator(t, latest);
        }
    }

    /**
     * Initializes the starting area of the game world by creating a room at a random position.
     *
     * @param t The 2D tile array representing the game world.
     */
    private static void starterArea(TETile[][] t) {
        CharacterPlacement c = new CharacterPlacement(RANDOM.nextInt(WIDTH / 2) + 2, RANDOM.nextInt(HEIGHT / 2) + 2);
        String path = "up";
        int x = RANDOM.nextInt(FIVE) + FIVE;
        int y = RANDOM.nextInt(FIVE) + FIVE;
        createA(t, c, x, y, path);
        restoreParameter(x, y, c, 0);
    }

    /**
     * Creates a hallway in the game world at the given character placement.
     *
     * @param t The 2D tile array representing the game world.
     * @param c The character placement of the hallway.
     */
    public static void hallWayCreator(TETile[][] t, CharacterPlacement c) {
        if (ID == 0 && c == null) {
            capacity = true;
        } else {
            if (c == null) {
                cannotCreate(t);
            } else {
                String direction;
                int x;
                int y;
                if (verification(ID)) {
                    direction = horizontal(t, c);
                    if (direction == null || direction.equals("left") || direction.equals("right")) {
                        x = RANDOM.nextInt(FIVE) + FOUR;
                        y = THREE;
                    } else {
                        x = THREE;
                        y = RANDOM.nextInt(FIVE) + FOUR;
                    }
                } else {
                    if (wasVertical()) {
                        direction = vertical(t, c);
                        x = RANDOM.nextInt(FIVE) + FOUR;
                        y = THREE;
                    } else {
                        direction = horizontal(t, c);
                        x = THREE;
                        y = RANDOM.nextInt(FIVE) + FOUR;
                    }
                }
                createA(t, c, x, y, direction);
                if (VERIFICATION) {
                    ID += 1;
                    tileRemover();
                    restoreParameter(x, y, c, 1);
                } else {
                    cannotCreate(t);
                }
            }
        }
    }

    /**
     * Creates a row of tiles with walls and floors in the game world.
     *
     * @param t        The 2D tile array representing the game world.
     * @param c        The character placement of the starting position.
     * @param rowLength The length of the row to create.
     */
    public static void rowCreator(TETile[][] t, CharacterPlacement c, int rowLength) {
        t[c.x][c.y] = Tileset.MY_WALL;
        WALL.add(c);
        WALLS.add(c);

        for (int i = 1; i < rowLength - 1; i++) {
            t[c.x + i][c.y] = Tileset.MY_FLOOR;
        }
        CharacterPlacement temp = c.moving(rowLength - 1, 0);
        WALL.add(temp);
        WALLS.add(temp);
        t[temp.x][temp.y] = Tileset.MY_WALL;
    }

    /**
     * Creates a row of wall tiles in the game world.
     *
     * @param t      The 2D tile array representing the game world.
     * @param c      The character placement of the starting position.
     * @param length The length of the row to create.
     */
    public static void rowWallCreator(TETile[][] t, CharacterPlacement c, int length) {
        CharacterPlacement temp = c;
        for (int i = 0; i < length; i++) {
            WALL.add(temp);
            WALLS.add(temp);
            t[c.x + i][c.y] = Tileset.MY_WALL;
            temp = temp.moving(1, 0);
        }
    }


    /**
     * Creates the walls and floor tiles for a rectangular room, including the side walls and floor tiles within.
     *
     * @param t The 2D tile array representing the game world.
     * @param c The starting position of the room.
     * @param x The width of the room.
     * @param y The height of the room.
     */
    private static void roomCreatorB(TETile[][] t, CharacterPlacement c, int x, int y) {
        rowWallCreator(t, c, x);
        for (int i = 1; i < y - 1; i++) {
            CharacterPlacement shifted = c.moving(0, i);
            rowCreator(t, shifted, x);
        }
        CharacterPlacement shifted2 = c.moving(0, y - 1);
        rowWallCreator(t, shifted2, x);
    }

    /**
     * Creates a room in the game world at the given character placement, handling different directions.
     *
     * @param t    The 2D tile array representing the game world.
     * @param c    The character placement of the room.
     * @param x    The width of the room.
     * @param y    The height of the room.
     * @param move The direction of the room's connection.
     */
    private static void roomCreatorA(TETile[][] t, CharacterPlacement c,
                                        int x, int y, String move) {
        ArrayList<CharacterPlacement> character1s = REMOVE;
        ArrayList<CharacterPlacement> last = TILES.get(ID);
        if (last == null) {
            last = new ArrayList<CharacterPlacement>();

        }
        switch (move) {
            case "up":
                for (int i = NEGATIVETHREE; i < x + 1; i++) {
                    CharacterPlacement shifted = c.moving(i, 0);
                    if (last.contains(shifted)) {
                        character1s.add(shifted);
                    }
                }
                roomCreatorB(t, c, x, y);
                latestPOSITION = c;
                break;
            case "right":
                for (int i = NEGATIVETHREE; i < y + 1; i++) {
                    CharacterPlacement shifted = c.moving(0, i);
                    if (last.contains(shifted)) {
                        character1s.add(shifted);
                    }
                }
                roomCreatorB(t, c, x, y);
                latestPOSITION = c;
                break;
            case "down":
                for (int i = NEGATIVETHREE; i < x + 1; i++) {
                    CharacterPlacement shifted = c.moving(i, 0);
                    if (last.contains(shifted)) {
                        character1s.add(shifted);
                    }
                }
                CharacterPlacement curr = c.moving(0, -y + 1);
                latestPOSITION = curr;
                roomCreatorB(t, curr, x, y);
                break;
            case "left":
                for (int i = NEGATIVETHREE; i < y + 1; i++) {
                    CharacterPlacement shifted = c.moving(0, i);
                    if (last.contains(shifted)) {
                        character1s.add(shifted);
                    }
                }
                CharacterPlacement curr1 = c.moving(-x + 1, 0);
                latestPOSITION = curr1;
                roomCreatorB(t, curr1, x, y);
                break;
            default:
                break;
        }
        REMOVE = character1s;
    }

    /**
     * Determines the direction of a horizontal connection from a given position.
     * Checks if a horizontal connection is possible in each direction and returns the valid direction.
     *
     * @param t The 2D tile array representing the game world.
     * @param c The CharacterPlacement representing the starting position.
     * @return The direction of the valid horizontal connection ("up", "down", "left", "right"),
     * or null if no valid connection is possible.
     */
    private static String horizontal(TETile[][] t, CharacterPlacement c) {
        if ((c.x + 1 >= WIDTH - 1)
                || (c.x - 1 <= 1)
                || (c.y + 1 >= HEIGHT - 1)
                || (c.y - 1 <= 1)) {
            return null;
        }
        if (t[c.x + 1][c.y].equals(Tileset.MY_WALL)
                && (t[c.x][c.y - 1].equals(Tileset.MY_FLOOR) || t[c.x][c.y - 1].equals(Tileset.MY_WALL))) {
            return "up";
        }
        if (t[c.x + 1][c.y].equals(Tileset.MY_WALL)
                && (t[c.x][c.y + 1].equals(Tileset.MY_FLOOR))) {
            return "down";
        }
        if (t[c.x][c.y + 1].equals(Tileset.MY_WALL)
                && (t[c.x + 1][c.y].equals(Tileset.MY_FLOOR) || t[c.x + 1][c.y].equals(Tileset.MY_WALL))) {
            return "left";
        }
        if (t[c.x][c.y + 1].equals(Tileset.MY_WALL)
                && (t[c.x - 1][c.y].equals(Tileset.MY_FLOOR) || t[c.x - 1][c.y].equals(Tileset.MY_WALL))) {
            return "right";
        }
        return null;
    }

    /**
     * Determines whether a room can be extended vertically at the given character placement.
     * Checks if the tiles adjacent to the character placement allow for vertical extension.
     *
     * @param t The 2D tile array representing the game world.
     * @param c The character placement representing the position to check for vertical extension.
     * @return A string indicating the direction of valid vertical extension ("up" or "down"), or null if not possible.
     */
    private static String vertical(TETile[][] t, CharacterPlacement c) {
        if ((c.x + 1 >= WIDTH - 1)
                || (c.x - 1 <= 1)
                || (c.y + 1 >= HEIGHT - 1)
                || (c.y - 1 <= 1)) {
            return null;
        }
        if (t[c.x + 1][c.y].equals(Tileset.MY_WALL)
                && (t[c.x][c.y - 1].equals(Tileset.MY_FLOOR) || t[c.x][c.y - 1].equals(Tileset.MY_WALL))) {
            return "up";
        }
        if (t[c.x + 1][c.y].equals(Tileset.MY_WALL)
                && (t[c.x][c.y + 1].equals(Tileset.MY_FLOOR) || t[c.x][c.y + 1].equals(Tileset.MY_WALL))) {
            return "down";
        }
        if (t[c.x][c.y + 1].equals(Tileset.MY_WALL)
                && (t[c.x + 1][c.y].equals(Tileset.MY_FLOOR))) {
            return "left";
        }
        if (t[c.x][c.y + 1].equals(Tileset.MY_WALL)
                && (t[c.x - 1][c.y].equals(Tileset.MY_FLOOR) || t[c.x - 1][c.y].equals(Tileset.MY_WALL))) {
            return "right";
        }
        return null;
    }

    /**
     * Handles the case where a new room or hallway cannot be created in the current area.
     * Attempts to find an alternative solution, such as connecting to an existing hallway.
     *
     * @param t The 2D tile array representing the game world.
     */
    private static void cannotCreate(TETile[][] t) {
        ID = closest();
        CharacterPlacement position = getPosition(ID);
        ArrayList<CharacterPlacement> availTiles = TILES.get(ID);
        if (availTiles.size() != 0) {
            CharacterPlacement latest = newHallWay(position);
            hallWayCreator(t, latest);
        } else {
            if (ID == 0) {
                hallWayCreator(t, null);
                return;
            }
            ID -= 1;
            cannotCreate(t);
        }
    }

    /**
     * Removes tiles that overlap between rooms and hallways.
     */
    private static void tileRemover() {
        ArrayList<CharacterPlacement> remove = REMOVE;
        ArrayList<CharacterPlacement> walls = WALL;
        ArrayList<CharacterPlacement> last = TILES.get(ID - 1);
        HashSet<CharacterPlacement> allSet = new HashSet<>(walls);
        HashSet<CharacterPlacement> prevSet = new HashSet<>(last);

        allSet.removeAll(remove);
        prevSet.removeAll(remove);

        walls.clear();
        walls.addAll(allSet);

        last.clear();
        last.addAll(prevSet);

        TILES.put(ID - 1, last);
    }

    /**
     * Restores parameters after constructing a room or hallway.
     *
     * @param x   The width of the area.
     * @param y   The height of the area.
     * @param c   The current position.
     * @param room The room identifier.
     */
    private static void restoreParameter(int x, int y, CharacterPlacement c, int room) {
        SIZES.add(0, x);
        SIZES.add(1, y);
        SIZES.add(2, room);
        SIZES.add(THREE, latestPOSITION.x);
        SIZES.add(FOUR, latestPOSITION.y);
        ROOMS.add(ID, SIZES);
        TOTAL++;
        TILES.put(ID, WALL);
        WALL = new ArrayList<CharacterPlacement>();
        SIZES = new ArrayList<>(FIVE);
        REMOVE = new ArrayList<CharacterPlacement>();
    }

    /**
     * Removes a tile from the list of tiles in the current area.
     *
     * @param c The tile to be removed.
     */
    private static void newTileRemover(CharacterPlacement c) {
        ArrayList<CharacterPlacement> list = TILES.get(ID);
        list.remove(c);
    }
    /**
     * Creates an area for a room to connect to.
     *
     * @param t The 2D array representing the game world.
     * @param c The current position.
     * @return The position of the created area.
     */
    private static CharacterPlacement createAreaForRoom(TETile[][] t, CharacterPlacement c) {
        int currW = horizontalSize(ID);
        int currH = verticalSize(ID);

        CharacterPlacement returnP;
        if (verification(ID - 1)) {
            if (currW > currH) {
                if (t[c.x - 1][c.y].equals(Tileset.MY_NOTHING)) {
                    returnP = c;
                } else {
                    returnP = c.moving(currW - 1, 0);
                }
            } else {
                if (t[c.x][c.y - 1].equals(Tileset.MY_NOTHING)) {
                    returnP = c;
                } else {
                    returnP = c.moving(0, currH - 1);
                }
            }
        } else {
            if (currW > currH) {
                returnP = c.moving(currW - 1, 0);
            } else {
                returnP = c.moving(0, currH - 1);
            }
        }
        return returnP;
    }

    /**
     * Generates a new hallway connected to a room.
     *
     * @param c The current position.
     * @return The position of the new hallway.
     */
    private static CharacterPlacement newHallWay(CharacterPlacement c) {
        int currWidth = horizontalSize(ID);
        int currHeight = verticalSize(ID);

        int lengthOfX = c.x + currWidth - 2;
        int lengthOfY = c.y + currHeight - 2;

        ArrayList<CharacterPlacement> temp = TILES.get(ID);
        if (!(temp.size() == 0)) {
            int randomize = RANDOM.nextInt(temp.size());
            CharacterPlacement latest = temp.get(randomize);
            newTileRemover(latest);
            if ((latest.x >= lengthOfX && (latest.y == c.y || latest.y == (c.y + currHeight - 1)))
                    || (latest.y >= lengthOfY && (latest.x == c.x || latest.x == (c.x + currWidth - 1)))) {
                return newHallWay(c);
            } else {
                return latest;
            }
        } else {
            return null;
        }
    }

    /**
     * Generates a new hallway connected to a room.
     *
     * @param t The 2D array representing the game world.
     * @param p The current position.
     * @return The position of the new hallway.
     */
    private static CharacterPlacement newHallway2(TETile[][] t, CharacterPlacement p) {
        int currWidth = horizontalSize(ID);
        int currHeight = verticalSize(ID);
        CharacterPlacement returnP;
        if (verification(ID - 1)) {
            if (currWidth > currHeight) {
                if (t[p.x - 1][p.y].equals(Tileset.MY_NOTHING)) {
                    returnP = p;
                } else {
                    returnP = p.moving(currWidth - 1, 0);
                }
            } else {
                if (t[p.x][p.y - 1].equals(Tileset.MY_NOTHING)) {
                    returnP = p;
                } else {
                    returnP = p.moving(0, currHeight - 1);
                }
            }
        } else {
            if (currWidth > currHeight) {
                returnP = p.moving(currWidth - 1, 0);
            } else {
                returnP = p.moving(0, currHeight - 1);
            }
        }
        return returnP;
    }

    /**
     * Connects rooms and hallways in the game world.
     *
     * @param t The 2D array representing the game world.
     */
    private static void roomConnection(TETile[][] t) {
        for (CharacterPlacement position : WALLS) {
            if (canConnectHorizontally(t, position)) {
                connectHorizontally(t, position);
            } else if (canConnectVertically(t, position)) {
                connectVertically(t, position);
            } else if (canConnectDiagonallyX(t, position)) {
                connectDiagonallyX(t, position);
            } else if (canConnectDiagonallyY(t, position)) {
                connectDiagonallyY(t, position);
            }
        }
    }

    /**
     * Checks if it's possible to connect horizontally from the specified position.
     *
     * @param t        The 2D array representing the game world.
     * @param position The current position.
     * @return true if a horizontal connection is possible, false otherwise.
     */
    private static boolean canConnectHorizontally(TETile[][] t, CharacterPlacement position) {
        return t[position.x + 1][position.y].equals(Tileset.MY_FLOOR)
                && t[position.x - 1][position.y].equals(Tileset.MY_FLOOR);
    }

    /**
     * Connects adjacent areas horizontally at the specified position.
     *
     * @param t        The 2D array representing the game world.
     * @param position The current position.
     */
    private static void connectHorizontally(TETile[][] t, CharacterPlacement position) {
        t[position.x][position.y] = Tileset.MY_FLOOR;
    }

    /**
     * Checks if it's possible to connect vertically from the specified position.
     *
     * @param t        The 2D array representing the game world.
     * @param position The current position.
     * @return true if a vertical connection is possible, false otherwise.
     */

    private static boolean canConnectVertically(TETile[][] t, CharacterPlacement position) {
        return t[position.x][position.y + 1].equals(Tileset.MY_FLOOR)
                && t[position.x][position.y - 1].equals(Tileset.MY_FLOOR);
    }

    /**
     * Connects adjacent areas vertically at the specified position.
     *
     * @param t        The 2D array representing the game world.
     * @param position The current position.
     */
    private static void connectVertically(TETile[][] t, CharacterPlacement position) {
        t[position.x][position.y] = Tileset.MY_FLOOR;
    }

    /**
     * Checks if it's possible to connect diagonally (along the X-axis) from the specified position.
     *
     * @param t        The 2D array representing the game world.
     * @param position The current position.
     * @return true if a diagonal connection along the X-axis is possible, false otherwise.
     */
    private static boolean canConnectDiagonallyX(TETile[][] t, CharacterPlacement position) {
        return t[position.x + 1][position.y].equals(Tileset.MY_FLOOR)
                && t[position.x - 1][position.y].equals(Tileset.MY_WALL)
                && t[position.x - 2][position.y].equals(Tileset.MY_FLOOR);
    }

    /**
     * Connects adjacent areas diagonally (along the X-axis) at the specified position.
     *
     * @param t        The 2D array representing the game world.
     * @param position The current position.
     */

    private static void connectDiagonallyX(TETile[][] t, CharacterPlacement position) {
        t[position.x][position.y] = Tileset.MY_FLOOR;
        t[position.x - 1][position.y] = Tileset.MY_FLOOR;
    }

    /**
     * Checks if it's possible to connect diagonally (along the Y-axis) from the specified position.
     *
     * @param t        The 2D array representing the game world.
     * @param position The current position.
     * @return true if a diagonal connection along the Y-axis is possible, false otherwise.
     */
    private static boolean canConnectDiagonallyY(TETile[][] t, CharacterPlacement position) {
        return t[position.x][position.y + 1].equals(Tileset.MY_FLOOR)
                && t[position.x][position.y - 1].equals(Tileset.MY_WALL)
                && t[position.x][position.y - 2].equals(Tileset.MY_FLOOR);
    }

    /**
     * Connects adjacent areas diagonally (along the Y-axis) at the specified position.
     *
     * @param t        The 2D array representing the game world.
     * @param position The current position.
     */
    private static void connectDiagonallyY(TETile[][] t, CharacterPlacement position) {
        t[position.x][position.y] = Tileset.MY_FLOOR;
        t[position.x][position.y - 1] = Tileset.MY_FLOOR;
    }

    /**
     * Draws the entire game world by generating rooms and hallways.
     *
     * @param t     The 2D array representing the game world.
     * @param total The total number of rooms and hallways to generate.
     */
    public static void drawWorld(TETile[][] t, int total) {
        starterArea(t);
        while (TOTAL < total) {
            if (!capacity) {
                boolean verify = verification(ID);
                CharacterPlacement curr = getPosition(ID);
                if (verify) {
                    CharacterPlacement hall = newHallWay(curr);
                    hallWayCreator(t, hall);
                } else {
                    int randomize = RANDOM.nextInt(2);
                    switch (randomize) {
                        case 0:
                            CharacterPlacement placement = createAreaForRoom(t, curr);
                            roomCreator(t, placement);
                            break;
                        case 1:
                            CharacterPlacement placement2 = newHallway2(t, curr);
                            hallWayCreator(t, placement2);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + randomize);
                    }
                }
            } else {
                TOTAL = total + 1;
            }
        }
        roomConnection(t);
    }
}
