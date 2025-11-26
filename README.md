# 🎁 zCrates

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/GroupeZ-dev/zCrates)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)](https://groupez.dev)

Un plugin Minecraft Spigot/Paper moderne et performant pour gérer des caisses (crates) sur votre serveur.

## 📋 Description

**zCrates** est un plugin de gestion de caisses pour serveurs Minecraft. Développé avec les dernières technologies Java 21 et l'API Adventure pour une expérience utilisateur moderne et fluide.

## ✨ Fonctionnalités

- 🎯 Système de caisses moderne et performant
- 🎨 Support complet de MiniMessage pour des messages colorés et stylisés
- ⚡ Architecture modulaire avec API séparée
- 🔧 Configuration YAML simple et intuitive
- 🌐 Support de PlaceholderAPI (optionnel)
- 📦 Intégration avec zMenu (optionnel)
- 🔄 Rechargement à chaud de la configuration
- 🐛 Mode debug intégré pour le développement
- 📝 Système de logging avancé avec SLF4J

## 📦 Prérequis

- **Serveur**: Spigot ou Paper 1.21+
- **Java**: Version 21 ou supérieure
- **Dépendances optionnelles**:
  - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (pour les placeholders)
  - [zMenu](https://www.spigotmc.org/resources/zmenu.109103/) (pour l'intégration des menus)

## 🚀 Installation

1. Téléchargez le fichier JAR depuis la [page des releases](https://github.com/GroupeZ-dev/zCrates/releases)
2. Placez le fichier dans le dossier `plugins/` de votre serveur
3. Redémarrez votre serveur
4. Les fichiers de configuration seront automatiquement générés dans `plugins/zCrates/`

## ⚙️ Configuration

### config.yml

```yaml
debug: true  # Active le mode debug pour voir les logs détaillés
```

### messages.yml

Le fichier `messages.yml` contient tous les messages du plugin personnalisables avec le format MiniMessage.

```yaml
# Messages des commandes
no-permission: "<red>You do not have permission to execute this command."
only-in-game: "<red>This command can only be executed in-game."
requirement-not-met: "<red>You do not meet the requirements to perform this command."
arg-not-recognized: "<red>Argument not recognized."
```

**Format MiniMessage**: Le plugin utilise [MiniMessage](https://docs.advntr.dev/minimessage/format.html) pour la mise en forme des messages. Vous pouvez utiliser:
- `<red>`, `<blue>`, `<green>`, etc. pour les couleurs
- `<bold>`, `<italic>`, `<underlined>` pour le formatage
- `<gradient:red:blue>` pour des dégradés
- Et bien plus encore !

## 🎮 Commandes

| Commande | Alias | Description | Permission |
|----------|-------|-------------|------------|
| `/zcrates` | `/zc`, `/crate`, `/crates` | Commande principale | `zcrates.command.admin` |
| `/zcrates reload` | - | Recharge la configuration | `zcrates.command.admin` |

## 🔐 Permissions

| Permission | Description | Défaut |
|-----------|-------------|--------|
| `zcrates.command.admin` | Accès aux commandes administrateur | OP |

## 🛠️ Pour les développeurs

### API

Le plugin inclut une API complète pour les développeurs qui souhaitent créer des extensions ou intégrations.

```java
// Exemple d'utilisation de l'API
CratesPlugin plugin = (CratesPlugin) Bukkit.getPluginManager().getPlugin("zCrates");
```

### Compilation

```bash
# Cloner le repository
git clone https://github.com/GroupeZ-dev/zCrates.git
cd zCrates

# Compiler avec Gradle
./gradlew build

# Le JAR sera généré dans target/
```

### Dépendances Maven

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

## 📚 Structure du projet

```
zCrates/
├── api/                    # Module API pour les développeurs
│   └── src/main/java/
│       └── fr/traqueur/crates/api/
├── src/                    # Code source principal
│   ├── main/java/
│   │   └── fr/traqueur/crates/
│   └── main/resources/
│       ├── config.yml
│       ├── messages.yml
│       └── plugin.yml
└── build.gradle.kts
```

## 🤝 Support

- **Site web**: [https://groupez.dev](https://groupez.dev)
- **Auteur**: Traqueur_
- **Issues**: [GitHub Issues](https://github.com/GroupeZ-dev/zCrates/issues)

## 📄 Licence

Tous droits réservés © 2024 GroupeZ. Ce plugin est une propriété privée.

## 🔄 Changelog

### Version 1.0.0
- 🎉 Version initiale
- ✅ Système de base du plugin
- ✅ Architecture API modulaire
- ✅ Support MiniMessage
- ✅ Configuration YAML
- ✅ Système de commandes
- ✅ Support PlaceholderAPI et zMenu

---

Développé avec ❤️ par [Traqueur_](https://groupez.dev)
