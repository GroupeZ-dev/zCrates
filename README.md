# 🎁 zCrates

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/GroupeZ-dev/zCrates)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)](https://groupez.dev)

A modern, JavaScript-powered Minecraft crate plugin with advanced animations and reward algorithms.

## 📋 Description

**zCrates** is a sophisticated crate management plugin for Minecraft servers. Built with Java 21, Adventure API, and powered by **Rhino JavaScript Engine** for maximum customization. Create stunning opening animations, custom reward algorithms, and condition systems using JavaScript.

## ✨ Features

### 🎯 Core Features
- **JavaScript-Powered** - Animations and algorithms written in JavaScript (Rhino engine)
- **MiniMessage Support** - Full support for gradients, colors, and formatting
- **Modular Architecture** - Separate API module for developers
- **Database Persistence** - SQLite, MySQL, MariaDB support with async operations
- **Hot Reload** - Reload all configurations without restart
- **zMenu Integration** - Beautiful GUI inventories

### 🎁 Reward System

**4 Reward Types:**

| Type | Description | Example |
|------|-------------|---------|
| **ITEM** | Single item reward | Diamond Sword with enchantments |
| **ITEMS** | Multiple items in one reward | Set of armor pieces |
| **COMMAND** | Single console command | `/give %player% diamond 64` |
| **COMMANDS** | Multiple commands | Rank upgrade + announcement |

**Features:**
- Weight-based probability (10/100 = 10% chance)
- Custom display items for animations
- MiniMessage formatting support
- `%player%` placeholder in commands

### 🔐 Opening Conditions

**2 Condition Types:**

| Type | Description | Configuration |
|------|-------------|---------------|
| **PERMISSION** | Requires permission | `permission: "crates.vip"` |
| **COOLDOWN** | Time-based restriction | `cooldown: 60000` (milliseconds) |

**Features:**
- Multiple conditions per crate (all must pass)
- Cooldown stored in player persistent data
- Custom error messages

### 🎬 Animation System

**Powered by JavaScript with phase-based architecture:**

- **instant** - Immediate reward display (500ms)
- **roulette** - Classic spinning wheel with 4 phases (4100ms)
- **cascade** - Progressive fill animation (1900ms)
- **simple** - Basic test animation

**JavaScript Animation API:**
```javascript
animations.register("my-animation", {
    phases: [
        {
            name: "spinning",
            duration: 3000,      // Total phase time
            interval: 50,        // Tick interval
            speedCurve: "EASE_OUT",
            onStart: () => {},
            onTick: (context, tickData) => {
                // Animation logic
                context.inventory().setItem(slot, item);
            },
            onComplete: () => {}
        }
    ],
    onComplete: () => {},  // Animation complete
    onCancel: () => {}     // Animation cancelled
});
```

**Speed Curves:** LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT

### 🎲 Random Algorithms

**3 Built-in Algorithms (JavaScript):**

| Algorithm | Description | Use Case |
|-----------|-------------|----------|
| **weighted** | Standard weight-based random | General crates |
| **pity_system** | Guarantees legendary after N openings | Premium crates |
| **progressive_luck** | Increases rare chance over time | Event crates |

**Pity System:**
- Guarantees legendary (weight ≤ 5.0) after 10 openings without one
- Automatically resets counter
- Tracks history per player

**Progressive Luck:**
- Base 15% rare chance (weight ≤ 10.0)
- Increases 10% per 3 openings
- Capped at 80% maximum

**JavaScript Algorithm API:**
```javascript
algorithms.register("custom", (context) => {
    let rewards = context.rewards();
    let player = context.player();
    let history = context.history();

    // Custom selection logic
    return rewards.weightedRandom();
});
```

### 🔑 Key Types

| Type | Storage | Description |
|------|---------|-------------|
| **VIRTUAL** | Database | Server-side tracking, no inventory clutter |
| **PHYSIC** | Inventory | Physical items with NBT data |

**Features:**
- Count-based management
- Automatic give/take operations
- Stackable (PHYSIC keys)

### 🖼️ Display Types

**6 Display Types for placed crates:**

- **BLOCK** - Standard block display
- **ENTITY** - Mob/entity display
- **MYTHIC_MOB** - MythicMobs integration
- **ITEMS_ADDER** - ItemsAdder integration
- **ORAXEN** - Oraxen integration
- **NEXO** - Nexo integration

## 🎬 Showcase

> **Note:** Add your GIF demonstrations here when ready
>
> Suggested sections:
> - Opening animations (instant, roulette, cascade)
> - Reward distribution
> - GUI interface
> - Placed crate displays

## 📦 Requirements

- **Server**: Spigot or Paper 1.21+
- **Java**: Version 21 or higher
- **Required Dependencies**:
  - [zMenu](https://www.spigotmc.org/resources/zmenu.109103/) (for GUI inventories)
- **Optional Dependencies**:
  - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (for placeholders)
  - [MythicMobs](https://www.spigotmc.org/resources/mythicmobs.5702/) (for MYTHIC_MOB display)
  - [ItemsAdder](https://www.spigotmc.org/resources/itemsadder.73355/) (for ITEMS_ADDER display)
  - [Oraxen](https://www.spigotmc.org/resources/oraxen.72448/) (for ORAXEN display)
  - [Nexo](https://www.spigotmc.org/resources/nexo.115448/) (for NEXO display)

## 🚀 Installation

1. Download the JAR file from the [releases page](https://github.com/GroupeZ-dev/zCrates/releases)
2. Install **zMenu** (required dependency)
3. Place both plugins in your server's `plugins/` folder
4. Restart your server
5. Configuration files will be generated in `plugins/zCrates/`

## ⚙️ Configuration

### config.yml

```yaml
debug: true

# Database configuration
database:
  type: SQLITE  # SQLITE, MARIADB, MYSQL
  table-prefix: app_
  # For MySQL/MariaDB:
  # host: localhost
  # port: 3306
  # database: mydatabase
  # user: user
  # password: password
```

### messages.yml

All messages use MiniMessage format:

```yaml
no-permission: "<red>You do not have permission to execute this command."
only-in-game: "<red>This command can only be executed in-game."
keys-given: "<green>Gave x<amount> key(s) for the crate <crate> to <player>."
crate-placed: "<green>Successfully placed crate '<crate>' with display type <type>."
no-key: "<red>You don't have a key for this crate!"
condition-cooldown: "<red>You must wait <time> before opening this crate again!"
```

### Example Crate Configuration

Located in `plugins/zCrates/crates/exemple.yml`:

```yaml
id: exemple
animation: roulette
algorithm: weighted
display-name: "<gradient:gold:yellow>Example Crate</gradient>"
max-rerolls: 3

key:
  type: VIRTUAL
  name: exemple

related-menu: crate_exemple

conditions:
  - type: PERMISSION
    permission: "crates.exemple"
  - type: COOLDOWN
    cooldown: 300000  # 5 minutes in milliseconds

rewards:
  - type: ITEM
    id: diamond_sword
    weight: 20.0
    display-item:
      material: DIAMOND_SWORD
      name: "<blue>Diamond Sword"
    item:
      material: DIAMOND_SWORD
      amount: 1
      enchantments:
        - SHARPNESS:5
        - UNBREAKING:3

  - type: COMMAND
    id: vip_rank
    weight: 5.0
    display-item:
      material: GOLD_INGOT
      name: "<gold>VIP Rank"
    command: "lp user %player% parent set vip"

  - type: ITEMS
    id: armor_set
    weight: 10.0
    display-item:
      material: DIAMOND_CHESTPLATE
      name: "<aqua>Diamond Armor Set"
    items:
      - material: DIAMOND_HELMET
        enchantments:
          - PROTECTION:4
      - material: DIAMOND_CHESTPLATE
        enchantments:
          - PROTECTION:4
      - material: DIAMOND_LEGGINGS
        enchantments:
          - PROTECTION:4
      - material: DIAMOND_BOOTS
        enchantments:
          - PROTECTION:4

  - type: COMMANDS
    id: special_event
    weight: 2.0
    display-item:
      material: NETHER_STAR
      name: "<rainbow>Special Event</rainbow>"
    commands:
      - "give %player% diamond 64"
      - "broadcast <gold>%player% won the special event!"
      - "effect give %player% speed 60 2"
```

## 🎮 Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/zcrates` | `zcrates.command.admin` | Main command, show plugin version |
| `/zcrates reload` | `zcrates.command.reload` | Reload all configurations |
| `/zcrates place <crate> <type> <value>` | `zcrates.command.place` | Place a crate at target location |
| `/zcrates remove` | `zcrates.command.remove` | Remove crate at target location |
| `/zcrates purge` | `zcrates.command.purge` | Remove all crates in current chunk |
| `/zcrates givekeys <player> <crate> <amount>` | `crates.command.givekeys` | Give keys to a player |
| `/zcrates open <player> <crate> [force]` | `crates.command.open` | Force player to open crate |
| `/zcrates animations debug <animation>` | - | Test animation (requires crate) |

**Aliases:** `/zc`, `/crates`

## 🔐 Permissions

| Permission | Description | Default |
|-----------|-------------|---------|
| `zcrates.command.admin` | Access to admin commands | OP |
| `zcrates.command.reload` | Reload configuration | OP |
| `zcrates.command.place` | Place crates | OP |
| `zcrates.command.remove` | Remove crates | OP |
| `zcrates.command.purge` | Purge crates in chunk | OP |
| `crates.command.givekeys` | Give keys to players | OP |
| `crates.command.open` | Force crate opening | OP |

## 📝 Creating Custom Content

### Custom Animation

Create `plugins/zCrates/animations/my_animation.js`:

```javascript
animations.register("my_animation", {
    phases: [
        {
            name: "startup",
            duration: 1000,
            interval: 50,
            speedCurve: "EASE_IN",
            onStart: () => {
                // Initialize animation
            },
            onTick: (context, tickData) => {
                let progress = tickData.progress();  // 0.0 to 1.0
                let elapsed = tickData.elapsed();    // Milliseconds

                // Update inventory display
                let inv = context.inventory();
                inv.setItem(13, randomReward);
            },
            onComplete: () => {
                // Phase complete
            }
        },
        {
            name: "spinning",
            duration: 3000,
            interval: 100,
            speedCurve: "LINEAR",
            onTick: (context, tickData) => {
                // Spin through rewards
                let rewards = context.crate().rewards();
                let index = Math.floor(tickData.tick() % rewards.length);
                context.inventory().setItem(13, rewards[index].displayItem());
            }
        },
        {
            name: "slowdown",
            duration: 2000,
            interval: 150,
            speedCurve: "EASE_OUT",
            onTick: (context, tickData) => {
                // Gradually slow down
            }
        }
    ],
    onComplete: () => {
        // Animation finished successfully
    },
    onCancel: () => {
        // Animation was cancelled
    }
});
```

### Custom Algorithm

Create `plugins/zCrates/algorithms/my_algorithm.js`:

```javascript
algorithms.register("my_algorithm", (context) => {
    let rewards = context.rewards();
    let player = context.player();
    let history = context.history();  // Recent openings

    // Example: Increase legendary chance based on openings
    let openingCount = history.length;
    let legendaryBoost = openingCount * 0.05;  // 5% per opening

    // Filter for legendary rewards (weight <= 5.0)
    let legendaries = rewards.filter(r => r.weight() <= 5.0);

    // Random chance with boost
    if (Math.random() < (0.10 + legendaryBoost)) {
        return legendaries.weightedRandom();
    }

    // Fall back to standard weighted random
    return rewards.weightedRandom();
});
```

## 🛠️ For Developers

### Java API

```java
// Get the plugin
CratesPlugin plugin = (CratesPlugin) Bukkit.getPluginManager().getPlugin("zCrates");

// Get managers
CratesManager cratesManager = plugin.getManager(CratesManager.class);
UsersManager usersManager = plugin.getManager(UsersManager.class);

// Get a crate
CratesRegistry registry = Registry.get(CratesRegistry.class);
Crate crate = registry.get("exemple");

// Give virtual keys
usersManager.getOrLoad(player.getUniqueId()).thenAccept(user -> {
    user.getKeysInventory().addKey("exemple", 5);
    usersManager.update(user);
});

// Open a crate programmatically
cratesManager.openCrate(player, crate, false);

// Listen to events
@EventHandler
public void onCrateOpen(CrateOpenEvent event) {
    Player player = event.getPlayer();
    Crate crate = event.getCrate();
    Reward reward = event.getReward();
}
```

### Available API Classes

- `CratesPlugin` - Main plugin interface
- `CratesManager` - Crate operations and placed crates
- `UsersManager` - User data and key inventory
- `CratesRegistry` - All registered crates
- `AnimationsRegistry` - All registered animations
- `RandomAlgorithmsRegistry` - All algorithms
- `Logger` - Advanced logging with MiniMessage
- `MessagesService` - Message handling

### Building

```bash
# Clone the repository
git clone https://github.com/GroupeZ-dev/zCrates.git
cd zCrates

# Build with Gradle
./gradlew build

# JAR will be generated in target/
```

### Maven Dependency

```xml
<repository>
    <id>groupez</id>
    <url>https://repo.groupez.dev/releases</url>
</repository>

<dependency>
    <groupId>fr.traqueur</groupId>
    <artifactId>zcrates-api</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

## 📚 Technology Stack

- **Java 21** - Modern Java features
- **Paper/Spigot API** - Minecraft server
- **Rhino JavaScript** - Scripting engine (Mozilla Rhino 1.7.14)
- **Adventure API** - Text components and MiniMessage
- **Sarah ORM** - Database abstraction
- **zMenu** - Inventory GUI framework
- **Structura** - YAML configuration

## 🗄️ Database

The plugin stores:
- **User profiles** - UUID, first join time
- **Virtual key inventory** - Per-player key counts
- **Crate opening history** - For algorithms and statistics
- **Cooldowns** - Per-player, per-crate cooldowns

Supports SQLite (default), MySQL, and MariaDB with async operations.

## 🤝 Support

- **Website**: [https://groupez.dev](https://groupez.dev)
- **Author**: Traqueur_
- **Issues**: [GitHub Issues](https://github.com/GroupeZ-dev/zCrates/issues)

## 📄 License

All rights reserved © 2024 GroupeZ. This plugin is private property.

## 🔄 Changelog

### Version 1.0.0
- 🎉 Initial release
- ✅ 4 reward types (ITEM, ITEMS, COMMAND, COMMANDS)
- ✅ 2 condition types (PERMISSION, COOLDOWN)
- ✅ JavaScript animation system (Rhino)
- ✅ 3 built-in algorithms (weighted, pity_system, progressive_luck)
- ✅ Virtual and physical key types
- ✅ 6 display types with plugin integrations
- ✅ Full database persistence
- ✅ zMenu GUI integration
- ✅ Complete command system
- ✅ Hot reload support
- ✅ MiniMessage formatting

---

Developed with ❤️ by [Traqueur_](https://groupez.dev)
