package src;

import java.awt.*;
import java.util.ArrayList;

class
CollisionHandler {

    //default constructor

    ArrayList<GameObject> HandleCollisions(ArrayList<GameObject> game_objects) {

        for (int i = 0; i < game_objects.size(); i++) {

            for (int j = i; j < game_objects.size(); j++) {
                GameObject objectPositioni = game_objects.get(i);
                GameObject objectPositionj = game_objects.get(j);

                if (i != j) {


                    if (objectPositioni instanceof Bullet && objectPositionj instanceof Tank && !(((Bullet) objectPositioni).getOwner().equals(((Tank) objectPositionj).getTag())) && !((Bullet) objectPositioni).collided) { // we make sure the bullet hasn't already collided(so we don't call collisions again)
                        if (objectPositioni.my_rectangle.intersects(objectPositionj.my_rectangle)) {
                            objectPositioni.collision();
                            ((Bullet) objectPositioni).setSmallExplosion(false); // we use large explosion image
                            objectPositionj.collision();
                        }


                    }
                    if (objectPositioni instanceof Tank && objectPositionj instanceof Bullet && !((Bullet) objectPositionj).getOwner().equals(((Tank) objectPositioni).getTag()) && !((Bullet) objectPositionj).collided) {
                        if (objectPositioni.my_rectangle.intersects(objectPositionj.my_rectangle)) {
                            ((Bullet) objectPositionj).setSmallExplosion(false); // we use large explosion image
                            objectPositionj.collision();
                            objectPositioni.collision();
                        }

                    }

                    if (((objectPositionj instanceof Bullet && objectPositioni instanceof BreakableWalls && !((Bullet) objectPositionj).collided))) {
                        if (objectPositioni.my_rectangle.intersects(objectPositionj.my_rectangle)) {
                            objectPositionj.collision();
                            objectPositioni.collision();
                        }

                    }


                    if (objectPositioni instanceof Tank && objectPositionj instanceof BreakableWalls) {
                        Rectangle r1 = ((Tank) objectPositioni).getOffsetBounds();
                        if (r1.intersects(objectPositionj.my_rectangle)) {

                            ((Tank) objectPositioni).setdont_move(true);

                        }

                    }

                    if (objectPositioni instanceof BreakableWalls && objectPositionj instanceof Tank) {

                        Rectangle r2 = ((Tank) objectPositionj).getOffsetBounds();
                        if (r2.intersects(objectPositioni.my_rectangle)) {  //intersection occurs

                            ((Tank) objectPositionj).setdont_move(true);

                        }

                    }

                    if (objectPositioni instanceof Tank && objectPositionj instanceof PowerUp) {
                        if (objectPositioni.my_rectangle.intersects(objectPositionj.my_rectangle)) {
                            if (((PowerUp) objectPositionj).isHealthBoost) {
                                ((Tank) objectPositioni).setTankLife(100);
                                System.out.println("health power up picked up");
                                game_objects.remove(j);

                            }
                            if (((PowerUp) objectPositionj).isSpeedBoost) {
                                ((Tank) objectPositioni).setSpeedBoost(System.currentTimeMillis()); //setting speed boost pick up time
                                ((Tank) objectPositioni).setSpeed_boosted(true); //turning on flag to denote that tank is speed boosted
                                System.out.println("Speed boost power up picked up");
                                game_objects.remove(j);
                            }
                        }

                    }
                }

            }
        }


        return game_objects;
    }
}
