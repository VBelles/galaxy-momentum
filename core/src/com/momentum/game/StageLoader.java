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
import com.badlogic.gdx.maps.tiled.TiledMapTile;
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
                            boolean killer = cell.getTile().getProperties().get("killer", false, Boolean.class);
                            buildTileEntity(engine, cell, getWorldPosition(cell.getTile(), row, col), physics, killer, level);
                        }
                    }
                }
            }

            // Find spawn
            TiledMapTileMapObject spawn = (TiledMapTileMapObject) layer.getObjects().get("spawn");
            if (spawn != null) {
                Gdx.app.log("StageLoader", "Build spawn");
                buildPlayerEntity(engine, resources, getWorldPosition(spawn), level);
            }

            // Find goal
            TiledMapTileMapObject goal = (TiledMapTileMapObject) layer.getObjects().get("goal");
            if (goal != null) {
                buildGoalEntity(engine, goal.getTextureRegion(), getWorldPosition(goal), level);
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
                    buildSwitch(engine, tiledObject.getTextureRegion(), getWorldPosition(tiledObject), level, object.getName());
                }

                // Build Gravity Field
                if (object instanceof TiledMapTileMapObject && object.getName().startsWith("gravity")) {
                    TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) object;
                    boolean constant = tiledObject.getProperties().get("constant", false, Boolean.class);
                    buildGravityField(engine, tiledObject.getTextureRegion(), getWorldPosition(tiledObject), constant, level);
                }

                // Build doors
                if (object instanceof TiledMapTileMapObject && object.getName().startsWith("door")) {
                    TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) object;
                    buildDoor(engine, tiledObject.getTextureRegion(), getWorldPosition(tiledObject), level, object.getName());
                }
            }

        }
    }

    private static Vector2 getWorldPosition(TiledMapTile tile, int row, int column) {
        return new Vector2(row * tile.getTextureRegion().getRegionWidth(), column * tile.getTextureRegion().getRegionHeight())
                .add(tile.getTextureRegion().getRegionWidth() / 2f, tile.getTextureRegion().getRegionHeight() / 2f);
    }

    private static Vector2 getWorldPosition(TiledMapTileMapObject tile) {
        return new Vector2(tile.getX(), tile.getY())
                .add(tile.getTextureRegion().getRegionWidth() / 2f, tile.getTextureRegion().getRegionHeight() / 2f);
    }

    private static void buildTileEntity(Engine engine, TiledMapTileLayer.Cell cell, Vector2 position,
                                        boolean physics, boolean killer, int level) {

        TextureRegion texture = cell.getTile().getTextureRegion();
        Entity entity = new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                );
        if (physics) {
            entity.add(engine.createComponent(Collider.class)
                    .setSize(texture.getRegionWidth(), texture.getRegionHeight()));
        }
        if (killer) {
            entity.add(engine.createComponent(Killer.class));
        }
        engine.addEntity(entity);
    }

    private static void buildPlayerEntity(Engine engine, Resources resources, Vector2 position, int level) {
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setSize(16f, 16f)
                )
                .add(engine.createComponent(Player.class))
                .add(engine.createComponent(Animated.class)
                        .addAnimation(Player.STATE_MOVING, resources.playerMove)
                        .addAnimation(Player.STATE_HIT, resources.playerHit)
                        .addAnimation(Player.STATE_DEAD, resources.playerDead)
                        .setCurrentAnimation(0)
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(12f, 12f)
                ).add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
        );
    }

    private static void buildGoalEntity(Engine engine, TextureRegion texture, Vector2 position, int level) {
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                )
                .add(engine.createComponent(Animated.class)
                        .setScale(new Animated.Scale(1f, 1.4f, 0.3f))
                )
                .add(engine.createComponent(Goal.class))
                .add(engine.createComponent(Collider.class)
                        .setSize(texture.getRegionWidth(), texture.getRegionHeight())
                        .setSensor(true)
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
        TextureRegion texture = resources.enemy;
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(vertices[0], vertices[1])
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(texture.getRegionWidth() * 0.85f, texture.getRegionHeight() * 0.85f)
                        .setSensor(true)
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
                .add(engine.createComponent(Killer.class))
                .add(movable)
        );
    }

    private static void buildSwitch(Engine engine, TextureRegion texture, Vector2 position, int level, String name) {
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(texture.getRegionWidth(), texture.getRegionHeight())
                        .setSensor(true)
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
                .add(engine.createComponent(Switch.class)
                        .setName(name)
                )
        );
    }

    private static void buildGravityField(Engine engine, TextureRegion texture, Vector2 position, boolean constant, int level) {
        float minPull = constant ? 0 : 0;
        float maxPull = constant ? 100 : 200;

        Entity entity = new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                )
                .add(engine.createComponent(Animated.class)
                        .setScale(new Animated.Scale(1f, 1.1f, 0.1f))
                        .setRotate(new Animated.Rotate(0f, Float.MAX_VALUE, 10f))
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(texture.getRegionWidth(), texture.getRegionHeight())
                        .setSensor(true)
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
                .add(engine.createComponent(GravityField.class)
                        .setConstantField(constant)
                        .setMinPull(minPull)
                        .setMaxPull(maxPull)
                );

        if (constant) {
            entity.add(engine.createComponent(Killer.class));
        }
        engine.addEntity(entity);

    }


    private static void buildDoor(Engine engine, TextureRegion texture, Vector2 position, int level, String name) {
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(texture.getRegionWidth(), texture.getRegionHeight())
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
