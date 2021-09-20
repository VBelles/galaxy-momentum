package com.momentum.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Player implements Component, Pool.Poolable {

    public Vector2 velocity = new Vector2();
    public Vector2 acceleration = new Vector2();

    @Override
    public void reset() {
        velocity.setZero();
        acceleration.setZero();
    }

    public Player setVelocity(Vector2 velocity){
        this.velocity = velocity;
        return this;
    }

    public Player setAcceleration(Vector2 acceleration){
        this.acceleration = acceleration;
        return this;
    }

    public static final ComponentMapper<Player> mapper = ComponentMapper.getFor(Player.class);
}
