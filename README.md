[unfinished]

# TC4 Research Port: Reborn

[![curse forge](https://img.shields.io/badge/TC4_Research_Port%3A_Reborn-12?style=flat&logo=curseforge&labelColor=black&color=grey)](https://www.curseforge.com/minecraft/mc-mods/oldresearchreborn)
[![modrinth](https://img.shields.io/badge/TC4_Research_Port%3A_Reborn-12?style=flat&logo=modrinth&labelColor=black&color=grey)](https://modrinth.com/mod/oldresearchreborn)
[![latest release](https://img.shields.io/github/v/release/Artur114Projects/TC4ResearchReborn?style=flat&logo=github&labelColor=black&color=6cc644)](https://github.com/Artur114Projects/TC4ResearchReborn/releases)

A maintained and heavily reworked fork of the original mod: [TC4 Research Port](https://github.com/Wong-Innovations/TC4Research)

### Main changes

- Added handcrafted research note templates for all Thaumcraft research entries.
- Added the **Deconstruction Table**.
- Fixed an incompatibility with **ThaumicAdditions**.
- Multiple theories within one stage of research are combined into one complex note.
- Improved procedural research note generation:
  - Research note generation is now based on the world seed.
  - Research notes can now contain up to 11 aspects.
  - Empty cells can now generate in research notes.
- Added translations for the following languages:
  - Russian (`ru_ru`)
  - German (`de_de`, machine translated)
  - Spanish (`es_es`, machine translated)
  - French (`fr_fr`, machine translated)
  - Italian (`it_it`, machine translated)
- Removed the dependency on **MixinBooter** and rewrote the CoreMod using ASM.

### Other changes
- Added an API layer; developers can now create custom note templates.
- Reworked aspect storage to use Forge capabilities.
- Added new configuration settings:
      - `researchDifficultyMultiplier` complexity of the note is multiplied by this value after the calculation.
      - `aspectObtainMultiplier` number of aspects obtained during scanning is multiplied by this value and rounded according to mathematical rules.
- Added console commands:
      - `/oldresearch researchaspect <player> <aspectid/all>` opens the specified aspect; "all" opens all aspects.
      - `/oldresearch addaspect <player> <aspectid/all> <amount>` adds the specified amount to the selected aspect; "all" adds the specified amount to all aspects.
      - `/oldresearch setaspect <player> <aspectid/all> <amount>` sets the amount of the specified aspect; "all" sets the specified number for all aspects.
- Added a Creative tab containing all registered notes.
- Scribing tools from any Thaumcraft add-on can now be used to create notes.
- Graphical interface of the research table was improved.
- Multiple fixes and improvements.