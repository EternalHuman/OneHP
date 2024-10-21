package ru.eternalhuman.streamers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import ru.eternalhuman.streamers.OneHP;
import ru.eternalhuman.streamers.config.LocalConfig;
import ru.eternalhuman.streamers.progress.Progress;
import ru.eternalhuman.streamers.utils.DateUtils;

public class MainListener implements Listener {
    private final LocalConfig localConfig;
    private final OneHP oneHP;
    private final Progress progress;

    public MainListener(OneHP oneHP, LocalConfig localConfig, Progress progress) {
        this.oneHP = oneHP;
        this.localConfig = localConfig;
        this.progress = progress;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.getRespawnReason() == PlayerRespawnEvent.RespawnReason.DEATH) {
            Bukkit.getScheduler().runTaskLater(oneHP, () -> event.getPlayer().setGameMode(GameMode.SPECTATOR), 1);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (this.oneHP.isReseting()) event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
    }


    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (this.oneHP.isReseting()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Сервер перезагружается");
            return;
        }
        if (Bukkit.getPlayerExact(event.getName()) != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server leak protect");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        localConfig.getSidebar().addViewer(player);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);
        progress.getBossBar().addPlayer(player);
        if (!player.hasPlayedBefore()) {
            player.sendMessage(LocalConfig.CHAT_PREFIX + "Список команд:");
            player.sendMessage(LocalConfig.CHAT_PREFIX + " - /reset - пересоздание игры и мира.");
            player.sendMessage(LocalConfig.CHAT_PREFIX + " - /tries <число> - сменить текущую попытку.");
            player.sendMessage(LocalConfig.CHAT_PREFIX + " - /changename <имя> - сменить имя игрока в меню.");
            player.sendMessage(LocalConfig.CHAT_PREFIX + " - /bestprogress <5-100> - сменить лучший прогресс.");
            player.sendMessage(LocalConfig.CHAT_PREFIX + " - /contacts - контакты разработчика для помощи.");
            player.sendMessage("");
            player.sendMessage(LocalConfig.CHAT_PREFIX + "Все миры успешно настроены под хардкор режим.");
            player.sendMessage(LocalConfig.CHAT_PREFIX + "Плагин - spigotmc.org/resources/onehp.120211/");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        localConfig.getSidebar().removeViewer(event.getPlayer());
        progress.getBossBar().removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            progress.killedDragon();
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Сид мира: " + Bukkit.getWorlds().get(0).getSeed());
            Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(player -> {
                long time = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) * 1000L;
                Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Время: " + DateUtils.getTranslatedDate(time));
            });
            Bukkit.broadcastMessage("");
        }
    }
}
