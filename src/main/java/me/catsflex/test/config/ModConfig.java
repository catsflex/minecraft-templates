package me.catsflex.test.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.catsflex.test.Main;
import me.catsflex.test.config.option.BooleanOption;
import me.catsflex.test.config.option.ConfigOption;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ModConfig {
	
	// Config saving stuff.
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String CONFIG_NAME = Main.MOD_ID + ".json";
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME);
	private static final List<ConfigOption<?>> OPTIONS = new ArrayList<>();
	private static final ModConfig INSTANCE = new ModConfig();
	
	public final BooleanOption isEnabled = new BooleanOption("isEnabled", true);
	
	private ModConfig() {}
	
	public static ModConfig getInstance() {
		return INSTANCE;
	}
	
	public static void registerOption(ConfigOption<?> option) {
		OPTIONS.add(option);
	}
	
	public void load() {
		if (!Files.exists(CONFIG_PATH)) {
			save();
			return;
		}
		
		try (final var reader = Files.newBufferedReader(CONFIG_PATH)) {
			final var element = JsonParser.parseReader(reader);
			if (!element.isJsonObject()) {
				throw new IllegalStateException("Config root is not a JSON object!");
			}
			
			final var json = element.getAsJsonObject();
			for (final var option : OPTIONS) {
				option.read(json);
			}
			
		} catch (Exception e) {
			Main.LOGGER.warn("Failed to load config, using defaults!", e);
			save();
		}
	}
	
	public void save() {
		final var json = new JsonObject();
		
		for (final var option : OPTIONS) {
			option.write(json);
		}
		
		try (final var writer = Files.newBufferedWriter(CONFIG_PATH)) {
			GSON.toJson(json, writer);
		} catch (Exception e) {
			Main.LOGGER.warn("Failed to save config!", e);
		}
	}
}
