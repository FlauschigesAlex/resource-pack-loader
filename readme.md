# Resource Pack Loader
A simple solution to handle resource packs on paper and velocity.

### Supported platforms & versions
- [Paper](https://papermc.io/software/paper/) Versions: 1.21.11
- [Velocity](https://velocitypowered.com/) Versions: 3.5.0

Although resource-pack-loader may work on other platforms or versions, I do not guarantee for their stability or functionality.

## Setup & Configuration
After initially loading the plugin on your server, you'll need to modify the configuration file in order to enable pack-loading.

Example configuration (with default values):
```json
{
  "useCommand": true,
  "required": false,
  "replace": false,
  "prompt": null, // "<yellow><b>This server recommends a resource pack."
  "packs": [
    {
      "_id": "00000000-0000-0000-0000-000000000000",
      "url": "https://example.com/resource.zip"
    }
  ]
}
```

``packs``  The list of resource packs to load.<br>
- ``_id``  The unique identifier for the resource pack.
- ``url``  The [URL](https://wikipedia.org/wiki/Uniform_Resource_Locator) that points to the resource pack.

``prompt`` The prompt to display when the player is sent a resource pack. ([MiniMessage Formatting](https://docs.papermc.io/adventure/minimessage/))

``replace`` Set whether to replace the player's server resource packs with the provided ones.

``required`` Sets the resource-packs to be required to play on the server. Disconnects the player if they reject the packs. (see [Resource Pack Requirements](https://minecraft.wiki/w/Resource_pack#Preloaded_resource_packs))

``useCommand`` Enables the ``/resource-pack-loader`` command. (see [Commands](#commands))

After initially setting up the configuration, you won't need to modify it to update your pack(s), just the resource the url(s) point to.

## Commands
### /resource-pack-loader
Requires config entry ``useCommand`` to be ``true``.<br>
Requires player permission ``rpl.admin``.

``/resource-pack-loader reload <player>`` Reloads all resource packs for the provided player.<br>
``/resource-pack-loader reload`` Reloads all resource packs for all online players.<br>

Aliases:
- ``/rpl``
- ``/resource-pack-loader-paper``, ``/rpl-paper`` (Conditional) Provided if the plugin is installed on Paper.
- ``/resource-pack-loader-velocity``, ``/rpl-velocity`` (Conditional) Provided if the plugin is installed on Velocity.