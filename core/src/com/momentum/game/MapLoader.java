package com.momentum.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.momentum.game.components.Collider;
import com.momentum.game.components.Renderable;
import com.momentum.game.components.Transform;

public class MapLoader {

    public static void load(TiledMap map, Engine engine) {
        for (MapLayer layer : map.getLayers()) {
            TiledMapTileLayer tiledLayer = ((TiledMapTileLayer) layer);
            boolean physics = tiledLayer.getProperties().get("physics", false, Boolean.class);
            for (int row = 0; row < tiledLayer.getWidth(); row++) {
                for (int col = 0; col < tiledLayer.getHeight(); col++) {
                    TiledMapTileLayer.Cell cell = tiledLayer.getCell(row, col);
                    if (cell != null) {
                        Entity entity = buildTileEntity(engine, cell, row, col, 16, physics);
                        engine.addEntity(entity);
                    }
                }
            }
        }
    }

    private static Entity buildTileEntity(Engine engine, TiledMapTileLayer.Cell cell, int row, int column, int size, boolean physics) {
        Entity entity = new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(row * size, column * size)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(cell.getTile().getTextureRegion())
                );
        if (physics) {
            entity.add(engine.createComponent(Collider.class)
                    .setWidth(size)
                    .setHeight(size));
        }
        return entity;
    }
}
