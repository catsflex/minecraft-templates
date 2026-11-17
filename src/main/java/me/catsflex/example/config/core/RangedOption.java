package me.catsflex.example.config.core;

import com.mojang.serialization.Codec;

public final class RangedOption<T extends Comparable<T>> extends ConfigOption<T> {
	private final T min;
	private final T max;
	
	RangedOption(String key, T defaultValue, T min, T max, Codec<T> codec) {
		super(key, defaultValue, codec, value -> clamp(value, min, max));
		
		if (min.compareTo(max) > 0) {
			throw new IllegalArgumentException("Option '" + key + "': min " + min + " is greater than max " + max);
		}
		
		this.min = min;
		this.max = max;
	}
	
	public T getMin() {
		return min;
	}
	
	public T getMax() {
		return max;
	}
	
	private static <T extends Comparable<T>> T clamp(T value, T min, T max) {
		if (value.compareTo(min) < 0) { return min; }
		if (value.compareTo(max) > 0) { return max; }
		return value;
	}
}
