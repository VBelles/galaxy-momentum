package com.momentum.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dongbat.jbump.*;
import com.momentum.game.components.Collider;
import com.momentum.game.components.Transform;

import java.util.ArrayList;


public class PhysicsUtils {

    public static Response.Result move(World<Entity> world, Collider collider, Transform targetTransform) {
        Response.Result result = world.move(collider.item,
                targetTransform.position.x - collider.width / 2 + collider.offset.x,
                targetTransform.position.y - collider.height / 2 + collider.offset.y,
                DefaultFilter.instance);
        updateTransform(world, collider, targetTransform);
        return result;
    }

    public static void update(World<Entity> world, Collider collider, Transform targetTransform) {
        world.update(collider.item,
                targetTransform.position.x - collider.width / 2 + collider.offset.x,
                targetTransform.position.y - collider.height / 2 + collider.offset.y
        );
        updateTransform(world, collider, targetTransform);
    }

    private static void updateTransform(World<Entity> world, Collider collider, Transform targetTransform) {
        Rect rect = world.getRect(collider.item);
        targetTransform.position.set(
                rect.x + collider.width / 2 - collider.offset.x,
                rect.y + collider.height / 2 - collider.offset.y
        );
    }

    @SuppressWarnings("rawtypes")
    public static ArrayList<Item> getTouchedItems(World<Entity> world, Viewport viewport) {
        Vector2 coordinates = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        ArrayList<Item> items = new ArrayList<>();
        world.queryPoint(coordinates.x, coordinates.y, CollisionFilter.defaultFilter, items);
        return items;

    }
}
