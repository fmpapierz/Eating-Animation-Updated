package ru.tpsd.eatinganimationmod;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Built-in pack backed by classpath resources, in development and packaged JARs. */
public final class SupportPackResources extends AbstractPackResources {
    public static final String ID = "eatinganimationid:supporteatinganimation";
    private static final String ROOT = "/resourcepacks/supporteatinganimation/";
    private static final Set<String> FILES = readIndex();

    public SupportPackResources(PackLocationInfo location) { super(location); }

    private static Set<String> readIndex() {
        try (InputStream stream = SupportPackResources.class.getResourceAsStream(ROOT + "resources.index")) {
            if (stream == null) throw new IllegalStateException("Missing Eating Animation resource index");
            return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.toUnmodifiableSet());
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Cannot read Eating Animation resource index", exception);
        }
    }

    public static Pack create() {
        PackLocationInfo location = new PackLocationInfo(ID, Component.literal("Eating Animation - mod support"),
                PackSource.BUILT_IN, Optional.empty());
        Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
            public PackResources openPrimary(PackLocationInfo info) { return new SupportPackResources(info); }
            public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) { return openPrimary(info); }
        };
        return Pack.readMetaAndCreate(location, supplier, PackType.CLIENT_RESOURCES,
                new PackSelectionConfig(false, Pack.Position.TOP, false));
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... segments) { return resource(String.join("/", segments)); }

    private IoSupplier<InputStream> resource(String path) {
        if (!FILES.contains(path)) return null;
        return () -> {
            InputStream stream = SupportPackResources.class.getResourceAsStream(ROOT + path);
            if (stream == null) throw new java.io.FileNotFoundException(ROOT + path);
            return stream;
        };
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, Identifier id) {
        return resource(type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath());
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        String root = type.getDirectory() + "/" + namespace + "/";
        String prefix = root + (path.isEmpty() ? "" : path + "/");
        for (String file : FILES) {
            if (file.startsWith(prefix)) {
                Identifier id = Identifier.tryBuild(namespace, file.substring(root.length()));
                if (id != null) output.accept(id, resource(file));
            }
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        String prefix = type.getDirectory() + "/";
        return FILES.stream().filter(file -> file.startsWith(prefix))
                .map(file -> file.substring(prefix.length()).split("/", 2)[0]).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void close() { }
}
