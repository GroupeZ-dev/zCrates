# 🎁 zCrates

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/GroupeZ-dev/zCrates)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)](https://groupez.dev)

A modern, JavaScript-powered Minecraft crate plugin with stunning animations and smart reward systems.

## 📋 Description

**zCrates** brings a new level of customization to your Minecraft server's crate system. Built with cutting-edge technology and powered by **JavaScript**, create unique opening experiences that keep your players engaged and coming back for more.

## ✨ Key Features

### 🎯 What Makes zCrates Special

- **🎬 JavaScript-Powered** - Create custom animations and reward algorithms without touching Java code
- **🎨 Beautiful Animations** - 4 built-in animations (instant, roulette, cascade, simple) with full customization
- **🎲 Smart Algorithms** - Pity system, progressive luck, and weighted random distribution
- **💎 Flexible Rewards** - Items, commands, or both - with weight-based probability
- **🔐 Advanced Conditions** - Permission, cooldown, and PlaceholderAPI support
- **🗄️ Database Support** - SQLite, MySQL, or MariaDB for persistent data
- **🎮 Beautiful GUI** - Powered by zMenu for stunning inventory interfaces
- **⚡ Hot Reload** - Update configurations without restarting your server

### 🎁 Reward Types

Create exciting rewards for your players:

- **ITEM** - Give single items with custom enchantments and names
- **ITEMS** - Award multiple items at once (perfect for armor sets!)
- **COMMAND** - Execute any console command
- **COMMANDS** - Run multiple commands together

All rewards support **weight-based probability** - a reward with weight 10 in a pool of 100 total weight = 10% chance!

### 🔐 Opening Conditions

Control who can open your crates:

- **PERMISSION** - Require specific permissions
- **COOLDOWN** - Set time restrictions (hourly, daily, etc.)
- **PLACEHOLDER** - Advanced PlaceholderAPI comparisons (level, balance, world, etc.)

Mix multiple conditions together - all must pass for the crate to open!

### 🎬 Animation System

**4 Built-in Animations:**

- **instant** - Quick reward reveal (500ms)
- **roulette** - Classic spinning wheel (11100ms)
- **cascade** - Progressive fill effect (3900ms)
- **simple** - Basic display (600ms)

Want more? **Create your own** with JavaScript - full control over timing, effects, and display!

### 🎲 Smart Reward Algorithms

**3 Intelligent Systems:**

| Algorithm            | How It Works                         | Best For            |
|----------------------|--------------------------------------|---------------------|
| **weighted**         | Standard probability-based selection | General crates      |
| **pity_system**      | Guarantees legendary after 10 tries  | Premium/paid crates |
| **progressive_luck** | Increases rare chances over time     | Event crates        |

**Create custom algorithms** with JavaScript to implement your own reward logic!

### 🔑 Key Management

Choose your key type:

- **VIRTUAL** - Stored in database (no inventory clutter)
- **PHYSIC** - Real items players can trade

### 🖼️ Display Options

**6 Ways to Display Your Crates:**

- BLOCK - Classic block display
- ENTITY - Animated mob display
- MYTHIC_MOB - MythicMobs integration
- ITEMS_ADDER - ItemsAdder custom items
- ORAXEN - Oraxen custom items
- NEXO - Nexo custom items

## 🎬 Showcase

> **Note:** GIFs are sped up for demonstration purposes - actual animations run smoother in-game!

### Virtual Crate Opening
Experience seamless crate openings with virtual keys - no inventory clutter, just pure excitement! Watch as the roulette animation reveals your reward.

![Virtual Crate Opening](https://img.groupez.dev/zcrates/virtual-crate.gif)

### Physical Crate Opening
Prefer tangible keys? Physical keys give players tradeable items they can hold, share, or collect before opening their crates.

![Physical Crate Opening](https://img.groupez.dev/zcrates/physic-opening.gif)

### Reroll System
Don't like your reward? The reroll feature lets players try their luck again for a chance at something better!

![Reroll Feature](https://img.groupez.dev/zcrates/reroll.gif)

### Block Display Placement
Place crates as interactive blocks anywhere in your world - perfect for spawn areas, shops, or event locations.

![Block Crate Placement](https://img.groupez.dev/zcrates/place-block-crate.gif)

### Entity Display Placement
Make your crates stand out with animated entity displays - floating, rotating, and eye-catching!

![Entity Crate Placement](https://img.groupez.dev/zcrates/place-entity-crate.gif)

### MythicMobs Display Placement
Integrate with MythicMobs for custom creature displays - turn your crates into unique, custom mob presentations!

![MythicMobs Crate Placement](https://img.groupez.dev/zcrates/place-mm-crate.gif)

## 📦 Requirements

- **Server**: Spigot or Paper 1.21+
- **Java**: Version 21 or higher
- **Required**: [zMenu](https://www.spigotmc.org/resources/zmenu.109103/)

**Optional Integrations:**
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) - For PLACEHOLDER conditions
- [MythicMobs](https://www.spigotmc.org/resources/mythicmobs.5702/) - For MYTHIC_MOB display
- [ItemsAdder](https://www.spigotmc.org/resources/itemsadder.73355/) - For custom item displays
- [Oraxen](https://www.spigotmc.org/resources/oraxen.72448/) - For custom item displays
- [Nexo](https://www.spigotmc.org/resources/nexo.115448/) - For custom item displays

## 🚀 Quick Start

1. Download zCrates and install **zMenu** (required)
2. Place both JARs in your `plugins/` folder
3. Restart your server
4. Edit configurations in `plugins/zCrates/`
5. Create your first crate and start rewarding players!

## ⚙️ Configuration Example

Simple crate configuration:

```yaml
id: my_crate
animation: roulette
algorithm: weighted
display-name: "<gradient:gold:yellow>Epic Crate</gradient>"

key:
  type: VIRTUAL
  name: epic_key

conditions:
  - type: PERMISSION
    permission: "crates.epic"
  - type: COOLDOWN
    cooldown: 3600000  # 1 hour
  - type: PLACEHOLDER
    placeholder: "%player_level%"
    comparison: GREATER_THAN_OR_EQUALS
    result: "10"

rewards:
  - type: ITEM
    id: diamond_sword
    weight: 20.0
    display-item:
      material: DIAMOND_SWORD
      name: "<blue>Legendary Sword"
    item:
      material: DIAMOND_SWORD
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
      name: "<aqua>Full Armor Set"
    items:
      - material: DIAMOND_HELMET
      - material: DIAMOND_CHESTPLATE
      - material: DIAMOND_LEGGINGS
      - material: DIAMOND_BOOTS
```

## 🎮 Commands

| Command                                       | Description                |
|-----------------------------------------------|----------------------------|
| `/zcrates`                                    | Show plugin info           |
| `/zcrates reload`                             | Reload configurations      |
| `/zcrates place <crate> <type> <value>`       | Place crate at location    |
| `/zcrates remove`                             | Remove crate at target     |
| `/zcrates purge`                              | Remove all crates in chunk |
| `/zcrates givekeys <player> <crate> <amount>` | Give keys to player        |
| `/zcrates open <player> <crate> [force]`      | Force crate opening        |

**Aliases:** `/zc`, `/crates`

## 🎨 Customization

### Create Custom Animations

Write your own animations in JavaScript:

```javascript
animations.register("my-animation", {
    phases: [
        {
            name: "spinning",
            duration: 3000,
            interval: 50,
            speedCurve: "EASE_OUT",
            onTick: (context, tickData) => {
                // Your animation logic
            }
        }
    ]
});
```

### Create Custom Algorithms

Implement your own reward logic:

```javascript
algorithms.register("my-algorithm", (context) => {
    // Your reward selection logic
    return context.rewards().weightedRandom();
});
```

## 💡 Why Choose zCrates?

✅ **Easy to Use** - YAML configuration with clear examples
✅ **Highly Customizable** - JavaScript support for unlimited possibilities
✅ **Performance** - Built with Java 21 and optimized for large servers
✅ **Modern** - MiniMessage support for beautiful text formatting
✅ **Reliable** - Database persistence ensures data safety
✅ **Supported** - Active development and updates

## 🤝 Support & Links

- **Website**: [groupez.dev](https://groupez.dev)
- **Author**: Traqueur_
- **Report Issues**: [GitHub Issues](https://github.com/GroupeZ-dev/zCrates/issues)

---

## 🔄 What's Included

✅ 4 reward types (ITEM, ITEMS, COMMAND, COMMANDS)
✅ 3 condition types (PERMISSION, COOLDOWN, PLACEHOLDER)
✅ JavaScript animation system with 4 built-in animations
✅ 3 smart reward algorithms
✅ Virtual and physical key support
✅ 6 display type integrations
✅ Full database persistence
✅ Beautiful GUI with zMenu
✅ Hot reload system
✅ MiniMessage formatting
✅ PlaceholderAPI integration

---

**Ready to elevate your server's crate experience?**

Download now and start creating unforgettable moments for your players! 🎉

---

Developed with ❤️ by [Traqueur_](https://groupez.dev)
