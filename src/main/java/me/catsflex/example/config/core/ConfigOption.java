package me.catsflex.example.config.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ConfigOption<T> {
	private final String key;
	private final T defaultValue;
	private final Codec<T> codec;
	private final UnaryOperator<T> sanitizer;
	private final List<Consumer<T>> listeners = new ArrayList<>();
	private T value;
	
	ConfigOption(String key, T defaultValue, Codec<T> codec, UnaryOperator<T> sanitizer) {
		this.key = Objects.requireNonNull(key, "key");
		this.codec = Objects.requireNonNull(codec, "codec");
		this.sanitizer = Objects.requireNonNull(sanitizer, "sanitizer");
		this.defaultValue = sanitizer.apply(Objects.requireNonNull(defaultValue, "defaultValue"));
		this.value = this.defaultValue;
	}
	
	public String getKey() {
		return key;
	}
	
	public T get() {
		return value;
	}
	
	public T getDefault() {
		return defaultValue;
	}
	
	public void set(T newValue) {
		final var sanitized = sanitizer.apply(Objects.requireNonNull(newValue, "newValue"));
		if (Objects.equals(value, sanitized)) { return; }
		
		value = sanitized;
		for (final var listener : listeners) {
			listener.accept(sanitized);
		}
	}
	
	public void reset() {
		set(defaultValue);
	}
	
	public void onChange(Consumer<T> listener) {
		listeners.add(Objects.requireNonNull(listener, "listener"));
	}
	
	public <R> boolean readFrom(DynamicOps<R> ops, R input, Consumer<String> onError) {
		final var parsed = codec.parse(ops, input).resultOrPartial(onError);
		parsed.ifPresent(this::set);
		return parsed.isPresent();
	}
	
	public <R> Optional<R> writeTo(DynamicOps<R> ops, Consumer<String> onError) {
		return codec.encodeStart(ops, value).resultOrPartial(onError);
	}
}
