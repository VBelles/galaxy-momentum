package com.momentum.game.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
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
    public Animation<TextureRegion> goal;
    public TextureRegion enemy;
    public TextureRegion blackHole;
    public TextureRegion veil;

    public List<Sound> bounceSounds = new ArrayList<>();
    public Sound switchSound;
    public Sound killerSound;
    public Sound gravityOn;
    public Sound gravityOff;
    public Sound goalSound;
    private Music music;
    private int musicIndex = -1;


    static class LoaderHandler<T> {
        public String fileName;
        public LoadListener<T> listener;
    }

    interface LoadListener<T> {
        void onLoad(T asset);
    }

    private final List<LoaderHandler<Object>> listeners = new ArrayList<>();

    public Resources() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoaderText());
        FileHandle fileHandle = Gdx.files.internal("stages/stages.txt");
        for (String stage : fileHandle.readString().split("\n")) {
            AssetDescriptor<TiledMap> descriptor = new AssetDescriptor<>("stages/" + stage.trim(), TiledMap.class);
            assetManager.load(descriptor);
            stages.add(descriptor);
        }

        load("momentum.atlas", TextureAtlas.class, (atlas) -> {
            playerMove = new Animation<>(0.5f, atlas.findRegions("player_move"));
            playerDead = new Animation<>(0.2f, atlas.findRegions("player_dead"));
            playerHit = new Animation<>(0.5f, atlas.findRegions("player_hit"));
            enemy = atlas.findRegion("enemy");
        });
        load("portal.png", Texture.class, (asset) -> {
            TextureRegion[][] portalRegions = new TextureRegion(asset)
                    .split(asset.getWidth() / 8, asset.getHeight() / 3);
            goal = new Animation<>(0.16f, portalRegions[0][0], portalRegions[0][1], portalRegions[0][2],
                    portalRegions[0][3], portalRegions[0][4], portalRegions[0][5], portalRegions[0][6], portalRegions[0][7]);
        });
        load("black_hole_area.png", Texture.class, (asset) -> blackHole = new TextureRegion(asset));
        load("veil.png", Texture.class, (asset) -> veil = new TextureRegion(asset));
        load("sound/bounce_phaser00.mp3", Sound.class, (sound) -> bounceSounds.add(sound));
        load("sound/bounce_phaser01.mp3", Sound.class, (sound) -> bounceSounds.add(sound));
        load("sound/bounce_phaser02.mp3", Sound.class, (sound) -> bounceSounds.add(sound));
        load("sound/bounce_phaser03.mp3", Sound.class, (sound) -> bounceSounds.add(sound));
        load("sound/bounce_phaser04.mp3", Sound.class, (sound) -> bounceSounds.add(sound));
        load("sound/killer2.mp3", Sound.class, (sound) -> killerSound = sound);
        load("sound/switch.mp3", Sound.class, (sound) -> switchSound = sound);
        load("sound/gravity_on_phaser.mp3", Sound.class, (sound) -> gravityOn = sound);
        load("sound/gravity_off_phaser.mp3", Sound.class, (sound) -> gravityOff = sound);
        load("sound/goal_echo.mp3", Sound.class, (sound) -> goalSound = sound);
    }

    private <T> void load(String fileName, Class<T> type, LoadListener<T> listener) {
        assetManager.load(fileName, type);
        LoaderHandler<Object> loaderHandler = new LoaderHandler<>();
        loaderHandler.fileName = fileName;
        //noinspection unchecked
        loaderHandler.listener = (LoadListener<Object>) listener;
        listeners.add(loaderHandler);
    }

    public void finishLoading() {
        assetManager.finishLoading();
        for (LoaderHandler<Object> listener : listeners) {
            listener.listener.onLoad(assetManager.get(listener.fileName));
        }
    }

    public <T> T get(AssetDescriptor<T> assetDescriptor) {
        return assetManager.get(assetDescriptor);
    }

    public void playMusic(int index) {
        if(index == musicIndex){
            return;
        }
        if (music != null) {
            music.stop();
            music.dispose();
        }
        musicIndex = index;
        music = Gdx.audio.newMusic(Gdx.files.internal("music/momentum_" + index + ".mp3"));
        music.setLooping(true);
        music.play();
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }


}
