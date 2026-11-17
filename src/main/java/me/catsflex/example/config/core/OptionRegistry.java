package me.catsflex.example.config.core;

import com.mojang.serialization.Codec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public final class OptionRegistry {
	private final Map<String, ConfigOption<?>> options = new LinkedHashMap<>();
	
	public ConfigOption<Boolean> booleanOption(String key, boolean defaultValue) {
		return option(key, defaultValue, Codec.BOOL);
	}
	
	public RangedOption<Integer> intOption(String key, int defaultValue, int min, int max) {
		return register(new RangedOption<>(key, defaultValue, min, max, Codec.INT));
	}
	
	public RangedOption<Float> floatOption(String key, float defaultValue, float min, float max) {
		return register(new RangedOption<>(key, defaultValue, min, max, Codec.FLOAT));
	}
	
	public <E extends Enum<E>> ConfigOption<E> enumOption(String key, E defaultValue) {
		return option(key, defaultValue, ConfigCodecs.enumCodec(defaultValue.getDeclaringClass()));
	}
	
	public ConfigOption<Integer> colorOption(String key, int defaultArgb) {
		return option(key, defaultArgb, ConfigCodecs.ARGB_HEX);
	}
	
	public <T> ConfigOption<T> option(String key, T defaultValue, Codec<T> codec) {
		return register(new ConfigOption<>(key, defaultValue, codec, UnaryOperator.identity()));
	}
	
	public List<ConfigOption<?>> getOptions() {
		return List.copyOf(options.values());
	}
	
	private <O extends ConfigOption<?>> O register(O option) {
		if (options.putIfAbsent(option.getKey(), option) != null) {
			throw new IllegalArgumentException("Duplicate config option key: " + option.getKey());
		}
		
		return option;
	}
}
