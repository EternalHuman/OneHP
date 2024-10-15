package me.catcoder.sidebar.util.version;

import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.UUID;

public final class VersionUtil {

    public static final int SERVER_VERSION = MinecraftProtocolVersion.getCurrentVersion();

    static {
        Bukkit.getLogger().info("[ProtocolSidebar] Server version: "
                + MinecraftVersion.getCurrentVersion() + " (protocol " + SERVER_VERSION + ")");
        Bukkit.getLogger().info("[ProtocolSidebar] Please report any bugs to the developer: https://github.com/CatCoderr/ProtocolSidebar/issues");
    }

    public static int getPlayerVersion(@NonNull UUID id) {
        return SERVER_VERSION;
    }
}
