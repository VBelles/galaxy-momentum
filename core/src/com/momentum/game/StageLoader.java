package com.momentum.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.momentum.game.components.*;
import com.momentum.game.resources.Resources;

public class StageLoader {

    public static void load(Engine engine, Resources resources, int level) {
        Gdx.app.log("StageLoader", "Load level " + level);
        TiledMap map = resources.get(resources.stages.get(level));
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tiledLayer = ((TiledMapTileLayer) layer);
                boolean physics = tiledLayer.getProperties().get("physics", false, Boolean.class);
                for (int row = 0; row < tiledLayer.getWidth(); row++) {
                    for (int col = 0; col < tiledLayer.getHeight(); col++) {
                        TiledMapTileLayer.Cell cell = tiledLayer.getCell(row, col);
                        if (cell != null) {
                            buildTileEntity(engine, cell, row, col, ((TiledMapTileLayer) layer).getTileWidth(), physics, level);
                        }
                    }
                }
            }

            // Find spawn
            TiledMapTileMapObject spawn = (TiledMapTileMapObject) layer.getObjects().get("spawn");
            if (spawn != null) {
                Gdx.app.log("StageLoader", "Build spawn");
                buildPlayerEntity(engine, resources, spawn.getX(), spawn.getY(), level);
            }

            // Find goal
            TiledMapTileMapObject goal = (TiledMapTileMapObject) layer.getObjects().get("goal");
            if (goal != null) {
                buildGoalEntity(engine, resources, goal.getX(), goal.getY(), level);
                Gdx.app.log("StageLoader", "Build goal");
            }

            for (MapObject object : layer.getObjects()) {

                // Build movables
                if (object.getName().startsWith("movable")) {
                    float speed = object.getProperties().get("speed", 50f, Float.class);
                    boolean cyclic;
                    float[] vertices;
                    if (object instanceof PolygonMapObject) {
                        vertices = ((PolygonMapObject) object).getPolygon().getTransformedVertices();
                        cyclic = true;
                    } else if (object instanceof PolylineMapObject) {
                        vertices = ((PolylineMapObject) object).getPolyline().getTransformedVertices();
                        cyclic = false;
                    } else {
                        throw new IllegalStateException("Invalid movable");
                    }
                    buildMovableEntity(engine, resources, vertices, speed, cyclic, level);
                }

                // Build switches
                if (object instanceof TiledMapTileMapObject && object.getName().startsWith("switch")) {
                    TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) object;
                    buildSwitch(engine, tiledObject.getTextureRegion(), tiledObject.getX(), tiledObject.getY(), level, object.getName());
                }

                // Build doors
                if (object instanceof TiledMapTileMapObject && object.getName().startsWith("door")) {
                    TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) object;
                    buildDoor(engine, tiledObject.getTextureRegion(), tiledObject.getX(), tiledObject.getY(), level, object.getName());
                }
            }

        }
    }

    private static void buildTileEntity(Engine engine, TiledMapTileLayer.Cell cell, int row, int column, int size,
                                        boolean physics, int level) {
        Entity entity = new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(row * size, column * size)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(cell.getTile().getTextureRegion())
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                );
        if (physics) {
            entity.add(engine.createComponent(Collider.class)
                    .setWidth(size)
                    .setHeight(size));
        }
        engine.addEntity(entity);
    }

    private static void buildPlayerEntity(Engine engine, Resources resources, float x, float y, int level) {
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(x, y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(new TextureRegion(resources.get(resources.player)))
                )
                .add(engine.createComponent(Player.class)
                )
                .add(engine.createComponent(Collider.class)
                        .setWidth(resources.get(resources.player).getWidth())
                        .setHeight(resources.get(resources.player).getHeight())
                ).add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
        );
    }

    private static void buildGoalEntity(Engine engine, Resources resources, float x, float y, int level) {
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(x, y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(new TextureRegion(resources.get(resources.player)))
                )
                .add(engine.createComponent(Goal.class))
                .add(engine.createComponent(Collider.class)
                        .setSensor(true)
                        .setWidth(resources.get(resources.player).getWidth())
                        .setHeight(resources.get(resources.player).getHeight())
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
        );
    }

    private static void buildMovableEntity(Engine engine, Resources resources, float[] vertices, float speed,
                                           boolean cyclic, int level) {
        Movable movable = engine.createComponent(Movable.class);
        for (int i = 0; i < vertices.length; i += 2) {
            movable.path.add(new Vector2(vertices[i], vertices[i + 1]));
            movable.speed = speed;
            movable.cyclic = cyclic;
        }
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(vertices[0], vertices[1])
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(new TextureRegion(resources.get(resources.player)))
                )
                .add(engine.createComponent(Collider.class)
                        .setWidth(resources.get(resources.player).getWidth())
                        .setHeight(resources.get(resources.player).getHeight())
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
                .add(movable)
        );
    }

    private static void buildSwitch(Engine engine, TextureRegion texture, float x, float y, int level, String name) {
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(x, y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                )
                .add(engine.createComponent(Collider.class)
                        .setSensor(true)
                        .setWidth(texture.getRegionWidth())
                        .setHeight(texture.getRegionHeight())
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
                .add(engine.createComponent(Switch.class)
                        .setName(name)
                )
        );
    }

    private static void buildDoor(Engine engine, TextureRegion texture, float x, float y, int level, String name) {
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(x, y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                )
                .add(engine.createComponent(Collider.class)
                        .setWidth(texture.getRegionWidth())
                        .setHeight(texture.getRegionHeight())
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
                .add(engine.createComponent(Door.class)
                        .setName(name)
                )
        );
    }


}
