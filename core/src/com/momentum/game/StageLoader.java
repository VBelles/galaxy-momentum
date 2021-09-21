package com.momentum.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
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
                            boolean killer = cell.getTile().getProperties().get("killer", false, Boolean.class);
                            buildTileEntity(engine, cell, row, col, ((TiledMapTileLayer) layer).getTileWidth(), physics, killer, level);
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

                // Build Gravity Field
                if (object instanceof TiledMapTileMapObject && object.getName().startsWith("gravity")) {
                    TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) object;
                    boolean constant = tiledObject.getProperties().get("constant", false, Boolean.class);
                    buildGravityField(engine, resources, tiledObject.getX(), tiledObject.getY(), constant, level);
                }

                // Build doors
                if (object instanceof TiledMapTileMapObject && object.getName().startsWith("door")) {
                    TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) object;
                    buildDoor(engine, tiledObject.getTextureRegion(), tiledObject.getX(), tiledObject.getY(), level, object.getName());
                }
            }

        }
//        Iterable<Entity> gravityFieldEntities;
//        gravityFieldEntities = engine.getEntitiesFor(Family.all(GravityField.class).get());
//        for (Entity gravityFieldEntity : gravityFieldEntities) {
//            GravityField field = GravityField.mapper.get(gravityFieldEntity);
//            if(field.constantField){
//                gravityFieldEntity.add(engine.createComponent(Killer.class));
//            }
//        }
        System.out.print("aquí sí arriba però no a lo de dalt");
    }

    private static void buildTileEntity(Engine engine, TiledMapTileLayer.Cell cell, int row, int column, int size,
                                        boolean physics, boolean killer, int level) {
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
        if (killer) {
            entity.add(engine.createComponent(Killer.class));
        }
        engine.addEntity(entity);
    }

    private static void buildPlayerEntity(Engine engine, Resources resources, float x, float y, int level) {
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(x, y)
                )
                .add(engine.createComponent(Renderable.class))
                .add(engine.createComponent(Player.class))
                .add(engine.createComponent(Animated.class)
                        .addAnimation(Player.STATE_MOVING, resources.playerMove)
                        .addAnimation(Player.STATE_HIT, resources.playerHit)
                        .addAnimation(Player.STATE_DEAD, resources.playerDead)
                        .setCurrentAnimation(0)
                )
                .add(engine.createComponent(Collider.class)
                        .setWidth(resources.playerMove.getKeyFrame(0).getRegionWidth())
                        .setHeight(resources.playerMove.getKeyFrame(0).getRegionHeight())
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
                        .setTexture(resources.playerDead.getKeyFrame(0))
                )
                .add(engine.createComponent(Goal.class))
                .add(engine.createComponent(Collider.class)
                        .setSensor(true)
                        .setWidth(resources.playerMove.getKeyFrame(0).getRegionWidth())
                        .setHeight(resources.playerMove.getKeyFrame(0).getRegionHeight())
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
                        .setTexture(resources.enemy)
                )
                .add(engine.createComponent(Collider.class)
                        .setWidth(resources.enemy.getRegionWidth())
                        .setHeight(resources.enemy.getRegionHeight())
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

    private static void buildGravityField(Engine engine, Resources resources, float x, float y, boolean constant, int level){

        TextureRegion tex = constant ? resources.playerDead.getKeyFrame(0) : resources.playerMove.getKeyFrame(0);
        float minPull = constant ? 0 : 300;
        float maxPull = constant ? 400 : 500;

        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(x, y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(tex)
                )
                .add(engine.createComponent(Collider.class)
                        .setSensor(true)
                        .setWidth(tex.getRegionWidth())
                        .setHeight(tex.getRegionHeight())
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
                .add(engine.createComponent(GravityField.class)
                        .setConstantField(constant)
                        .setMinPull(minPull)
                        .setMaxPull(maxPull)
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
