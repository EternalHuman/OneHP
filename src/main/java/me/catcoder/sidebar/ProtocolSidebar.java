package me.catcoder.sidebar;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.catcoder.sidebar.text.TextIterator;
import me.catcoder.sidebar.text.TextProvider;
import me.catcoder.sidebar.text.provider.AdventureTextProvider;
import me.catcoder.sidebar.text.provider.BungeeCordChatTextProvider;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.plugin.Plugin;

/**
 * Entry point class for creating new sidebars.
 * <p>
 * This class provides methods for creating new sidebars with different text providers.
 * <p>
 *
 * @author CatCoder
 */
@UtilityClass
public class ProtocolSidebar {

    /**
     * Creates new sidebar with custom text provider.
     *
     * @param title        - sidebar title
     * @param plugin       - plugin instance
     * @param textProvider - text provider
     * @param <R>          - component entity type
     * @return new sidebar
     */
    public <R> Sidebar<R> newSidebar(
            @NonNull R title,
            @NonNull Plugin plugin,
            @NonNull TextProvider<R> textProvider
    ) {
        return new Sidebar<>(title, plugin, textProvider);
    }

    /**
     * {@inheritDoc}
     */
    public <R> Sidebar<R> newSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin,
            @NonNull TextProvider<R> textProvider
    ) {
        return new Sidebar<>(title, plugin, textProvider);
    }



    /**
     * Creates new sidebar with {@see https://docs.advntr.dev/getting-started.html Adventure} text provider.
     * Adventure are natively supported on Paper 1.16.5+.
     *
     * @param title  - sidebar title
     * @param plugin - plugin instance
     * @return new sidebar
     */
    public Sidebar<Component> newAdventureSidebar(
            @NonNull Component title,
            @NonNull Plugin plugin
    ) {
        return newSidebar(title, plugin, new AdventureTextProvider());
    }

    /**
     * {@inheritDoc}
     */
    public Sidebar<Component> newAdventureSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin
    ) {
        return newSidebar(title, plugin, new AdventureTextProvider());
    }

    /**
     * Creates new sidebar with BungeeCord Chat API text provider.
     * Use this method if you're running on Spigot.
     *
     * @param title  - sidebar title
     * @param plugin - plugin instance
     * @return new sidebar
     */
    public Sidebar<BaseComponent[]> newBungeeChatSidebar(
            @NonNull BaseComponent[] title,
            @NonNull Plugin plugin
    ) {
        return newSidebar(title, plugin, new BungeeCordChatTextProvider());
    }

    /**
     * {@inheritDoc}
     */
    public Sidebar<BaseComponent[]> newBungeeChatSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin
    ) {
        return newSidebar(title, plugin, new BungeeCordChatTextProvider());
    }
}
