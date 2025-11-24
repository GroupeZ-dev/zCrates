# zCrates API Reference

This document provides comprehensive API documentation for developers who want to integrate with or extend the zCrates plugin.

## Table of Contents

- [Getting Started](#getting-started)
- [Core Interfaces](#core-interfaces)
- [Managers](#managers)
- [Models](#models)
- [Events](#events)
- [Registries](#registries)
- [Conditions System](#conditions-system)
- [Examples](#examples)

---

## Getting Started

### Maven/Gradle Dependency

```xml
<dependency>
    <groupId>fr.traqueur</groupId>
    <artifactId>zcrates-api</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Accessing the API

```java
// Get the plugin instance
CratesPlugin plugin = (CratesPlugin) JavaPlugin.getPlugin(CratesPlugin.class);

// Get managers
CratesManager cratesManager = plugin.getManager(CratesManager.class);
UsersManager usersManager = plugin.getManager(UsersManager.class);

// Get registries
CratesRegistry cratesRegistry = Registry.get(CratesRegistry.class);
AnimationsRegistry animationsRegistry = Registry.get(AnimationsRegistry.class);
```

---

## Core Interfaces

### CratesPlugin

The main plugin class providing access to all plugin services.

```java
public abstract class CratesPlugin extends JavaPlugin {

    // Get a registered manager
    public <T extends Manager> T getManager(Class<T> clazz);

    // Register an event listener
    public void registerListener(Listener listener);

    // Get the zMenu inventory manager
    public abstract InventoryManager getInventoryManager();
}
```

---

## Managers

### CratesManager

Handles crate opening, animations, and placed crates.

```java
public interface CratesManager extends Manager {

    /**
     * Attempts to open a crate for a player.
     * Checks key ownership, conditions, and fires events.
     *
     * @param player the player opening the crate
     * @param crate the crate to open
     * @return OpenResult indicating success or failure reason
     */
    OpenResult tryOpenCrate(Player player, Crate crate);

    /**
     * Force opens a crate (bypasses key check and conditions).
     *
     * @param player the player
     * @param crate the crate
     * @param animation the animation to play
     */
    void openCrate(Player player, Crate crate, Animation animation);

    /**
     * Opens the preview menu for a crate.
     *
     * @param player the player
     * @param crate the crate to preview
     */
    void openPreview(Player player, Crate crate);

    /**
     * Gets the crate a player is currently previewing.
     *
     * @param player the player
     * @return Optional containing the crate, or empty
     */
    Optional<Crate> getPreviewingCrate(Player player);

    /**
     * Checks if a player can reroll their reward.
     *
     * @param player the player
     * @return true if reroll is available
     */
    boolean canReroll(Player player);

    /**
     * Gets remaining rerolls for a player.
     *
     * @param player the player
     * @return number of rerolls remaining
     */
    int getRerollsRemaining(Player player);

    /**
     * Performs a reroll for a player.
     *
     * @param player the player
     * @return true if reroll succeeded
     */
    boolean reroll(Player player);

    /**
     * Gets the current reward for a player (during animation).
     *
     * @param player the player
     * @return Optional containing the reward, or empty
     */
    Optional<Reward> getCurrentReward(Player player);

    /**
     * Checks if animation has completed for a player.
     *
     * @param player the player
     * @return true if animation is complete
     */
    boolean isAnimationCompleted(Player player);

    // Placed Crates Management

    /**
     * Places a crate at a location.
     *
     * @param crateId the crate ID
     * @param location the location
     * @param displayType the display type (BLOCK, ENTITY, etc.)
     * @param displayValue the display value (material/entity type)
     * @param yaw the rotation
     * @return the created PlacedCrate
     */
    PlacedCrate placeCrate(String crateId, Location location,
                          DisplayType displayType, String displayValue, float yaw);

    /**
     * Removes a placed crate.
     *
     * @param placedCrate the placed crate to remove
     */
    void removePlacedCrate(PlacedCrate placedCrate);

    /**
     * Finds a placed crate by block.
     *
     * @param block the block
     * @return Optional containing the placed crate, or empty
     */
    Optional<PlacedCrate> findPlacedCrateByBlock(Block block);

    /**
     * Finds a placed crate by entity.
     *
     * @param entity the entity
     * @return Optional containing the placed crate, or empty
     */
    Optional<PlacedCrate> findPlacedCrateByEntity(Entity entity);
}
```

### UsersManager

Handles player data and key management.

```java
public interface UsersManager extends Manager {

    /**
     * Gets a user from cache (synchronous).
     *
     * @param uuid the player UUID
     * @return the User object
     */
    User getUser(UUID uuid);

    /**
     * Loads a user from database (asynchronous).
     *
     * @param uuid the player UUID
     * @return CompletableFuture with the User
     */
    CompletableFuture<User> loadUser(UUID uuid);

    /**
     * Saves a user to database (asynchronous).
     *
     * @param user the user to save
     * @return CompletableFuture
     */
    CompletableFuture<Void> saveUser(User user);

    /**
     * Persists a crate opening to database.
     *
     * @param opening the opening to persist
     */
    void persistCrateOpening(CrateOpening opening);
}
```

---

## Models

### Crate

Represents a crate configuration.

```java
public interface Crate {
    String id();
    String displayName();
    Key key();
    Animation animation();
    RandomAlgorithm algorithm();
    String relatedMenu();
    List<Reward> rewards();
    int maxRerolls();
    List<OpenCondition> conditions();
    ItemStackWrapper randomDisplay();
    Reward generateReward(User user);
}
```

### Key

Represents a key type (virtual or physical).

```java
@Polymorphic
public interface Key extends Loadable {
    String name();
    boolean has(Player player);
    void give(Player player, int amount);
    void remove(Player player);
}
```

**Implementations:**
- `VIRTUAL` - Stored in database, no physical item
- `PHYSIC` - Physical item in player's inventory

### Reward

Represents a reward that can be won from a crate.

```java
@Polymorphic
public interface Reward extends Loadable {
    String id();
    double weight();
    ItemStackWrapper displayItem();
    void give(Player player);
}
```

**Implementations:**
- `ITEM` - Single item reward
- `ITEMS` - Multiple items reward
- `COMMAND` - Single command reward
- `COMMANDS` - Multiple commands reward

### OpenCondition

Represents a condition to open a crate.

```java
@Polymorphic
public interface OpenCondition extends Loadable {

    /**
     * Checks if the player meets this condition.
     *
     * @param player the player
     * @param crate the crate
     * @return true if condition is met
     */
    boolean check(Player player, Crate crate);

    /**
     * Called when player successfully opens the crate.
     * Used for side effects like setting cooldowns.
     *
     * @param player the player
     * @param crate the crate
     */
    default void onOpen(Player player, Crate crate) {}

    /**
     * Gets the error message key for failed condition.
     *
     * @return the message key
     */
    String errorMessageKey();
}
```

**Implementations:**
- `PERMISSION` - Requires a permission node
- `COOLDOWN` - Requires time since last opening

### OpenResult

Result of attempting to open a crate.

```java
public record OpenResult(Status status, @Nullable OpenCondition failedCondition) {

    public enum Status {
        SUCCESS,           // Crate opened successfully
        NO_KEY,           // Player doesn't have the key
        CONDITION_FAILED, // A condition was not met
        EVENT_CANCELLED   // CratePreOpenEvent was cancelled
    }

    public static OpenResult success();
    public static OpenResult noKey();
    public static OpenResult conditionFailed(OpenCondition condition);
    public static OpenResult eventCancelled();

    public boolean isSuccess();
}
```

### User

Represents a player's data.

```java
public interface User {
    UUID uuid();
    Map<String, Integer> getKeys();
    int getKeyAmount(String keyName);
    void addKey(String keyName, int amount);
    void removeKey(String keyName, int amount);
    List<CrateOpening> getCrateOpenings(String crateId);
    CrateOpening addCrateOpening(String crateId, String rewardId);
}
```

### CrateOpening

Represents a recorded crate opening.

```java
public record CrateOpening(
    UUID userUuid,
    String crateId,
    String rewardId,
    LocalDateTime openedAt
) {}
```

---

## Events

All events extend `CrateEvent` which provides access to player and crate.

### CratePreOpenEvent (Cancellable)

Fired before a crate opens. Cancel to prevent opening.

```java
@EventHandler
public void onPreOpen(CratePreOpenEvent event) {
    Player player = event.getPlayer();
    Crate crate = event.getCrate();

    if (/* some condition */) {
        event.setCancelled(true);
    }
}
```

### CrateOpenEvent

Fired when a crate opens (after key consumed, menu opening).

```java
@EventHandler
public void onOpen(CrateOpenEvent event) {
    Player player = event.getPlayer();
    Crate crate = event.getCrate();
    Animation animation = event.getAnimation();
}
```

### RewardGeneratedEvent

Fired when a reward is generated (during animation).

```java
@EventHandler
public void onRewardGenerated(RewardGeneratedEvent event) {
    Player player = event.getPlayer();
    Crate crate = event.getCrate();
    Reward reward = event.getReward();
    boolean isReroll = event.isReroll();
}
```

### CrateRerollEvent (Cancellable)

Fired before a reroll. Cancel to prevent.

```java
@EventHandler
public void onReroll(CrateRerollEvent event) {
    Player player = event.getPlayer();
    Reward currentReward = event.getCurrentReward();
    int rerollsRemaining = event.getRerollsRemaining();

    if (/* some condition */) {
        event.setCancelled(true);
    }
}
```

### RewardGivenEvent

Fired when a reward is given to a player.

```java
@EventHandler
public void onRewardGiven(RewardGivenEvent event) {
    Player player = event.getPlayer();
    Crate crate = event.getCrate();
    Reward reward = event.getReward();

    // Log, notify, etc.
}
```

---

## Registries

### Accessing Registries

```java
// Get a registry
CratesRegistry cratesRegistry = Registry.get(CratesRegistry.class);
AnimationsRegistry animationsRegistry = Registry.get(AnimationsRegistry.class);
RandomAlgorithmRegistry algorithmRegistry = Registry.get(RandomAlgorithmRegistry.class);

// Get items from registry
Crate crate = cratesRegistry.getById("example");
Animation animation = animationsRegistry.getById("roulette");
Collection<Crate> allCrates = cratesRegistry.getAll();
```

### Creating Custom Hooks

```java
@AutoHook("YourPluginName")
public class YourPluginHook implements Hook {

    @Override
    public void onEnable() {
        // Register custom display factories, item providers, etc.
        CrateDisplayFactoriesRegistry registry =
            Registry.get(CrateDisplayFactoriesRegistry.class);
        registry.registerGeneric(DisplayType.YOUR_TYPE, new YourDisplayFactory());
    }
}
```

---

## Conditions System

### Creating Custom Conditions

1. Create the condition class:

```java
public record MyCustomCondition(String myParam) implements OpenCondition {

    @Override
    public boolean check(Player player, Crate crate) {
        // Your logic here
        return true;
    }

    @Override
    public void onOpen(Player player, Crate crate) {
        // Called after successful open
    }

    @Override
    public String errorMessageKey() {
        return "my-custom-error";
    }
}
```

2. Register in your plugin:

```java
PolymorphicRegistry.create(OpenCondition.class, registry -> {
    registry.register("MY_CUSTOM", MyCustomCondition.class);
});
```

3. Use in crate YAML:

```yaml
conditions:
  - type: MY_CUSTOM
    myParam: "value"
```

---

## Examples

### Opening a Crate Programmatically

```java
CratesManager manager = plugin.getManager(CratesManager.class);
CratesRegistry registry = Registry.get(CratesRegistry.class);

Crate crate = registry.getById("example");
if (crate != null) {
    OpenResult result = manager.tryOpenCrate(player, crate);

    if (!result.isSuccess()) {
        switch (result.status()) {
            case NO_KEY -> player.sendMessage("You need a key!");
            case CONDITION_FAILED -> {
                OpenCondition condition = result.failedCondition();
                // Handle specific condition failure
            }
            case EVENT_CANCELLED -> player.sendMessage("Opening was cancelled.");
        }
    }
}
```

### Giving Keys to a Player

```java
UsersManager usersManager = plugin.getManager(UsersManager.class);
User user = usersManager.getUser(player.getUniqueId());

// For virtual keys
user.addKey("example-key", 5);

// For physical keys
Crate crate = registry.getById("example");
crate.key().give(player, 5);
```

### Listening for Rewards

```java
public class MyRewardListener implements Listener {

    @EventHandler
    public void onRewardGiven(RewardGivenEvent event) {
        Player player = event.getPlayer();
        Reward reward = event.getReward();

        // Log to external system
        myLogger.log(player.getName() + " won " + reward.id());

        // Broadcast rare rewards
        if (reward.weight() < 5.0) {
            Bukkit.broadcastMessage(player.getName() + " won a rare reward!");
        }
    }
}
```

### Creating a Custom Algorithm

Create `plugins/zCrates/algorithms/my-algorithm.js`:

```javascript
algorithms.register("my-algorithm", function(context) {
    var rewards = context.rewards();
    var history = context.history();

    // Get player's recent openings
    var recentOpenings = history.getRecent(10);

    // Custom logic
    if (recentOpenings.length < 3) {
        // New players get better odds
        return rewards.weightedRandom(2.0); // 2x weight boost
    }

    return rewards.weightedRandom();
});
```

---

## Support

For issues and feature requests, please use the GitHub issue tracker.