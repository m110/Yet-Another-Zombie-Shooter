package org.m110.shooter.weapons.magazines;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class ShotgunMagazine extends Magazine {

    private int allBullets;
    private int maxAllBullets = 60;

    public ShotgunMagazine(int maxBullets, int bullets, int allBullets) {
        super(MagazineSlot.SHOTGUN, maxBullets, bullets);
        this.allBullets = allBullets;
    }

    public int getAllBullets() {
        return allBullets;
    }

    public boolean loadBullet() {
        if (allBullets > 0 && !isFull()) {
            allBullets--;
            addBullet();
            return true;
        } else {
            return false;
        }
    }

    public void addAllBullets(int allBullets) {
        if (allBullets == 0) {
            allBullets = getMaxBullets();
        }

        this.allBullets += allBullets;
        if (this.allBullets > maxAllBullets) {
            this.allBullets = maxAllBullets;
        }
    }
}
