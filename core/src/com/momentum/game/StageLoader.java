package com.momentum.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import com.momentum.game.resources.map.TextLabelMapObject;

import java.util.Collections;

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
                buildGoalEntity(engine, resources, getWorldPosition(goal), level);
                Gdx.app.log("StageLoader", "Build goal");
            }

            for (MapObject object : layer.getObjects()) {

                // Build movables
                if (object.getName() != null && object.getName().startsWith("movable")) {
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
                if (object instanceof TiledMapTileMapObject && object.getName() != null && object.getName().startsWith("switch")) {
                    TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) object;
                    float rotation = tiledObject.getRotation();
                    //System.out.println(rotation);
                    //if(tiledObject.isFlipVertically()) rotation = 2;
                    buildSwitch(engine, map.getTileSets().getTile(262).getTextureRegion(),
                            map.getTileSets().getTile(265).getTextureRegion(), getWorldPosition(tiledObject), rotation,
                            level, object.getName());
                }

                // Build Gravity Field
                if (object instanceof TiledMapTileMapObject && object.getName() != null && object.getName().startsWith("gravity")) {
                    TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) object;
                    boolean constant = tiledObject.getProperties().get("constant", false, Boolean.class);
                    buildGravityField(engine, resources, tiledObject.getTextureRegion(), getWorldPosition(tiledObject), constant, level);
                }

                // Build doors
                if (object instanceof TiledMapTileMapObject && object.getName() != null && object.getName().startsWith("door")) {
                    TiledMapTileMapObject tiledObject = (TiledMapTileMapObject) object;
                    boolean horizontal = tiledObject.getProperties().get("horizontal", true, Boolean.class);
                    buildDoor(engine, tiledObject.getTextureRegion(), getWorldPosition(tiledObject), level, object.getName(), horizontal);
                }

                if (object instanceof TextLabelMapObject) {
                    buildText(engine, (TextLabelMapObject) object, level);
                }
            }

        }

        int tileSize = (int) map.getProperties().get("tilewidth");
        int width = (int) map.getProperties().get("width") * tileSize;
        int height = (int) map.getProperties().get("height") * tileSize;

        // Restart button
        engine.addEntity(new Entity()
                .add(engine.createComponent(Renderable.class)
                        .setTexture(resources.veil)
                        .setSize(15, 15)
                )
                .add(engine.createComponent(Transform.class)
                        .setPosition(width - 11, height - 11)
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(30, 30)
                        .setSensor(true)
                )
                .add(engine.createComponent(Button.class).setAction(Button.RESTART))
                .add(engine.createComponent(Tag.class).addTag(level))
        );

        engine.addEntity(new Entity()
                .add(engine.createComponent(Renderable.class)
                        .setText("R")
                        .setSize(20, 20)
                )
                .add(engine.createComponent(Transform.class)
                        .setPosition(width - 22, height - 8)
                )
                .add(engine.createComponent(Tag.class).addTag(level))
        );

        // Next button
        engine.addEntity(new Entity()
                .add(engine.createComponent(Renderable.class)
                        .setTexture(resources.veil)
                        .setSize(15, 15)
                )
                .add(engine.createComponent(Transform.class)
                        .setPosition(width - 11, 11)
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(30, 30)
                        .setSensor(true)
                )
                .add(engine.createComponent(Button.class).setAction(Button.NEXT))
                .add(engine.createComponent(Tag.class).addTag(level))
        );

        engine.addEntity(new Entity()
                .add(engine.createComponent(Renderable.class)
                        .setText("N")
                        .setSize(20, 20)
                )
                .add(engine.createComponent(Transform.class)
                        .setPosition(width - 21, 14)
                )
                .add(engine.createComponent(Tag.class).addTag(level))
        );

        // Previous button
        engine.addEntity(new Entity()
                .add(engine.createComponent(Renderable.class)
                        .setTexture(resources.veil)
                        .setSize(15, 15)
                )
                .add(engine.createComponent(Transform.class)
                        .setPosition(width - 40, 11)
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(30, 30)
                        .setSensor(true)
                )
                .add(engine.createComponent(Button.class).setAction(Button.PREVIOUS))
                .add(engine.createComponent(Tag.class).addTag(level))
        );


        engine.addEntity(new Entity()
                .add(engine.createComponent(Renderable.class)
                        .setText("P")
                        .setSize(20, 20)
                )
                .add(engine.createComponent(Transform.class)
                        .setPosition(width - 50, 14)
                )
                .add(engine.createComponent(Tag.class).addTag(level))
        );


        engine.addEntity(new Entity()
                .add(engine.createComponent(Renderable.class)
                        .setSize(width, height * 0.5f)
                        .setTexture(resources.veil)
                )
                .add(engine.createComponent(Transform.class)
                        .setPosition(width * 0.5f, height * 0.5f)
                )
                .add(engine.createComponent(ClickToStart.class))
                .add(engine.createComponent(Tag.class).addTag(level))
        );

        String stage = "Chapter " + (level + 1 < 10 ? "0" : "") + (level + 1) + "\n\n";
        String title = map.getProperties().get("title", "", String.class) + "\n";
        String history = map.getProperties().get("history", "", String.class) + "\n";
        String decoration = String.join("", Collections.nCopies(title.length(), "-")) + "\n";
        engine.addEntity(new Entity()
                .add(engine.createComponent(Renderable.class)
                        .setText(stage +
                                decoration + title + decoration +
                                "\n" + history +
                                "\n\n\nclick to start")
                        .setSize(width, height)
                )
                .add(engine.createComponent(Transform.class)
                        .setPosition(0, height * 0.75f)
                )
                .add(engine.createComponent(ClickToStart.class))
                .add(engine.createComponent(Tag.class).addTag(level))
        );


        resources.playMusic(Math.min(level / 4, 3) + 1);
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

        float angle = cell.getRotation() * 90f;
        if (cell.getFlipHorizontally()) {
            angle = 180f;
        }

        Vector2 colliderOffset = new Vector2();
        Vector2 colliderSize = new Vector2(texture.getRegionWidth(), texture.getRegionHeight());
        if (killer) {
            if (angle == 0) {
                colliderSize.y /= 3;
                colliderOffset.y -= colliderSize.y;
                colliderSize.x *= 0.66f;
            } else if (angle == 90) {
                colliderSize.x /= 3;
                colliderOffset.x += colliderSize.x;
                colliderSize.y *= 0.66f;
            } else if (angle == 180) {
                colliderSize.y /= 3;
                colliderOffset.y += colliderSize.y;
                colliderSize.x *= 0.66f;
            } else if (angle == 270) {
                colliderSize.x /= 3;
                colliderOffset.x -= colliderSize.x;
                colliderSize.y *= 0.66f;
            }
        }

        Entity entity = new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                        .setAngle(angle)
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                );
        if (physics) {
            entity.add(engine.createComponent(Collider.class)
                    .setSize(colliderSize.x, colliderSize.y)
                    .setOffset(colliderOffset.x, colliderOffset.y)
            );
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

    private static void buildGoalEntity(Engine engine, Resources resources, Vector2 position, int level) {
        TextureRegion texture = resources.goal.getKeyFrame(0);
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                )
                .add(engine.createComponent(Animated.class)
                        .addAnimation(0, resources.goal)
                        .setCurrentAnimation(0)
                )
                .add(engine.createComponent(Goal.class))
                .add(engine.createComponent(Collider.class)
                        .setSize(texture.getRegionWidth() / 8.8f, texture.getRegionHeight() / 2.0f)
                        .setOffset(-1f, -5f)
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
        float size = 20f;
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(vertices[0], vertices[1])
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                        .setSize(size, size)
                )
                .add(engine.createComponent(Animated.class)
                        .setScale(new Animated.Scale(1f, 1.1f, 0.1f))
                        .setRotate(new Animated.Rotate(0f, Float.MAX_VALUE, 100f))
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(size * 0.85f, size * 0.85f)
                        .setSensor(true)
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
                .add(engine.createComponent(Killer.class))
                .add(movable)
        );
    }

    private static Color colorFromName(String name) {
        switch (name) {
            case "1":
                return new Color(1f, 0.8f, 0.8f, 1f);
            case "2":
                return new Color(0.8f, 1f, 0.8f, 1f);
            case "3":
                return new Color(1f, 1f, 0.8f, 1f);
            case "4":
                return new Color(0.8f, 1f, 1f, 1f);
        }
        return Color.WHITE;
    }

    private static void buildSwitch(Engine engine, TextureRegion tex1, TextureRegion tex2, Vector2 position, float rotation, int level, String name) {
        if (rotation < 0) {
            rotation += 36000;
            rotation = rotation % 360;
        }

        if (rotation == 270) {
            position.x -= 16;
        } else if (rotation == 180) {
            position.x -= 16;
            position.y -= 16;
        } else if (rotation == 90) {
            position.y -= 16;
        }

        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(tex1)
                        .setAngle(-rotation)
                        .setColor(colorFromName(name.split("_")[1]))
                )
                .add(engine.createComponent(Animated.class)
                        .addAnimation(0, new Animation<>(0, tex1))
                        .addAnimation(1, new Animation<>(0, tex2))
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(tex1.getRegionWidth() * 0.5f, tex1.getRegionHeight() * 0.5f)
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

    private static void buildGravityField(Engine engine, Resources resources, TextureRegion texture, Vector2 position, boolean constant, int level) {
        float minPull = constant ? 0 : 0;
        float maxPull = constant ? 30 : 40;

        float scaleFactor = 1;
        if (!constant) scaleFactor = 1.4f;

        // Add another entity for the background
        if (constant) {
            engine.addEntity(new Entity()
                    .add(engine.createComponent(Transform.class).setPosition(position.x, position.y))
                    .add(engine.createComponent(Renderable.class).setTexture(resources.blackHole))
                    .add(engine.createComponent(Animated.class)
                            .setRotate(new Animated.Rotate(0f, Float.MAX_VALUE, 600f))
                            .setScale(new Animated.Scale(0.8f, 1f, 0.3f))
                    )
                    .add(engine.createComponent(Tag.class).addTag(level))
            );
        }

        Entity entity = new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                )
                .add(engine.createComponent(Animated.class)
                        .setScale(new Animated.Scale(1f, 1.2f, 0.1f))
                        .setRotate(new Animated.Rotate(0f, Float.MAX_VALUE, -20f))
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(texture.getRegionWidth() * scaleFactor, texture.getRegionHeight() * scaleFactor)
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
        engine.addEntity(entity);

        if (constant) entity.add(engine.createComponent(Killer.class));

    }


    private static void buildDoor(Engine engine, TextureRegion texture, Vector2 position, int level, String name, boolean horizontal) {
        float xColliderSizeFactor = 1;
        float yColliderSizeFactor = 1;
        if (horizontal) {
            yColliderSizeFactor = 3;
        } else {
            xColliderSizeFactor = 3;
        }

        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(position.x, position.y)
                )
                .add(engine.createComponent(Renderable.class)
                        .setTexture(texture)
                        .setColor(colorFromName(name.split("_")[1]))
                )
                .add(engine.createComponent(Collider.class)
                        .setSize(texture.getRegionWidth() / xColliderSizeFactor, texture.getRegionHeight() / yColliderSizeFactor)
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
                .add(engine.createComponent(Door.class)
                        .setName(name)
                )
        );
    }

    private static void buildText(Engine engine, TextLabelMapObject text, int level) {
        engine.addEntity(new Entity()
                .add(engine.createComponent(Transform.class)
                        .setPosition(text.getRectangle().x, text.getRectangle().y + text.getRectangle().height)
                )
                .add(engine.createComponent(Renderable.class)
                        .setSize(text.getRectangle().width, text.getRectangle().height)
                        .setText(text.getText())
                )
                .add(engine.createComponent(Tag.class)
                        .addTag(level)
                )
        );
    }


}
