# zCrates

A modern, feature-rich crate/loot box plugin for Minecraft Paper servers with JavaScript-powered animations and algorithms.

## Features

- **JavaScript Animations** - Create custom animations using ES6 JavaScript
- **Custom Algorithms** - Define reward selection logic with JavaScript
- **Multiple Key Types** - Virtual (database) and physical (item) keys
- **Flexible Rewards** - Items, commands, or multiple of each
- **Opening Conditions** - Permissions, cooldowns, and custom conditions
- **Reroll System** - Allow players to reroll for a different reward
- **Multi-Display Support** - Blocks, entities, MythicMobs, ItemsAdder, Nexo, Oraxen
- **PlaceholderAPI** - Built-in placeholders for keys and statistics
- **Database Support** - SQLite, MySQL, MariaDB

## Requirements

- **Minecraft**: 1.21+
- **Server**: Paper/Purpur
- **Java**: 21+
- **Dependencies**: zMenu (bundled)

## Installation

1. Download `zCrates.jar`
2. Place in `plugins/` folder
3. Restart server
4. Configure crates in `plugins/zCrates/crates/`

## Quick Start

### Creating a Crate

Create `plugins/zCrates/crates/example.yml`:

```yaml
id: example
animation: roulette
algorithm: weighted
display-name: "<gold>Example Crate"
max-rerolls: 1

key:
  type: VIRTUAL
  name: "example-key"

related-menu: crate-menu-example

rewards:
  - type: ITEM
    id: diamond-reward
    weight: 10.0
    display-item:
      material: DIAMOND
      name: "<aqua>Diamonds"
    item:
      material: DIAMOND
      amount: 5

  - type: COMMAND
    id: money-reward
    weight: 20.0
    display-item:
      material: GOLD_INGOT
      name: "<yellow>$1000"
    command: "eco give %player% 1000"
```

### Placing a Crate

```bash
/zcrates place example BLOCK CHEST
```

### Giving Keys

```bash
/zcrates give <player> example 5
```

## Commands

| Command                                   | Description              | Permission              |
|-------------------------------------------|--------------------------|-------------------------|
| `/zcrates reload`                         | Reload configurations    | `crates.command.reload` |
| `/zcrates open <player> <crate> [force]`  | Open a crate             | `crates.command.open`   |
| `/zcrates give <player> <crate> <amount>` | Give keys                | `crates.command.give`   |
| `/zcrates place <crate> <type> [value]`   | Place a crate            | `crates.command.place`  |
| `/zcrates remove`                         | Remove placed crate      | `crates.command.remove` |
| `/zcrates purge`                          | Remove all placed crates | `crates.command.purge`  |

## Key Types

### Virtual Key
Stored in database, no physical item.
```yaml
key:
  type: VIRTUAL
  name: "my-key"
```

### Physical Key
Item in player inventory.
```yaml
key:
  type: PHYSIC
  name: "my-key"
  item:
    material: TRIPWIRE_HOOK
    name: "<gold>Legendary Key"
    glow: true
```

## Reward Types

| Type       | Description           |
|------------|-----------------------|
| `ITEM`     | Single item reward    |
| `ITEMS`    | Multiple items reward |
| `COMMAND`  | Single command        |
| `COMMANDS` | Multiple commands     |

## Display Types

| Type          | Description                       |
|---------------|-----------------------------------|
| `BLOCK`       | Placed block (CHEST, ENDER_CHEST) |
| `ENTITY`      | Spawned entity (ARMOR_STAND)      |
| `MYTHIC_MOB`  | MythicMobs entity                 |
| `ITEMS_ADDER` | ItemsAdder furniture              |
| `NEXO`        | Nexo furniture                    |
| `ORAXEN`      | Oraxen furniture                  |

## JavaScript API

### Custom Animation

Create `plugins/zCrates/animations/my-animation.js`:

```javascript
animations.register("my-animation", {
    phases: [
        {
            name: "spin",
            duration: 3000,
            interval: 2,
            speedCurve: "EASE_OUT",
            onTick: function(context, tickData) {
                context.inventory().randomizeSlots();
            }
        }
    ],
    onComplete: function(context) {
        context.inventory().close(60);
    }
});
```

### Custom Algorithm

Create `plugins/zCrates/algorithms/my-algorithm.js`:

```javascript
algorithms.register("my-algorithm", function(context) {
    var rewards = context.rewards();
    var history = context.history();

    // Custom selection logic
    return rewards.weightedRandom();
});
```

## Opening Conditions

```yaml
conditions:
  - type: PERMISSION
    permission: "zcrates.open.vip"
  - type: COOLDOWN
    cooldown: 3600000  # 1 hour in ms
  - type: PLACEHOLDER # Requires PlaceholderAPI hook
    placeholder: "%player_level%"
    comparison: GREATER_THAN_OR_EQUALS  # EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUALS, LESS_THAN_OR_EQUALS
    result: "10"
```

**Comparison Types for PLACEHOLDER:**
- `EQUALS` - String/numeric equality (default)
- `NOT_EQUALS` - String/numeric inequality
- `GREATER_THAN` - Numeric comparison (>)
- `LESS_THAN` - Numeric comparison (<)
- `GREATER_THAN_OR_EQUALS` - Numeric comparison (>=)
- `LESS_THAN_OR_EQUALS` - Numeric comparison (<=)

## PlaceholderAPI

Numbers are formatted in compact notation (1.2K, 3.5M, etc.). Use `-raw` suffix for raw numbers.

| Placeholder                    | Description                           | Example   |
|--------------------------------|---------------------------------------|-----------|
| `%zcrates_<crate>_keys%`       | Key count (formatted)                 | `1.2K`    |
| `%zcrates_<crate>_keys-raw%`   | Key count (raw)                       | `1234`    |
| `%zcrates_<crate>_opened%`     | Crate openings (formatted)            | `3.5M`    |
| `%zcrates_<crate>_opened-raw%` | Crate openings (raw)                  | `3500000` |
| `%zcrates_crates_opened%`      | Total openings all crates (formatted) | `15K`     |
| `%zcrates_crates_opened_raw%`  | Total openings all crates (raw)       | `15000`   |

**Examples:**
```
%zcrates_legendary_keys%      → 5
%zcrates_legendary_opened%    → 1.2K
%zcrates_crates_opened%       → 3.5M
```

## API Usage

### Maven Dependency

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
CratesPlugin plugin = (CratesPlugin) Bukkit.getPluginManager().getPlugin("zCrates");
CratesManager cratesManager = plugin.getManager(CratesManager.class);

// Open a crate
Crate crate = Registry.get(CratesRegistry.class).getById("example");
OpenResult result = cratesManager.tryOpenCrate(player, crate);

// Give keys
UsersManager usersManager = plugin.getManager(UsersManager.class);
User user = usersManager.getUser(player.getUniqueId());
user.addKeys("example-key", 5);
```

### Listening to Events

```java
@EventHandler
public void onRewardGiven(RewardGivenEvent event) {
    Player player = event.getPlayer();
    Reward reward = event.getReward();
    // Log, broadcast, etc.
}
```

## Documentation

- **[User Guide](docs/USER_GUIDE.md)** - Complete configuration guide
- **[API Reference](docs/API_REFERENCE.md)** - Developer documentation

## Building

```bash
./gradlew build
```

Output: `target/zCrates.jar`

## License

Proprietary - All rights reserved.

## Support

- Discord: [Server Link]
- GitHub Issues: [Report bugs]