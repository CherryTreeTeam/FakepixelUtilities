<h1 align="center">FakepixelUtilities</h1>

<div align="center">
   
<img src="src/main/resources/pack.png" width="128">

[![Discord](https://img.shields.io/badge/DISCORD-5865F2?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/TJvSwuBxbD)
[![GitHub](https://img.shields.io/badge/GITHUB-1a1a1a?style=for-the-badge&logo=github&logoColor=white)](https://github.com/CherryTreeTeam/FakepixelUtilities/releases/)
[![Modrinth](https://img.shields.io/badge/MODRINTH-00AF5C?style=for-the-badge&logo=modrinth&logoColor=white)](https://modrinth.com/mod/fakepixelutilities/version/1.0.0)
[![Website](https://img.shields.io/badge/WEBSITE-ff6b6b?style=for-the-badge&logo=googlechrome&logoColor=white)](https://cherryteam.page.gd)

</div>

---

## 📋 IMPORTANT (Data Transparency)

On server join, this mod checks connection flags to verify network routing and coordinates strictly with our centralized database to fetch live market streams. Only the data strictly necessary for a feature to work is ever processed or shared.

### 🛡️ What We Collect/Transmit:
- **Marketplace Metadata:** Dynamic asset properties (`Item ID`, `Bazaar Buy/Sell values`, `Auction House Bids`) encountered during container hover rendering loops.
- **Analytics Check:** Purely for data consistency, tracking metrics ensure item IDs align properly across remote database indices.

### ❌ What We NEVER Collect:
- **No Passwords** or login session tokens (`Session ID` extraction routines are completely absent from the source code).
- **No Personal Files**, local system paths, private chat history, or restricted tracking variables.

All collected pricing parameters are openly stored inside our hosting infrastructure to maintain active lookup tables for the entire community.

---

## ✨ Features
- **SkyBlock-Only Trigger System:** Auto-activates strictly inside SkyBlock instances. Auto-drops execution cycles on proxy hub changes (`/l`, `/lobby`, `/hub`) to maintain 0% background overhead.
- **Flawless Potion & Book Parsers:** Custom NBT fallback guards protect dynamic items (like Absorption VII) from data collisions.
- **Active Auction Trackers:** Caches active biddings matching the `Top bid:` formatting nodes seamlessly.

## 🛠️ Commands
- `/fpu` - Launches the structural Graphical User Interface (GUI) configuration dashboard.
- `/fpu price <item_name>` - Queries live pricing variables straight from the central database indexers.

## 📦 How to Build / Compile (Install Guide)

If you want to compile this mod yourself from the source code, follow these simple steps:

1. **Clone the Repository:**
   ```bash
   git clone [https://github.com/CherryTreeTeam/FakepixelUtilities.git](https://github.com/CherryTreeTeam/FakepixelUtilities.git)
   
2. **Open Folder:**
   ```bash
   cd FakepixelUtilities

3. **Commands:**
 
 For Linux:
```bash
./gradlew clean build
```
 For Windows:
```bash
 gradlew.bat clean build
```
## 👥 Credits & Contributors
This project was made by CherryTree Team:
- **Project Founder:** c1727.c
- **Project Manager & Operator:** _jatin_e

---
*Disclaimer: This project is an open-source, unofficial utility layer and is not affiliated with Mojang Studios, Microsoft, or Fakepixel.*
