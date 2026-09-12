package ru.tpsd.eatinganimationmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.tpsd.eatinganimationmod.ResourceChecks;

@Mixin(targets = "net.minecraft.client.main.Main")
public abstract class NeoClientSmokeMixin {
    // NeoForge registers item-model extension events during ClientModLoader.begin.
    @Inject(method = "main", at = @At(value = "INVOKE",
            target = "Lnet/neoforged/neoforge/client/loading/ClientModLoader;begin()V", shift = At.Shift.AFTER))
    private static void eatinganimation$smoke(String[] args, CallbackInfo ci) {
        try {
            ResourceChecks.run(true);
            System.exit(0);
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        }
    }
}
