# Velocity Carbon Discord
Bridging [Carbon](https://modrinth.com/plugin/carbon) chat channels to Discord.

Carbon must be installed on the proxy to use this plugin. If you use Carbon on your backend servers, please use their DiscordSRV compatibility.

## Features
- Highly configurable
- Webhooks or bot messages for chat
- Display player count in bot status
- Ping discord users from in-game
- Reply to in-game messages from Discord
- Handle separate chat channel mappings (E.g. one channel for global chat, one channel for staff chat)
- Customisable Minecraft Avatar display
- Link discord attachments in-game
- Status messages on server start and shutdown, as well as player join, leave or change server
- Formats Xaero's Minimap shared waypoints in Discord

## Installation
1. Create a discord bot application [here](https://discordapp.com/developers/applications/)
   - Click `Add application` and then go to the `Bot` tab
2. Enable the `SERVER MEMBERS INTENT` and `MESSAGE CONTENT INTENT` under `Privileged Gateway Intents`
3. Go to the `OAuth2` tab and copy the `CLIENT ID`
4. Create an invite link for your new bot [here](https://discordapi.com/permissions.html)
   - Enable permissions: `View Channels`, `Send Messages` and `Read Message History`
   - Paste the copied `CLIENT ID` into the field on the page
   - Click the generated link to invite your bot to your discord server
3. Go to the `Bot` tab and copy the bot token, you may need to click `Reset Token` first
4. Install the plugin on your server, start the server once to generate the config and stop the server again.
5. Open the plugin config files at `plugins/velocitycarbondiscord/config.yaml`
6. Replace where it says `TOKEN` with the bot token you copied previously
7. Edit the channel mapping to include the carbon channels and discord channels IDs you want to link
8. Set any additional config options you want
9. Start the server

## Support

Support for Carbon and Velocity Carbon Discord can be obtained in the [Carbon discord server](https://discord.gg/S8s75Yf). For Velocity Carbon Discord issues, please ping `@Jarva`

## Configuration
Default config generated on startup:
```yaml
discord:
   token: 'TOKEN'
   # Available placeholders: <username>
   messages:
      # Additional placeholders: <displayname> <message>
      chat_message: '<username>: <message>'
      # Additional placeholders: <server>
      join_message: '**<username> joined <server>**'
      leave_message: '**<username> left <server>**'
      # Additional placeholders: N/A
      disconnect_message: '**<username> was disconnected**'
      # Additional placeholders: <previous_server> <server>
      server_switch_message: '**<username> moved from <previous_server> to <server>**'
      # Available placeholders: N/A
      shutdown_message: '**Proxy shutting down**'
      start_message: '**Ready for connections**'
   webhook:
      # Available placeholders: <displayname> <username> <uuid>
      avatar_url: 'https://crafatar.com/renders/head/<uuid>?overlay'
      username: '<username>'
      # Available placeholders: <message>
      message: '<message>'
   show_bot_messages: false
   show_attachments_ingame: true
   show_activity: true
   # Available placeholders: <amount>
   activity_text: 'with <amount> players online'
   enable_mentions: true
   enable_everyone_and_here: false
   prefer_webhook: true
# Available placeholders: <message> <nickname> <role_color> <username>
minecraft:
   # Additional placeholders: <attachments> <discord_format> <reply_format> <username_format>
   format: '<discord_format> <reply_format><username_format> <dark_gray>» <reset><message><attachments>'
   discord_format: '<dark_gray>(<color:#7289da>discord<dark_gray>)<reset>'
   username_format: '<color:<role_color>><hover:show_text:<username>><nickname></hover><reset>'
   mention_format: '<color:<role_color>><underlined>@<nickname></underlined></color>'
   # Additional placeholders: <reply_message> <reply_nickname> <reply_role_color> <reply_url> <reply_username>
   reply_format: '<dark_gray><click:open_url:<reply_url>>[<color:#4abdff>←<color:<reply_role_color>><hover:show_text:<reply_username>><reply_nickname></hover><dark_gray>]</click><reset> '
   # Additional placeholders: <attachment_url>
   attachment_format: '<dark_gray><click:open_url:<attachment_url>>[<color:#4abdff>Attachment<dark_gray>]</click><reset>'
channels:
   -   name: 'carbon:global'
       enabled: true
       broadcast_events: true
       discord:
          webhook:
             url: 'WEBHOOK_URL'
          channel_id: 'CHANNEL_ID'
   -   name: 'carbon:staff'
       enabled: true
       minecraft:
          format: '<discord_format> <gray>✦</gray> <username_format> <dark_gray>» <aqua><message><attachments>'
       discord:
          webhook:
             url: 'WEBHOOK_URL'
          channel_id: 'CHANNEL_ID'
```

## Credits
- fooooooooooooooo: Creation of [Velocity Discord](https://modrinth.com/plugin/velocitydiscord/) which heavily inspired the features, and this README
- Draycia: Creator of [Carbon](https://modrinth.com/plugin/carbon), as well as provided lots of help figuring out implementation details
