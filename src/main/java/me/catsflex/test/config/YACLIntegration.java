package me.catsflex.test.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class YACLIntegration {
	public static Screen createScreen(Screen parent) {
		var config = TestConfig.getInstance();
		
		return YetAnotherConfigLib.createBuilder()
			.title(Component.translatable("config.title"))
			
			// 'General' category
			.category(ConfigCategory.createBuilder().name(Component.translatable("config.category.general"))
				
				// 'Enabled' option
				.option(Option.<Boolean>createBuilder()
					.name(Component.translatable("config.option.enabled.name"))
					.description(OptionDescription.of(Component.translatable("config.option.enabled.desc")))
					.binding(TestConfig.DEF_ENABLED, () -> config.enabled, v -> config.enabled = v)
					.controller(TickBoxControllerBuilder::create)
					.build())
				
				// 'Debug mode' option
				.option(Option.<Boolean>createBuilder()
					.name(Component.translatable("config.option.debug.name"))
					.description(OptionDescription.of(Component.translatable("config.option.debug.desc")))
					.binding(TestConfig.DEF_DEBUG_MODE, () -> config.debugMode, v -> config.debugMode = v)
					.controller(TickBoxControllerBuilder::create)
					.build())
				
				.build())
			.save(config::save)
			.build()
			.generateScreen(parent);
	}
}
