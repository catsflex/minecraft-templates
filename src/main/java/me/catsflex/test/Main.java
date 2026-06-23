package me.catsflex.test;

import me.catsflex.test.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final String MOD_ID = "test-mod-id";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	@Override
	public void onInitialize() {
		ModConfig.getInstance().load();
		LOGGER.info("Mod initialized successfully!");
	}
}
