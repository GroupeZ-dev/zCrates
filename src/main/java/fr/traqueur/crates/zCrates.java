package fr.traqueur.crates;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.loader.NoneLoader;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.sarah.RequestHelper;
import fr.traqueur.commands.spigot.CommandManager;
import fr.traqueur.crates.animations.ZAnimationEngine;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.managers.UsersManager;
import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.Key;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.registries.*;
import fr.traqueur.crates.api.storage.repositories.Repository;
import fr.traqueur.crates.api.services.MessagesService;
import fr.traqueur.crates.api.settings.Settings;
import fr.traqueur.crates.api.settings.models.DatabaseSettings;
import fr.traqueur.crates.commands.ZCratesCommand;
import fr.traqueur.crates.commands.arguments.AnimationArgument;
import fr.traqueur.crates.commands.arguments.CrateArgument;
import fr.traqueur.crates.commands.arguments.DisplayTypeArgument;
import fr.traqueur.crates.commands.handler.CommandsMessageHandler;
import fr.traqueur.crates.managers.ZCratesManager;
import fr.traqueur.crates.managers.ZUsersManager;
import fr.traqueur.crates.models.keys.PhysicKey;
import fr.traqueur.crates.models.keys.VirtualKey;
import fr.traqueur.crates.models.placedcrates.BlockCrateDisplayFactory;
import fr.traqueur.crates.models.placedcrates.EntityCrateDisplayFactory;
import fr.traqueur.crates.models.rewards.CommandReward;
import fr.traqueur.crates.models.rewards.CommandsListReward;
import fr.traqueur.crates.models.rewards.ItemReward;
import fr.traqueur.crates.models.rewards.ItemsListReward;
import fr.traqueur.crates.registries.ZAnimationRegistry;
import fr.traqueur.crates.registries.ZCrateDisplayFactoriesRegistry;
import fr.traqueur.crates.registries.ZCratesRegistry;
import fr.traqueur.crates.registries.ZHooksRegistry;
import fr.traqueur.crates.registries.ZItemsProviderRegistry;
import fr.traqueur.crates.api.serialization.Keys;
import fr.traqueur.crates.serialization.ZPlacedCrateDataType;
import fr.traqueur.crates.storage.repositories.UserRepository;
import fr.traqueur.crates.settings.PluginSettings;
import fr.traqueur.crates.settings.models.SQLSettings;
import fr.traqueur.crates.settings.models.SQLiteSettings;
import fr.traqueur.crates.settings.readers.AnimationReader;
import fr.traqueur.crates.views.buttons.AnimationButton;
import fr.traqueur.structura.api.Structura;
import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.registries.CustomReaderRegistry;
import fr.traqueur.structura.registries.PolymorphicRegistry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class zCrates extends CratesPlugin {

    private static final String CONFIG_FILE = "config.yml";
    private static final String MESSAGES_FILE = "messages.yml";
    private static final String ANIMATIONS_FOLDER = "animations";
    private static final String CRATES_FOLDER = "crates";

    private InventoryManager inventoryManager;
    private ButtonManager buttonManager;
    private ZAnimationEngine animationEngine;
    private DatabaseConnection databaseConnection;

    @Override
    public void onEnable() {

        long enableTime = System.currentTimeMillis();
        this.saveDefaultConfig();
        this.injectPolymorphismAdapters();

        PluginSettings settings = this.createSettings(CONFIG_FILE, PluginSettings.class);
        Logger.init(this.getSLF4JLogger(), settings.debug());

        Logger.info("<yellow>=== ENABLE START ===");
        Logger.info("<gray>Plugin Version V<red>{}", this.getPluginMeta().getVersion());

        ZPlacedCrateDataType.initialize();
        Keys.initialize(this);
        MessagesService.initialize(this);

        this.injectReaders();
        this.reloadConfig();

        this.animationEngine = new ZAnimationEngine();

        this.populateInventoriesRelatedStuffs();

        this.injectButtons();

        this.registerRegistries();

        this.populateRegistries();

        this.databaseConnection = settings.database().connection(settings.debug());
        if (!databaseConnection.isValid()) {
            Logger.severe("Unable to connect to database !");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        RequestHelper requestHelper = new RequestHelper(databaseConnection, Logger::info);
        Repository<User, UUID> userRepository = new UserRepository(requestHelper);

        UsersManager usersManager = this.registerManager(UsersManager.class, new ZUsersManager(userRepository));
        CratesManager cratesManager = this.registerManager(CratesManager.class, new ZCratesManager(inventoryManager));

        usersManager.init();
        cratesManager.init();
        MigrationManager.execute(this.databaseConnection, Logger::info);

        this.registerCommands(settings);

        Logger.info("<yellow>=== ENABLE DONE <gray>(<gold>" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<gray>) <yellow>===");

    }

    private void populateInventoriesRelatedStuffs() {
        var inventoryProvider = getServer().getServicesManager().getRegistration(InventoryManager.class);
        var buttonProvider = getServer().getServicesManager().getRegistration(ButtonManager.class);
        if (inventoryProvider == null) {
            Logger.severe("zMenu InventoryManager not found! Is zMenu installed?");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (buttonProvider == null) {
            Logger.severe("zMenu ButtonManager not found! Is zMenu installed?");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.buttonManager = buttonProvider.getProvider();
        this.inventoryManager = inventoryProvider.getProvider();
    }

    private void populateRegistries() {
        HooksRegistry hooksRegistry = Registry.get(HooksRegistry.class);
        hooksRegistry.scanPackage(this, "fr.traqueur.crates");
        hooksRegistry.enableAll();

        CrateDisplayFactoriesRegistry crateDisplayFactoriesRegistry = Registry.get(CrateDisplayFactoriesRegistry.class);
        crateDisplayFactoriesRegistry.registerGeneric(DisplayType.BLOCK, new BlockCrateDisplayFactory());
        crateDisplayFactoriesRegistry.registerGeneric(DisplayType.ENTITY, new EntityCrateDisplayFactory());

        Registry.get(AnimationsRegistry.class).loadFromFolder();

        CratesRegistry cratesRegistry = Registry.get(CratesRegistry.class);
        cratesRegistry.loadFromFolder();
        if (cratesRegistry.getAll().isEmpty()) {
            Logger.warning("<yellow>No crates loaded! Please create crates in the 'crates' folder. Animation debug command will not work without crates.</yellow>");
        }
    }

    private void registerRegistries() {
        Registry.register(AnimationsRegistry.class, new ZAnimationRegistry(this, this.animationEngine, ANIMATIONS_FOLDER));
        Registry.register(CratesRegistry.class, new ZCratesRegistry(this, CRATES_FOLDER));
        Registry.register(ItemsProvidersRegistry.class, new ZItemsProviderRegistry());
        Registry.register(HooksRegistry.class, new ZHooksRegistry());
        Registry.register(CrateDisplayFactoriesRegistry.class, new ZCrateDisplayFactoriesRegistry());
    }

    private void injectPolymorphismAdapters() {
        PolymorphicRegistry.create(Reward.class, registry -> {
            registry.register("ITEM", ItemReward.class);
            registry.register("ITEMS", ItemsListReward.class);
            registry.register("COMMAND", CommandReward.class);
            registry.register("COMMANDS", CommandsListReward.class);
        });

        PolymorphicRegistry.create(DatabaseSettings.class, registry -> {
            registry.register("MARIADB", SQLSettings.class);
            registry.register("MYSQL", SQLSettings.class);
            registry.register("SQLITE", SQLiteSettings.class);
        });

        PolymorphicRegistry.create(Key.class, registry -> {
            registry.register("VIRTUAL", VirtualKey.class);
            registry.register("PHYSIC", PhysicKey.class);
        });

    }

    private void injectReaders() {
        CustomReaderRegistry.getInstance().register(Animation.class, new AnimationReader());
    }

    private void injectButtons() {
        if(this.buttonManager != null) {
            this.buttonManager.register(new NoneLoader(this, AnimationButton.class, "ZCRATES_ANIMATION"));
        }
    }

    @Override
    public void onDisable() {
        long disableTime = System.currentTimeMillis();
        Logger.info("<yellow>=== DISABLE START ===");
        Logger.info("<gray>Plugin Version V<red>{}", this.getPluginMeta().getVersion());

        this.animationEngine.close();
        MessagesService.close();

        CratesManager cratesManager = this.getManager(CratesManager.class);
        if (cratesManager != null) {
            cratesManager.stopAllOpening();
            cratesManager.unloadAllPlacedCrates();
        }

        if (this.databaseConnection != null) {
            this.databaseConnection.disconnect();
        }

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

        CratesRegistry cratesRegistry = Registry.get(CratesRegistry.class);
        if (cratesRegistry != null) {
            cratesRegistry.loadFromFolder();
        }

        CratesManager cratesManager = this.getManager(CratesManager.class);
        if (cratesManager != null) {
            cratesManager.ensureInventoriesExist();
        }

    }

    @Override
    public InventoryManager getInventoryManager() {
        return inventoryManager;
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

        commandManager.registerConverter(Animation.class, new AnimationArgument());
        commandManager.registerConverter(Crate.class, new CrateArgument());
        commandManager.registerConverter(DisplayType.class, new DisplayTypeArgument());

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
