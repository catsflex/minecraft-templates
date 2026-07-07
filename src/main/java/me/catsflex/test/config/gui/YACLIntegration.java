package me.catsflex.test.config.gui;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import me.catsflex.test.config.ModConfig;
import net.minecraft.client.gui.screens.Screen;

public final class YACLIntegration {
	private YACLIntegration() {}
	
	public static Screen createScreen(Screen parent) {
		final var config = ModConfig.getInstance();
		
		return YetAnotherConfigLib.createBuilder().title(YACLHelper.createTitle())
			
			.category(YACLHelper.createCategory("general")
				
				.group(YACLHelper.createGroup("main")
					.option(YACLHelper.tickBoxOption(config.isEnabled))
					.build())
				
				.build())
			
			.save(config::save)
			.build()
			.generateScreen(parent);
	}
}
