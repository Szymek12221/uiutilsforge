# UI-Utils Forge

Forge 1.20.1 port of UI-Utils plugin debugging mod.

Original mod: https://github.com/Coderx-Gamer/ui-utils

## Features

- Close GUI without packet
- De-sync GUI (client-side only)
- Send/Delay packets controls
- Save GUI state
- Fabricate custom packets (ClickSlotC2SPacket, ButtonClickC2SPacket)
- GUI title JSON copy
- Chat input in GUI
- Bypass resource pack requirements
- Support for inventory manipulation

## Building

```bash
./gradlew build
```

## Running in Development

```bash
./gradlew runClient
```

## Usage

Open any container/inventory screen and you will see UI-Utils buttons and controls.

### Commands
- Type `^toggleuiutils` in the chat field to enable/disable the mod

### Controls

**Close without packet** - Closes your current GUI without sending a close packet to the server

**De-sync** - Closes your current GUI server-side while keeping it open client-side

**Send packets: true/false** - Toggle whether click packets should be sent to the server

**Delay packets: true/false** - Store packets and send them all at once when toggled off

**Save GUI** - Save current GUI state (accessible with keybinding)

**Disconnect and send packets** - Send all delayed packets then disconnect

**Fabricate packet** - Create custom packets (Windows/Linux only)

**Copy GUI Title JSON** - Copy the current GUI title as JSON to clipboard

## License

CC BY-NC-SA 4.0 - See original repository for details.

## Credits

Based on UI-Utils by Coderx_Gamer and MrBreakNFix
Forge port by Szymek12221
