package me.catsflex.example.config.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.catsflex.example.config.TranslationKeys;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public final class ModMenuIntegration implements ModMenuApi {
	private static final String YACL_MOD_ID = "yet_another_config_lib_v3";
	
	@Override
	public @NonNull ConfigScreenFactory<?> getModConfigScreenFactory() {
		return FabricLoader.getInstance().isModLoaded(YACL_MOD_ID)
			? YaclIntegration::createScreen
			: ModMenuIntegration::createYaclMissingScreen;
	}
	
	private static Screen createYaclMissingScreen(Screen parent) {
		return new AlertScreen(
			() -> Minecraft.getInstance().gui.setScreen(parent),
			Component.translatable(TranslationKeys.yaclMissingTitle()),
			Component.translatable(TranslationKeys.yaclMissingMessage())
		);
	}
}
