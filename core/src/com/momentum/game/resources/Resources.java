package com.momentum.game.resources;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class Resources implements Disposable {

    private final AssetManager assetManager = new AssetManager();

    public AssetDescriptor<Texture> player = new AssetDescriptor<>("player.png", Texture.class);
    public AssetDescriptor<TiledMap> map = new AssetDescriptor<>("stage_1.tmx", TiledMap.class);

    public Resources() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(player);
        assetManager.load(map);
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
