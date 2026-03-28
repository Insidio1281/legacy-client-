# ItemFramePlacer - Meteor Client Addon

A powerful Meteor Client addon that automatically detects and manages item frames around the player.

## 🎯 Features

- **Detects up to 36 item frames** in a configurable radius around the player
- **Automatic block placement** from inventory into detected frames
- **Automatic removal** of placed blocks at configurable speed
- **Whitelist system** to control which blocks can be placed
- **GUI configuration** accessible with F1 keybind
- **Custom keybind** to toggle module on/off (configurable in GUI)
- **100% client-side** - works on any server including paper servers
- **Multiplayer compatible** - no server modifications required
- **Real-time toggle** with auto-disable when no items available
- **Search & filter** blocks in the GUI

## 📦 Installation

1. Download the latest JAR from [Releases](https://github.com/Insidio1281/ItemFramePlacer/releases)
2. Place it in your Meteor Client addons folder:
   - **Windows**: `%APPDATA%\.minecraft\meteor-client\addons\`
   - **Linux**: `~/.minecraft/meteor-client/addons/`
   - **macOS**: `~/Library/Application Support/minecraft/meteor-client/addons/`
3. Restart Minecraft

## 🎮 Usage

### Opening the GUI
- Press **F1** to open the configuration GUI

### Toggling the Module
- Press the configured keybind (default: **X**) to toggle on/off
- Or use the Meteor Client menu (press **Y**)

### Configuration Options

#### Speed Setting
Controls the delay (in ticks) between placing/removing items:
- **5 ticks**: Extremely fast (2.5 items/second)
- **10 ticks**: Very fast (5 items/second)
- **20 ticks**: Fast (1 item/second) [Default]
- **50 ticks**: Medium
- **100+ ticks**: Slow

#### Auto Toggle
Automatically disables the module when your inventory has no whitelisted items

#### Keybind Settings
- Click **"Record Keybind"** to record a new keybind
- Press any key (A-Z, 0-9, F1-F12, etc.)
- Click **"Apply Keybind"** to save
- Supported keys: A-Z, 0-9, F1-F12, SHIFT, CTRL, ALT, TAB, ESC, SPACE, ENTER

#### Whitelist Configuration
- Search for blocks using the search bar
- Check/uncheck blocks to enable/disable
- Use **"Select All"** and **"Deselect All"** buttons for bulk operations
- Supports 30+ common blocks (dirt, stone, wood, etc.)

## ⚙️ Building from Source

```bash
git clone https://github.com/Insidio1281/ItemFramePlacer.git
cd ItemFramePlacer
./gradlew build