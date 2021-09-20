package com.momentum.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.momentum.game.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Galaxy Momentum";
		config.width = 16 * 80;
		config.height = 9 * 80;
		new LwjglApplication(new Game(), config);
	}
}
