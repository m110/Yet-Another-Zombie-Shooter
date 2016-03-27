package org.m110.shooter.core;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class Stats {

    private int kills = 0;

    public Stats() {

    }

    public void addKill() {
        kills++;
    }

    public int getKills() {
        return kills;
    }
}
