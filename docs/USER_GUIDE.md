# zCrates - User Guide

Complete guide for Minecraft server administrators to configure and use the zCrates plugin.

## Table of Contents

- [Installation](#installation)
- [Basic Configuration](#basic-configuration)
- [Creating a Crate](#creating-a-crate)
- [Key Types](#key-types)
- [Reward Types](#reward-types)
- [Opening Conditions](#opening-conditions)
- [Animations](#animations)
- [Selection Algorithms](#selection-algorithms)
- [Placing Crates](#placing-crates)
- [Commands](#commands)
- [Permissions](#permissions)
- [Hooks & Integrations](#hooks--integrations)
- [FAQ](#faq)

---

## Installation

1. Download the `zCrates.jar` file
2. Place it in your server's `plugins/` folder
3. Restart the server
4. Configuration files will be generated automatically

### Requirements

- **Minecraft**: 1.21+
- **Server**: Paper/Purpur
- **Java**: 21+
- **Dependencies**: zMenu (bundled)

---

## Basic Configuration

### config.yml

```yaml
# Enable debug messages
debug: false

# Database configuration
database:
  type: SQLITE  # SQLITE, MYSQL, or MARIADB
  table-prefix: "zcrates_"

  # For MySQL/MariaDB only
  host: "localhost"
  port: 3306
  database: "minecraft"
  username: "root"
  password: ""
```

### messages.yml

Customize all plugin messages:

```yaml
no-key: "<red>You don't have a key for this crate!"
no-permission: "<red>You don't have permission."
condition-no-permission: "<red>You don't have permission to open this crate!"
condition-cooldown: "<red>You must wait <time> before opening this crate again!"
no-rerolls-left: "<red>You have no rerolls remaining!"
reroll-success: "<green>Rerolled! <gray>(<remaining> rerolls left)"
```

---

## Creating a Crate

Create a file in `plugins/zCrates/crates/`. Example: `legendary.yml`

```yaml
# Unique crate identifier
id: legendary

# Animation to use (see /animations)
animation: roulette

# Reward selection algorithm
algorithm: weighted

# Display name
display-name: "<gold><bold>Legendary Crate"

# Number of rerolls allowed (0 = disabled)
max-rerolls: 3

# Key configuration
key:
  type: VIRTUAL  # or PHYSIC
  name: "legendary-key"

# Associated zMenu menu
related-menu: crate-menu-legendary

# Opening conditions (optional)
conditions:
  - type: PERMISSION
    permission: "zcrates.open.legendary"
  - type: COOLDOWN
    cooldown: 3600000  # 1 hour in milliseconds

# Rewards list
rewards:
  - type: ITEM
    id: diamond-reward
    weight: 10.0
    display-item:
      material: DIAMOND
      name: "<aqua>Diamonds"
      lore:
        - "<gray>5 diamonds"
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

---

## Key Types

### Virtual Key (VIRTUAL)

Stored in database, no physical item.

```yaml
key:
  type: VIRTUAL
  name: "my-key"
```

### Physical Key (PHYSIC)

Item in player's inventory.

```yaml
key:
  type: PHYSIC
  name: "my-physical-key"
  item:
    material: TRIPWIRE_HOOK
    name: "<gold>Legendary Key"
    lore:
      - "<gray>Right-click on a crate"
    glow: true
    custom-model-data: 1001
```

---

## Reward Types

### ITEM - Single item

```yaml
- type: ITEM
  id: my-item
  weight: 10.0
  display-item:
    material: DIAMOND_SWORD
    name: "<red>Chaos Sword"
  item:
    material: DIAMOND_SWORD
    name: "<red>Chaos Sword"
    enchantments:
      SHARPNESS: 5
      UNBREAKING: 3
```

### ITEMS - Multiple items

```yaml
- type: ITEMS
  id: my-kit
  weight: 5.0
  display-item:
    material: CHEST
    name: "<green>PvP Kit"
  items:
    - material: DIAMOND_SWORD
      enchantments:
        SHARPNESS: 3
    - material: GOLDEN_APPLE
      amount: 8
    - material: ENDER_PEARL
      amount: 16
```

### COMMAND - Single command

```yaml
- type: COMMAND
  id: my-command
  weight: 15.0
  display-item:
    material: PAPER
    name: "<yellow>XP Bonus"
  command: "xp add %player% 1000 points"
```

### COMMANDS - Multiple commands

```yaml
- type: COMMANDS
  id: my-commands
  weight: 2.0
  display-item:
    material: BEACON
    name: "<light_purple>Complete Pack"
  commands:
    - "eco give %player% 10000"
    - "xp add %player% 5000 points"
    - "lp user %player% permission set essentials.fly true"
```

---

## Opening Conditions

### PERMISSION

Requires a permission to open the crate.

```yaml
conditions:
  - type: PERMISSION
    permission: "zcrates.open.legendary"
```

### COOLDOWN

Wait time between openings (in milliseconds).

```yaml
conditions:
  - type: COOLDOWN
    cooldown: 60000  # 1 minute
```

The cooldown is stored in the player's persistent data container (PDC).

### Combining conditions

```yaml
conditions:
  - type: PERMISSION
    permission: "zcrates.vip"
  - type: COOLDOWN
    cooldown: 1800000  # 30 minutes
```

---

## Animations

Animations are defined in JavaScript in `plugins/zCrates/animations/`.

### Included Animation: Roulette

File: `animations/roulette.js`

Creates a classic roulette effect in the menu.

### Creating a Custom Animation

```javascript
// animations/my-animation.js

animations.register("my-animation", {
    phases: [
        {
            name: "spin",
            duration: 3000,      // 3 seconds
            interval: 2,         // Every 2 ticks
            speedCurve: "EASE_OUT",
            onStart: function(context) {
                context.player().playSound("BLOCK_NOTE_BLOCK_PLING", 1.0, 1.0);
            },
            onTick: function(context, tickData) {
                var progress = tickData.progress();
                var inventory = context.inventory();
                inventory.randomizeSlots();
            },
            onEnd: function(context) {
                context.player().playSound("ENTITY_PLAYER_LEVELUP", 1.0, 1.0);
            }
        },
        {
            name: "reveal",
            duration: 1000,
            interval: 20,
            onStart: function(context) {
                var reward = context.crate().reward();
                context.player().sendTitle("<gold>Congratulations!", reward.displayName());
            }
        }
    ],
    onComplete: function(context) {
        if (!context.crate().hasRerolls()) {
            context.inventory().close(60);  // Close after 3 seconds
        }
    },
    onCancel: function(context) {
        context.player().sendMessage("<red>Animation cancelled");
    }
});
```

### Available Speed Curves

- `LINEAR` - Constant speed
- `EASE_IN` - Starts slow, accelerates
- `EASE_OUT` - Starts fast, decelerates
- `EASE_IN_OUT` - Slow → Fast → Slow

---

## Selection Algorithms

Algorithms determine how rewards are selected.

### Included Algorithm: Weighted

File: `algorithms/weighted.js`

Selection based on reward weights.

### Creating a Custom Algorithm

```javascript
// algorithms/pity-system.js

algorithms.register("pity-system", function(context) {
    var rewards = context.rewards();
    var history = context.history();
    var crateId = context.crateId();

    // Get last 50 openings
    var recentOpenings = history.getRecent(50);

    // Count openings without rare reward
    var openingsWithoutRare = 0;
    for (var i = 0; i < recentOpenings.size(); i++) {
        var opening = recentOpenings.get(i);
        if (opening.crateId() === crateId) {
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
        return rewards.getRare();
    }

    return rewards.weightedRandom();
});
```

---

## Placing Crates

### Via Command

```
/zcrates place <crate-id> <display-type> [display-value]
```

**Display Types:**

| Type          | Description          | Example                 |
|---------------|----------------------|-------------------------|
| `BLOCK`       | Placed block         | `CHEST`, `ENDER_CHEST`  |
| `ENTITY`      | Spawned entity       | `ARMOR_STAND`, `ZOMBIE` |
| `MYTHIC_MOB`  | MythicMobs entity    | `MyMob`                 |
| `ITEMS_ADDER` | ItemsAdder furniture | `custom:my_crate`       |
| `NEXO`        | Nexo furniture       | `nexo:crate`            |
| `ORAXEN`      | Oraxen furniture     | `oraxen:crate`          |

### Examples

```bash
# Chest block crate
/zcrates place legendary BLOCK CHEST

# Armor stand entity crate
/zcrates place legendary ENTITY ARMOR_STAND

# MythicMobs crate
/zcrates place legendary MYTHIC_MOB CrateMob
```

### Removing a Placed Crate

Look at the crate and type:
```
/zcrates remove
```

### Removing All Crates in a World

```
/zcrates purge
```

---

## Commands

| Command                                   | Description               | Permission                  |
|-------------------------------------------|---------------------------|-----------------------------|
| `/zcrates reload`                         | Reload configurations     | `crates.command.reload`     |
| `/zcrates open <player> <crate> [force]`  | Open a crate for a player | `crates.command.open`       |
| `/zcrates give <player> <crate> <amount>` | Give keys                 | `crates.command.give`       |
| `/zcrates place <crate> <type> [value]`   | Place a crate             | `crates.command.place`      |
| `/zcrates remove`                         | Remove a placed crate     | `crates.command.remove`     |
| `/zcrates purge`                          | Remove all placed crates  | `crates.command.purge`      |
| `/zcrates animations debug`               | Debug animations          | `crates.command.animations` |

---

## Permissions

### Command Permissions

- `crates.command.reload` - Access to /zcrates reload
- `crates.command.open` - Access to /zcrates open
- `crates.command.give` - Access to /zcrates give
- `crates.command.place` - Access to /zcrates place
- `crates.command.remove` - Access to /zcrates remove
- `crates.command.purge` - Access to /zcrates purge
- `crates.command.animations` - Access to animation commands

### Crate Permissions

Defined in crate conditions:

```yaml
conditions:
  - type: PERMISSION
    permission: "zcrates.open.legendary"
```

---

## Hooks & Integrations

### PlaceholderAPI

Available placeholders:

| Placeholder                        | Description                |
|------------------------------------|----------------------------|
| `%zcrates_keys_<crate-id>%`        | Number of keys for a crate |
| `%zcrates_openings_<crate-id>%`    | Total openings count       |
| `%zcrates_last_reward_<crate-id>%` | Last reward won            |

### MythicMobs

Use MythicMobs mobs as crate displays:

```bash
/zcrates place legendary MYTHIC_MOB MobName
```

### ItemsAdder / Nexo / Oraxen

Use custom furnitures as crates:

```bash
/zcrates place legendary ITEMS_ADDER namespace:furniture_id
/zcrates place legendary NEXO furniture_id
/zcrates place legendary ORAXEN furniture_id
```

---

## FAQ

### How do I see a crate's rewards?

Left-click on a placed crate to open the preview.

### How does reroll work?

After the animation, if `max-rerolls` > 0, a reroll button appears. The player can restart the animation for a new reward.

### Are virtual keys persisted?

Yes, they are stored in the database (SQLite by default).

### How do I give keys via command?

```bash
/zcrates give <player> <crate-id> <amount>
```

### How do I force open without a key?

```bash
/zcrates open <player> <crate-id> true
```

### Why isn't my crate loading?

1. Check console for errors
2. Make sure the YAML file is valid
3. Verify the referenced animation exists
4. Use `/zcrates reload` after changes

### How do I create a custom menu?

Create a file in `plugins/zCrates/inventories/` following the zMenu format. Reference it with `related-menu` in your crate.

---

## Support

- **Discord**: [https://discord.gg/PTSYTC53d3]
- **GitHub Issues**: [https://github.com/GroupeZ-dev/zCrates/issues]
- **API Documentation**: See `API_REFERENCE.md`