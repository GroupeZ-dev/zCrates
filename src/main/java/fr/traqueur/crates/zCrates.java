package fr.traqueur.crates;

import fr.traqueur.commands.spigot.CommandManager;
import fr.traqueur.crates.animations.ZAnimationEngine;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.registries.AnimationsRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.crates.api.services.MessagesService;
import fr.traqueur.crates.api.settings.Settings;
import fr.traqueur.crates.commands.ZCratesCommand;
import fr.traqueur.crates.commands.handler.CommandsMessageHandler;
import fr.traqueur.crates.registries.ZAnimationRegistry;
import fr.traqueur.crates.settings.PluginSettings;
import fr.traqueur.structura.api.Structura;
import fr.traqueur.structura.exceptions.StructuraException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class zCrates extends CratesPlugin {

    private static final String CONFIG_FILE = "config.yml";
    private static final String MESSAGES_FILE = "messages.yml";
    private static final String ANIMATIONS_FOLDER = "animations";

    private ZAnimationEngine animationEngine;

    @Override
    public void onEnable() {

        long enableTime = System.currentTimeMillis();
        this.saveDefaultConfig();

        PluginSettings settings = this.createSettings(CONFIG_FILE, PluginSettings.class);
        Logger.init(this.getSLF4JLogger(), settings.debug());

        Logger.info("<yellow>=== ENABLE START ===");
        Logger.info("<gray>Plugin Version V<red>{}", this.getPluginMeta().getVersion());


        this.reloadConfig();

        this.animationEngine = new ZAnimationEngine();

        Registry.register(AnimationsRegistry.class, new ZAnimationRegistry(this, this.animationEngine, ANIMATIONS_FOLDER));


        Registry.get(AnimationsRegistry.class).loadFromFolder();

        this.registerCommands(settings);

        Logger.info("<yellow>=== ENABLE DONE <gray>(<gold>" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<gray>) <yellow>===");

    }

    @Override
    public void onDisable() {
        long disableTime = System.currentTimeMillis();
        Logger.info("<yellow>=== DISABLE START ===");
        Logger.info("<gray>Plugin Version V<red>{}", this.getPluginMeta().getVersion());

        this.animationEngine.close();
        MessagesService.close();

        Logger.info("<yellow>=== DISABLE DONE <gray>(<gold>" + Math.abs(disableTime - System.currentTimeMillis()) + "ms<gray>) <yellow>===");
    }

    @Override
    public void saveDefaultConfig() {
        List.of(CONFIG_FILE, MESSAGES_FILE).forEach(this::saveIfNotExits);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        PluginSettings settings = this.createSettings(CONFIG_FILE, PluginSettings.class);
        Logger.setDebug(settings.debug());
        try {
            Structura.loadEnum(this.getDataPath().resolve(MESSAGES_FILE), Messages.class);
        } catch (StructuraException e) {
            this.getSLF4JLogger().error("Failed to load messages configuration.", e);
        }

        AnimationsRegistry animationsRegistry = Registry.get(AnimationsRegistry.class);
        if (animationsRegistry != null) {
            animationsRegistry.loadFromFolder();
        }

    }

    private void registerCommands(PluginSettings settings) {
        CommandManager<@NotNull CratesPlugin> commandManager = new CommandManager<>(this);
        commandManager.setLogger(new fr.traqueur.commands.api.logging.Logger() {
            @Override
            public void error(String s) {
                Logger.severe(s);
            }

            @Override
            public void info(String s) {
                Logger.info(s);
            }
        });
        commandManager.setDebug(settings.debug());
        commandManager.setMessageHandler(new CommandsMessageHandler());

        commandManager.registerCommand(new ZCratesCommand(this));
    }

    private void saveIfNotExits(String fileName) {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        File file = new File(this.getDataFolder(), fileName);
        if (!file.exists()) {
            this.saveResource(fileName, false);
        }
    }

    private <T extends Settings> T createSettings(String path, Class<T> clazz) {
        File file = new File(this.getDataFolder(), path);
        if (!file.exists()) {
            throw new IllegalArgumentException("File " + path + " does not exist.");
        }
        T instance = Structura.load(file, clazz);
        Settings.register(clazz, instance);
        return instance;
    }
}
