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

        //Get movement that happenned during last frame (not counting collisions)
        Vector2 deltaMovement = new Vector2(
                (player.velocity.x + ((0.5f) * player.acceleration.x * deltaTime)) * deltaTime,
                (player.velocity.y + ((0.5f) * player.acceleration.y * deltaTime)) * deltaTime
        );

        //reset acceleration
        player.setAcceleration(Vector2.Zero);

        //if there are gravity points the touched checks should be there
        if (Gdx.input.isTouched()) {
            Vector3 worldCoordinates = getWorldInputCoordinates();
            //pick pull acceleration from another object
            float pullAccelerationModule = 500;
            //foreach pull point (maybe we only have 1... probably)
            Vector2 pullDirection = new Vector2(
                    worldCoordinates.x - transform.position.x,
                    worldCoordinates.y - transform.position.y
            );

            pullDirection.nor();

            Vector2 newAcceleration = new Vector2(
                    player.acceleration.x + pullDirection.x * pullAccelerationModule,
                    player.acceleration.y + pullDirection.y * pullAccelerationModule
            );

            player.setAcceleration(newAcceleration);
        }

        Vector2 targetPosition = transform.position;
        targetPosition.add(deltaMovement);

        //set velocity for next frame
        player.setVelocity(player.velocity.add(player.acceleration.scl(deltaTime)));


        // Try to move in world (checks collision with other collides)
        Response.Result result = world.move(collider.item, targetPosition.x, targetPosition.y,
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
