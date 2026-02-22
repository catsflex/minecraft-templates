package me.catsflex.test.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.catsflex.test.util.Helper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class TestConfig {
	
	// Default values
	public static final boolean DEF_IS_ENABLED = true;
	public static final boolean DEF_IS_DEBUG_MODE = false;
	
	// Current values
	public boolean isEnabled = DEF_IS_ENABLED;
	public boolean isDebugMode = DEF_IS_DEBUG_MODE;
	
	// Config saving stuff
	private static final Gson _GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path _CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(Helper.CONFIG_NAME);
	
	private static TestConfig _instance;
	
	public static TestConfig getInstance() {
		if (_instance == null) {
			_instance = load();
		}
		return _instance;
	}
	
	public static TestConfig load() {
		if (Files.exists(_CONFIG_PATH)) {
			try (var reader = Files.newBufferedReader(_CONFIG_PATH)) {
				var loaded = _GSON.fromJson(reader, TestConfig.class);
				if (loaded != null) {
					return loaded;
				}
			} catch (Exception e) {
				Helper.LOGGER.warn("{} Failed to load config, using defaults!", Helper.MOD_PREFIX, e);
			}
		}
		var config = new TestConfig();
		config.save();
		return config;
	}
	
	public void save() {
		try (var writer = Files.newBufferedWriter(_CONFIG_PATH)) {
			_GSON.toJson(this, writer);
		} catch (Exception e) {
			Helper.LOGGER.warn("{} Failed to save config!", Helper.MOD_PREFIX, e);
		}
	}
}
