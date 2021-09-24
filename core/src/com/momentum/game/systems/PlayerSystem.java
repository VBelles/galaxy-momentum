package com.momentum.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dongbat.jbump.*;
import com.momentum.game.PhysicsUtils;
import com.momentum.game.components.*;

import java.util.ArrayList;

public class PlayerSystem extends IteratingSystem {

    private final Camera camera;
    private final World<Entity> world;
    private Iterable<Entity> stageEntity;
    private Iterable<Entity> gravityFieldEntities;

    // only when not constant, debug variable for testing which option is better
    // in the final game all of them should be true or false
    public boolean toggleableGravityFields = true;

    public PlayerSystem(Camera camera, World<Entity> world) {
        super(Family.all(Player.class, Transform.class, Collider.class, Animated.class, Renderable.class).get());
        this.camera = camera;
        this.world = world;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        stageEntity = engine.getEntitiesFor(Family.all(Stage.class).get());
        gravityFieldEntities = engine.getEntitiesFor(Family.all(GravityField.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        Player player = Player.mapper.get(entity);
        Transform transform = Transform.mapper.get(entity);
        Collider collider = Collider.mapper.get(entity);
        Animated animated = Animated.mapper.get(entity);
        Renderable renderable = Renderable.mapper.get(entity);

        if (animated.isCurrentAnimationFinished()) {
            animated.setCurrentAnimation(Player.STATE_MOVING);
        }

        //Get movement that happened during last frame (not counting collisions)
        Vector2 deltaMovement = new Vector2(
                (player.velocity.x + ((0.5f) * player.acceleration.x * deltaTime)) * deltaTime,
                (player.velocity.y + ((0.5f) * player.acceleration.y * deltaTime)) * deltaTime
        );

        //cannot move more than the equivalent of one frame at max speed
        if(deltaMovement.len() > player.maxSpeed * deltaTime)
        {
            deltaMovement.nor();
            deltaMovement.scl(player.maxSpeed * deltaTime);
        }

        transform.position.add(deltaMovement);
        Vector2 dir = deltaMovement.cpy().nor();
        renderable.angle = MathUtils.atan2(dir.y, dir.x) * MathUtils.radDeg;

        //reset acceleration
        player.setAcceleration(Vector2.Zero);

        // Obtain clicked entities;
        ArrayList<Item> clickedItems = new ArrayList<>();
        if (toggleableGravityFields) {
            if (Gdx.input.justTouched()) {
                Vector3 worldCoordinates = getWorldInputCoordinates();
                world.queryPoint(worldCoordinates.x, worldCoordinates.y, CollisionFilter.defaultFilter, clickedItems);
                for (Item clickedItem : clickedItems) {
                    Entity clickedEntity = (Entity) clickedItem.userData;
                    GravityField field = GravityField.mapper.get(clickedEntity);
                    if (field != null && !field.constantField) {
                        field.active = !field.active;
                    }
                }
            }
        } else if (Gdx.input.isTouched()) {
            Vector3 worldCoordinates = getWorldInputCoordinates();
            world.queryPoint(worldCoordinates.x, worldCoordinates.y, CollisionFilter.defaultFilter, clickedItems);
        }

        //for each pull point we add the accelerations it generates on the player
        for (Entity gravityFieldEntity : gravityFieldEntities) {
            GravityField field = GravityField.mapper.get(gravityFieldEntity);
            Collider fieldCollider = Collider.mapper.get(gravityFieldEntity);
            Transform fieldTransform = Transform.mapper.get(gravityFieldEntity);
            Renderable gravityRenderable = Renderable.mapper.get(gravityFieldEntity);
            Animated gravityAnimated = Animated.mapper.get(gravityFieldEntity);

            //only have to check click if it's not constant and the player is clicking
            if (!toggleableGravityFields) {
                field.active = field.constantField || clickedItems.contains(fieldCollider.item);
            }

            // Update scale and color
            if (field.active) {
                gravityRenderable.color = new Color(1, 1, 0.8f, 1);
                gravityAnimated.scale.from = 1.15f;
                gravityAnimated.scale.to = 1.25f;
            } else {
                gravityRenderable.color = Color.WHITE;
                gravityAnimated.scale.from = 1f;
                gravityAnimated.scale.to = 1.1f;
            }

            //only has pull if active or constant field (always active)
            if (!field.active && !field.constantField) continue;

            //calculate direction of the pull
            Vector2 pullDirection = fieldTransform.position.cpy().sub(transform.position);
            float distance = pullDirection.len();
            pullDirection.nor();

            //calculate magnitude of the pull
            float pullAccelerationMagnitude = field.maxPull;
            if (field.constantField) {
                float maxAffectedDistance = 250;
                float progress =
                        MathUtils.clamp(distance * distance, 0, maxAffectedDistance * maxAffectedDistance)
                                / (maxAffectedDistance * maxAffectedDistance);
                pullAccelerationMagnitude = MathUtils.lerp(field.maxPull, field.minPull, progress);
            }

            Vector2 newAcceleration = new Vector2(
                    player.acceleration.x + pullDirection.x * pullAccelerationMagnitude,
                    player.acceleration.y + pullDirection.y * pullAccelerationMagnitude
            );

            player.setAcceleration(newAcceleration);

        }

        // Try to move in world (checks collision with other colliders)
        Response.Result result = PhysicsUtils.move(world, collider, transform);

        boolean reacted = false;
        for (int i = 0; i < result.projectedCollisions.size(); i++) {
            Collision collision = result.projectedCollisions.get(i);
            Entity collidedEntity = ((Entity) collision.other.userData);
            Collider collided = Collider.mapper.get(collidedEntity);

            // Bounce against first not sensor collided
            if (!reacted && !collided.isSensor) {
                reacted = true;
                IntPoint normalIntPoint = collision.normal;
                Vector2 normal = new Vector2(normalIntPoint.x, normalIntPoint.y);
                Vector2 projectedVector = normal.scl(player.velocity.dot(normal));
                Vector2 reflectedVector = projectedVector.scl(-2).add(player.velocity);
                if (reflectedVector.len() > 30) {
                    reflectedVector.scl(0.5f);//magic number for elasticity
                    animated.setCurrentAnimation(Player.STATE_HIT);
                } else {
                    reflectedVector.scl(0.8f);//magic number for elasticity
                }

                player.setVelocity(reflectedVector);
            }

            Goal goal = Goal.mapper.get(collidedEntity);
            if (goal != null && !goal.achieved) {
                Gdx.app.log("PlayerSystem", "Goal achieved");
                goal.achieved = true;
            }
            Switch aSwitch = Switch.mapper.get(collidedEntity);
            if (aSwitch != null && !aSwitch.pressed) {
                Gdx.app.log("PlayerSystem", "Pressed switch");
                aSwitch.justPressed = true;
            }

            if (Killer.mapper.has(collidedEntity)) {
                Gdx.app.log("PlayerSystem", "You died");
                Stage stage = Stage.mapper.get(stageEntity.iterator().next());
                stage.failure = true;
            }
        }

        //set velocity for next frame
        player.velocity.mulAdd(player.acceleration, deltaTime);
    }

    private Vector3 getWorldInputCoordinates() {
        return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

}
