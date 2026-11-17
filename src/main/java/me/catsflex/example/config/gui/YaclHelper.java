package me.catsflex.example.config.gui;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.*;
import me.catsflex.example.config.TranslationKeys;
import me.catsflex.example.config.core.ConfigOption;
import me.catsflex.example.config.core.RangedOption;
import net.minecraft.network.chat.Component;

import java.awt.*;

public final class YaclHelper {
	private YaclHelper() {}
	
	public static Component title() {
		return Component.translatable(TranslationKeys.title());
	}
	
	public static ConfigCategory.Builder category(String key) {
		return ConfigCategory.createBuilder().name(Component.translatable(TranslationKeys.category(key)));
	}
	
	public static OptionGroup.Builder group(String key) {
		return OptionGroup.createBuilder().name(Component.translatable(TranslationKeys.group(key)));
	}
	
	public static Option.Builder<Boolean> tickBox(ConfigOption<Boolean> option) {
		return base(option)
			.controller(TickBoxControllerBuilder::create);
	}
	
	public static Option.Builder<Integer> intSlider(RangedOption<Integer> option, int step) {
		return base(option)
			.controller(opt -> IntegerSliderControllerBuilder.create(opt)
				.range(option.getMin(), option.getMax())
				.step(step)
			);
	}
	
	public static Option.Builder<Float> floatSlider(RangedOption<Float> option, float step) {
		return base(option)
			.controller(opt -> FloatSliderControllerBuilder.create(opt)
				.range(option.getMin(), option.getMax())
				.step(step)
			);
	}
	
	public static <E extends Enum<E>> Option.Builder<E> enumCycler(ConfigOption<E> option) {
		return base(option)
			.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(option.getDefault().getDeclaringClass())
				.formatValue(value -> Component.translatable(TranslationKeys.enumValue(option.getKey(), value)))
			);
	}
	
	public static Option.Builder<Color> colorPicker(ConfigOption<Integer> option, boolean allowAlpha) {
		return Option.<Color>createBuilder()
			.name(name(option))
			.description(description(option))
			.binding(
				new Color(option.getDefault(), true),
				() -> new Color(option.get(), true),
				color -> option.set(allowAlpha ? color.getRGB() : color.getRGB() | 0xFF000000)
			)
			.controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(allowAlpha));
	}
	
	public static <T> Option<T> dependsOn(Option<T> target, Option<Boolean> condition) {
		target.setAvailable(condition.pendingValue());
		condition.addEventListener((option, event) -> target.setAvailable(option.pendingValue()));
		return target;
	}
	
	private static <T> Option.Builder<T> base(ConfigOption<T> option) {
		return Option.<T>createBuilder()
			.name(name(option))
			.description(description(option))
			.binding(option.getDefault(), option::get, option::set);
	}
	
	private static Component name(ConfigOption<?> option) {
		return Component.translatable(TranslationKeys.optionName(option.getKey()));
	}
	
	private static OptionDescription description(ConfigOption<?> option) {
		return OptionDescription.of(Component.translatable(TranslationKeys.optionDescription(option.getKey())));
	}
}
