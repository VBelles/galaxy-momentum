package com.momentum.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.momentum.game.components.*;
import com.momentum.game.resources.Resources;

public class StageLoader {

    public static void load(Engine engine, Resources resources, int level) {
        TiledMap map = resources.get(resources.stages.get(level));
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tiledLayer = ((TiledMapTileLayer) layer);
                boolean physics = tiledLayer.getProperties().get("physics", false, Boolean.class);
                for (int row = 0; row < tiledLayer.getWidth(); row++) {
                    for (int col = 0; col < tiledLayer.getHeight(); col++) {
                        TiledMapTileLayer.Cell cell = tiledLayer.getCell(row, col);
                        if (cell != null) {
                            buildTileEntity(engine, cell, row, col, 16, physics, level);
                        }
                    }
                }
            }

            // Find spawn
            TiledMapTileMapObject spawn = (TiledMapTileMapObject) layer.getObjects().get("spawn");
            if (spawn != null) {
                buildPlayerEntity(engine, resources, spawn.getX(), spawn.getY(), level);
            }

            // Find goal
            TiledMapTileMapObject goal = (TiledMapTileMapObject) layer.getObjects().get("goal");
            if (goal != null) {
                buildGoalEntity(engine, resources, goal.getX(), goal.getY(), level);
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
                        .setSpeed(100)
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
}
