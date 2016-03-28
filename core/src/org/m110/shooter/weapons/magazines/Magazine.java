package org.m110.shooter.weapons.magazines;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Magazine {

    private final int maxBullets;
    private int bullets;

    public Magazine(int maxBullets, int bullets) {
        this.maxBullets = maxBullets;

        if (bullets > maxBullets) {
            this.bullets = maxBullets;
        } else {
            this.bullets = bullets;
        }
    }

    public int getMaxBullets() {
        return maxBullets;
    }

    public int getBullets() {
        return bullets;
    }

    public boolean isEmpty() {
        return bullets <= 0;
    }

    public boolean isFull() {
        return bullets >= maxBullets;
    }

    public void takeBullet() {
        if (bullets > 0) {
            bullets--;
        }
    }

    public boolean addBullets(int amount) {
        if (bullets == maxBullets) {
            return false;
        } else {
            if (bullets + amount < maxBullets) {
                bullets += amount;
            } else {
                bullets = maxBullets;
            }
            return true;
        }
    }

    public void setBullets(int bullets) {
        if (bullets > maxBullets) {
            this.bullets = maxBullets;
        } else if (bullets > 0) {
            this.bullets = bullets;
        } else {
            this.bullets = 0;
        }
    }

    public float getBulletsPercent() {
        return (float)bullets / maxBullets;
    }
}
