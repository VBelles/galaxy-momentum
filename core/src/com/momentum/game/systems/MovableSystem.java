package com.momentum.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.World;
import com.momentum.game.DefaultFilter;
import com.momentum.game.components.Collider;
import com.momentum.game.components.Movable;
import com.momentum.game.components.Transform;

public class MovableSystem extends IteratingSystem {

    private final World<Entity> world;

    public MovableSystem(World<Entity> world) {
        super(Family.all(Collider.class, Transform.class, Movable.class).get());
        this.world = world;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Collider collider = Collider.mapper.get(entity);
        Transform transform = Transform.mapper.get(entity);
        Movable movable = Movable.mapper.get(entity);

        if (movable.path.isEmpty()) {
            return;
        }

        Vector2 target = movable.path.get(movable.nextPoint);
        movable.dir.set(target)
                .sub(transform.position)
                .nor();
        float delta = movable.speed * deltaTime;
        transform.position.mulAdd(movable.dir, delta);

        // TODO: Make the movement "kinematic"
        world.move(collider.item, transform.position.x, transform.position.y, DefaultFilter.instance);
        Rect rect = world.getRect(collider.item);
        transform.position.set(rect.x, rect.y);

        if (target.dst(transform.position) < delta) {
            movable.nextPoint += movable.increment;
            if (movable.cyclic) {
                if (movable.nextPoint >= movable.path.size()) {
                    movable.nextPoint = 0;
                }
            } else {
                if (movable.nextPoint >= movable.path.size() || movable.nextPoint < 0) {
                    movable.increment *= -1;
                    movable.nextPoint += movable.increment;
                }
            }
        }
    }


}
