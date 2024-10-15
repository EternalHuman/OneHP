package ru.eternalhuman.streamers.verify;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;

public class Verifier {

    public Verifier() {
        Bukkit.getWorlds().forEach(world -> {
            world.setHardcore(true);
            world.setAutoSave(true);
            world.setDifficulty(Difficulty.HARD);
        });
        Bukkit.getServer().setSpawnRadius(0);
        Bukkit.getServer().setDefaultGameMode(GameMode.SURVIVAL);
        Bukkit.getLogger().fine("All worlds hardcore verified!");
    }
}
