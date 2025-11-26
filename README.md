# 🎁 zCrates

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/GroupeZ-dev/zCrates)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)](https://groupez.dev)

A modern and performant Minecraft Spigot/Paper plugin for managing crates on your server.

## 📋 Description

**zCrates** is a modern crate management plugin for Minecraft servers currently in early development. Built with the latest Java 21 technologies and Adventure API foundation.

> ⚠️ **Development Status**: This plugin is in active development. Core features are currently being implemented.

## 🏗️ Current Status

### ✅ Implemented
- **Plugin Infrastructure** - Complete plugin lifecycle with enable/disable
- **Command System** - `/zcrates` main command and `/zcrates reload` subcommand
- **Configuration System** - YAML-based configuration with hot reload support
- **Logging System** - Advanced SLF4J logging with debug mode and MiniMessage formatting
- **Messages Service** - Full MiniMessage support for colorful and stylized messages
- **Platform Detection** - Automatic Paper vs Spigot detection with native/wrapper support
- **PlaceholderAPI Integration** - Ready for placeholder parsing
- **Registry System** - Generic registry system for managing game objects
- **Modular API** - Separate API module for developers

### 🚧 In Development
- Crate system and mechanics
- Reward distribution
- Opening conditions
- Animations
- GUI interface

## ✨ Planned Features

### Core Features
- 🎯 **Modern Crate System** - Advanced and performant crate management
- 🎨 **MiniMessage Support** - ✅ Already implemented for messages
- ⚡ **Modular Architecture** - ✅ Separate API module ready
- 🔧 **YAML Configuration** - ✅ Configuration system implemented
- 🔄 **Hot Reload** - ✅ Already functional
- 📝 **Advanced Logging** - ✅ SLF4J logging system active

### Reward System (Planned)
- 🎁 **Multiple Reward Types** - Commands, items, permissions, economy, and more
- 📊 **Weight & Probability** - Customizable chances for each reward
- 🔀 **Random & Guaranteed** - Mix random rewards with guaranteed ones

### Opening Conditions (Planned)
- 🔐 **Permission-Based** - Require specific permissions to open crates
- 💰 **Economy Integration** - Set costs to open crates
- ⏰ **Cooldown System** - Time-based restrictions
- 📝 **Custom Conditions** - Flexible condition system

### Animations & Effects (Planned)
- 🎬 **Custom Animations** - Customizable opening animations
- ✨ **Visual Effects** - Particles, sounds, and visual feedback
- 🎮 **Interactive GUI** - Beautiful and smooth user interface

### Integrations
- 🌐 **PlaceholderAPI** - ✅ Integration ready
- 📦 **zMenu** - Planned menu integration
- 💵 **Economy Plugins** - Planned Vault support

## 🎬 Showcase

> **Coming Soon**: Visual demonstrations will be added as features are implemented

<!--
Planned showcase sections:
- Opening Animations
- Reward Distribution
- Interactive GUI
- Particle Effects
-->

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

### Current API

The plugin includes a modular API architecture. Currently available:

```java
// Example: Access the plugin
CratesPlugin plugin = (CratesPlugin) Bukkit.getPluginManager().getPlugin("zCrates");

// Example: Use the Logger API
import fr.traqueur.crates.api.Logger;
Logger.info("Message with <red>color</red> support!");
Logger.debug("Debug message");
Logger.severe("Error message");

// Example: Access settings
import fr.traqueur.crates.api.settings.Settings;
PluginSettings settings = Settings.get(PluginSettings.class);
boolean debug = settings.debug();

// Example: Use the Registry system
import fr.traqueur.crates.api.registries.Registry;
// Registry API available for future crate/reward management
```

**Available API Classes:**
- `CratesPlugin` - Main plugin interface
- `Logger` - Advanced logging with MiniMessage support
- `MessagesService` - Message handling for Paper/Spigot
- `Settings` - Configuration management
- `Registry<T>` - Generic registry for managing objects
- `FileBasedRegistry` - YAML-based registry implementation
- `PlaceholderParser` - Placeholder parsing interface
- `Manager` - Base manager interface

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

### Version 1.0.0 (In Development)
- ✅ Plugin infrastructure and lifecycle
- ✅ Modular API architecture
- ✅ Command system (`/zcrates`, `/zcrates reload`)
- ✅ YAML configuration with hot reload
- ✅ Advanced logging system with MiniMessage
- ✅ Messages service with Paper/Spigot support
- ✅ Platform detection (Paper vs Spigot)
- ✅ Registry system for object management
- ✅ PlaceholderAPI integration ready
- 🚧 Crate system (coming soon)
- 🚧 Reward distribution (coming soon)
- 🚧 Opening conditions (coming soon)
- 🚧 Animations (coming soon)

---

Developed with ❤️ by [Traqueur_](https://groupez.dev)
