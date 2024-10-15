package ru.eternalhuman.streamers;

import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.programmatic.LiteCommand;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import ru.eternalhuman.streamers.config.LocalConfig;
import ru.eternalhuman.streamers.listeners.MainListener;
import ru.eternalhuman.streamers.utils.FileUtils;
import ru.eternalhuman.streamers.verify.Verifier;

import java.io.File;
import java.io.IOException;

@Getter
public class OneHP extends JavaPlugin {
    public static String SERVER_FOLDER;
    public static OneHP INSTANCE;

    public boolean reseting;
    private LocalConfig localConfig;

    public void onLoad() {
        getLogger().info("Loading...");
        this.localConfig = new LocalConfig(this);

        try {
            SERVER_FOLDER = new File(".").getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getLogger().info("Server folder: " + SERVER_FOLDER);
        try {
            if (this.localConfig.getConfig().getBoolean("reset_world")) {
                this.localConfig.getConfig().set("reset_world", false);
                this.localConfig.saveConfig();

                getLogger().info("Double world resetting!");
                getLogger().info("Ignore uid.dat error!");

                File directory = new File(SERVER_FOLDER + File.separator + "." + File.separator + "world");
                FileUtils.forceDelete(directory);

                File world_the_end = new File(SERVER_FOLDER + File.separator + "." + File.separator + "world_the_end");
                File world_nether = new File(SERVER_FOLDER + File.separator + "." + File.separator + "world_nether");
                FileUtils.forceDelete(world_nether);
                FileUtils.forceDelete(world_the_end);
            }
        } catch (IOException ignored) {
        }

        confirmPlayerDataFolder();
    }

    public void onEnable() {
        INSTANCE = this;

        new Verifier();
        this.localConfig.initSidebar();

        confirmPlayerDataFolder();

        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("onehp") // fallback prefix - used by bukkit to identify command
                )
                .commands(
                        new LiteCommand<CommandSender>("reset")
                                .execute(context -> {
                                    reseting = true;
                                    Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                                        this.localConfig.getConfig().set("reset_world", true);
                                        this.localConfig.getConfig().set("try_number", localConfig.getCurrentTry() + 1);
                                        this.saveConfig();
                                    });

                                    Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("Пересоздание мира, зайдите через 15 секунд."));

                                    Bukkit.getWorlds().forEach(world -> {
                                        world.setAutoSave(false);
                                        try {
                                            File file = world.getWorldFolder();
                                            FileUtils.forceDelete(file);
                                            getLogger().info("Deleted: " + file.getName());
                                            getLogger().info("File: " + file.getAbsolutePath());
                                        } catch (IOException ignored) {

                                        }
                                    });
                                    Bukkit.getScheduler().runTaskLater(this, Bukkit::shutdown, 20 * 3);
                                }),
                        new LiteCommand<CommandSender>("contacts")
                                .execute(context -> {
                                    context.invocation().sender().sendMessage("Telegram: @EternalHuman");
                                }),
                        new LiteCommand<CommandSender>("tries")
                                .argument("number", Integer.class)
                                .execute(context -> {
                                    int tries = context.argument("number", Integer.class);
                                    localConfig.getConfig().set("try_number", tries);
                                    localConfig.setCurrentTry(tries);
                                    localConfig.saveConfig();
                                    context.invocation().sender().sendMessage("Попытка изменена на " + tries + ".");
                                }),
                        new LiteCommand<CommandSender>("changename")
                                .argument("name", String.class)
                                .execute(context -> {
                                    String name = context.argument("name", String.class);
                                    localConfig.getConfig().set("scoreboard_placeholder", name);
                                    localConfig.saveConfig();
                                    localConfig.getSidebar().setTitle(localConfig.createIterator());
                                    context.invocation().sender().sendMessage("Плейсхолдер изменен на " + name + ".");
                                })
                )
                .build();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            World world = Bukkit.getWorld("world");
            if (world == null) return;

            if (Bukkit.getOnlinePlayers().isEmpty()) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            } else {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                world.setGameRule(GameRule.RANDOM_TICK_SPEED, 3);
            }
        }, 20 * 5, 20 * 5);

        Bukkit.getPluginManager().registerEvents(new MainListener(this, this.localConfig), this);
        getLogger().info("Plugin loaded successfully.");
    }

    private void confirmPlayerDataFolder() {
        try {
            new File(SERVER_FOLDER + File.separator + "." + File.separator + "world" + File.separator + "playerdata").mkdirs();
        } catch (Exception ignored) {
        }
    }
}
