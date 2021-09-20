package com.momentum.game.resources;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

public class Resources implements Disposable {

    private final AssetManager assetManager = new AssetManager();

    public AssetDescriptor<Texture> player = new AssetDescriptor<>("player.png", Texture.class);

    public List<AssetDescriptor<TiledMap>> stages = new ArrayList<>();

    public Resources() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(player);
        for (int i = 0; i < 2; i++) {
            stages.add(new AssetDescriptor<>("stage_" + i + ".tmx", TiledMap.class));
            assetManager.load(stages.get(i));
        }
    }

    public void finishLoading() {
        assetManager.finishLoading();

    }

    public <T> T get(AssetDescriptor<T> assetDescriptor) {
        return assetManager.get(assetDescriptor);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }


}
