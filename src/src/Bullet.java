package src;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class Bullet extends GameObject {
    private String bulletOwner; //which tank owns the bullet?
    private boolean isInActive = false;
    private boolean smallExplosion = true; // this variable controls if we print the large explosion(for collisions with tank) or the small explosion for collisions with walls
    boolean collided = false; //whether the bullet should explode
    static private BufferedImage bulletImage; //24x24
    static private BufferedImage smallExplosionImage;
    static private BufferedImage largeExplosionImage;
    private int iterations_since_collided = 0;

    String getOwner() {
        return this.bulletOwner;
    }

    void setOwner(String owner) {
        this.bulletOwner = owner;
    }

    boolean getIsInactive() {
        return this.isInActive;
    }

    void setIsInactive(boolean val) {
        this.isInActive = val;
    }

    static void setBufferedImage(BufferedImage img) { // used to set the static bullet image
        bulletImage = img;
    }

    static void setExplosionImage(BufferedImage exp) { // used to set the explosion image
        smallExplosionImage = exp;
    }

    static void setLargeExplosionImage(BufferedImage e){
        largeExplosionImage = e;
    }

    void setSmallExplosion(boolean val){ //if val is true, we use the small explosion image, if val is false, we use the big explosion image
        this.smallExplosion = val;
    }
    boolean getSmallExplosion(){
        return this.smallExplosion;
    }

    Bullet(int x, int y, int angle) {
        this.x = x;
        this.y = y;
        this.vx = (int) Math.round(3 * Math.cos(Math.toRadians(angle)));
        this.vy = (int) Math.round(3 * Math.sin(Math.toRadians(angle)));
        this.angle = angle;
        this.my_rectangle = new Rectangle(x, y, bulletImage.getWidth(), bulletImage.getHeight());
    }

    public void update() {
        if (!collided) {  //if its not collided, keep moving, else dont move the bounds
            this.x = x + vx;
            this.y = y + vy;
            this.checkBorder(); //this allows us to mark the bullet for deletion if it is out of bounds
        } else {
            iterations_since_collided++;
        }
        this.my_rectangle.setLocation(x,y);
    }


    public void drawImage(Graphics2D g2d) {

        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), bulletImage.getWidth() / 2.0, bulletImage.getHeight() / 2.0);

        if (collided && smallExplosion) {

            g2d.drawImage(smallExplosionImage, rotation, null);

            if (iterations_since_collided >= 5) { //this allows us to make sure that there is enough time to see the bullet exploding
                this.isInActive = true;
            }

        }else if(collided && !smallExplosion){ // large explosion
            g2d.drawImage(largeExplosionImage, rotation, null);

            if (iterations_since_collided >= 5) { //this allows us to make sure that there is enough time to see the bullet exploding
                this.isInActive = true;
            }
        } else {
            g2d.drawImage(bulletImage, rotation, null);
        }
    }

    public void collision() {
        collided = true;
    }

    private void checkBorder() { //this allows us to mark the bullet as inactive(so it can later be removed) if out of map bounds
        //variables used to identify limits
        int left_limit = 30;
        if (x < left_limit) {
            this.isInActive = true;
        }
        int right_limit = GameWorld.FullScreenWidth - 65;
        if (x >= right_limit) {
            this.isInActive = true;
        }
        int lower_limit = 40;
        if (y < lower_limit) {
            this.isInActive = true;
        }
        int upper_limit = GameWorld.FullScreenHeight - 60;
        if (y >= upper_limit) {
            this.isInActive = true;
        }
    }
}
