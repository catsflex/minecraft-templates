package me.catsflex.example.config;

import me.catsflex.example.config.core.ConfigOption;
import me.catsflex.example.config.core.OptionRegistry;

import java.util.List;

public final class ModOptions {
	private static final OptionRegistry REGISTRY = new OptionRegistry();
	
	public static final ConfigOption<Boolean> enabled = REGISTRY.booleanOption("enabled", true);
	
	// Examples of the other option types:
	// public static final RangedOption<Integer> autoclicker = REGISTRY.intOption("autoclicker", 10, 1, 20);
	// public static final RangedOption<Float> reach = REGISTRY.floatOption("reach", 3.0F, 0.0F, 6.0F);
	// public static final ConfigOption<Integer> espColor = REGISTRY.colorOption("esp_color", 0xFFFFFFFF);
	// public static final ConfigOption<KillauraMode> killauraMode = REGISTRY.enumOption("killaura_mode", KillauraMode.LEGIT);
	
	private ModOptions() {}
	
	public static List<ConfigOption<?>> all() {
		return REGISTRY.getOptions();
	}
}
