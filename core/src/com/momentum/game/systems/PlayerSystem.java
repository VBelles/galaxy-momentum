package com.momentum.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.momentum.game.components.Player;
import com.momentum.game.components.Transform;

public class PlayerSystem extends IteratingSystem {

    private final ComponentMapper<Player> playerMapper = ComponentMapper.getFor(Player.class);
    private final ComponentMapper<Transform> transformMapper = ComponentMapper.getFor(Transform.class);

    public PlayerSystem() {
        super(Family.one(Player.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Player player = playerMapper.get(entity);
        Transform transform = transformMapper.get(entity);
        player.direction.setZero();
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.direction.x -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.direction.x += 1f;
        }

        transform.position.mulAdd(player.direction, player.speed * deltaTime);

    }
}
