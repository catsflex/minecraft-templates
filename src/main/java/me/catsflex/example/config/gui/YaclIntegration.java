package me.catsflex.example.config.gui;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import me.catsflex.example.config.ModConfig;
import me.catsflex.example.config.ModOptions;
import net.minecraft.client.gui.screens.Screen;

public final class YaclIntegration {
	private YaclIntegration() {}
	
	public static Screen createScreen(Screen parent) {
		// Options that depend on another one:
		// final var enabled = YaclHelper.tickBox(ModOptions.enabled).build();
		// final var reach = YaclHelper.dependsOn(YaclHelper.floatSlider(ModOptions.reach, 0.1F).build(), enabled);
		
		return YetAnotherConfigLib.createBuilder()
			.title(YaclHelper.title())
			
			.category(YaclHelper.category("general")
				
				.group(YaclHelper.group("main")
					.option(YaclHelper.tickBox(ModOptions.enabled).build())
					.build())
				
				.build())
			
			.save(ModConfig::save)
			.build()
			.generateScreen(parent);
	}
}
