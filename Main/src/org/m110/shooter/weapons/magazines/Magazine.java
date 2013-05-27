package org.m110.shooter.weapons.magazines;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class Magazine {

    private final MagazineSlot slot;
    private final int maxBullets;
    private int bullets;

    public Magazine(MagazineSlot slot, int maxBullets, int bullets) {
        this.slot = slot;
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

    public MagazineSlot  getSlot() {
        return slot;
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

    public void addBullet() {
        if (bullets < maxBullets) {
            bullets++;
        }
    }

    public float getBulletsPercent() {
        return (float)bullets / maxBullets;
    }
}
