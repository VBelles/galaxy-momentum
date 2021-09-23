package com.momentum.game.resources;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.momentum.game.resources.map.TmxMapLoaderText;

import java.util.ArrayList;
import java.util.List;

public class Resources implements Disposable {

    private final AssetManager assetManager = new AssetManager();

    public List<AssetDescriptor<TiledMap>> stages = new ArrayList<>();


    public Animation<TextureRegion> playerMove;
    public Animation<TextureRegion> playerDead;
    public Animation<TextureRegion> playerHit;
    public TextureRegion enemy;
    public Animation<TextureRegion> goal;

    public TextureAtlas atlas;


    public Resources() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoaderText());
        assetManager.load("momentum.atlas", TextureAtlas.class);

        assetManager.load("portal.png", Texture.class);

        for (int i = 0; i < 2; i++) {
            stages.add(new AssetDescriptor<>("stages/stage_" + i + ".tmx", TiledMap.class));
            assetManager.load(stages.get(i));
        }
    }

    public void finishLoading() {
        assetManager.finishLoading();
        atlas = assetManager.get("momentum.atlas");
        playerMove = new Animation<TextureRegion>(0.5f, atlas.findRegions("player_move"));
        playerDead = new Animation<TextureRegion>(0.2f, atlas.findRegions("player_dead"));
        playerHit = new Animation<TextureRegion>(0.5f, atlas.findRegions("player_hit"));
        enemy = atlas.findRegion("enemy");
        Texture portalTexture = assetManager.get("portal.png");
        TextureRegion[][] portalRegions = new TextureRegion(portalTexture)
                .split(portalTexture.getWidth() / 8, portalTexture.getHeight() / 3);
        goal = new Animation<>(0.16f, portalRegions[0][0], portalRegions[0][1], portalRegions[0][2],
                portalRegions[0][3], portalRegions[0][4], portalRegions[0][5], portalRegions[0][6], portalRegions[0][7]);
    }

    public <T> T get(AssetDescriptor<T> assetDescriptor) {
        return assetManager.get(assetDescriptor);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }


}
