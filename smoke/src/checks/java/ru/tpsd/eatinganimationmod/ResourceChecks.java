package ru.tpsd.eatinganimationmod;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.world.level.validation.DirectoryValidator;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public final class ResourceChecks {
    public static void run(boolean checkMixin) throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        ItemModels.bootstrap();
        ItemTintSources.bootstrap();
        ConditionalItemModelProperties.bootstrap();
        RangeSelectItemModelProperties.bootstrap();
        SelectItemModelProperties.bootstrap();
        Pack pack = SupportPackResources.create();
        require(pack != null, "Built-in support pack loads");
        require(pack.getCompatibility() == PackCompatibility.COMPATIBLE, "Support pack targets 26.2");
        require(!pack.isRequired(), "Support pack can be disabled");
        AtomicInteger supportCount = new AtomicInteger();
        try (var resources = pack.open()) {
            require(resources.getRootResource("pack.png") != null, "Support pack icon present");
            for (String namespace : resources.getNamespaces(PackType.CLIENT_RESOURCES)) {
                resources.listResources(PackType.CLIENT_RESOURCES, namespace, "items", (id, supplier) -> {
                    try (var reader = new InputStreamReader(supplier.get(), StandardCharsets.UTF_8)) {
                        ClientItem.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).getOrThrow();
                        supportCount.incrementAndGet();
                    } catch (Exception exception) { throw new AssertionError(id.toString(), exception); }
                });
            }
        }
        require(supportCount.get() == 271, "Decode all 271 mod-support item definitions");
        int vanillaCount = 0;
        try (var stream = ResourceChecks.class.getResourceAsStream("/eatinganimation-items.index")) {
            require(stream != null, "Vanilla item index present");
            for (String resource : new String(stream.readAllBytes(), StandardCharsets.UTF_8).lines().toList()) {
                try (var reader = new InputStreamReader(ResourceChecks.class.getResourceAsStream("/" + resource), StandardCharsets.UTF_8)) {
                    ClientItem.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).getOrThrow();
                    vanillaCount++;
                }
            }
        }
        require(vanillaCount == 43, "Decode all 43 vanilla item definitions");
        if (checkMixin) {
            var discovered = new ArrayList<Pack>();
            var directory = Files.createTempDirectory("eatinganimation-smoke");
            try {
                new ClientPackSource(directory, new DirectoryValidator(path -> false)).loadPacks(discovered::add);
                require(discovered.stream().anyMatch(p -> p.getId().equals(SupportPackResources.ID)),
                        "Loader applies ClientPackSourceMixin and discovers the support pack");
            } finally { Files.deleteIfExists(directory); }
        }
        System.out.println("EATING_ANIMATION_CHECKS_PASSED: 43 vanilla + 271 support models; pack compatible; mixin=" + checkMixin);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
