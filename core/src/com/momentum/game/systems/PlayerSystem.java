package com.momentum.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.momentum.game.components.Player;
import com.momentum.game.components.Transform;

public class PlayerSystem extends IteratingSystem {

    private final ComponentMapper<Player> playerMapper = ComponentMapper.getFor(Player.class);
    private final ComponentMapper<Transform> transformMapper = ComponentMapper.getFor(Transform.class);
    private final Camera camera;

    public PlayerSystem(Camera camera) {
        super(Family.one(Player.class).get());
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Player player = playerMapper.get(entity);
        Transform transform = transformMapper.get(entity);
        if (Gdx.input.justTouched()) {
            Vector3 worldCoordinates = getWorldInputCoordinates();
            player.startDrag.set(worldCoordinates.x, worldCoordinates.y);
            player.dragging = true;
        } else if (!Gdx.input.isTouched() && player.dragging) {
            Vector3 worldCoordinates = getWorldInputCoordinates();
            player.direction
                    .set(player.startDrag)
                    .sub(worldCoordinates.x, worldCoordinates.y)
                    .nor();
            player.dragging = false;
        }
        transform.position.mulAdd(player.direction, player.speed * deltaTime);
    }

    private Vector3 getWorldInputCoordinates() {
        return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }
}
