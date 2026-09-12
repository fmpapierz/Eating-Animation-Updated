package ru.tpsd.eatinganimationmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.tpsd.eatinganimationmod.ResourceChecks;

@Mixin(targets = "net.minecraft.client.main.Main")
public abstract class ClientSmokeMixin {
    @Inject(method = "main", at = @At("HEAD"))
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
