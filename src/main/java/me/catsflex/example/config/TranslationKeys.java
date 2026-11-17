package me.catsflex.example.config;

import me.catsflex.example.ModInfo;
import me.catsflex.example.config.core.ConfigCodecs;

public final class TranslationKeys {
	private static final String CONFIG_PREFIX = "config." + ModInfo.MOD_ID + ".";
	
	private TranslationKeys() {}
	
	public static String title() {
		return CONFIG_PREFIX + "title";
	}
	
	public static String category(String key) {
		return CONFIG_PREFIX + "category." + key;
	}
	
	public static String group(String key) {
		return CONFIG_PREFIX + "group." + key;
	}
	
	public static String optionName(String optionKey) {
		return CONFIG_PREFIX + "option." + optionKey + ".name";
	}
	
	public static String optionDescription(String optionKey) {
		return CONFIG_PREFIX + "option." + optionKey + ".description";
	}
	
	public static String enumValue(String optionKey, Enum<?> value) {
		return CONFIG_PREFIX + "option." + optionKey + ".value." + ConfigCodecs.serializedName(value);
	}
	
	public static String yaclMissingTitle() {
		return CONFIG_PREFIX + "yacl_missing.title";
	}
	
	public static String yaclMissingMessage() {
		return CONFIG_PREFIX + "yacl_missing.message";
	}
}
