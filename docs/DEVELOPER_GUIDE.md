# zCrates - Developer Guide

Comprehensive guide for developers extending or integrating with the zCrates plugin.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Core Systems](#core-systems)
- [Extension Points](#extension-points)
- [JavaScript API](#javascript-api)
- [Database Schema](#database-schema)
- [Security Model](#security-model)
- [Best Practices](#best-practices)

---

## Architecture Overview

zCrates follows a layered architecture with clear separation between API, implementation, and extensions:

```
┌─────────────────────────────────────────────────────┐
│                   Extensions Layer                   │
│  (Hooks: ItemsAdder, MythicMobs, PlaceholderAPI)    │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│                  Business Logic Layer                │
│         (Managers, Commands, Listeners)              │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│                   Registry Layer                     │
│   (Crates, Animations, Algorithms, Providers)       │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│                   Data Layer                         │
│         (Storage, Repositories, DTOs)                │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│                   API Layer                          │
│    (Interfaces, Events, Models, Services)            │
└─────────────────────────────────────────────────────┘
```

### Key Design Patterns

1. **Registry Pattern** - Centralized component registration and retrieval
2. **Manager Pattern** - Business logic orchestration via Bukkit ServicesManager
3. **Wrapper Pattern** - Safe object exposure to JavaScript sandboxes
4. **Factory Pattern** - Display creation via CrateDisplayFactory
5. **Polymorphic Serialization** - Type-safe YAML deserialization with Structura
6. **Repository Pattern** - Database access abstraction via Sarah ORM

---

## Project Structure

```
zCrates/
├── api/                    # Public API module (interfaces, models, registries)
│   └── src/main/java/fr/traqueur/crates/api/
│       ├── annotations/    # @AutoHook for hook auto-discovery
│       ├── events/         # Event system (6 event types)
│       ├── hooks/          # Hook interface
│       ├── managers/       # Manager interfaces
│       ├── models/         # Core models (Crate, Key, Reward, etc.)
│       ├── providers/      # Provider interfaces
│       ├── registries/     # Registry system
│       ├── services/       # Service interfaces
│       ├── serialization/  # PDC and serialization
│       ├── settings/       # Configuration models
│       └── storage/        # Storage layer interfaces
│
├── common/                 # Shared implementations (displays, wrappers)
│
├── src/main/java/fr/traqueur/crates/
│   ├── zCrates.java       # Main plugin entry point
│   ├── algorithms/        # Algorithm system
│   ├── animations/        # Animation system
│   ├── commands/          # Command implementations
│   ├── engine/            # ZScriptEngine (secure Rhino)
│   ├── listeners/         # Event listeners
│   ├── managers/          # Manager implementations
│   ├── models/            # Data models and wrappers
│   ├── registries/        # Registry implementations
│   ├── settings/          # Settings implementation
│   ├── storage/           # Storage implementation
│   └── views/             # zMenu integration
│
└── hooks/                 # Optional plugin integrations
    ├── ItemsAdder/        # ItemsAdder custom items
    ├── MythicMobs/        # MythicMobs entity displays
    ├── Nexo/              # Nexo custom items
    ├── Oraxen/            # Oraxen custom items
    ├── PlaceholderAPI/    # Placeholder support
    └── zItems/            # zItems integration
```

---

## Core Systems

### 1. Registry System

The registry system provides centralized component registration and retrieval using Guava's `ClassToInstanceMap`.

#### Registry Interface

```java
public interface Registry<ID, T> {
    void register(ID id, T item);
    T getById(ID id);
    Collection<T> getAll();
    void clear();

    // Static registry access
    static <T extends Registry<?, ?>> T get(Class<T> clazz);
    static void register(Class<? extends Registry<?, ?>> clazz, Registry<?, ?> registry);
}
```

#### FileBasedRegistry

Abstract base class for file-loaded registries with folder hierarchy support:

```java
public abstract class FileBasedRegistry<ID, T> implements Registry<ID, T> {
    // Auto-loads from folder
    // Supports folder.properties metadata
    // Hot-reload via reload()

    protected abstract T loadFile(Path path) throws IOException;
    protected abstract ID getId(T item);
}
```

#### Available Registries

| Registry                        | ID Type     | Item Type           | Source               |
|---------------------------------|-------------|---------------------|----------------------|
| `AnimationsRegistry`            | String      | Animation           | `animations/*.js`    |
| `RandomAlgorithmsRegistry`      | String      | RandomAlgorithm     | `algorithms/*.js`    |
| `CratesRegistry`                | String      | Crate               | `crates/*.yml`       |
| `ItemsProvidersRegistry`        | String      | ItemsProvider       | Runtime registration |
| `HooksRegistry`                 | String      | Hook                | Classpath scanning   |
| `CrateDisplayFactoriesRegistry` | DisplayType | CrateDisplayFactory | Runtime registration |

**Usage Example:**

```java
// Get a registry
CratesRegistry registry = Registry.get(CratesRegistry.class);

// Retrieve items
Crate crate = registry.getById("legendary");
Collection<Crate> allCrates = registry.getAll();

// Register custom item
registry.register("custom-crate", new MyCrate());
```

---

### 2. Manager Layer

Managers orchestrate business logic and are registered via Bukkit's ServicesManager.

#### CratesManager

Handles crate opening, animations, and placed crates.

**Key Methods:**

```java
// Opening system
OpenResult tryOpenCrate(Player player, Crate crate);
void openCrate(Player player, Crate crate, Animation animation);
void openPreview(Player player, Crate crate);

// Reroll system
boolean canReroll(Player player);
int getRerollsRemaining(Player player);
boolean reroll(Player player);

// Animation state
Optional<Reward> getCurrentReward(Player player);
boolean isAnimationCompleted(Player player);

// Placed crates
PlacedCrate placeCrate(String crateId, Location location, DisplayType type, String value, float yaw);
void removePlacedCrate(PlacedCrate placedCrate);
Optional<PlacedCrate> findPlacedCrateByBlock(Block block);
Optional<PlacedCrate> findPlacedCrateByEntity(Entity entity);
```

**Internal State Management:**

```java
// Active openings stored in memory
Map<UUID, OpenedCrate> openedCrates;

record OpenedCrate(
    Crate crate,
    Animation animation,
    Reward reward,
    int rerollsRemaining,
    AnimationProgress progress
);
```

#### UsersManager

Handles player data and key management with async database operations.

**Key Methods:**

```java
// User lifecycle
User getUser(UUID uuid);                      // Synchronous (cache)
CompletableFuture<User> loadUser(UUID uuid);  // Asynchronous (DB)
CompletableFuture<Void> saveUser(User user);  // Asynchronous (DB)

// Opening persistence
void persistCrateOpening(CrateOpening opening);
```

**Caching Strategy:**

```java
// In-memory cache
ConcurrentHashMap<UUID, User> cache;

// Auto-load on join, auto-save on quit
// Accessed synchronously via getUser()
```

---

### 3. Script Engine System

ZScriptEngine provides secure, sandboxed JavaScript execution using Mozilla Rhino.

#### Security Features

```java
public class ZScriptEngine implements AutoCloseable {
    // Rhino context with security restrictions
    - initSafeStandardObjects()  // No java.* access
    - Optimization level -1       // Interpreter mode
    - Null parent scopes          // Isolation
    - Custom WrapFactory          // Method blocking
}
```

**Blocked Methods:**
- `getClass()`, `class`
- `notify()`, `notifyAll()`, `wait()`
- `clone()`, `finalize()`, `hashCode()`

**Allowed JavaScript:**
- ES6 features (arrow functions, let/const, template literals)
- Standard objects (Array, Object, Math, JSON, Date, RegExp)
- Context objects (player, inventory, crate, rewards, history)

#### Script Execution Methods

```java
// Fire-and-forget
void executeFunction(ScriptableObject scope, String functionName, Object... args);

// With result
<T> T executeFunctionWithResult(ScriptableObject scope, String functionName, Class<T> returnType, Object... args);

// Load script
void evaluateFile(Path scriptPath, ScriptableObject scope);

// Create isolated scope
ScriptableObject createSecureScope();
```

**Usage Example:**

```java
ZScriptEngine engine = new ZScriptEngine();
ScriptableObject scope = engine.createSecureScope();

// Load script
engine.evaluateFile(Paths.get("animations/my_animation.js"), scope);

// Execute function
AnimationContext context = new AnimationContext(player, crate, inventory);
engine.executeFunction(scope, "onStart", context);
```

---

### 4. Animation System

Animations provide frame-by-frame visual effects during crate opening.

#### Animation Model

```java
public record Animation(
    String id,
    List<AnimationPhase> phases,
    JavaScriptFunction onComplete,
    JavaScriptFunction onCancel
) {}

public record AnimationPhase(
    String name,
    long duration,         // milliseconds
    int interval,          // ticks between onTick calls
    SpeedCurve speedCurve, // Easing function
    JavaScriptFunction onStart,
    JavaScriptFunction onTick,
    JavaScriptFunction onComplete
) {}

public enum SpeedCurve {
    LINEAR,        // Constant speed
    EASE_IN,       // Starts slow, accelerates
    EASE_OUT,      // Starts fast, decelerates
    EASE_IN_OUT    // Slow → Fast → Slow
}
```

#### AnimationExecutor

Manages animation timing and phase transitions:

```java
public class AnimationExecutor {
    void start(Player player, Animation animation, AnimationContext context);
    void stop(Player player);
    boolean isRunning(Player player);
}
```

**Execution Flow:**
1. Initialize phase 0
2. Call `onStart(context)`
3. Every `interval` ticks: call `onTick(context, tickData)`
4. After `duration` ms: call `onComplete(context)` and move to next phase
5. After all phases: call animation's `onComplete(context)`

#### AnimationContext

Provides safe access to runtime objects:

```java
public record AnimationContext(
    PlayerWrapper player,
    InventoryWrapper inventory,
    CrateWrapper crate
) {}
```

#### TickData

Per-tick information passed to `onTick()`:

```java
public record TickData(
    int tickNumber,      // Current tick in phase
    double progress,     // 0.0 to 1.0 (with speed curve applied)
    long elapsedTime     // Milliseconds since phase start
) {}
```

**JavaScript API:**

```javascript
animations.register("my_animation", {
    phases: [
        {
            name: "spin",
            duration: 3000,
            interval: 2,
            speedCurve: "EASE_OUT",
            onStart: function(context) {
                context.player().playSound("BLOCK_NOTE_BLOCK_PLING", 1.0, 1.0);
            },
            onTick: function(context, tickData) {
                var progress = tickData.progress();
                context.inventory().randomizeSlots();
            },
            onComplete: function(context) {
                context.player().playSound("ENTITY_PLAYER_LEVELUP", 1.0, 1.0);
            }
        }
    ],
    onComplete: function(context) {
        if (!context.crate().hasRerolls()) {
            context.inventory().close(60);
        }
    },
    onCancel: function(context) {
        context.player().sendMessage("<red>Animation cancelled");
    }
});
```

---

### 5. Algorithm System

Algorithms provide customizable reward selection logic.

#### RandomAlgorithm Model

```java
public record RandomAlgorithm(
    String id,
    JavaScriptFunction selector
) {}
```

#### AlgorithmContext

Runtime context passed to selector function:

```java
public record AlgorithmContext(
    RewardsWrapper rewards,
    HistoryWrapper history,
    String crateId,
    UUID playerUuid
) {}
```

**JavaScript API:**

```javascript
algorithms.register("pity_system", function(context) {
    var rewards = context.rewards();
    var history = context.history();

    // Get last 50 openings
    var recentOpenings = history.getRecent(50);

    // Count openings without rare reward
    var openingsWithoutRare = 0;
    for (var i = 0; i < recentOpenings.size(); i++) {
        var opening = recentOpenings.get(i);
        if (opening.crateId() === context.crateId()) {
            var reward = rewards.getById(opening.rewardId());
            if (reward && reward.weight() > 5.0) {
                openingsWithoutRare++;
            } else {
                break;
            }
        }
    }

    // If 50 openings without rare, guarantee one
    if (openingsWithoutRare >= 50) {
        return rewards.filterByMaxWeight(5.0).weightedRandom();
    }

    return rewards.weightedRandom();
});
```

---

### 6. Reward System

Rewards define what players receive from crates.

#### Reward Interface

```java
@Polymorphic
public interface Reward extends Loadable {
    String id();
    double weight();
    ItemStackWrapper displayItem();
    void give(Player player);
}
```

#### Implementations

| Type     | Class              | Fields                         | Behavior                  |
|----------|--------------------|--------------------------------|---------------------------|
| ITEM     | ItemReward         | `ItemStackWrapper item`        | Give single item          |
| ITEMS    | ItemsListReward    | `List<ItemStackWrapper> items` | Give multiple items       |
| COMMAND  | CommandReward      | `String command`               | Execute command           |
| COMMANDS | CommandsListReward | `List<String> commands`        | Execute multiple commands |

**YAML Configuration:**

```yaml
rewards:
  - type: ITEM
    id: diamond_reward
    weight: 10.0
    display-item:
      material: DIAMOND
    item:
      material: DIAMOND
      amount: 5
      display-name: "<aqua>Diamonds"

  - type: COMMAND
    id: money_reward
    weight: 20.0
    display-item:
      material: GOLD_INGOT
    command: "eco give %player% 1000"
```

#### ItemStackWrapper System

Supports both vanilla materials and custom item plugins:

```yaml
# Vanilla material
item:
  material: DIAMOND_SWORD
  amount: 1
  display-name: "<gold>Legendary Sword"
  lore:
    - "<gray>A powerful weapon"

# Delegate to custom item plugin
item:
  copy-from:
    plugin-name: "Oraxen"
    item-id: "ruby_sword"
  amount: 1
  display-name: "<red>Ruby Sword"  # Override display name
```

**Available Delegates:**
- `ItemsAdder` - ItemsAdder custom items
- `Oraxen` - Oraxen custom items
- `Nexo` - Nexo custom items
- `zItems` - zItems custom items (via ItemsAdder provider)

---

### 7. Key System

Keys control crate access and come in two types.

#### Key Interface

```java
@Polymorphic
public interface Key extends Loadable {
    String name();
    boolean has(Player player);
    void give(Player player, int amount);
    void remove(Player player);
}
```

#### VirtualKey

Database-stored key balance:

```yaml
key:
  type: VIRTUAL
  name: "legendary-key"
```

**Storage:**
- Table: `user_keys`
- Columns: `user_id`, `key_name`, `amount`
- Managed by `UsersManager`

#### PhysicKey

Physical item in inventory:

```yaml
key:
  type: PHYSIC
  name: "legendary-physical-key"
  item:
    material: TRIPWIRE_HOOK
    display-name: "<gold>Legendary Key"
    lore:
      - "<gray>Right-click on a crate"
    glow: true
    custom-model-data: 1001
```

**Matching:**
- Uses PDC (PersistentDataContainer) for identification
- Key: `zcrates:key_name`
- Value: key name string

---

### 8. Condition System

Conditions gate crate opening with arbitrary requirements.

#### OpenCondition Interface

```java
@Polymorphic
public interface OpenCondition extends Loadable {
    boolean check(Player player, Crate crate);
    void onOpen(Player player, Crate crate);  // Side effects
    String errorMessageKey();
}
```

#### Built-in Conditions

| Type        | Class                | Configuration                     | Behavior             |
|-------------|----------------------|-----------------------------------|----------------------|
| PERMISSION  | PermissionCondition  | `permission: node`                | Check permission     |
| COOLDOWN    | CooldownCondition    | `cooldown: 60000`                 | Rate-limit (ms)      |
| PLACEHOLDER | PlaceholderCondition | `placeholder, comparison, result` | PlaceholderAPI check |

**YAML Configuration:**

```yaml
conditions:
  - type: PERMISSION
    permission: "zcrates.open.legendary"

  - type: COOLDOWN
    cooldown: 3600000  # 1 hour

  - type: PLACEHOLDER
    placeholder: "%player_level%"
    comparison: GREATER_THAN_OR_EQUALS
    result: "50"
```

---

### 9. Display System

Displays represent crates in the game world.

#### CrateDisplay Interface

```java
public interface CrateDisplay<T> {
    void spawn();
    void remove();
    boolean matches(T value);
    Location getLocation();
}
```

#### CrateDisplayFactory Interface

```java
public interface CrateDisplayFactory<T> {
    CrateDisplay<T> create(Location location, String value, float yaw);
    boolean isValidValue(String value);
    List<String> getSuggestions();
}
```

#### Built-in Display Types

| DisplayType | Factory                      | Display               | Value Type              |
|-------------|------------------------------|-----------------------|-------------------------|
| BLOCK       | BlockCrateDisplayFactory     | BlockCrateDisplay     | Material name           |
| ENTITY      | EntityCrateDisplayFactory    | EntityCrateDisplay    | EntityType name         |
| MYTHIC_MOB  | MythicMobCrateDisplayFactory | MythicMobCrateDisplay | MythicMob ID            |
| ITEMS_ADDER | IACrateDisplayFactory        | IACrateDisplay        | ItemsAdder furniture ID |
| NEXO        | NexoCrateDisplayFactory      | NexoCrateDisplay      | Nexo furniture ID       |
| ORAXEN      | OraxenCrateDisplayFactory    | OraxenCrateDisplay    | Oraxen furniture ID     |

#### PlacedCrate Record

```java
public record PlacedCrate(
    UUID id,
    String crateId,
    String worldName,
    int x, int y, int z,
    DisplayType displayType,
    String displayValue,
    float yaw
) {}
```

**Persistence:**
- Stored in Chunk PDC via `PlacedCrateDataType`
- Loads on chunk load, unloads on chunk unload
- Managed by `CratesManager`

---

### 10. Event System

All events extend `CrateEvent` which provides player and crate context.

#### Event Types

| Event                | Cancellable | When Fired         | Purpose            |
|----------------------|-------------|--------------------|--------------------|
| CratePreOpenEvent    | Yes         | Before opening     | Prevent opening    |
| CrateOpenEvent       | No          | After key consumed | Track opening      |
| CrateRerollEvent     | Yes         | Before reroll      | Prevent reroll     |
| RewardGeneratedEvent | No          | Reward selected    | Track selection    |
| RewardGivenEvent     | No          | Reward given       | Track distribution |

**Usage Example:**

```java
@EventHandler
public void onRewardGiven(RewardGivenEvent event) {
    Player player = event.getPlayer();
    Crate crate = event.getCrate();
    Reward reward = event.getReward();

    // Log to external system
    myLogger.log(player.getName() + " won " + reward.id() + " from " + crate.id());

    // Broadcast rare rewards
    if (reward.weight() < 5.0) {
        Bukkit.broadcastMessage(
            player.getName() + " won a rare reward from " + crate.displayName()
        );
    }
}
```

---

## Extension Points

### 1. Creating Custom Hooks

Hooks integrate with external plugins using the `@AutoHook` annotation.

**Step 1: Create Hook Class**

```java
package fr.traqueur.crates.hooks.myplugin;

import fr.traqueur.crates.api.annotations.AutoHook;
import fr.traqueur.crates.api.hooks.Hook;
import fr.traqueur.crates.api.registries.*;

@AutoHook("MyPlugin")  // Name of target plugin
public class MyPluginHook implements Hook {

    @Override
    public void onEnable() {
        // Register providers, factories, etc.
        ItemsProvidersRegistry itemsRegistry = Registry.get(ItemsProvidersRegistry.class);
        itemsRegistry.register("MyPlugin", (player, itemId) -> {
            // Return ItemStack from your plugin
            return MyPlugin.getAPI().getItem(itemId);
        });

        CrateDisplayFactoriesRegistry displayRegistry = Registry.get(CrateDisplayFactoriesRegistry.class);
        displayRegistry.register(DisplayType.MY_TYPE, new MyDisplayFactory());
    }
}
```

**Step 2: Create build.gradle.kts (if in hooks/ directory)**

```kotlin
dependencies {
    compileOnly("my-plugin:api:version")
}
```

**Step 3: Hook Auto-Discovery**

Hooks are automatically discovered if:
- Located in package `fr.traqueur.crates` or subpackages
- Annotated with `@AutoHook("PluginName")`
- Target plugin is loaded

### 2. Creating Custom Conditions

**Step 1: Implement OpenCondition**

```java
public record MyCustomCondition(String myParam) implements OpenCondition {

    @Override
    public boolean check(Player player, Crate crate) {
        // Your validation logic
        return player.hasPermission("custom.permission");
    }

    @Override
    public void onOpen(Player player, Crate crate) {
        // Side effects after successful open
        player.sendMessage("Custom condition passed!");
    }

    @Override
    public String errorMessageKey() {
        return "custom-condition-failed";
    }
}
```

**Step 2: Register Polymorphic Type**

```java
@Override
public void onEnable() {
    PolymorphicRegistry.get(OpenCondition.class, registry -> {
        registry.register("MY_CUSTOM", MyCustomCondition.class);
    });
}
```

**Step 3: Use in YAML**

```yaml
conditions:
  - type: MY_CUSTOM
    myParam: "value"
```

### 3. Creating Custom ItemsProvider

```java
ItemsProvidersRegistry registry = Registry.get(ItemsProvidersRegistry.class);

registry.register("MyPlugin", (player, itemId) -> {
    // Resolve item from your plugin
    MyCustomItem item = MyPlugin.getItemRegistry().get(itemId);
    if (item == null) return null;

    return item.build(player);
});
```

**Usage in YAML:**

```yaml
item:
  copy-from:
    plugin-name: "MyPlugin"
    item-id: "custom_sword"
  display-name: "<gold>Custom Sword"
```

### 4. Creating Custom CrateDisplayFactory

```java
public class MyDisplayFactory implements CrateDisplayFactory<String> {

    @Override
    public CrateDisplay<String> create(Location location, String value, float yaw) {
        return new MyDisplay(location, value, yaw);
    }

    @Override
    public boolean isValidValue(String value) {
        return MyPlugin.getAPI().exists(value);
    }

    @Override
    public List<String> getSuggestions() {
        return MyPlugin.getAPI().getAllIds();
    }
}

public class MyDisplay implements CrateDisplay<String> {
    private final Location location;
    private final String value;
    private MyObject object;

    @Override
    public void spawn() {
        this.object = MyPlugin.getAPI().spawn(location, value);
    }

    @Override
    public void remove() {
        if (object != null) {
            object.remove();
        }
    }

    @Override
    public boolean matches(String value) {
        return this.value.equals(value);
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
```

**Registration:**

```java
CrateDisplayFactoriesRegistry registry = Registry.get(CrateDisplayFactoriesRegistry.class);
registry.register(DisplayType.MY_TYPE, new MyDisplayFactory());
```

---

## JavaScript API

### Animation Context API

```javascript
// PlayerWrapper methods
context.player().sendMessage("<gold>Message");
context.player().sendTitle("<gold>Title", "<gray>Subtitle", fadeIn, stay, fadeOut);
context.player().playSound("SOUND_NAME", volume, pitch);

// InventoryWrapper methods
context.inventory().setItem(slot, itemStack);
context.inventory().setRandomItem(slot);
context.inventory().setWinningItem(slot, itemStack);
context.inventory().rotateItems([13, 14, 15, 16]);  // Array of slots
context.inventory().clear();
context.inventory().clear(slot);
context.inventory().highlightSlot(slot, "GLOWING");
context.inventory().close(delayTicks);
context.inventory().closeImmediately();

// CrateWrapper methods
context.crate().displayName();
context.crate().id();
context.crate().getReward();  // Current reward
context.crate().rerollsRemaining();
context.crate().hasRerolls();

// TickData (in onTick callback)
tickData.tickNumber();    // Current tick
tickData.progress();      // 0.0 to 1.0 (with speed curve)
tickData.elapsedTime();   // Milliseconds since phase start
```

### Algorithm Context API

```javascript
// RewardsWrapper methods
var rewards = context.rewards();

rewards.weightedRandom();                    // Standard weighted random
rewards.weightedRandom([reward1, reward2]);  // From specific list
rewards.getAll();                            // All rewards as list
rewards.size();                              // Total count
rewards.getById("reward_id");                // Find by ID
rewards.filterByMinWeight(5.0);              // Filter by min weight
rewards.filterByMaxWeight(10.0);             // Filter by max weight

// HistoryWrapper methods
var history = context.history();

history.getRecent(50);                       // Last N openings
history.getRecentForCrate("crate_id", 50);   // Last N for specific crate
history.getAll();                            // All openings
```

### ArrayHelper Utility

```javascript
// Convert JavaScript arrays to Java int[] arrays
var javaArray = ArrayHelper.toIntArray([1, 2, 3, 4, 5]);
context.inventory().rotateItems(javaArray);
```

---

## Database Schema

### Tables

**users**
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY
);
```

**user_keys**
```sql
CREATE TABLE user_keys (
    user_id UUID NOT NULL,
    key_name VARCHAR(255) NOT NULL,
    amount INT NOT NULL,
    PRIMARY KEY (user_id, key_name),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**crate_openings**
```sql
CREATE TABLE crate_openings (
    id UUID PRIMARY KEY,
    player_id UUID NOT NULL,
    crate_id VARCHAR(255) NOT NULL,
    reward_id VARCHAR(255) NOT NULL,
    timestamp BIGINT NOT NULL
);
```

---

## Security Model

### JavaScript Sandbox

**Blocked Java Access:**
- No `java.*` package access
- No `Packages` global
- No reflection APIs

**Blocked Methods:**
- `getClass()`, `class`
- `notify()`, `notifyAll()`, `wait()`
- `clone()`, `finalize()`, `hashCode()`

**Allowed:**
- Standard JavaScript objects (Array, Object, Math, JSON, Date, RegExp)
- ES6 features (arrow functions, let/const, template literals)
- Context objects (player, inventory, crate, rewards, history)
- Utility: `ArrayHelper`

**Isolation:**
- Each script execution uses isolated scope
- Null parent scopes prevent prototype pollution
- Interpreter mode (no bytecode generation)

### Inventory Security

**Slot Authorization:**
- Only authorized slots can be manipulated during animation
- Prevents inventory manipulation exploits

### Data Security

**PDC Storage:**
- Chunk data persisted in PersistentDataContainer
- Type-safe serialization via `PlacedCrateDataType`

**User Cache:**
- Proper lifecycle (load on join, save on quit)
- Concurrent-safe operations

**Database:**
- Async operations via `CompletableFuture`
- Connection pooling
- Prepared statements (SQL injection protection)

---

## Best Practices

### 1. Event Handling

```java
// Always check if event is cancelled
@EventHandler(priority = EventPriority.HIGHEST)
public void onRewardGiven(RewardGivenEvent event) {
    if (event.isCancelled()) return;  // If event becomes cancellable in future

    // Your logic
}

// Listen at appropriate priority
// LOWEST - Execute first
// NORMAL - Default
// HIGHEST - Execute last
// MONITOR - Observe only (don't modify)
```

### 2. Registry Access

```java
// Cache registry references
private final CratesRegistry cratesRegistry = Registry.get(CratesRegistry.class);

// Don't call Registry.get() in loops
for (Player player : players) {
    Crate crate = cratesRegistry.getById("legendary");  // Good
}
```

### 3. Async Operations

```java
// Use async for database operations
usersManager.loadUser(uuid).thenAccept(user -> {
    // Process user data
}).exceptionally(throwable -> {
    Logger.error("Failed to load user", throwable);
    return null;
});

// Return to main thread for Bukkit API
usersManager.loadUser(uuid).thenAcceptAsync(user -> {
        player.sendMessage("Loaded!");
}, Bukkit.getScheduler().getMainThreadExecutor(plugin));
```

### 4. Custom Animations

```javascript
// Use speedCurve for smooth animations
{
    speedCurve: "EASE_OUT",  // Decelerates naturally
    onTick: function(context, tickData) {
        var progress = tickData.progress();  // 0.0 to 1.0
        // Use progress for smooth transitions
    }
}

// Clean up in onCancel
onCancel: function(context) {
    context.inventory().clear();
    context.player().sendMessage("<red>Cancelled");
}
```

### 5. Custom Algorithms

```javascript
// Cache rewards list
algorithms.register("my_algo", function(context) {
    var rewards = context.rewards();
    var allRewards = rewards.getAll();  // Cache

    // Don't call getAll() in loops
    for (var i = 0; i < 100; i++) {
        // Use allRewards
    }

    return rewards.weightedRandom();
});
```

### 6. Error Handling

```java
// Always handle Optional properly
Optional<Crate> optCrate = registry.getById("legendary");
optCrate.ifPresent(crate -> {
    // Process crate
});

// Or with fallback
Crate crate = registry.getById("legendary")
    .orElseThrow(() -> new IllegalStateException("Crate not found"));
```

---

## Support

For questions, issues, or contributions:
- **Discord**: [https://discord.gg/PTSYTC53d3]
- **GitHub**: [https://github.com/GroupeZ-dev/zCrates]
- **Documentation**: See `USER_GUIDE.md`