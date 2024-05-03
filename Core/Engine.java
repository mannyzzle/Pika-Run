package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import static byow.Core.Utils.*;
import static java.lang.Character.*;

public class Engine {
    // Renderer for displaying the game world
    private static TERenderer ter = new TERenderer();

    // The game world represented as a 2D grid of tiles
    private static TETile[][] TETILE;
    public static final int WIDTH = 90;
    public static final int HEIGHT = 50;
    private static final int TWOHUNDRED = 200;
    private static final int TWOHUNDREDFIFTY = 250;
    private static final int FOURTY = 40;
    private static final int THREE = 3;
    private static final int TWENTY = 20;
    private static final int FOUR = 4;
    private static final int SIX = 6;
    private static final int TEN = 10;
    private static final int FIFTY = 50;
    private static final int MAXIMUM = 5000000;

    // File and boolean variables for game state
    private static final File CWD = new File(System.getProperty("user.dir"));
    private static final File SAVED = join(CWD, "saved.txt");
    private static boolean MENUSCREEN;
    private static boolean N;
    private static boolean COLON;
    private static boolean WIN;
    private static boolean iSTRING;
    private static boolean FINISHED;

    // Seed and input variables
    private static String SEED;
    private static String INPUT;


    // Lists to store playable character and door coordinates
    private static ArrayList<Integer> playableCHARACTER;
    private static ArrayList<Integer> EXIT;

    // Constructor initializes game state variables
    public Engine() {
        COLON = false;
        WIN = false;
        iSTRING = false;
        FINISHED = false;
        MENUSCREEN = true;
        N = false;
        SEED = "";
        INPUT = "";
        TETILE = new TETile[WIDTH][HEIGHT];
        playableCHARACTER = new ArrayList<Integer>();
        EXIT = new ArrayList<Integer>();
    }
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public static void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char keys = toLowerCase(StdDraw.nextKeyTyped());
                pressKeys(keys);
            }
            generateMenu();
            if (!MENUSCREEN) {
                break;
            }
        }

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char keys = toLowerCase(StdDraw.nextKeyTyped());
                pressKeys(keys);
            }
            ter.renderFrame(TETILE);
            hudScreen();
            if (WIN) {
                displayWin();
            }
        }
    }

    /**
     * Method used for  testing the code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param remainingInput the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] interactWithInputString(String remainingInput) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        
        iSTRING = true;
        if (remainingInput.charAt(0) == 'l') {
            String saved = readContentsAsString(SAVED);
            String newer = saved + remainingInput.substring(1);
            return interactWithInputString(newer);
        }
        processInput(remainingInput);
        return TETILE;
    }

    /**
     * Main method to start the game
     */
    public static void main(String[] args) {
        Engine e = new Engine();
        interactWithKeyboard();


    }

    /**
     * Method to process input recursively. Presses keys based on the remaining input string.
     *
     * @param remainingInput The remaining input string to be processed.
     */
    private static void processInput(String remainingInput) {
        if (FINISHED) {
            return;
        }

        char curr = remainingInput.charAt(0);
        curr = toLowerCase(curr);
        pressKeys(curr);
        String restOfInput = remainingInput.substring(1);
        if (restOfInput.length() == 0) {
            FINISHED = true;
        }
        processInput(restOfInput);
    }

    /**
     * Method to generate and display the main menu.
     */
    private static void generateMenu() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        StdDraw.setFont(new Font("Arial", Font.BOLD, FOURTY));
        StdDraw.text(WIDTH / 2, HEIGHT * 2 / THREE, "PIKA RUN");

        StdDraw.setFont(new Font("Arial", Font.PLAIN, TWENTY));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - FOUR, "Replay Game (R)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - SIX, "Quit (Q)");

        if (N) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Arial", Font.PLAIN, TWENTY));
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Seed: " + SEED);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2, "(Press a series of number and S to start the game)");
        }

        StdDraw.show();
    }

    /**
     * Method to display the HUD (Heads-Up Display) screen.
     */
    private static void hudScreen() {
        int width = (int) StdDraw.mouseX();
        int height = (int) StdDraw.mouseY();
        String type = TETILE[width][height].description();
        Font wording = new Font("Arial", Font.PLAIN, TWENTY);

        StdDraw.setFont(wording);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, HEIGHT - 1, type);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy/MM/dd");
        LocalDateTime time1 = LocalDateTime.now();
        String time = formatter.format(time1);
        StdDraw.textLeft(TEN, HEIGHT - 1, time);

        StdDraw.show();
        StdDraw.pause(FIFTY);
    }

    /**
     * Method to display the win screen.
     */
    private static void displayWin() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        StdDraw.setFont(new Font("Arial", Font.BOLD, FOURTY));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "CONGRATS! YOU WIN!");
        StdDraw.show();
        StdDraw.pause(MAXIMUM);
    }

    /**
     * Method to process user input characters and execute corresponding actions.
     *
     * @param k The character representing the user's input.
     */
    private static void pressKeys(char k) {
        if (MENUSCREEN) {
            switch (k) {
                case 'n':
                    N = true;
                    INPUT = INPUT + k;
                    break;
                case 's':
                    createWorld();
                    INPUT = INPUT + k;
                    break;
                case 'l':
                    loadGame();
                    break;
                case 'r':
                    replayGame();
                    break;
                case 'q':
                    System.exit(0);
                    break;
                default:
                    if (Character.isDigit(k)) {
                        SEED = SEED + k;
                    }
                    INPUT = INPUT + k;
                    break;
            }
        } else {

            switch (k) {
                case 'w':
                case 's':
                case 'a':
                case 'd':
                    movements(k);
                    INPUT = INPUT + k;
                    break;
                case ':':
                    COLON = true;
                    break;
                case 'q':
                    if (COLON) {
                        saveGame();
                        if (!(iSTRING && !FINISHED)) {
                            System.exit(0);
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + k);
            }
        }
    }

    /**
     * Method to create the game world based on the provided seed.
     */
    private static void createWorld() {
        if (N) {
            long l = Long.parseLong(SEED);
            Room newRoom = new Room(l, WIDTH, HEIGHT);
            newRoom.emptyBoard(TETILE);
            newRoom.drawWorld(TETILE, TWOHUNDRED);
            setup();
            MENUSCREEN = false;
        } else {
            System.exit(0);
        }
    }

    /**
     * Method to generate a list of floor coordinates in the game world.
     *
     * @return A list of floor coordinates in the game world.
     */
    private static ArrayList<ArrayList<Integer>> floors() {
        ArrayList<ArrayList<Integer>> floors1 = new ArrayList();

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (TETILE[i][j].equals(Tileset.MY_FLOOR)) {
                    ArrayList<Integer> latestFloor = new ArrayList<>();
                    latestFloor.add(i);
                    latestFloor.add(j);
                    floors1.add(latestFloor);
                }
            }
        }
        return floors1;
    }

    /**
     * Method to set up the initial playable character and exit positions in the game world.
     */
    private static void setup() {
        ArrayList<ArrayList<Integer>> floors = floors();
        int[] playerCoords = new int[2];
        int[] gateCoords = new int[2];
        findFarthestPoints(floors, playerCoords, gateCoords);

        playableCHARACTER.add(playerCoords[0]);
        playableCHARACTER.add(playerCoords[1]);
        TETILE[playerCoords[0]][playerCoords[1]] = Tileset.MY_AVATAR;

        adjustGateLocation(gateCoords);
        TETILE[gateCoords[0]][gateCoords[1]] = Tileset.LOCKED_DOOR;
        EXIT.add(gateCoords[0]);
        EXIT.add(gateCoords[1]);
    }

    /**
     * Method to find the farthest points for the playable character and exit.
     *
     * @param points The list of floor coordinates in the game world.
     * @param player The array to store the playable character's coordinates.
     * @param gate   The array to store the exit's coordinates.
     */
    private static void findFarthestPoints(ArrayList<ArrayList<Integer>> points, int[] player, int[] gate) {
        double maxDistance = 0;
        for (ArrayList<Integer> p1 : points) {
            for (ArrayList<Integer> p2 : points) {
                double distance = Math.sqrt(Math.pow(p1.get(0) - p2.get(0), 2) + Math.pow(p1.get(1) - p2.get(1), 2));
                if (distance > maxDistance) {
                    maxDistance = distance;
                    player[0] = p1.get(0);
                    player[1] = p1.get(1);
                    gate[0] = p2.get(0);
                    gate[1] = p2.get(1);
                }
            }
        }
    }

    /**
     * Method to adjust the exit's location to be adjacent to a floor tile.
     *
     * @param coords The exit's coordinates to be adjusted.
     */
    private static void adjustGateLocation(int[] coords) {
        int gateWidth = coords[0];
        int gateHeight = coords[1];

        if (TETILE[gateWidth - 1][gateHeight].equals(Tileset.MY_FLOOR)) {
            gateWidth += 1;
        } else if (TETILE[gateWidth + 1][gateHeight].equals(Tileset.MY_FLOOR)) {
            gateWidth -= 1;
        } else if (TETILE[gateWidth][gateHeight - 1].equals(Tileset.MY_FLOOR)) {
            gateHeight += 1;
        } else if (TETILE[gateWidth][gateHeight + 1].equals(Tileset.MY_FLOOR)) {
            gateHeight -= 1;
        }
        coords[0] = gateWidth;
        coords[1] = gateHeight;
    }

    /**
     * Method to save the game state to a file.
     */
    private static void saveGame() {
        SAVED.delete();
        try {
            SAVED.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeContents(SAVED, INPUT);
    }

    /**
     * Method to load a saved game state from a file.
     */
    private static void loadGame() {
        String userInput = readContentsAsString(SAVED);
        interactWithInputString(userInput);
    }

    /**
     * Method to handle player movements in the game world.
     *
     * @param characterChoice The character representing the chosen movement direction.
     */
    private static void movements(char characterChoice) {
        int position1 = playableCHARACTER.get(0);
        int position2 = playableCHARACTER.get(1);
        int next1 = position1, next2 = position2;

        switch (characterChoice) {
            case 'w':
                next2 += 1;
                break;
            case 's':
                next2 -= 1;
                break;
            case 'd':
                next1 += 1;
                break;
            case 'a':
                next1 -= 1;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + characterChoice);
        }

        if (TETILE[next1][next2].equals(Tileset.MY_FLOOR)
                || TETILE[next1][next2].equals(Tileset.LOCKED_DOOR)) {

            TETILE[position1][position2] = Tileset.MY_FLOOR;

            if (TETILE[next1][next2].equals(Tileset.LOCKED_DOOR)) {
                WIN = true;
            }

            TETILE[next1][next2] = Tileset.MY_AVATAR;
            updateCharacter(next1, next2);
        }
    }

    /**
     * Method to replay a saved game state.
     */
    private static void replayGame() {
        String replayMovements = readContentsAsString(SAVED);
        String emptyString = "";
        for (int i = 0; i < replayMovements.length(); i++) {
            char latestPos = replayMovements.charAt(0);
            emptyString += latestPos;
            replayMovements = replayMovements.substring(1);
            if (latestPos == 's') {
                break;
            }
        }
        INPUT = emptyString;
        N = true;
        SEED = emptyString.substring(1, emptyString.length() - 1);
        createWorld();

        ter.renderFrame(TETILE);
        while (replayMovements.length() != 0) {
            char latestPos = replayMovements.charAt(0);

            int width = playableCHARACTER.get(0);
            int height = playableCHARACTER.get(1);

            pressKeys(latestPos);
            ter.renderFrame(TETILE);
            try {
                Thread.sleep(TWOHUNDREDFIFTY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            replayMovements = replayMovements.substring(1);
        }
        interactWithKeyboard();
    }

    /**
     * Method to update the playable character's position.
     *
     * @param x The new x-coordinate of the playable character.
     * @param y The new y-coordinate of the playable character.
     */
    private static void updateCharacter(int x, int y) {
        playableCHARACTER = new ArrayList<Integer>();
        playableCHARACTER.add(x);
        playableCHARACTER.add(y);
    }


}
