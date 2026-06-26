package me.catsflex.test.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class TestMixin {
	
	@Inject(method = "startAttack", at = @At("HEAD"))
	public void doNothing(CallbackInfoReturnable<Boolean> cir) {
		return;
	}
}
