package me.catsflex.example.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	
	@Inject(method = "startAttack", at = @At("HEAD"))
	private void onStartAttack(CallbackInfoReturnable<Boolean> cir) {
		// Runs right before the player starts an attack.
	}
}
