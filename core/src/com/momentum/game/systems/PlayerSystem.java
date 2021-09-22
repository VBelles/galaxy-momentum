package com.momentum.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dongbat.jbump.*;
import com.momentum.game.DefaultFilter;
import com.momentum.game.PhysicsUtils;
import com.momentum.game.components.*;

public class PlayerSystem extends IteratingSystem {

    private final Camera camera;
    private final World<Entity> world;
    private Iterable<Entity> stageEntity;
    private Iterable<Entity> gravityFieldEntities;

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

        //reset acceleration
        player.setAcceleration(Vector2.Zero);

        boolean isClicking = Gdx.input.isTouched();//cached

        //for each pull point we add the accelerations it generates on the player
        for (Entity gravityFieldEntity : gravityFieldEntities) {
            GravityField field = GravityField.mapper.get(gravityFieldEntity);
            Collider fieldCollider = Collider.mapper.get(gravityFieldEntity);
            Transform fieldTransform = Transform.mapper.get(gravityFieldEntity);

            //only have to check click if it's not constant and the player is clicking
            if (field.constantField == false)
            {
                if(isClicking == true) {
                    //we check if the player is clicking on the collider
                    Vector3 worldCoordinates = getWorldInputCoordinates();
                    if(worldCoordinates.x >= fieldTransform.position.x - collider.width/2
                            && worldCoordinates.x <= fieldTransform.position.x + collider.width/2
                            && worldCoordinates.y >= fieldTransform.position.y - collider.height/2
                            && worldCoordinates.y <= fieldTransform.position.y + collider.height/2
                    ){
                        field.active = true;
                    }
                    else{
                        field.active = false;
                    }
                }else{
                    field.active = false;
                }

            }
            //only has pull if active or constant field (always active)
            if (field.active == false && field.constantField == false) continue;


            //calculate direction of the pull
            Vector2 pullDirection = new Vector2(
                    fieldTransform.position.x - transform.position.x,
                    fieldTransform.position.y - transform.position.y
            );
            float distance = pullDirection.len();
            pullDirection.nor();

            //calculate magnitude of the pull
            float pullAccelerationMagnitude = field.maxPull;
            if (field.constantField) {
                float maxAffectedDistance = 300;
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


        Vector2 targetPosition = transform.position;
        Vector2 previousPosition = targetPosition.cpy();
        targetPosition.add(deltaMovement);
        Vector2 dir = targetPosition.cpy().sub(previousPosition).nor();
        renderable.angle = MathUtils.atan2(dir.y, dir.x) * MathUtils.radDeg;

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

            Killer killer = Killer.mapper.get(collidedEntity);
            if (killer != null) {
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
