package com.momentum.game.systems;

import com.badlogic.ashley.core.*;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;
import com.momentum.game.components.Collider;
import com.momentum.game.components.Transform;

public class PhysicsSystem extends EntitySystem implements EntityListener {

    final World<Entity> world;

    public PhysicsSystem(World<Entity> world) {
        this.world = world;
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(Collider.class, Transform.class).get(), this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) {
        Transform transform = Transform.mapper.get(entity);
        Collider collider = Collider.mapper.get(entity);
        collider.item = new Item<>(entity);
        world.add(collider.item, transform.position.x, transform.position.y, collider.width, collider.height);
    }

    @Override
    public void entityRemoved(Entity entity) {
        Collider collider = Collider.mapper.get(entity);
        world.remove(collider.item);
    }
}
