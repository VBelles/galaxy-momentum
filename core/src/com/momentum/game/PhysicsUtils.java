package com.momentum.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.momentum.game.components.Collider;
import com.momentum.game.components.Transform;


public class PhysicsUtils {

    public static Response.Result move(World<Entity> world, Collider collider, Transform targetTransform) {
        Response.Result result = world.move(collider.item,
                targetTransform.position.x - collider.width / 2 + collider.offset.x,
                targetTransform.position.y - collider.height / 2 + collider.offset.y,
                DefaultFilter.instance);
        Rect rect = world.getRect(collider.item);
        targetTransform.position.set(
                rect.x + collider.width / 2 - collider.offset.x,
                rect.y + collider.height / 2 - collider.offset.y
        );
        return result;

    }
}
