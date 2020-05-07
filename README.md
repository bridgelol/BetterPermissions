# BetterPermissions
Utility made to reduce the load for setting/updating player permissions in bukkit.

## What exactly does this utility change?
BetterPermissions changes the fact that bukkit "recalculates" permissions everytime a permission is set on an attachment,
causing major performance issues. This can provide an overall way better experience updating player's permissions.

## Example Usage
```
public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ExampleListener(this), this);
    }

    public static class ExampleListener implements Listener {

        private final ExamplePlugin plugin;

        public ExampleListener(ExamplePlugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            CustomPermissionAttachment permissionAttachment = new CustomPermissionAttachment(plugin, event.getPlayer());

            // Add permission to the attachment
            permissionAttachment.setPermission("rank.mod", true);

            // Add a collection that contains multiple permissions
            permissionAttachment.setPermissions(Arrays.asList("examplepermission", "anotherexample"), true);

            // Apply the permissions to the player.
            permissionAttachment.applyAttachment();
        }
    }
}
```