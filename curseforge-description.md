Allow players to freely assemble TACZ firearm attachments in creative mode.

Players can install firearm attachments without needing the items in their inventory.

Access can also be restricted by mode through config or gamerules, including survival and adventure mode setups.

The mod also adds a few quality-of-life features:

<span style="color:#f1c40f">1. Feature toggles in the Mods config screen</span>

<span style="color:#2dc26b">2. Attachment category management directly in the TACZ weapon customization interface, making it easier to organize attachments from different TACZ addon packs</span>

3. Multiple access modes: Creative only, Survival only, Creative & Survival, Adventure only, or Adventure & Creative

4. Server-side control through vanilla gamerules for admins and server operators

5. Smoother first-person sprint-jump gun animation, with softer takeoff and landing transitions while sprinting

6. Wall collision handling inspired by Tarkov: guns will be pushed aside when too close to walls, and firing is blocked until there is enough space again

This improves compatibility and makes managing attachments from multiple TACZ addons much easier.

## Gamerule Usage

On servers, admins can control the feature with the vanilla `/gamerule` command.

Enable or disable the feature:

```mcfunction
/gamerule taczCreativeSupplementEnabled true
/gamerule taczCreativeSupplementEnabled false
```

Set which game modes can use it:

```mcfunction
/gamerule taczCreativeSupplementMode 0
/gamerule taczCreativeSupplementMode 1
/gamerule taczCreativeSupplementMode 2
/gamerule taczCreativeSupplementMode 3
/gamerule taczCreativeSupplementMode 4
```

Values for `taczCreativeSupplementMode`:

- `0` = Creative only
- `1` = Survival only
- `2` = Creative and Survival
- `3` = Adventure only
- `4` = Adventure and Creative
