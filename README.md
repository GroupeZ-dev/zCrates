# 🎁 zCrates

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/GroupeZ-dev/zCrates)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)](https://groupez.dev)

A modern and performant Minecraft Spigot/Paper plugin for managing crates on your server.

## 📋 Description

**zCrates** is a crate management plugin for Minecraft servers. Built with the latest Java 21 technologies and Adventure API for a modern and smooth user experience.

## 🌟 Why Choose zCrates?

- **🚀 JavaScript-Powered** - Unlike other crate plugins, zCrates uses JavaScript for animations, reward algorithms, and conditions, giving you unlimited customization possibilities
- **⚡ High Performance** - Built with modern Java 21 and optimized for large servers
- **🎨 Beautiful Animations** - Create stunning opening animations that keep your players engaged
- **🔧 Developer-Friendly** - Complete API for creating extensions and integrations
- **📦 Lightweight** - Modular architecture means you only load what you need

## ✨ Features

### Core Features
- 🎯 **Modern Crate System** - Advanced and performant crate management
- 🎨 **MiniMessage Support** - Full support for colorful and stylized messages
- ⚡ **Modular Architecture** - Separate API for developers and integrations
- 🔧 **YAML Configuration** - Simple and intuitive configuration files
- 🔄 **Hot Reload** - Reload configuration without restarting the server
- 📝 **Advanced Logging** - SLF4J logging system with debug mode

### Reward System
- 🎁 **Multiple Reward Types** - Commands, items, permissions, economy, and custom rewards
- 🎲 **Smart Algorithms** - JavaScript-based reward distribution algorithms
- 📊 **Weight & Probability** - Customizable chances for each reward
- 🔀 **Random & Guaranteed** - Mix random rewards with guaranteed ones

### Opening Conditions
- 🔐 **Permission-Based** - Require specific permissions to open crates
- 💰 **Economy Integration** - Set costs to open crates
- ⏰ **Cooldown System** - Time-based restrictions
- 📝 **Custom Conditions** - Create your own JavaScript conditions

### Animations & Effects
- 🎬 **JavaScript Animations** - Fully customizable opening animations written in JavaScript
- ✨ **Visual Effects** - Particles, sounds, and visual feedback
- 🎮 **Interactive GUI** - Beautiful and smooth user interface
- ⚙️ **Custom Algorithms** - Create your own animation logic with JavaScript

### Integrations
- 🌐 **PlaceholderAPI** - Full placeholder support (optional)
- 📦 **zMenu** - Advanced menu integration (optional)
- 💵 **Economy Plugins** - Vault and other economy plugin support

## 🎬 Showcase

> **Note**: Replace the placeholder images below with your actual GIF demonstrations

### Opening Animations
![Crate Opening Animation](https://via.placeholder.com/800x400?text=Crate+Opening+Animation+GIF)
<!-- Replace with: ![Crate Opening Animation](docs/gifs/opening-animation.gif) -->

> *Smooth and customizable opening animations powered by JavaScript*

### Reward Distribution
![Reward System](https://via.placeholder.com/800x400?text=Reward+Distribution+GIF)
<!-- Replace with: ![Reward System](docs/gifs/reward-system.gif) -->

> *Advanced reward algorithms with visual feedback*

### Interactive GUI
![Interactive Interface](https://via.placeholder.com/800x400?text=Interactive+GUI+GIF)
<!-- Replace with: ![Interactive Interface](docs/gifs/interactive-gui.gif) -->

> *Beautiful user interface with real-time updates*

### Custom Effects
![Particle Effects](https://via.placeholder.com/800x400?text=Particle+Effects+GIF)
<!-- Replace with: ![Particle Effects](docs/gifs/particle-effects.gif) -->

> *Stunning visual effects and animations*

## 📦 Requirements

- **Server**: Spigot or Paper 1.21+
- **Java**: Version 21 or higher
- **Optional Dependencies**:
  - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (for placeholders)
  - [zMenu](https://www.spigotmc.org/resources/zmenu.109103/) (for menu integration)

## 🚀 Installation

1. Download the JAR file from the [releases page](https://github.com/GroupeZ-dev/zCrates/releases)
2. Place the file in your server's `plugins/` folder
3. Restart your server
4. Configuration files will be automatically generated in `plugins/zCrates/`

## ⚙️ Configuration

### config.yml

```yaml
debug: true  # Enable debug mode to see detailed logs
```

### messages.yml

The `messages.yml` file contains all customizable plugin messages using MiniMessage format.

```yaml
# Command Messages
no-permission: "<red>You do not have permission to execute this command."
only-in-game: "<red>This command can only be executed in-game."
requirement-not-met: "<red>You do not meet the requirements to perform this command."
arg-not-recognized: "<red>Argument not recognized."
```

**MiniMessage Format**: The plugin uses [MiniMessage](https://docs.advntr.dev/minimessage/format.html) for message formatting. You can use:
- `<red>`, `<blue>`, `<green>`, etc. for colors
- `<bold>`, `<italic>`, `<underlined>` for formatting
- `<gradient:red:blue>` for gradients
- And much more!

## 🎁 Reward Types

zCrates supports multiple reward types to give you maximum flexibility:

### Available Reward Types

| Reward Type | Description | Example |
|------------|-------------|---------|
| **COMMAND** | Execute commands when won | `/give {player} diamond 64` |
| **ITEM** | Give items to the player | Enchanted Diamond Sword |
| **PERMISSION** | Grant permissions | `vip.rank` |
| **ECONOMY** | Give money (Vault) | `$1000` |
| **CUSTOM** | JavaScript-based rewards | Custom scripted actions |

### JavaScript Reward Algorithms

Create custom reward distribution logic with JavaScript:

```javascript
// Example: Weighted random selection
function selectReward(player, rewards) {
    let totalWeight = rewards.reduce((sum, r) => sum + r.weight, 0);
    let random = Math.random() * totalWeight;

    for (let reward of rewards) {
        random -= reward.weight;
        if (random <= 0) return reward;
    }
}
```

## 🔐 Opening Conditions

Control who can open crates and when with flexible conditions:

### Condition Types

| Condition Type | Description | Configuration |
|---------------|-------------|---------------|
| **PERMISSION** | Player must have permission | `permission: "crates.vip"` |
| **COST** | Requires money/items | `cost: 1000` |
| **COOLDOWN** | Time-based restriction | `cooldown: "24h"` |
| **LEVEL** | Minimum experience level | `level: 30` |
| **CUSTOM** | JavaScript conditions | Custom logic |

### JavaScript Conditions

Create complex conditions with JavaScript:

```javascript
// Example: Check if player is in specific world and has permission
function canOpen(player, crate) {
    return player.getWorld().getName() === "spawn"
        && player.hasPermission("crates.vip")
        && getBalance(player) >= 500;
}
```

## 🎬 Animations & JavaScript

### Animation System

All crate opening animations are powered by **JavaScript**, giving you complete control:

```javascript
// Example: Custom spin animation
function animate(player, rewards) {
    let speed = 100; // Initial speed in ms
    let currentIndex = 0;

    // Speed up the spin
    for (let i = 0; i < 20; i++) {
        displayReward(rewards[currentIndex % rewards.length]);
        wait(speed);
        currentIndex++;
    }

    // Slow down gradually
    while (speed < 500) {
        displayReward(rewards[currentIndex % rewards.length]);
        speed += 20;
        wait(speed);
        currentIndex++;
    }

    // Return final reward
    return rewards[currentIndex % rewards.length];
}
```

### Pre-built Animations

- **INSTANT** - Instant reward without animation
- **ROULETTE** - Classic spinning roulette style
- **CSGO** - CS:GO style horizontal scroll
- **SPIN** - 3D spinning animation
- **CUSTOM** - Your own JavaScript animation

## 📝 Configuration Examples

### Complete Crate Example

```yaml
crates:
  epic_crate:
    # Display information
    display-name: "<gradient:red:gold>Epic Crate</gradient>"
    description:
      - "<gray>Contains rare and valuable rewards!"
      - "<yellow>Open for a chance to win amazing prizes"

    # Opening conditions
    conditions:
      permission: "crates.epic"
      cost: 1000
      cooldown: "1h"

    # Animation settings
    animation:
      type: "CSGO"
      duration: 5000
      sound: "ENTITY_PLAYER_LEVELUP"
      script: "animations/csgo.js"

    # Reward algorithm
    algorithm:
      type: "WEIGHTED"
      script: "algorithms/weighted.js"

    # Rewards list
    rewards:
      - type: "ITEM"
        weight: 50
        item:
          material: "DIAMOND"
          amount: 16
          name: "<blue>Shiny Diamonds"

      - type: "COMMAND"
        weight: 30
        commands:
          - "give {player} diamond_sword 1"
          - "broadcast <gold>{player} won a Diamond Sword!"

      - type: "ECONOMY"
        weight: 15
        amount: 5000

      - type: "CUSTOM"
        weight: 5
        script: "rewards/custom_vip_rank.js"
```

### Custom JavaScript Animation Example

Create `animations/csgo.js`:

```javascript
function animate(player, rewards, finalReward) {
    let slots = 7;
    let iterations = 30;
    let delay = 50;

    // Build visible rewards array
    let visibleRewards = [];
    for (let i = 0; i < slots * iterations; i++) {
        visibleRewards.push(rewards[Math.floor(Math.random() * rewards.length)]);
    }

    // Place final reward in the middle
    visibleRewards[visibleRewards.length - Math.floor(slots / 2)] = finalReward;

    // Animate scrolling
    for (let i = 0; i < visibleRewards.length - slots; i++) {
        let display = visibleRewards.slice(i, i + slots);
        displayRewards(display, Math.floor(slots / 2)); // Highlight middle

        // Gradually slow down
        if (i > visibleRewards.length - slots - 20) {
            delay += 10;
        }

        playSound("UI_BUTTON_CLICK", 0.5, 1.0);
        wait(delay);
    }

    // Final celebration
    playSound("ENTITY_PLAYER_LEVELUP", 1.0, 1.0);
    spawnParticle("FIREWORKS_SPARK", player.getLocation(), 50);

    return finalReward;
}
```

### Custom Condition Example

Create `conditions/vip_world.js`:

```javascript
function canOpen(player, crate) {
    // Check if player is VIP
    if (!player.hasPermission("rank.vip")) {
        player.sendMessage("<red>You must be VIP to open this crate!");
        return false;
    }

    // Check if in spawn world
    if (player.getWorld().getName() !== "spawn") {
        player.sendMessage("<red>You can only open this crate in spawn!");
        return false;
    }

    // Check custom economy balance
    if (getBalance(player) < 1000) {
        player.sendMessage("<red>You need $1000 to open this crate!");
        return false;
    }

    return true;
}
```

## 🎮 Commands

| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/zcrates` | `/zc`, `/crate`, `/crates` | Main command | `zcrates.command.admin` |
| `/zcrates reload` | - | Reload configuration | `zcrates.command.admin` |

## 🔐 Permissions

| Permission | Description | Default |
|-----------|-------------|---------|
| `zcrates.command.admin` | Access to admin commands | OP |

## 🛠️ For Developers

### Java API

The plugin includes a complete API for developers who want to create extensions or integrations.

```java
// Example: Get the zCrates API
CratesPlugin plugin = (CratesPlugin) Bukkit.getPluginManager().getPlugin("zCrates");

// Example: Listen to crate events
@EventHandler
public void onCrateOpen(CrateOpenEvent event) {
    Player player = event.getPlayer();
    Crate crate = event.getCrate();
    // Custom logic here
}

// Example: Create a custom reward type
public class CustomReward implements Reward {
    @Override
    public void give(Player player) {
        // Your reward logic
    }
}
```

### JavaScript API

Create powerful scripts for animations, conditions, and reward algorithms:

```javascript
// Available JavaScript API:
// - player: Player object
// - crate: Crate configuration
// - server: Server instance
// - rewards: List of available rewards
// - displayReward(reward): Show reward in GUI
// - wait(ms): Pause execution
// - playSound(sound, volume, pitch): Play sound
// - spawnParticle(particle, location): Spawn particle effect
// - log(message): Debug logging

// Example: Advanced reward selection with streak bonus
function selectReward(player, rewards) {
    let streak = getPlayerData(player, "streak") || 0;
    let bonusMultiplier = 1 + (streak * 0.1); // 10% bonus per streak

    let adjustedRewards = rewards.map(r => ({
        ...r,
        weight: r.weight * bonusMultiplier
    }));

    return weightedRandom(adjustedRewards);
}
```

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

## 📚 Project Structure

```
zCrates/
├── api/                    # API module for developers
│   └── src/main/java/
│       └── fr/traqueur/crates/api/
├── src/                    # Main source code
│   ├── main/java/
│   │   └── fr/traqueur/crates/
│   └── main/resources/
│       ├── config.yml
│       ├── messages.yml
│       └── plugin.yml
└── build.gradle.kts
```

## 🤝 Support

- **Website**: [https://groupez.dev](https://groupez.dev)
- **Author**: Traqueur_
- **Issues**: [GitHub Issues](https://github.com/GroupeZ-dev/zCrates/issues)

## 📄 License

All rights reserved © 2024 GroupeZ. This plugin is private property.

## 🔄 Changelog

### Version 1.0.0
- 🎉 Initial release
- ✅ Base plugin system
- ✅ Modular API architecture
- ✅ MiniMessage support
- ✅ YAML configuration
- ✅ Command system
- ✅ PlaceholderAPI and zMenu support

---

Developed with ❤️ by [Traqueur_](https://groupez.dev)
