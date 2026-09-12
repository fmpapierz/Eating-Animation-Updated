package ru.tpsd.eatinganimationmod.mixin;

import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.tpsd.eatinganimationmod.SupportPackResources;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(ClientPackSource.class)
public abstract class ClientPackSourceMixin {
    @Inject(method = "populatePackList", at = @At("TAIL"))
    private void eatinganimation$addSupportPack(BiConsumer<String, Function<String, Pack>> output, CallbackInfo ci) {
        output.accept(SupportPackResources.ID, id -> SupportPackResources.create());
    }
}
