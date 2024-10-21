package ru.eternalhuman.streamers.progress;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
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
    private static final int ENDER_PEARLS_AMOUNT = 12, BLAZE_RODS_AMOUNT = 6;

    @Getter
    private final BossBar bossBar;
    private final OneHP oneHP;
    @Setter @Getter
    private double progress, bestProgress;
    private int enderPearls, blazeRods;
    private boolean enteredNether, foundFortress, foundPearls, foundBlazeRod, foundBucketOrObsidian, foundBed, ironIngots, stonePickaxe, leftNether, foundArrows, foundStronghold, enteredEnd, killedDragon;

    public Progress(OneHP oneHP) {
        this.oneHP = oneHP;
        bossBar = Bukkit.createBossBar("§aПрохождение", BarColor.GREEN, BarStyle.SEGMENTED_10);
        bestProgress = oneHP.getLocalConfig().getConfig().getDouble("best_progress", 0);
        bossBar.setProgress(0);
        Bukkit.getScheduler().runTaskTimer(oneHP, () -> {
            if (!foundPearls) Bukkit.getOnlinePlayers().forEach(player -> checkPearls(getItemsCount(player, Material.ENDER_PEARL)));

            if (!foundBed) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (findItem(player, "_BED")) {
                        foundBed();
                    }
                });
            }
            if (!foundBucketOrObsidian) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (findItem(player, "BUCKET") || getItemsCount(player, Material.OBSIDIAN) >= 10) {
                        foundBucketOrObsidian();
                    }
                });
            }
            if (!foundArrows) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    int totalArrows = getItemsCount(player, Material.ARROW) + getItemsCount(player, Material.SPECTRAL_ARROW);
                    if (totalArrows >= 80) {
                        foundArrows();
                    }
                });
            }
            if (!foundBlazeRod) Bukkit.getOnlinePlayers().forEach(player -> checkBlazeRods(getItemsCount(player, Material.BLAZE_ROD)));
        }, 20, 20);
    }

    private void checkPearls(int pearls) {
        if (pearls < ENDER_PEARLS_AMOUNT && !foundPearls && pearls > this.enderPearls) {
            int foundAmount = pearls - this.enderPearls;
            this.enderPearls = pearls;
            this.progress += (foundAmount * 1.25f);
            updateProgress();
            return;
        }
        if (pearls >= ENDER_PEARLS_AMOUNT) {
            foundPearls();
        }
    }

    private void checkBlazeRods(int blazeRods) {
        if (blazeRods < BLAZE_RODS_AMOUNT && !foundBlazeRod && blazeRods > this.blazeRods) {
            int foundAmount = blazeRods - this.blazeRods;
            this.blazeRods = blazeRods;
            this.progress += (foundAmount * 2.5);
            updateProgress();
            return;
        }
        if (blazeRods >= BLAZE_RODS_AMOUNT) {
            foundBlazeRods();
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || to.getWorld() == null || from.getWorld() == null) return;
        World toWorld = to.getWorld();
        World fromWorld = from.getWorld();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (toWorld.getEnvironment() == World.Environment.NETHER && fromWorld.getEnvironment() == World.Environment.NORMAL) {
                foundBucketOrObsidian();
                foundBed();
                stonePickaxe();
                ironIngots();

                enterNether();

                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Координаты портала - §2X: " + to.getBlockX() + " Y: " + to.getBlockY() + " Z: " + to.getBlockZ());
                Bukkit.broadcastMessage("");
            } else if (fromWorld.getEnvironment() == World.Environment.NETHER && toWorld.getEnvironment() == World.Environment.NORMAL) {
                if (foundBlazeRod) leftNether();
            }
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
        } if (key.equals("story/upgrade_tools")) {
            stonePickaxe();
        } if (key.equals("story/smelt_iron")) {
            ironIngots();
        } if (key.equals("adventure/sleep_in_bed")) {
            foundBed();
        } if (key.equals("story/lava_bucket")) {
            foundBucketOrObsidian();
        } else if (key.equals("story/follow_ender_eye")) {
            foundStronghold();
            foundPearls();
            foundBlazeRods();
            leftNether();
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

    public boolean findItem(Player player, String materialSuffix) {
        // Получаем инвентарь игрока
        ItemStack[] inventory = player.getInventory().getContents();

        // Перебираем все предметы в инвентаре
        for (ItemStack item : inventory) {
            // Проверяем, что предмет не null и является эндер жемчугом
            if (item != null && item.getType().name().endsWith(materialSuffix)) {
               return true;
            }
        }
        return false;
    }

    public void stonePickaxe() {
        if (stonePickaxe) return;
        stonePickaxe = true;
        progress += 1;
        updateProgress();
    }

    public void ironIngots() {
        if (ironIngots) return;
        ironIngots = true;
        progress += 2;
        updateProgress();
    }

    public void foundBed() {
        if (foundBed) return;
        foundBed = true;
        progress += 2;
        updateProgress();
    }

    public void foundBucketOrObsidian() {
        if (foundBucketOrObsidian) return;
        foundBucketOrObsidian = true;
        progress += 2;
        updateProgress();
    }

    public void enterNether() {
        if (enteredNether) return;
        enteredNether = true;
        progress += 8;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Посещение ада +8% прогресс.");
        Bukkit.broadcastMessage("");
        updateProgress();
    }

    public void foundFortress() {
        if (foundFortress) return;
        foundFortress = true;
        progress += 15;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Найдена крепость +15% прогресс.");
        Bukkit.broadcastMessage("");
        updateProgress();
    }

    public void foundBlazeRods() {
        if (foundBlazeRod) return;
        foundBlazeRod = true;
        progress -= blazeRods * 2.5;
        progress += 15;
        updateProgress();
    }

    public void foundArrows() {
        if (foundArrows) return;
        foundArrows = true;
        progress += 5;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Получены стрелы +5% прогресс.");
        Bukkit.broadcastMessage("");
        updateProgress();
    }

    public void foundPearls() {
        if (foundPearls) return;
        foundPearls = true;
        progress -= enderPearls * 1.25;
        progress += 15;
        updateProgress();
    }

    public void leftNether() {
        if (leftNether) return;
        leftNether = true;
        progress += 10;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Ад успешно пройден +10% прогресс.");
        Bukkit.broadcastMessage("");
        updateProgress();
    }

    public void foundStronghold() {
        if (foundStronghold) return;
        foundStronghold = true;
        progress += 10;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Найдена крепость в Энд +10% прогресс.");
        Bukkit.broadcastMessage("");
        updateProgress();
    }

    public void enteredEnd() {
        if (enteredEnd) return;
        enteredEnd = true;
        progress += 10;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Посещение Энда +10% прогресс.");
        Bukkit.broadcastMessage("");
        updateProgress();
    }

    public void killedDragon() {
        if (killedDragon) return;
        killedDragon = true;
        progress += 5;
        updateProgress();
    }

    private void updateProgress() {
        bossBar.setProgress(Math.min(progress / 100D, 1));
        long roundedProgress = Math.round(progress);
        long roundedBestProgress = Math.round(bestProgress);

        if (roundedProgress > roundedBestProgress) {
            oneHP.getLocalConfig().getConfig().set("best_progress", progress);
            oneHP.getLocalConfig().saveConfig();
            Bukkit.broadcastMessage(LocalConfig.CHAT_PREFIX + "Новый рекорд прогресса - " + roundedProgress + "%!");
            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1));
        }
    }
}
