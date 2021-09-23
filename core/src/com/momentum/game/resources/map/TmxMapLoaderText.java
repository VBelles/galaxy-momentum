package com.momentum.game.resources.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.utils.XmlReader;

public class TmxMapLoaderText extends TmxMapLoader {

    @Override
    protected void loadObject(TiledMap map, MapObjects objects, XmlReader.Element element, float heightInPixels) {
        if (element.getName().equals("object")) {
            MapObject object = null;

            float scaleX = convertObjectToTileSpace ? 1.0f / mapTileWidth : 1.0f;
            float scaleY = convertObjectToTileSpace ? 1.0f / mapTileHeight : 1.0f;

            float x = element.getFloatAttribute("x", 0) * scaleX;
            float y = (flipY ? (heightInPixels - element.getFloatAttribute("y", 0)) : element.getFloatAttribute("y", 0)) * scaleY;

            float width = element.getFloatAttribute("width", 0) * scaleX;
            float height = element.getFloatAttribute("height", 0) * scaleY;

            if (element.getChildCount() > 0) {
                XmlReader.Element child = null;
                if ((child = element.getChildByName("polygon")) != null) {
                    String[] points = child.getAttribute("points").split(" ");
                    float[] vertices = new float[points.length * 2];
                    for (int i = 0; i < points.length; i++) {
                        String[] point = points[i].split(",");
                        vertices[i * 2] = Float.parseFloat(point[0]) * scaleX;
                        vertices[i * 2 + 1] = Float.parseFloat(point[1]) * scaleY * (flipY ? -1 : 1);
                    }
                    Polygon polygon = new Polygon(vertices);
                    polygon.setPosition(x, y);
                    object = new PolygonMapObject(polygon);
                } else if ((child = element.getChildByName("polyline")) != null) {
                    String[] points = child.getAttribute("points").split(" ");
                    float[] vertices = new float[points.length * 2];
                    for (int i = 0; i < points.length; i++) {
                        String[] point = points[i].split(",");
                        vertices[i * 2] = Float.parseFloat(point[0]) * scaleX;
                        vertices[i * 2 + 1] = Float.parseFloat(point[1]) * scaleY * (flipY ? -1 : 1);
                    }
                    Polyline polyline = new Polyline(vertices);
                    polyline.setPosition(x, y);
                    object = new PolylineMapObject(polyline);
                } else if ((child = element.getChildByName("ellipse")) != null) {
                    object = new EllipseMapObject(x, flipY ? y - height : y, width, height);
                } else if ((child = element.getChildByName("text")) != null) {
                    object = new TextLabelMapObject(child.getText(), x, flipY ? y - height : y, width, height);
                }
            }
            if (object == null) {
                String gid = null;
                if ((gid = element.getAttribute("gid", null)) != null) {
                    int id = (int) Long.parseLong(gid);
                    boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
                    boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);

                    TiledMapTile tile = map.getTileSets().getTile(id & ~MASK_CLEAR);
                    TiledMapTileMapObject tiledMapTileMapObject = new TiledMapTileMapObject(tile, flipHorizontally, flipVertically);
                    TextureRegion textureRegion = tiledMapTileMapObject.getTextureRegion();
                    tiledMapTileMapObject.getProperties().put("gid", id);
                    tiledMapTileMapObject.setX(x);
                    tiledMapTileMapObject.setY(flipY ? y : y - height);
                    float objectWidth = element.getFloatAttribute("width", textureRegion.getRegionWidth());
                    float objectHeight = element.getFloatAttribute("height", textureRegion.getRegionHeight());
                    tiledMapTileMapObject.setScaleX(scaleX * (objectWidth / textureRegion.getRegionWidth()));
                    tiledMapTileMapObject.setScaleY(scaleY * (objectHeight / textureRegion.getRegionHeight()));
                    tiledMapTileMapObject.setRotation(element.getFloatAttribute("rotation", 0));
                    object = tiledMapTileMapObject;
                } else {
                    object = new RectangleMapObject(x, flipY ? y - height : y, width, height);
                }
            }
            object.setName(element.getAttribute("name", null));
            String rotation = element.getAttribute("rotation", null);
            if (rotation != null) {
                object.getProperties().put("rotation", Float.parseFloat(rotation));
            }
            String type = element.getAttribute("type", null);
            if (type != null) {
                object.getProperties().put("type", type);
            }
            int id = element.getIntAttribute("id", 0);
            if (id != 0) {
                object.getProperties().put("id", id);
            }
            object.getProperties().put("x", x);

            if (object instanceof TiledMapTileMapObject) {
                object.getProperties().put("y", y);
            } else {
                object.getProperties().put("y", (flipY ? y - height : y));
            }
            object.getProperties().put("width", width);
            object.getProperties().put("height", height);
            object.setVisible(element.getIntAttribute("visible", 1) == 1);
            XmlReader.Element properties = element.getChildByName("properties");
            if (properties != null) {
                loadProperties(object.getProperties(), properties);
            }
            objects.add(object);
        }
    }
}
