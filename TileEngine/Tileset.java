package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * 
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");

    public static final TETile MY_AVATAR = new TETile('@', Color.white, Color.green, "you",
            "/Users/enmanuel/su23-s155/proj3/byow/textures/pngegg-9.png");
    public static final TETile MY_WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall","/Users/enmanuel/su23-s155/proj3/byow/textures/d51a33b68cd3fa8-2.png");
    public static final TETile MY_FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor","/Users/enmanuel/su23-s155/proj3/byow/textures/grasstop.png");
    public static final TETile MY_NOTHING = new TETile(' ', Color.black, Color.black, "nothing",
            "/Users/enmanuel/su23-s155/proj3/byow/textures/Ocean_SpriteSheet-2.png");

}


