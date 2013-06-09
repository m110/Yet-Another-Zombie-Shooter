package org.m110.shooter.entities.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import org.m110.shooter.Shooter;
import org.m110.shooter.core.Font;
import org.m110.shooter.core.timers.CountdownTimer;
import org.m110.shooter.core.timers.IntervalTimer;
import org.m110.shooter.entities.Entity;
import org.m110.shooter.entities.EntityProto;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class CombatEntity extends Entity {

    private final EntityProto proto;
    private final int points;

    private int pointsEarned = 0;
    protected final CountdownTimer pointsEarnedTimer;

    private Entity victim = null;

    // Combat
    private final IntervalTimer attackTimer;

    public CombatEntity(EntityProto proto, TextureRegion texture, Array<TextureRegion> fleshTextures, String name,
                        float startX, float startY, Sound attackSound, Sound damageSound, Sound deathSound) {
        super(texture, fleshTextures, name, startX, startY);
        this.proto = proto;
        this.points = proto.points;

        setBaseHealth(proto.health);
        setVelocity(proto.velocity);

        setAttackSound(attackSound);
        setDamageSound(damageSound);
        setDeathSound(deathSound);

        attackTimer = new IntervalTimer(proto.attackInterval);
        pointsEarnedTimer = new CountdownTimer(2.0f);
        pointsEarnedTimer.disable();
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (pointsEarned > 0 && !pointsEarnedTimer.ready()) {
            BitmapFont font = Font.large;
            font.setColor(1.0f, 1.0f, 1.0f, Math.min(Math.max(pointsEarnedTimer.getTimeLeft(), 0.0f), 1.0f));
            font.draw(batch, ""+pointsEarned, getWorldX() - font.getBounds(""+pointsEarned).width / 2.0f,
                    getY() + getHeight() + getHeight() *
                    (2.0f - pointsEarnedTimer.getTimeLeft()));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (inCombat()) {
            if (victim.isDead()) {
                stopCombat();
            }
        }

        attackTimer.update(delta);
        pointsEarnedTimer.update(delta);
    }

    @Override
    public void die() {
        super.die();
        pointsEarnedTimer.reset();
    }

    @Override
    public boolean isInMeleeRange(Entity who) {
        return inCombat() && super.isInMeleeRange(who);

    }

    public void dealDamage(Entity victim) {
        dealDamage(victim, getDamage());
    }

    public void dealDamage(Entity victim, int damage) {
        if (attackTimer.ready()) {
            playAttackSound();
            victim.takenDamage(damage, this);
            attackTimer.reset();
        }
    }

    public int getDamage() {
        if (proto.minDamage == proto.maxDamage) {
            return proto.minDamage;
        }

        return MathUtils.random(proto.minDamage, proto.maxDamage);
    }

    public void attackChase() {
        if (isInMeleeRange(victim)) {
            dealDamage(victim);
        } else {
            moveChase();
        }
    }

    public void moveChase() {
        moveChase(victim.getWorldX(), victim.getWorldY());
    }

    public void moveChase(float x, float y) {
        lookAt(x, y);
        moveForward();
    }

    public int getPoints() {
        return points;
    }

    public Entity getVictim() {
        return victim;
    }

    public void startCombat(Entity victim) {
        this.victim = victim;
    }

    public void stopCombat() {
        victim = null;
    }

    public boolean inCombat() {
        return victim != null;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
}
