package org.m110.shooter.screens.menu;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public class MenuItem {

    private final String caption;

    public MenuItem(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public void action() {}
}
