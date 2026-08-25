package me.catsflex.test;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {
	public static final String PLUGIN_ID = "test-plugin-id";
	public static final Logger LOGGER = JavaPlugin.getPlugin(Main.class).getLogger();
	
	@Override
	public void onEnable() {
		// Plugin startup logic.
	}
	
	@Override
	public void onDisable() {
		// Plugin shutdown logic.
	}
}
