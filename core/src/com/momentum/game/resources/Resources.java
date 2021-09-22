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

import java.util.ArrayList;
import java.util.List;

public class Resources implements Disposable {

    private final AssetManager assetManager = new AssetManager();

    public List<AssetDescriptor<TiledMap>> stages = new ArrayList<>();


    public Animation<TextureRegion> playerMove;
    public Animation<TextureRegion> playerDead;
    public Animation<TextureRegion> playerHit;
    public TextureRegion enemy;

    //public TextureRegion goal;

    public TextureAtlas atlas;


    public Resources() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("momentum.atlas", TextureAtlas.class);

        //assetManager.load("goal.png", Texture.class);

        for (int i = 0; i < 2; i++) {
            stages.add(new AssetDescriptor<>("stage_" + i + ".tmx", TiledMap.class));
            assetManager.load(stages.get(i));
        }
    }

    public void finishLoading() {
        assetManager.finishLoading();
        atlas = assetManager.get("momentum.atlas", TextureAtlas.class);
        playerMove = new Animation<TextureRegion>(0.5f, atlas.findRegions("player_move"));
        playerDead = new Animation<TextureRegion>(0.2f, atlas.findRegions("player_dead"));
        playerHit = new Animation<TextureRegion>(0.5f, atlas.findRegions("player_hit"));
        enemy = atlas.findRegion("enemy");
        //goal = new TextureRegion(assetManager.get("goal.png", Texture.class));
    }

    public <T> T get(AssetDescriptor<T> assetDescriptor) {
        return assetManager.get(assetDescriptor);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }


}
