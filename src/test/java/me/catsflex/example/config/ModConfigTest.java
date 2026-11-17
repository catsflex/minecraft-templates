package me.catsflex.example.config;

import me.catsflex.example.config.core.ConfigOption;
import me.catsflex.example.config.core.OptionRegistry;
import me.catsflex.example.config.core.RangedOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ModConfigTest {
	
	@TempDir
	Path dir;
	
	private Path path;
	private OptionRegistry registry;
	private ConfigOption<Boolean> flag;
	private RangedOption<Integer> count;
	private ConfigOption<Integer> color;
	private ConfigOption<Mode> mode;
	
	enum Mode { SIMPLE, FANCY }
	
	@BeforeEach
	void setUp() {
		path = dir.resolve("test.json");
		registry = new OptionRegistry();
		flag = registry.booleanOption("flag", true);
		count = registry.intOption("count", 5, 0, 10);
		color = registry.colorOption("color", 0xFF112233);
		mode = registry.enumOption("mode", Mode.SIMPLE);
	}
	
	@Test
	void savedValuesAreLoadedBack() throws IOException {
		flag.set(false);
		count.set(7);
		color.set(0x80FFFFFF);
		mode.set(Mode.FANCY);
		ModConfig.save(path, registry.getOptions());
		
		registry.getOptions().forEach(ConfigOption::reset);
		ModConfig.load(path, registry.getOptions());
		
		assertFalse(flag.get());
		assertEquals(7, count.get());
		assertEquals(0x80FFFFFF, color.get());
		assertEquals(Mode.FANCY, mode.get());
		assertTrue(Files.readString(path).contains("\"#80FFFFFF\""));
		assertTrue(Files.readString(path).contains("\"fancy\""));
	}
	
	@Test
	void invalidValuesFallBackToDefaults() throws IOException {
		Files.writeString(path, "{\"flag\": false, \"count\": \"abc\", \"color\": \"red\", \"mode\": \"nope\"}");
		
		ModConfig.load(path, registry.getOptions());
		
		assertFalse(flag.get());
		assertEquals(5, count.get());
		assertEquals(0xFF112233, color.get());
		assertEquals(Mode.SIMPLE, mode.get());
	}
	
	@Test
	void brokenJsonIsReplacedWithDefaults() throws IOException {
		Files.writeString(path, "{ not json");
		
		ModConfig.load(path, registry.getOptions());
		
		assertTrue(flag.get());
		assertTrue(Files.readString(path).contains("\"flag\": true"));
	}
	
	@Test
	void outOfRangeValueIsClamped() throws IOException {
		Files.writeString(path, "{\"count\": 100}");
		
		ModConfig.load(path, registry.getOptions());
		
		assertEquals(10, count.get());
	}
	
	@Test
	void listenersFireOnlyOnRealChanges() {
		final var calls = new int[1];
		flag.onChange(value -> calls[0]++);
		
		flag.set(true);
		flag.set(false);
		flag.set(false);
		
		assertEquals(1, calls[0]);
	}
}
