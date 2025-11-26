# 🎁 zCrates

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/GroupeZ-dev/zCrates)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)](https://groupez.dev)

A modern and performant Minecraft Spigot/Paper plugin for managing crates on your server.

## 📋 Description

**zCrates** is a crate management plugin for Minecraft servers. Built with the latest Java 21 technologies and Adventure API for a modern and smooth user experience.

## ✨ Features

- 🎯 Modern and performant crate system
- 🎨 Full MiniMessage support for colorful and stylized messages
- ⚡ Modular architecture with separate API
- 🔧 Simple and intuitive YAML configuration
- 🌐 PlaceholderAPI support (optional)
- 📦 zMenu integration (optional)
- 🔄 Hot reload of configuration
- 🐛 Built-in debug mode for development
- 📝 Advanced logging system with SLF4J

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

### API

The plugin includes a complete API for developers who want to create extensions or integrations.

```java
// Example API usage
CratesPlugin plugin = (CratesPlugin) Bukkit.getPluginManager().getPlugin("zCrates");
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
