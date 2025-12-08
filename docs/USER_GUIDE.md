# zCrates - User Guide

Complete guide for Minecraft server administrators to configure and use the zCrates plugin.

## Table of Contents

- [Installation](#installation)
- [Basic Configuration](#basic-configuration)
- [Creating a Crate](#creating-a-crate)
- [Key Types](#key-types)
- [Reward Types](#reward-types)
- [ItemStack Configuration with Delegates](#itemstack-configuration-with-delegates)
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

## ItemStack Configuration with Delegates

The `ItemStackWrapper` system allows you to configure items in two ways: direct material specification or delegation to custom item plugins. This is used throughout the plugin for reward items, display items, and physical keys.

### Direct Material Specification

Define items using vanilla Minecraft materials:

```yaml
item:
  material: DIAMOND_SWORD
  amount: 1
  display-name: "<gold>Legendary Sword"
  item-name: "legendary_sword"  # Custom item name (different from display name)
  lore:
    - "<gray>A powerful weapon"
    - "<gray>Forged in fire"
```

### Delegate System (copy-from)

The delegate system allows you to reference items from custom item plugins using the `copy-from` parameter. This creates a base item from the plugin, which you can then customize with additional properties.

**Structure:**

```yaml
item:
  copy-from:
    plugin-name: "PluginName"  # The plugin providing the item
    item-id: "item_identifier"  # The item ID in that plugin
  amount: 1                      # Optional: Override the item amount
  display-name: "<gold>Custom"   # Optional: Override the display name
  item-name: "custom_name"       # Optional: Override the item name
  lore:                          # Optional: Override the lore
    - "<gray>Custom lore line"
```

**Important Notes:**
- Either `material` or `copy-from` must be specified (not both)
- When using `copy-from`, you can still override `display-name`, `lore`, `item-name`, and `amount`
- The delegate retrieves the base item from the plugin, then applies your customizations
- If the item provider returns null, an error will be thrown

### Available Delegates

The following custom item plugins are supported through the hook system:

#### ItemsAdder

```yaml
copy-from:
  plugin-name: "ItemsAdder"
  item-id: "namespace:item_id"
```

**Example:**
```yaml
# Using ItemsAdder custom item as reward
rewards:
  - type: ITEM
    id: custom-weapon
    weight: 5.0
    display-item:
      material: PAPER  # Simple display
    item:
      copy-from:
        plugin-name: "ItemsAdder"
        item-id: "custom:legendary_sword"
      amount: 1
      display-name: "<gold>Legendary Blade"  # Override IA display name
      lore:
        - "<gray>A custom weapon from ItemsAdder"
```

#### Oraxen

```yaml
copy-from:
  plugin-name: "Oraxen"
  item-id: "item_id"
```

**Example:**
```yaml
# Using Oraxen custom item
item:
  copy-from:
    plugin-name: "Oraxen"
    item-id: "ruby_sword"
  amount: 1
```

#### Nexo

```yaml
copy-from:
  plugin-name: "Nexo"
  item-id: "item_id"
```

**Example:**
```yaml
# Using Nexo custom item
item:
  copy-from:
    plugin-name: "Nexo"
    item-id: "magic_wand"
  display-name: "<light_purple>Enchanted Wand"  # Custom display name
```

#### zItems

When the zItems hook is enabled, it registers itself as an ItemsAdder provider:

```yaml
copy-from:
  plugin-name: "zItems"  # Note: uses "ItemsAdder" as plugin name
  item-id: "item_id"
```

**Example:**
```yaml
# Using zItems
item:
  copy-from:
    plugin-name: "zItems"
    item-id: "custom_tool"
  amount: 5
```

### Complete Examples

#### Physical Key with Delegate

```yaml
key:
  type: PHYSIC
  name: "legendary-key"
  item:
    copy-from:
      plugin-name: "Oraxen"
      item-id: "custom_key"
    display-name: "<gold><bold>Legendary Crate Key"
    lore:
      - "<gray>Right-click on a legendary crate"
      - "<gray>to unlock amazing rewards!"
```

#### Item Reward with Multiple Customizations

```yaml
rewards:
  - type: ITEM
    id: custom-armor
    weight: 8.0
    display-item:
      material: DIAMOND_CHESTPLATE
      display-name: "<aqua>Custom Armor Preview"
    item:
      copy-from:
        plugin-name: "ItemsAdder"
        item-id: "armors:dragon_chestplate"
      amount: 1
      display-name: "<red><bold>Dragon Chestplate"
      lore:
        - "<gray>Legendary armor piece"
        - "<gray>Grants fire resistance"
        - ""
        - "<gold>Obtained from Legendary Crate"
```

#### Items List Reward with Mixed Sources

```yaml
rewards:
  - type: ITEMS
    id: mixed-kit
    weight: 10.0
    display-item:
      material: CHEST
      display-name: "<green>Starter Kit"
    items:
      # Vanilla item
      - material: DIAMOND_SWORD
        amount: 1
        enchantments:
          SHARPNESS: 5

      # Custom item from Oraxen
      - copy-from:
          plugin-name: "Oraxen"
          item-id: "custom_helmet"

      # Custom item from ItemsAdder
      - copy-from:
          plugin-name: "ItemsAdder"
          item-id: "custom:special_potion"
        amount: 3
```

### Troubleshooting

**"No item provider found for plugin: X"**
- The hook for that plugin is not enabled
- Make sure the target plugin is installed and loaded
- Check console logs for hook loading status

**"Item provider returned null for itemId: X"**
- The item ID doesn't exist in the target plugin
- Check the spelling and namespace of the item ID
- Verify the item exists using the target plugin's commands

**Items have wrong properties**
- Remember that `copy-from` creates the base item from the plugin first
- Your customizations (display-name, lore, etc.) are applied after
- Some properties might be overridden by the base item's NBT data

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

### PLACEHOLDER

Checks a PlaceholderAPI placeholder value with flexible comparison types. Requires the PlaceholderAPI hook to be enabled.

**Comparison Types:**
- `EQUALS` - String or numeric equality
- `NOT_EQUALS` - String or numeric inequality
- `GREATER_THAN` - Numeric comparison (>)
- `LESS_THAN` - Numeric comparison (<)
- `GREATER_THAN_OR_EQUALS` - Numeric comparison (>=)
- `LESS_THAN_OR_EQUALS` - Numeric comparison (<=)

**Basic Examples:**

```yaml
conditions:
  # Player must have at least level 10
  - type: PLACEHOLDER
    placeholder: "%player_level%"
    comparison: GREATER_THAN_OR_EQUALS
    result: "10"

  # Player must have more than $1000
  - type: PLACEHOLDER
    placeholder: "%vault_eco_balance%"
    comparison: GREATER_THAN
    result: "1000"

  # Player must be in the VIP group
  - type: PLACEHOLDER
    placeholder: "%vault_group%"
    comparison: EQUALS
    result: "VIP"

  # Player must NOT be in the Banned group
  - type: PLACEHOLDER
    placeholder: "%vault_group%"
    comparison: NOT_EQUALS
    result: "Banned"
```

**Advanced Examples:**

```yaml
# VIP Crate - requires VIP rank and level 50+
conditions:
  - type: PLACEHOLDER
    placeholder: "%luckperms_primary_group_name%"
    comparison: EQUALS
    result: "vip"
  - type: PLACEHOLDER
    placeholder: "%player_level%"
    comparison: GREATER_THAN_OR_EQUALS
    result: "50"

# Premium Crate - requires money and specific world
conditions:
  - type: PLACEHOLDER
    placeholder: "%vault_eco_balance%"
    comparison: GREATER_THAN_OR_EQUALS
    result: "5000"
  - type: PLACEHOLDER
    placeholder: "%player_world%"
    comparison: EQUALS
    result: "world_premium"
```

**Notes:**
- When using numeric comparisons (`GREATER_THAN`, `LESS_THAN`, etc.), the plugin automatically parses both values as numbers
- Common formatting characters (commas, spaces) are automatically removed during parsing
- If parsing fails for numeric comparisons, the condition returns `false`
- For `EQUALS` and `NOT_EQUALS`, if numeric parsing fails, string comparison is used as fallback (case-insensitive)

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