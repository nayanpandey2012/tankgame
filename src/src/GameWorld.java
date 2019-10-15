
package src;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import static javax.imageio.ImageIO.read;


public class GameWorld extends JPanel {


    private static final int ScreenHeight = 672;
    private static final int ScreenWidth = 960;
    static final int FullScreenHeight = 1920; // 1920/32 = 60 walls high
    static final int FullScreenWidth = 1536;  // 1536/32 = 48 walls wide
    private final int tank1PositionX = 200;
    private final int tank1PositionY = 150;
    private final int tank1PositionAngle = 0;
    private final int tank2PositionX = 1200;
    private final int tank2PositionY = 1700;
    private final int tank2PositionAngle = 180;
    private BufferedImage FullScreen;
    private Graphics2D buffer;
    private JFrame jf;
    private Tank t1;
    private Tank t2;
    private static BufferedImage player1WinImg;
    private static BufferedImage player2WinImg;
    private static BufferedImage menuImage;
    private static BufferedImage helpImage;
    private boolean player1Won = false;
    private boolean player2Won = false;

    private CollisionHandler CH;
    static private Menu m;

    void addGame_object(GameObject obj) { //package private
        this.game_objects.add(obj);
    }

    private ArrayList<GameObject> game_objects = new ArrayList<>();
    private int Player1_num_lives = 2;
    private int Player2_num_lives = 2;

    enum Game_State {
        menu, game, help, exit,
    }
    //Creating Default State
    static Game_State state = Game_State.menu;

    public static void main(String[] args) {
        Thread x;
        GameWorld trex = new GameWorld();
        trex.CH = new CollisionHandler();
        trex.init();
        try {


            while (true) {

                trex.repaint();

                if (GameWorld.state == Game_State.game) {

                    for (int i = 0; i < trex.game_objects.size(); i++) {
                        if (trex.game_objects.get(i) instanceof Bullet) {
                            if (((Bullet) trex.game_objects.get(i)).getIsInactive()) { // is inactive
                                trex.game_objects.remove(i);

                                i--;
                            } else {
                                trex.game_objects.get(i).update();
                            }
                        }
                        if (trex.game_objects.get(i) instanceof Tank) {
                            if (((Tank) trex.game_objects.get(i)).getTankLife() == 0) { //tank is destroyed
                                if ((((Tank) trex.game_objects.get(i)).getTag()).equals("Tank1")) { //player 1 tank is destroyed
                                    if (trex.Player1_num_lives > 1) { //game continues (player 1 has remaining lives)
                                        trex.Player1_num_lives--;

                                        //respawning
                                        ((Tank) trex.game_objects.get(i)).setTankLife(100);  //replenishing health to full health
                                        trex.game_objects.get(i).setX(trex.tank1PositionX);
                                        trex.game_objects.get(i).setY(trex.tank1PositionY);
                                        trex.game_objects.get(i).setAngle(trex.tank1PositionAngle);


                                    } else { // player 2 has won
                                        trex.Player1_num_lives = 0;
                                        trex.player2Won = true;
                                        break;
                                    }
                                }
                                if ((((Tank) trex.game_objects.get(i)).getTag()).equals("Tank2")) { //player 2 tank is destroyed
                                    if (trex.Player2_num_lives > 1) { //game continues (player 2 has remaining lives)
                                        trex.Player2_num_lives--;


                                        ((Tank) trex.game_objects.get(i)).setTankLife(100);
                                        trex.game_objects.get(i).setX(trex.tank2PositionX);
                                        trex.game_objects.get(i).setY(trex.tank2PositionY);
                                        trex.game_objects.get(i).setAngle(trex.tank2PositionAngle);


                                    } else { //player 1 has won
                                        trex.Player2_num_lives = 0;
                                        trex.player1Won = true;
                                        break;
                                    }
                                }

                            }
                        }
                        if (((trex.game_objects.get(i) instanceof BreakableWalls) && ((BreakableWalls) trex.game_objects.get(i)).getWallLife() == 0)) {
                            trex.game_objects.remove(i);
                        }
                    }

                    trex.game_objects = trex.CH.HandleCollisions(trex.game_objects);  //handling collisions


                    trex.t1.update();
                    trex.t2.update();

                    Thread.sleep(1000 / 144);
                } else if (state == Game_State.exit) { //exiting
                    trex.jf.dispose();
                    System.exit(0);
                }
            }
        } catch (InterruptedException ignored) {

        }

    }


    private void init() {
        this.jf = new JFrame("***Tank Wars***");
        this.FullScreen = new BufferedImage(GameWorld.FullScreenWidth, GameWorld.FullScreenHeight, BufferedImage.TYPE_INT_RGB);
        BufferedImage tankImage = null, bulletImage, backgroundImage, unbreakableWallImage, breakableWallImage, explodeImage, largeExplosionImage;

        try {
            BufferedImage tmp;

            tankImage = read(getClass().getResource("/resources/tank1.png"));

            unbreakableWallImage = read(getClass().getResource("/resources/unbreakable_wall.gif"));
            Wall.set_unbreakable_wall_img(unbreakableWallImage);

            backgroundImage = read(getClass().getResource("/resources/Background.bmp"));
            Wall.setBackground_img(backgroundImage);

            breakableWallImage = read(getClass().getResource("/resources/breakable_wall.gif"));
            BreakableWalls.set_breakable_wall_img(breakableWallImage);

            bulletImage = read(getClass().getResource("/resources/Weapon.gif"));
            Bullet.setBufferedImage(bulletImage); //setting the bullet image

            explodeImage = read(getClass().getResource("/resources/small_explode.gif"));
            Bullet.setExplosionImage(explodeImage);

            largeExplosionImage = read(getClass().getResource("/resources/large_explode.gif"));
            Bullet.setLargeExplosionImage(largeExplosionImage);

            GameWorld.menuImage = read(getClass().getResource("/resources/Menu_page.PNG"));
            GameWorld.helpImage = read(getClass().getResource("/resources/help.jpg"));

            GameWorld.player1WinImg = read(getClass().getResource("/resources/player1_wins.jpg"));
            GameWorld.player2WinImg = read(getClass().getResource("/resources/player2_wins.jpg"));

            PowerUp.setHealth_img(read(getClass().getResource("/resources/tankLife_potion.gif")));
            PowerUp.setSpeed_img(read(getClass().getResource("/resources/run.png")));


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        //tanks
        t1 = new Tank(tank1PositionX, tank1PositionY, 0, 0, tank1PositionAngle, tankImage);
        t1.setTag("Tank1");
        t2 = new Tank(tank2PositionX, tank2PositionY, 0, 0, tank2PositionAngle, tankImage);
        t2.setTag("Tank2");

        m = new Menu();

        //background
        for (int i = 0; i < FullScreenWidth; i = i + 250) {
            for (int j = 0; j < FullScreenHeight; j = j + 250) {
                game_objects.add(new Wall(i, j, true)); // the true is denote its a background(allows for linking proper images)
            }
        }

        int[] new_map_array = { //width is 48, height is 60
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 2, 2, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 2, 2, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};


        int column = 0; //left to right(0->24)
        int entire_index = 0;

        for (int i = 0; i < 60; i++) { //loops up and down(entire horizontal row)

            for (int j = 0; j < 48; j++) {
                if (column == 60) { //resetting column
                    column = 0;
                }
                int temp_val = new_map_array[entire_index]; //holds value in array
                if (temp_val != 0) {
                    if (temp_val == 2) { // breakable wall
                        game_objects.add(new BreakableWalls(j * 32, i * 32));
                    } else {
                        game_objects.add(new Wall(j * 32, i * 32, false));
                    }
                }
                column++;
                entire_index++;
            }
        }


        game_objects.add(t1); //adding tank 1
        t1.setGW(this);
        game_objects.add(t2); //adding tank 2
        t2.setGW(this);


        PowerUp power1 = new PowerUp(780, 750, true, false); //making a health PowerUp
        PowerUp power2 = new PowerUp(682, 750, true, false); //making a health PowerUp
        game_objects.add(power1);
        game_objects.add(power2);
        PowerUp power3 = new PowerUp(730, 852, false, true);  //making a speed PowerUp
        game_objects.add(power3);

        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_Q); //adding control
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);

        this.jf.setLayout(new BorderLayout());
        this.jf.add(this); //adding the GameWorld to the Jframe


        this.jf.addKeyListener(tc1);
        this.jf.addKeyListener(tc2);
        this.addMouseListener(new MouseReader());

        this.jf.setSize(GameWorld.ScreenWidth + 20, GameWorld.ScreenHeight + 40);
        this.jf.setResizable(false);
        jf.setLocationRelativeTo(null);

        this.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jf.setVisible(true);


    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        buffer = FullScreen.createGraphics();
        super.paintComponent(g2);


        if (GameWorld.state == Game_State.menu) {       //menu state
            (g).drawImage(menuImage, 0, 0, ScreenWidth + 2, ScreenHeight, null);
            m.drawImage(g);
        } else if (GameWorld.state == Game_State.help) {  //help state
            (g).drawImage(helpImage, 0, 0, ScreenWidth + 2, ScreenHeight, null);
        } else if (GameWorld.state == Game_State.game) {  //game state


            for (int i = 0; i < game_objects.size(); i++) {

                game_objects.get(i).drawImage(buffer);

            }

            //these 4 variables are not required per say, but make reading the code sections below easier
            int player1_x_Coord = t1.getX();
            int player2_x_Coord = t2.getX();
            int player1_y_Coord = t1.getY();
            int player2_y_Coord = t2.getY();


            if (player1_x_Coord < ScreenWidth / 4) {
                player1_x_Coord = ScreenWidth / 4;
            }
            if (player2_x_Coord < ScreenWidth / 4) {
                player2_x_Coord = ScreenWidth / 4;
            }
            if (player1_x_Coord > FullScreenWidth - ScreenWidth / 4) {
                player1_x_Coord = FullScreenWidth - ScreenWidth / 4;
            }
            if (player2_x_Coord > FullScreenWidth - ScreenWidth / 4) {
                player2_x_Coord = FullScreenWidth - ScreenWidth / 4;
            }
            if (player1_y_Coord < ScreenHeight / 2) {
                player1_y_Coord = ScreenHeight / 2;
            }
            if (player2_y_Coord < ScreenHeight / 2) {
                player2_y_Coord = ScreenHeight / 2;
            }
            if (player1_y_Coord > FullScreenHeight - ScreenHeight / 2) {
                player1_y_Coord = FullScreenHeight - ScreenHeight / 2;
            }
            if (player2_y_Coord > FullScreenHeight - ScreenHeight / 2) {
                player2_y_Coord = FullScreenHeight - ScreenHeight / 2;
            }


            BufferedImage left_split_screen = FullScreen.getSubimage(player1_x_Coord - ScreenWidth / 4, player1_y_Coord - ScreenHeight / 2, ScreenWidth / 2, ScreenHeight);
            BufferedImage right_split_screen = FullScreen.getSubimage(player2_x_Coord - ScreenWidth / 4, player2_y_Coord - ScreenHeight / 2, ScreenWidth / 2, ScreenHeight);

            g2.drawImage(left_split_screen, 0, 0, null);
            g2.drawImage(right_split_screen, ScreenWidth / 2 + 5, 0, null); //the +5 is to have a gap between the split screens

            g2.drawImage(FullScreen, ScreenWidth / 2 - GameWorld.FullScreenWidth / 6 / 2, ScreenHeight - GameWorld.FullScreenHeight / 6, GameWorld.FullScreenWidth / 6, FullScreenHeight / 6, null);
            g2.setFont(new Font("SansSerif", Font.BOLD, 30));
            g2.setColor(Color.WHITE);
            g2.drawString("Player1 lives: " + this.Player1_num_lives, 10, 28);
            g2.drawString("Player2 lives: " + this.Player2_num_lives, ScreenWidth / 2 + 10, 28);


            g2.drawString("[", 10, 58);
            g2.drawString("[", ScreenWidth / 2 + 10, 58);
            g2.drawString("]", 230, 58);
            g2.drawString("]", ScreenWidth / 2 + 230, 58);
            g2.setColor(Color.red);


            g2.fillRect(25, 40, 2 * t1.getTankLife(), 20);
            g2.fillRect(ScreenWidth / 2 + 25, 40, 2 * t2.getTankLife(), 20);


            if (player1Won) {
                g2.drawImage(player1WinImg, 0, 0, ScreenWidth + 10, ScreenHeight, null);
            }
            if (player2Won) {
                g2.drawImage(player2WinImg, 0, 0, ScreenWidth + 10, ScreenHeight, null);
            }

        }
    }


}
