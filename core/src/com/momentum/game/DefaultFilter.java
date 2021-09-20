package com.momentum.game;

import com.badlogic.ashley.core.Entity;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.momentum.game.components.Collider;

public class DefaultFilter implements CollisionFilter {
    @Override
    public Response filter(Item item, Item other) {
        Collider colliderA = Collider.mapper.get((Entity) item.userData);
        Collider colliderB = Collider.mapper.get((Entity) other.userData);
        if (colliderA.isSensor || colliderB.isSensor) {
            return Response.cross;
        }
        return Response.slide;
    }

    public static CollisionFilter instance = new DefaultFilter();
}
