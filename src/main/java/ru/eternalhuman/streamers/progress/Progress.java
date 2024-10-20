package ru.eternalhuman.streamers.progress;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import ru.eternalhuman.streamers.OneHP;
import ru.eternalhuman.streamers.config.LocalConfig;

public class Progress implements Listener {
    @Getter
    private final BossBar bossBar;
    private float progress;
    private boolean enteredNether, foundFortress, foundPearls, foundBlazeRod, leftNether, foundArrows, foundStronghold, enteredEnd, killedDragon;

    public Progress(OneHP oneHP) {
        bossBar = Bukkit.createBossBar("§aПрохождение", BarColor.GREEN, BarStyle.SEGMENTED_10);
        bossBar.setProgress(0);
        Bukkit.getScheduler().runTaskTimer(oneHP, () -> {
            if (!foundPearls) {
                Bukkit.getOnlinePlayers().forEach(player -> checkPearls(getItemsCount(player, Material.ENDER_PEARL)));
            }
            if (!foundArrows) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (getItemsCount(player, Material.ARROW) >= 80) {
                        foundArrows();
                    }
                });
            }
            if (!foundBlazeRod) {
                Bukkit.getOnlinePlayers().forEach(player -> checkBlazeRods(getItemsCount(player, Material.BLAZE_ROD)));
            }
        }, 20, 20);
    }

    private void checkPearls(int pearls) {
        if (pearls >= 14) {
            foundPearls();
        }
    }

    private void checkBlazeRods(int blazeRods) {
        if (blazeRods >= 7) {
            foundBlazeRods();
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || to.getWorld() == null) return;
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL &&
                from.getWorld().getEnvironment() == World.Environment.NETHER && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
            if (getItemsCount(event.getPlayer(), Material.ENDER_PEARL) >= 12) foundPearls();
            if (getItemsCount(event.getPlayer(), Material.BLAZE_ROD) >= 6) foundBlazeRods();

            if (foundBlazeRod && foundPearls) leftNether();
        } else if (to.getWorld().getEnvironment() == World.Environment.THE_END) {
            enteredEnd();
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        String key = advancement.getKey().getKey();
        if (key.equals("nether/find_fortress")) {
            foundFortress();
        } else if (key.equals("story/follow_ender_eye")) {
            foundStronghold();
            foundPearls();
            foundBlazeRods();
        } else if (key.equals("story/enter_the_nether")) {
            enterNether();
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack itemStack = event.getItem().getItemStack();
            if (itemStack.getType() == Material.ENDER_PEARL) {
                checkPearls(getItemsCount(player, Material.ENDER_PEARL) + itemStack.getAmount());
            } else if (itemStack.getType() == Material.BLAZE_ROD) {
                checkBlazeRods(getItemsCount(player, Material.BLAZE_ROD) + itemStack.getAmount());
            }
        }
    }

    public int getItemsCount(Player player, Material material) {
        int count = 0;

        // Получаем инвентарь игрока
        ItemStack[] inventory = player.getInventory().getContents();

        // Перебираем все предметы в инвентаре
        for (ItemStack item : inventory) {
            // Проверяем, что предмет не null и является эндер жемчугом
            if (item != null && item.getType() == material) {
                count += item.getAmount(); // Добавляем количество эндер жемчугов
            }
        }

        return count; // Возвращаем количество эндер жемчугов
    }

    public void enterNether() {
        if (enteredNether) return;
        enteredNether = true;
        progress += 0.15f;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Посещение ада +15% прогресс.");
        Bukkit.broadcastMessage("");
        updateBossBar();
    }

    public void foundFortress() {
        if (foundFortress) return;
        foundFortress = true;
        progress += 0.15f;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Найдена крепость +15% прогресс.");
        Bukkit.broadcastMessage("");
        updateBossBar();
    }

    public void foundBlazeRods() {
        if (foundBlazeRod) return;
        foundBlazeRod = true;
        progress += 0.15f;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Получены стержни ифрита +15% прогресс.");
        Bukkit.broadcastMessage("");
        updateBossBar();
    }

    public void foundArrows() {
        if (foundArrows) return;
        foundArrows = true;
        progress += 0.05f;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Получены стрелы +5% прогресс.");
        Bukkit.broadcastMessage("");
        updateBossBar();
    }

    public void foundPearls() {
        if (foundPearls) return;
        foundPearls = true;
        progress += 0.15f;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Получен жемчуг эндера +15% прогресс.");
        Bukkit.broadcastMessage("");
        updateBossBar();
    }

    public void leftNether() {
        if (leftNether) return;
        leftNether = true;
        progress += 0.10f;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Ад успешно пройден +10% прогресс.");
        Bukkit.broadcastMessage("");
        updateBossBar();
    }

    public void foundStronghold() {
        if (foundStronghold) return;
        foundStronghold = true;
        progress += 0.10f;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Найдена крепость в Энд +10% прогресс.");
        Bukkit.broadcastMessage("");
        updateBossBar();
    }

    public void enteredEnd() {
        if (enteredEnd) return;
        enteredEnd = true;
        progress += 0.10f;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Посещение Энда +10% прогресс.");
        Bukkit.broadcastMessage("");
        updateBossBar();
    }

    public void killedDragon() {
        if (killedDragon) return;
        killedDragon = true;
        progress += 0.05f;
        updateBossBar();
    }

    private void updateBossBar() {
        bossBar.setProgress(Math.min(progress, 1));
    }
}
