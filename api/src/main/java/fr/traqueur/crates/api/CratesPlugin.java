package fr.traqueur.crates.api;

import fr.traqueur.crates.api.managers.Manager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CratesPlugin extends JavaPlugin {

    /**
     * Registers a manager with Bukkit's {@code ServicesManager}.
     *
     * <p>This method is called during plugin initialization to register manager
     * implementations as services. External plugins can then retrieve these services
     * to interact with zItems.</p>
     *
     * <p><b>Registration Example:</b></p>
     * <pre>{@code
     * // In plugin onEnable()
     * EffectsManager effectsManager = new ZEffectsManager(this);
     * registerManager(EffectsManager.class, effectsManager);
     *
     * ItemsManager itemsManager = new ZItemsManager(this);
     * registerManager(ItemsManager.class, itemsManager);
     * }</pre>
     *
     * <p><b>Service Priority:</b> Managers are registered with {@link ServicePriority#Normal}.
     * This allows other plugins to override the implementation if needed (though not recommended).</p>
     *
     * @param clazz   the manager interface class (e.g., {@code EffectsManager.class})
     * @param manager the concrete manager implementation instance
     * @param <I>     the manager type extending {@link Manager}
     * @return the registered manager instance (for method chaining)
     * @see Manager
     * @see org.bukkit.plugin.ServicesManager
     */
    public <I extends Manager> I registerManager(Class<I> clazz, I manager) {
        this.getServer().getServicesManager().register(clazz, manager, this, ServicePriority.Normal);
        return manager;
    }

    /**
     * Retrieves a registered manager instance from Bukkit's service system.
     *
     * <p>This is a convenience method for accessing managers within zItems code.
     * External plugins should use {@code Bukkit.getServicesManager().load()} instead.</p>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * ItemsPlugin plugin = JavaPlugin.getPlugin(ItemsPlugin.class);
     * EffectsManager manager = plugin.getManager(EffectsManager.class);
     *
     * if (manager != null) {
     *     manager.applyEffect(player, item, effect);
     * }
     * }</pre>
     *
     * @param clazz the manager interface class to retrieve
     * @param <I>   the manager type extending {@link Manager}
     * @return the manager instance, or {@code null} if not registered
     * @see #registerManager(Class, Manager)
     */
    public <I extends Manager> I getManager(Class<I> clazz) {
        var rsp = this.getServer().getServicesManager().getRegistration(clazz);
        if (rsp == null) {
            return null;
        }
        return rsp.getProvider();
    }

    protected void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
