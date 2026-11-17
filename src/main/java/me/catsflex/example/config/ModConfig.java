package me.catsflex.example.config;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import me.catsflex.example.ModInfo;
import me.catsflex.example.config.core.ConfigOption;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class ModConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private ModConfig() {}
	
	public static void load() {
		load(getPath(), ModOptions.all());
	}
	
	public static void save() {
		save(getPath(), ModOptions.all());
	}
	
	private static Path getPath() {
		return FabricLoader.getInstance().getConfigDir().resolve(ModInfo.MOD_ID + ".json");
	}
	
	// Package-private with explicit path and options for tests.
	static void load(Path path, List<ConfigOption<?>> options) {
		if (Files.notExists(path)) {
			save(path, options);
			return;
		}
		
		final JsonObject json;
		try {
			json = readJson(path);
		} catch (IOException | JsonParseException e) {
			ModInfo.LOGGER.warn("Failed to read config {}, using defaults!", path, e);
			options.forEach(ConfigOption::reset);
			save(path, options);
			return;
		}
		
		var needsSave = false;
		for (final var option : options) {
			final var element = json.get(option.getKey());
			if (element == null) {
				option.reset();
				needsSave = true;
				continue;
			}
			
			final var isRead = option.readFrom(JsonOps.INSTANCE, element, error ->
				ModInfo.LOGGER.warn("Invalid value of '{}' in config {}: {}", option.getKey(), path, error)
			);
			if (!isRead) {
				option.reset();
				needsSave = true;
			}
		}
		
		if (needsSave) {
			save(path, options);
		}
	}
	
	static void save(Path path, List<ConfigOption<?>> options) {
		final var json = new JsonObject();
		for (final var option : options) {
			option.writeTo(JsonOps.INSTANCE, error ->
				ModInfo.LOGGER.warn("Failed to write config option '{}': {}", option.getKey(), error)
			).ifPresent(element -> json.add(option.getKey(), element));
		}
		
		// Written to a temp file first, so a crash mid-save can't leave a half-written config.
		final var tempPath = path.resolveSibling(path.getFileName() + ".tmp");
		try {
			Files.createDirectories(path.toAbsolutePath().getParent());
			
			try (final var writer = Files.newBufferedWriter(tempPath)) {
				GSON.toJson(json, writer);
			}
			
			moveReplacing(tempPath, path);
		} catch (IOException e) {
			ModInfo.LOGGER.error("Failed to save config {}!", path, e);
		}
	}
	
	private static JsonObject readJson(Path path) throws IOException {
		try (final var reader = Files.newBufferedReader(path)) {
			final var element = JsonParser.parseReader(reader);
			if (!element.isJsonObject()) {
				throw new JsonParseException("Config root is not a JSON object!");
			}
			
			return element.getAsJsonObject();
		}
	}
	
	private static void moveReplacing(Path from, Path to) throws IOException {
		try {
			Files.move(from, to, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (AtomicMoveNotSupportedException e) {
			Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
		}
	}
}
