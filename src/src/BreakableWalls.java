package src;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BreakableWalls extends Wall {
    private int wallLife = 2;
    private static BufferedImage breakableWallImage; //32x32
    private boolean dead = false;

    BreakableWalls(int x, int y) {
        this.x = x;
        this.y = y;
        this.my_rectangle = new Rectangle(x, y, breakableWallImage.getWidth(), breakableWallImage.getHeight());
    }



    private void breakWall(int val) { //private access because only the BreakableWalls should be able to removeHealth(in its collision method)
        if (wallLife - val < 0) {
            wallLife = 0; //BreakableWalls died
            dead = true;
        } else {
            wallLife -= val;
        }
    }

    int getWallLife() {
        return this.wallLife;
    }

    boolean isDead() {
        return dead;
    }

    void setDead(boolean dead) {
        this.dead = dead;
    }

    static void set_breakable_wall_img(BufferedImage image) {
        BreakableWalls.breakableWallImage = image;
    }

    public void update() {

    }

    public void collision() {
        this.breakWall(1);

    }

    public void drawImage(Graphics2D g2d) {

        if (!dead) {
            g2d.drawImage(breakableWallImage, x, y, null);
        }

    }


}
