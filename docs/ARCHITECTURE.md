# zCrates - Architecture Documentation

High-level architecture overview for maintainers and contributors.

## Table of Contents

- [System Overview](#system-overview)
- [Module Structure](#module-structure)
- [Core Subsystems](#core-subsystems)
- [Data Flow](#data-flow)
- [Plugin Lifecycle](#plugin-lifecycle)
- [Design Decisions](#design-decisions)
- [Performance Considerations](#performance-considerations)
- [Security Architecture](#security-architecture)

---

## System Overview

zCrates is a modular Minecraft crate plugin built on Paper 1.21+ with Java 21. The architecture emphasizes:

- **Separation of Concerns** - API, implementation, and extensions are distinct
- **Extensibility** - Hook system for seamless plugin integration
- **Security** - Sandboxed JavaScript execution with restricted API surface
- **Performance** - Async database operations, in-memory caching, efficient registries
- **Type Safety** - Polymorphic YAML deserialization, strong typing throughout

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        USER INTERFACE                            │
│  (Commands, GUIs, Chat Messages, Placed Crates)                 │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                          │
│  (Commands, Listeners, zMenu Integration)                       │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                     BUSINESS LOGIC LAYER                         │
│  (CratesManager, UsersManager, AnimationExecutor)               │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      DOMAIN MODEL LAYER                          │
│  (Crate, Reward, Key, Animation, Algorithm, User)               │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                          │
│  (Registries, Script Engine, Storage, Serialization)            │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      EXTERNAL SERVICES                           │
│  (Database, zMenu, Bukkit API, Custom Item Plugins)             │
└─────────────────────────────────────────────────────────────────┘
```

---

## Module Structure

### 1. API Module (`api/`)

**Purpose:** Public contract for external plugins

**Responsibilities:**
- Define interfaces for all core models
- Expose event system
- Provide registry access
- Define extension points (Hook, ItemsProvider, etc.)

**Key Characteristics:**
- No implementation details
- Minimal dependencies (only Bukkit API, annotations)
- Semantic versioning
- Backward compatibility guarantees

**Exported Packages:**
- `fr.traqueur.crates.api` - Core interfaces
- `fr.traqueur.crates.api.events` - Event system
- `fr.traqueur.crates.api.models` - Model interfaces
- `fr.traqueur.crates.api.registries` - Registry system
- `fr.traqueur.crates.api.managers` - Manager interfaces
- `fr.traqueur.crates.api.hooks` - Hook system
- `fr.traqueur.crates.api.providers` - Provider interfaces

### 2. Common Module (`common/`)

**Purpose:** Shared implementations used across main plugin and hooks

**Responsibilities:**
- Block/Entity display implementations
- Shared wrapper classes
- Common utilities

**Dependencies:**
- API module
- Bukkit API

### 3. Main Plugin Module (`src/`)

**Purpose:** Core plugin implementation

**Responsibilities:**
- Implement all API interfaces
- Manage plugin lifecycle
- Orchestrate business logic
- Persist data
- Execute JavaScript

**Dependencies:**
- API module
- Common module
- Bukkit/Paper API
- Rhino (JavaScript engine)
- Structura (YAML serialization)
- CommandsAPI (command framework)
- Sarah (ORM)
- zMenu (GUI system)
- Reflections (classpath scanning)

### 4. Hooks Module (`hooks/`)

**Purpose:** Optional plugin integrations

**Structure:**
```
hooks/
├── ItemsAdder/     - ItemsAdder custom items
├── MythicMobs/     - MythicMobs entity displays
├── Nexo/           - Nexo custom items
├── Oraxen/         - Oraxen custom items
├── PlaceholderAPI/ - Placeholder support
└── zItems/         - zItems integration
```

**Characteristics:**
- Each hook is a separate Gradle subproject
- Isolated dependencies (only required hook targets)
- Auto-discovered via `@AutoHook` annotation
- Gracefully disabled if target plugin missing

---

## Core Subsystems

### 1. Registry System

**Architecture:**

```
┌────────────────────────────────────────┐
│      Static Registry Accessor          │
│  ClassToInstanceMap<Registry>          │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│     Registry<ID, T> Interface          │
│  - register(ID, T)                     │
│  - getById(ID): T                      │
│  - getAll(): Collection<T>             │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│  FileBasedRegistry<ID, T> Abstract     │
│  - loadFromFolder()                    │
│  - reload()                            │
│  - folder hierarchy support            │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│    Concrete Implementations            │
│  - ZAnimationRegistry (.js)            │
│  - ZRandomAlgorithmRegistry (.js)      │
│  - ZCratesRegistry (.yml)              │
└────────────────────────────────────────┘
```

**Design Decisions:**

1. **Static Access via ClassToInstanceMap**
   - Avoids dependency injection complexity
   - Type-safe registry retrieval
   - Single source of truth for all registries

2. **FileBasedRegistry Pattern**
   - Automatic resource copying from JAR
   - Folder hierarchy with metadata
   - Hot-reload support
   - Consistent loading behavior

3. **Generic ID Type**
   - String IDs for file-based registries
   - Enum IDs for runtime registries (DisplayType)
   - Type safety at compile time

### 2. Manager System

**Architecture:**

```
┌────────────────────────────────────────┐
│     Bukkit ServicesManager             │
│  (Plugin service registration)         │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│      Manager Interface                 │
│  - init()                              │
│  - reload()                            │
└────────────────────────────────────────┘
               ↓
┌────────────────┬───────────────────────┐
│ CratesManager  │   UsersManager        │
│ - Opening      │   - User cache        │
│ - Animation    │   - Key management    │
│ - Reroll       │   - Persistence       │
│ - Preview      │   - History           │
│ - Placed       │                       │
└────────────────┴───────────────────────┘
```

**Design Decisions:**

1. **ServicesManager Integration**
   - Standard Bukkit pattern
   - Automatic lifecycle management
   - Accessible from any plugin

2. **Single Responsibility**
   - CratesManager: crate operations
   - UsersManager: user data
   - Clear separation of concerns

3. **In-Memory State**
   - Active openings in CratesManager
   - User cache in UsersManager
   - Placed crates loaded per chunk

### 3. Script Engine System

**Architecture:**

```
┌────────────────────────────────────────┐
│      ZScriptEngine (Singleton)         │
│  - Rhino Context                       │
│  - Custom WrapFactory                  │
│  - Security constraints                │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│     ScriptableObject Scopes            │
│  (Isolated per execution)              │
└────────────────────────────────────────┘
               ↓
┌────────────────┬───────────────────────┐
│   animations   │    algorithms         │
│   global       │    global             │
│   object       │    object             │
└────────────────┴───────────────────────┘
               ↓
┌────────────────────────────────────────┐
│     Context Objects (Wrappers)         │
│  - PlayerWrapper                       │
│  - InventoryWrapper                    │
│  - CrateWrapper                        │
│  - RewardsWrapper                      │
│  - HistoryWrapper                      │
└────────────────────────────────────────┘
```

**Design Decisions:**

1. **Single Engine Instance**
   - Shared Rhino Context across all scripts
   - Reduced memory footprint
   - Consistent security settings

2. **Isolated Scopes**
   - Each script execution uses new scope
   - Null parent (no prototype chain access)
   - Prevents cross-script interference

3. **Wrapper Pattern**
   - Controlled API surface
   - Type-safe method exposure
   - No direct Java object access

4. **Security First**
   - Interpreter mode (no bytecode)
   - Method blacklist via WrapFactory
   - No Java package access
   - No reflection

### 4. Animation System

**Architecture:**

```
┌────────────────────────────────────────┐
│      AnimationsRegistry                │
│  - Load .js files                      │
│  - Register via AnimationsRegistrar    │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│      Animation Model                   │
│  - List<AnimationPhase>                │
│  - onComplete callback                 │
│  - onCancel callback                   │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│      AnimationExecutor                 │
│  - Phase timing (BukkitRunnable)       │
│  - SpeedCurve application              │
│  - Context passing                     │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│      JavaScript Callbacks              │
│  - onStart(context)                    │
│  - onTick(context, tickData)           │
│  - onComplete(context)                 │
└────────────────────────────────────────┘
```

**Design Decisions:**

1. **Phase-Based Execution**
   - Sequential phase execution
   - Independent timing per phase
   - Clean state transitions

2. **SpeedCurve Integration**
   - Mathematical easing functions
   - Applied to progress value
   - Smooth visual transitions

3. **BukkitRunnable Scheduling**
   - Sync with server tick rate
   - Cancel-safe execution
   - Automatic cleanup

### 5. Storage System

**Architecture:**

```
┌────────────────────────────────────────┐
│      DatabaseConnection                │
│  - SQLite / MySQL / MariaDB            │
│  - Connection pooling (HikariCP)       │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│      Sarah ORM Layer                   │
│  - RequestHelper                       │
│  - Repository pattern                  │
│  - Migration system                    │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│      UserRepository                    │
│  - CRUD operations                     │
│  - CompletableFuture async             │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│      DTO ↔ Domain Mapping              │
│  - UserDTO → ZUser                     │
│  - Lightweight transfer objects        │
└────────────────────────────────────────┘
```

**Design Decisions:**

1. **Sarah ORM**
   - Lightweight abstraction
   - Multiple database support
   - Migration management
   - Connection pooling

2. **Repository Pattern**
   - Separation of persistence logic
   - Testable data access
   - Async by default

3. **DTO Pattern**
   - Database schema independence
   - Clear domain/persistence boundary
   - Easy schema evolution

4. **Async Operations**
   - Non-blocking database access
   - CompletableFuture API
   - Main thread safety

### 6. Display System

**Architecture:**

```
┌────────────────────────────────────────┐
│  CrateDisplayFactoriesRegistry         │
│  Map<DisplayType, Factory>             │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│  CrateDisplayFactory<T>                │
│  - create(Location, value, yaw)        │
│  - isValidValue(value)                 │
│  - getSuggestions()                    │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│  CrateDisplay<T>                       │
│  - spawn()                             │
│  - remove()                            │
│  - matches(T)                          │
└────────────────────────────────────────┘
               ↓
┌────────────────────────────────────────┐
│  PlacedCrate (Immutable Record)        │
│  - Stored in Chunk PDC                 │
│  - Load/unload with chunks             │
└────────────────────────────────────────┘
```

**Design Decisions:**

1. **Generic Type Parameter**
   - Type-safe value matching
   - Compile-time validation
   - Flexible display types

2. **Factory Pattern**
   - Encapsulates creation logic
   - Validation before creation
   - Tab completion support

3. **Chunk PDC Storage**
   - Persistent across restarts
   - Automatic chunk association
   - No separate database table

4. **Lazy Loading**
   - Displays created on chunk load
   - Removed on chunk unload
   - Memory efficient

---

## Data Flow

### Crate Opening Flow

```
1. Player interacts with placed crate
   ↓
2. CratesListener handles interaction
   ↓
3. CratesManager.tryOpenCrate(player, crate)
   ├─ Check if player has key
   ├─ Check all OpenConditions
   ├─ Fire CratePreOpenEvent (cancellable)
   └─ If all pass, continue
   ↓
4. Fire CrateOpenEvent
   ↓
5. Consume key (virtual or physical)
   ↓
6. CratesManager.openCrate(player, crate, animation)
   ├─ Generate reward via algorithm
   ├─ Fire RewardGeneratedEvent
   ├─ Store OpenedCrate state
   └─ Start animation
   ↓
7. AnimationExecutor.start(player, animation, context)
   ├─ Execute phases sequentially
   ├─ Call JavaScript callbacks
   └─ Update inventory
   ↓
8. Animation completes
   ↓
9. If rerolls available, show reroll button
   OR
   Give reward and close inventory
   ↓
10. Fire RewardGivenEvent
    ↓
11. Persist CrateOpening to database (async)
    ↓
12. Cleanup OpenedCrate state
```

### User Data Flow

```
1. Player joins server
   ↓
2. PlayerJoinEvent listener
   ↓
3. UsersManager.loadUser(uuid) (async)
   ├─ Query database via UserRepository
   ├─ Convert UserDTO → ZUser
   └─ Store in cache
   ↓
4. Player operations (synchronous via cache)
   ├─ getKeyAmount()
   ├─ addKey()
   ├─ removeKey()
   └─ All modifications in-memory
   ↓
5. Player quits server
   ↓
6. PlayerQuitEvent listener
   ↓
7. UsersManager.saveUser(user) (async)
   ├─ Convert ZUser → UserDTO
   ├─ Update database via UserRepository
   └─ Remove from cache
```

### Configuration Loading Flow

```
1. Plugin enable or /zcrates reload
   ↓
2. For each FileBasedRegistry:
   ├─ Clear existing entries
   ├─ Scan folder recursively
   ├─ Load each file via loadFile(Path)
   │  ├─ For .js: evaluate via ZScriptEngine
   │  └─ For .yml: deserialize via Structura
   ├─ Capture registrations
   └─ Store in internal map
   ↓
3. Validate cross-references
   ├─ Crate → Animation exists
   ├─ Crate → Algorithm exists
   └─ Reward display items valid
   ↓
4. Log statistics
```

---

## Plugin Lifecycle

### Initialization Sequence

```
onEnable()
├─1. Load config files (config.yml, messages.yml)
│
├─2. Register polymorphic type adapters
│   ├─ Reward types (ITEM, ITEMS, COMMAND, COMMANDS)
│   ├─ Key types (VIRTUAL, PHYSIC)
│   ├─ DatabaseSettings types (SQLITE, MYSQL, MARIADB)
│   └─ OpenCondition types (PERMISSION, COOLDOWN)
│
├─3. Initialize core services
│   ├─ Logger (debug mode)
│   ├─ Keys (PDC key registry)
│   ├─ MessagesService (MiniMessage parsing)
│   └─ PlacedCrateDataType (PDC serialization)
│
├─4. Create ZScriptEngine instance
│
├─5. Integrate with zMenu
│   ├─ Get InventoryManager
│   ├─ Get ButtonManager
│   └─ Register custom buttons (ZCRATES_ANIMATION, etc.)
│
├─6. Create and register all registries
│   ├─ AnimationsRegistry
│   ├─ RandomAlgorithmsRegistry
│   ├─ CratesRegistry
│   ├─ ItemsProvidersRegistry
│   ├─ HooksRegistry
│   └─ CrateDisplayFactoriesRegistry
│
├─7. Discover and enable hooks
│   ├─ Scan classpath for @AutoHook
│   ├─ Check if target plugin loaded
│   └─ Call hook.onEnable()
│
├─8. Register built-in display factories
│   ├─ BLOCK (BlockCrateDisplayFactory)
│   └─ ENTITY (EntityCrateDisplayFactory)
│
├─9. Load configurations
│   ├─ loadFromFolder(animations/)
│   ├─ loadFromFolder(algorithms/)
│   └─ loadFromFolder(crates/)
│
├─10. Initialize database
│   ├─ Create DatabaseConnection
│   ├─ Validate connection
│   ├─ Create UserRepository
│   └─ Execute migrations
│
├─11. Create and register managers
│   ├─ UsersManager (with repository)
│   └─ CratesManager
│
├─12. Initialize managers
│   ├─ usersManager.init()
│   │   ├─ Register listeners
│   │   └─ Load online players
│   └─ cratesManager.init()
│       └─ Verify menu files exist
│
└─13. Register commands
    └─ ZCratesCommand with subcommands
```

### Shutdown Sequence

```
onDisable()
├─1. Stop all active animations
│   └─ cratesManager.stopAllOpening()
│
├─2. Unload all placed crate displays
│   └─ cratesManager.unloadAllPlacedCrates()
│
├─3. Save all cached users
│   └─ For each cached user: saveUser(user)
│
├─4. Close database connection
│   └─ databaseConnection.disconnect()
│
├─5. Close script engine
│   └─ scriptEngine.close()
│
└─6. Shutdown services
    └─ MessagesService.close()
```

### Reload Sequence

```
/zcrates reload
├─1. Stop all active animations
│
├─2. Clear all registries
│   ├─ animationsRegistry.clear()
│   ├─ algorithmsRegistry.clear()
│   └─ cratesRegistry.clear()
│
├─3. Reload configuration files
│   ├─ config.yml
│   └─ messages.yml
│
├─4. Reload file-based registries
│   ├─ loadFromFolder(animations/)
│   ├─ loadFromFolder(algorithms/)
│   └─ loadFromFolder(crates/)
│
└─5. Notify managers
    └─ cratesManager.reload()
```

---

## Design Decisions

### 1. Why Rhino Instead of Nashorn/GraalVM?

**Decision:** Use Rhino JavaScript engine

**Rationale:**
- **Security:** Fine-grained control over Java access
- **Compatibility:** Works on all Java versions (11+)
- **Sandbox:** Built-in sandboxing with `initSafeStandardObjects()`
- **Performance:** Interpreter mode sufficient for animation/algorithm scripts

**Trade-offs:**
- Not ES2015+ compliant (limited ES6 support)
- Slower than GraalVM for compute-intensive tasks
- No native Promise support

### 2. Why Static Registry Access?

**Decision:** Use `ClassToInstanceMap` for static registry access

**Rationale:**
- **Simplicity:** No dependency injection framework needed
- **Type Safety:** Compile-time verification of registry types
- **Accessibility:** Accessible from any plugin code
- **Singleton:** Single source of truth

**Trade-offs:**
- Global state (harder to test in isolation)
- Cannot swap implementations at runtime
- Requires manual registration

### 3. Why Chunk PDC for Placed Crates?

**Decision:** Store placed crates in Chunk PDC

**Rationale:**
- **Automatic Association:** Chunks own their crates
- **Persistence:** Survives server restarts
- **Performance:** No database queries for crate lookup
- **Simplicity:** No separate placed_crates table

**Trade-offs:**
- Limited to chunk-sized queries (can't query all crates in world easily)
- PDC size limits (unlikely to hit in practice)
- Chunk load required for data access

### 4. Why ServicesManager for Managers?

**Decision:** Register managers via Bukkit ServicesManager

**Rationale:**
- **Standard Pattern:** Follows Bukkit conventions
- **Discovery:** Other plugins can find managers
- **Lifecycle:** Bukkit manages service lifecycle
- **Type Safety:** Retrieval by class

**Trade-offs:**
- Couples to Bukkit API
- Limited lifecycle control
- No priority/ordering guarantees

### 5. Why Polymorphic YAML with Structura?

**Decision:** Use Structura for YAML deserialization

**Rationale:**
- **Type Safety:** Compile-time validation of YAML structure
- **Polymorphism:** `@Polymorphic` annotation for type field
- **Validation:** Built-in validation (@Options, @DefaultInt, etc.)
- **Maintainability:** YAML changes reflected in code

**Trade-offs:**
- Additional dependency
- Learning curve for contributors
- Less flexible than manual parsing

---

## Performance Considerations

### 1. Memory Management

**User Cache:**
- Cleared on player quit (prevents memory leaks)
- ConcurrentHashMap for thread-safe access
- No unbounded growth

**Registry Storage:**
- HashMap-based (O(1) lookup)
- Immutable after loading (no synchronization needed)
- Cleared on reload (prevents memory bloat)

**Animation State:**
- Removed immediately after completion
- No persistent animation history
- Bounded by concurrent player count

### 2. Database Optimization

**Connection Pooling:**
- HikariCP for connection management
- Configurable pool size
- Connection validation

**Async Operations:**
- All DB operations use CompletableFuture
- No main thread blocking
- Batch operations where possible

**Caching:**
- User data cached in memory
- Keys stored locally (no query per check)
- Opening history loaded on-demand

### 3. JavaScript Performance

**Interpreter Mode:**
- No bytecode compilation overhead
- Lower memory usage
- Acceptable performance for animations/algorithms

**Scope Reuse:**
- Scopes created per execution (not per phase)
- Shared Rhino Context across all scripts
- Minimal GC pressure

**Wrapper Objects:**
- Lightweight wrappers (no heavy computation)
- Direct method calls (no reflection)
- Cached references where possible

### 4. Event System

**Listener Registration:**
- Single listener instances (not per crate)
- Priority-based execution
- Ignore if not handling event

**Event Frequency:**
- CratePreOpenEvent: Once per open attempt
- CrateOpenEvent: Once per successful open
- RewardGeneratedEvent: Once per opening + rerolls
- RewardGivenEvent: Once per completion

---

## Security Architecture

### 1. JavaScript Sandbox

**Threat Model:**
- Malicious server admin uploading dangerous scripts
- Exploiting Java reflection to bypass security
- Accessing file system or network
- Escaping sandbox via prototype pollution

**Mitigations:**
- **No Java Package Access:** `initSafeStandardObjects()` blocks java.*
- **Method Blacklist:** Custom WrapFactory blocks dangerous methods
- **Null Parent Scopes:** Prevents prototype chain manipulation
- **Interpreter Mode:** No bytecode execution (no JIT exploits)
- **Wrapper Objects:** Controlled API surface

**Residual Risks:**
- Infinite loops (no timeout mechanism)
- Memory exhaustion (no heap limits)
- Malicious animation logic (e.g., inventory spam)

### 2. Inventory Security

**Threat Model:**
- Script manipulating unauthorized slots
- Duplication exploits via inventory manipulation
- Item theft via inventory swaps

**Mitigations:**
- **Slot Authorization:** Only authorized slots can be modified
- **Wrapper Methods:** Controlled inventory access
- **State Validation:** Animation state tracked by manager

### 3. Data Security

**Threat Model:**
- SQL injection via user input
- Race conditions in user data
- Data loss on crashes

**Mitigations:**
- **Prepared Statements:** Sarah ORM uses prepared statements
- **Concurrent Collections:** Thread-safe user cache
- **Transactions:** Database operations in transactions
- **Async Saves:** Non-blocking persistence

### 4. Permission System

**Threat Model:**
- Unauthorized crate access
- Permission bypass via exploits

**Mitigations:**
- **PermissionCondition:** Checks before opening
- **Event Cancellation:** External plugins can prevent opens
- **Key Validation:** Keys verified before consumption

---

## Future Considerations

### Scalability

**Potential Bottlenecks:**
1. User cache growth (many concurrent players)
2. Database connection pool exhaustion
3. Animation executor (many concurrent animations)
4. Chunk PDC size limits

**Mitigation Strategies:**
1. TTL-based cache eviction
2. Configurable pool size
3. Queue system for concurrent animations
4. Migration to separate placed_crates table if needed

### Extensibility

**Extension Points:**
1. Custom OpenConditions via polymorphic registry
2. Custom Rewards via polymorphic registry
3. Custom DisplayTypes via factory registry
4. Custom Hooks via @AutoHook annotation
5. Custom ItemsProviders via provider registry

**API Stability:**
- Semantic versioning for API module
- Deprecation warnings before removal
- Backward compatibility for configuration

### Monitoring

**Observability:**
1. Debug logging for troubleshooting
2. Event system for external tracking
3. Metrics (opening counts, reward distribution)

**Health Checks:**
1. Database connection validation
2. Registry population verification
3. Script engine health checks

---

## Conclusion

The zCrates architecture prioritizes:

1. **Modularity** - Clear separation between API, implementation, and extensions
2. **Security** - Sandboxed JavaScript, controlled API surface
3. **Performance** - Async operations, efficient caching, optimized data structures
4. **Extensibility** - Hook system, polymorphic types, provider pattern
5. **Maintainability** - Type safety, clear abstractions, comprehensive documentation

For detailed implementation guidance, see:
- `DEVELOPER_GUIDE.md` - Extension and integration guide
- `USER_GUIDE.md` - Configuration and usage guide