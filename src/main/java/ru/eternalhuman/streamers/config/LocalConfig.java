package ru.eternalhuman.streamers.config;

import lombok.Getter;
import lombok.Setter;
import me.catcoder.sidebar.ProtocolSidebar;
import me.catcoder.sidebar.Sidebar;
import me.catcoder.sidebar.text.TextIterator;
import me.catcoder.sidebar.text.TextIterators;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.file.FileConfiguration;
import ru.eternalhuman.streamers.OneHP;
import ru.eternalhuman.streamers.progress.Progress;
import ru.eternalhuman.streamers.utils.DateUtils;

@Getter
public class LocalConfig {
    public static final String CHAT_PREFIX = "§7[§c§l1HP§7] §a";
    private static final String LOWER_SPLITTER = "⏷", SPLITTER = "⏵", YES_EMOJI = "✔", NO_EMOJI = "✖";

    private Sidebar<Component> sidebar;
    private final FileConfiguration config;
    @Setter
    private int currentTry;
    private final OneHP oneHP;
    private long cachedWinTime = -1;

    public LocalConfig(OneHP oneHP) {
        this.oneHP = oneHP;

        oneHP.saveDefaultConfig();
        this.config = oneHP.getConfig();

        this.currentTry = this.config.getInt("try_number");
    }

    public void initSidebar() {
        sidebar = ProtocolSidebar.newAdventureSidebar(createIterator(), oneHP);

        sidebar.addLine(Component.text(LOWER_SPLITTER).color(NamedTextColor.GRAY).append(
                Component.text(" Статистика:").color(NamedTextColor.GOLD)));

        sidebar.addUpdatableLine((player) -> Component.text(" " + SPLITTER).color(NamedTextColor.GRAY).append(
                Component.text(" Попытка: ").color(NamedTextColor.GOLD)
                        .append(Component.text("#" + this.currentTry).color(NamedTextColor.GREEN))));

        Advancement advancementDragon = Bukkit.getAdvancement(NamespacedKey.minecraft("end/kill_dragon"));

        sidebar.addUpdatableLine((player) -> {
            boolean completed = player.getAdvancementProgress(advancementDragon).isDone();
            if (completed) {
                if (this.cachedWinTime == -1) this.cachedWinTime = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) * 1000L;

                return Component.text(" " + SPLITTER).color(NamedTextColor.GRAY).append(
                        Component.text(" Таймер: ").color(NamedTextColor.GOLD)
                                .append(Component.text(DateUtils.getTranslatedDate(this.cachedWinTime)).color(NamedTextColor.GREEN).decorate(TextDecoration.UNDERLINED)));
            }
            long time = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) * 1000L;
            return Component.text(" " + SPLITTER).color(NamedTextColor.GRAY).append(
                    Component.text(" Таймер: ").color(NamedTextColor.GOLD)
                            .append(Component.text(DateUtils.getTranslatedDate(time)).color(NamedTextColor.GREEN)));
        });
        sidebar.addUpdatableLine((player) -> {
            double days = player.getStatistic(Statistic.DAMAGE_TAKEN) / 10D;

            return Component.text(" " + SPLITTER).color(NamedTextColor.GRAY).append(
                    Component.text(" Получил урона: ").color(NamedTextColor.GOLD)
                            .append(Component.text(String.format("%.2f", days)).color(NamedTextColor.GREEN)));
        });
        sidebar.addUpdatableLine((player) -> {
            int mobKills = player.getStatistic(Statistic.MOB_KILLS);

            return Component.text(" " + SPLITTER).color(NamedTextColor.GRAY).append(
                    Component.text(" Убито мобов: ").color(NamedTextColor.GOLD)
                            .append(Component.text(mobKills).color(NamedTextColor.GREEN)));
        });
        sidebar.addUpdatableLine((player) -> {
            int mobKills = (int) (oneHP.getProgress().getBossBar().getProgress() * 100);

            return Component.text(" " + SPLITTER).color(NamedTextColor.GRAY).append(
                    Component.text(" Прогресс: ").color(NamedTextColor.GOLD)
                            .append(Component.text(mobKills + "%").color(NamedTextColor.GREEN)));
        });

        sidebar.addBlankLine();
        sidebar.addLine(Component.text(LOWER_SPLITTER).color(NamedTextColor.GRAY).append(
                Component.text(" Достижения:").color(NamedTextColor.GOLD)));

        Advancement advancementNether = Bukkit.getAdvancement(NamespacedKey.minecraft("story/enter_the_nether"));
        sidebar.addUpdatableLine((player) -> {
            boolean completed = player.getAdvancementProgress(advancementNether).isDone();

            return Component.text(" " + SPLITTER).color(NamedTextColor.GRAY).append(
                    Component.text(" Посетил ад: ").color(NamedTextColor.GOLD)
                            .append(Component.text(completed ? YES_EMOJI : NO_EMOJI).color(completed ? NamedTextColor.GREEN : NamedTextColor.RED)));
        });

        Advancement advancementNetherFortress = Bukkit.getAdvancement(NamespacedKey.minecraft("nether/find_fortress"));
        sidebar.addUpdatableLine((player) -> {
            boolean completed = player.getAdvancementProgress(advancementNetherFortress ).isDone();

            return Component.text(" " + SPLITTER).color(NamedTextColor.GRAY).append(
                    Component.text(" Нашел крепость: ").color(NamedTextColor.GOLD)
                            .append(Component.text(completed ? YES_EMOJI : NO_EMOJI).color(completed ? NamedTextColor.GREEN : NamedTextColor.RED)));
        });
        Advancement advancementEnd = Bukkit.getAdvancement(NamespacedKey.minecraft("story/enter_the_end"));
        sidebar.addUpdatableLine((player) -> {
            boolean completed = player.getAdvancementProgress(advancementEnd).isDone();

            return Component.text(" " + SPLITTER).color(NamedTextColor.GRAY).append(
                    Component.text(" Посетил Энд: ").color(NamedTextColor.GOLD)
                            .append(Component.text(completed ? YES_EMOJI : NO_EMOJI).color(completed ? NamedTextColor.GREEN : NamedTextColor.RED)));
        });

        sidebar.addUpdatableLine((player) -> {
            boolean completed = player.getAdvancementProgress(advancementDragon).isDone();

            return Component.text(" " + SPLITTER).color(NamedTextColor.GRAY).append(
                    Component.text(" Убил дракона: ").color(NamedTextColor.GOLD)
                            .append(Component.text(completed ? YES_EMOJI : NO_EMOJI).color(completed ? NamedTextColor.GREEN : NamedTextColor.RED)));
        });
        sidebar.addBlankLine();
        sidebar.addLine(Component.text(" by @EternalHuman").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.ITALIC));

        sidebar.getObjective().scoreNumberFormatBlank();

        sidebar.updateLinesPeriodically(20, 20);
    }

    public TextIterator createIterator() {
        return TextIterators
                .textFade(this.config.getString("scoreboard_placeholder") + "'s Hardcore",
                        ChatColor.RED, ChatColor.GOLD, ChatColor.DARK_RED);
    }

    public void saveConfig() {
        this.oneHP.saveConfig();
    }
}
