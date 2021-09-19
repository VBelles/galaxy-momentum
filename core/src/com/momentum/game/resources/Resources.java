package com.momentum.game.resources;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class Resources implements Disposable {

    private final AssetManager assetManager = new AssetManager();

    public AssetDescriptor<Texture> player = new AssetDescriptor<>("badlogic.jpg", Texture.class);

    public Resources() {
        assetManager.load(player);
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
