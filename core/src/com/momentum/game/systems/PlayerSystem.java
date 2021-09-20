package com.momentum.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.momentum.game.DefaultFilter;
import com.momentum.game.components.Collider;
import com.momentum.game.components.Goal;
import com.momentum.game.components.Player;
import com.momentum.game.components.Transform;

public class PlayerSystem extends IteratingSystem {

    private final Camera camera;
    private final World<Entity> world;

    public PlayerSystem(Camera camera, World<Entity> world) {
        super(Family.all(Player.class, Transform.class, Collider.class).get());
        this.camera = camera;
        this.world = world;
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        Player player = Player.mapper.get(entity);
        Transform transform = Transform.mapper.get(entity);
        Collider collider = Collider.mapper.get(entity);

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
        transform.position.mulAdd(delta, player.speed * deltaTime);

        // Try to move in world (checks collision with other collides)
        Response.Result result = world.move(collider.item, transform.position.x, transform.position.y,
                DefaultFilter.instance);

        if (!result.projectedCollisions.isEmpty()) {
            for (@SuppressWarnings("rawtypes") Item item : result.projectedCollisions.others) {
                Entity collidedEntity = ((Entity) item.userData);
                Goal goal = Goal.mapper.get(collidedEntity);
                if (goal != null) {
                    goal.achieved = true;
                }
            }
        }

        // Update transform position given world position
        Rect rect = world.getRect(collider.item);
        transform.position.set(rect.x, rect.y);
    }

    private Vector3 getWorldInputCoordinates() {
        return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }
}
