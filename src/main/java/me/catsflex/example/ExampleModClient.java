package me.catsflex.example;

import me.catsflex.example.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;

public final class ExampleModClient implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		ModConfig.load();
		ModInfo.LOGGER.info("{} v{} initialized!", ModInfo.MOD_NAME, ModInfo.MOD_VERSION);
	}
}
