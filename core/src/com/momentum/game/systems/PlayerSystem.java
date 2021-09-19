package com.momentum.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.momentum.game.components.Collider;
import com.momentum.game.components.Player;
import com.momentum.game.components.Renderable;
import com.momentum.game.components.Transform;

public class PlayerSystem extends EntitySystem {

    private final Camera camera;
    private Iterable<Entity> players;
    private Iterable<Entity> colliders;

    public PlayerSystem(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        players = engine.getEntitiesFor(Family.all(Player.class, Renderable.class, Transform.class).get());
        colliders = engine.getEntitiesFor(Family.all(Collider.class, Renderable.class, Transform.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity player : players) {
            processEntity(player, deltaTime);
        }
    }

    private void processEntity(Entity entity, float deltaTime) {
        Player player = Player.mapper.get(entity);
        Transform transform = Transform.mapper.get(entity);
        Renderable renderable = Renderable.mapper.get(entity);

        // Process drag action
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

        // Calculate position increment
        Vector2 delta = player.direction.cpy().scl(player.speed * deltaTime);

        // Check collision
        Rectangle playerBounds = new Rectangle(transform.position.x, transform.position.y,
                renderable.texture.getRegionWidth(), renderable.texture.getRegionHeight());
        for (Entity collider : colliders) {
            Transform colTransform = Transform.mapper.get(collider);
            Renderable colRenderable = Renderable.mapper.get(collider);
            Rectangle colliderBounds = new Rectangle(colTransform.position.x, colTransform.position.y,
                    colRenderable.texture.getRegionWidth(), colRenderable.texture.getRegionHeight());
            if (collides(delta, playerBounds, colliderBounds)) {
                System.out.println("Collision!");
                player.direction.setZero();
            }
        }

        transform.position.mulAdd(delta, player.speed * deltaTime);
    }

    private Vector3 getWorldInputCoordinates() {
        return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    private boolean collides(Vector2 delta, Rectangle rectA, Rectangle rectB) {
        boolean collides = false;

        // Horizontal collision
        if (rectA.x + rectA.width + delta.x > rectB.x &&
                rectA.x + delta.x < rectB.x + rectB.width &&
                rectA.y + rectA.height > rectB.y &&
                rectA.y < rectB.y + rectB.height
        ) {
            // TODO modify delta
            collides = true;
        }

        // Vertical collision
        if (rectA.x + rectA.width > rectB.x &&
                rectA.x < rectB.x + rectB.width &&
                rectA.y + rectA.height + delta.y > rectB.y &&
                rectA.y + delta.y < rectB.y + rectB.height
        ) {
            // TODO modify delta
            collides = true;
        }
        return collides;
    }
}
