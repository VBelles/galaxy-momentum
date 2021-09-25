package com.momentum.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.momentum.game.Game;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
                GwtApplicationConfiguration configuration = new GwtApplicationConfiguration(true);
                configuration.padHorizontal = 0;
                configuration.padVertical = 0;
                configuration.fullscreenOrientation = GwtGraphics.OrientationLockType.LANDSCAPE;
                return configuration;
                // Fixed size application:
                //return new GwtApplicationConfiguration(1280, 720);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new Game();
        }
}