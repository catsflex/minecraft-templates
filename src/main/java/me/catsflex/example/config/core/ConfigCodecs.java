package me.catsflex.example.config.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public final class ConfigCodecs {
	private ConfigCodecs() {}
	
	public static final Codec<Integer> ARGB_HEX = Codec.STRING.comapFlatMap(
		ConfigCodecs::parseArgbHex,
		argb -> String.format("#%08X", argb)
	);
	
	public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass) {
		return Codec.STRING.comapFlatMap(name -> parseEnum(enumClass, name), ConfigCodecs::serializedName);
	}
	
	public static String serializedName(Enum<?> value) {
		return value instanceof StringRepresentable representable
			? representable.getSerializedName()
			: value.name().toLowerCase(Locale.ROOT);
	}
	
	private static <E extends Enum<E>> DataResult<E> parseEnum(Class<E> enumClass, String name) {
		for (final var value : enumClass.getEnumConstants()) {
			if (serializedName(value).equals(name)) { return DataResult.success(value); }
		}
		
		return DataResult.error(() -> "Unknown " + enumClass.getSimpleName() + " value: " + name);
	}
	
	private static DataResult<Integer> parseArgbHex(String text) {
		if (text.length() != 9 || text.charAt(0) != '#') {
			return DataResult.error(() -> "Expected a #AARRGGBB color, got: " + text);
		}
		
		try {
			return DataResult.success(Integer.parseUnsignedInt(text, 1, text.length(), 16));
		} catch (NumberFormatException e) {
			return DataResult.error(() -> "Invalid hex color: " + text);
		}
	}
}
