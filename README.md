# BetterPermissions
Utility made to reduce the load for setting/updating player permissions in bukkit.

## What exactly does this utility change?
BetterPermissions changes the fact that bukkit "recalculates" permissions everytime a permission is set on an attachment,
causing major performance issues. This can provide an overall way better experience updating player's permissions.

## Thread Safe Permission Updates
This plugin allows you to make PermissibleBase use thread-safe collections ultimately resulting in you being able to update permissions asynchronously.

## Example Usage

```java
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(ThreadLocalRandom.current().nextBoolean() ? 
                new ThreadSafeExampleListener(this) : new ExampleListener(this), this);
    }

    static class ExampleListener implements Listener {

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

    static class ThreadSafeExampleListener implements Listener {

        private final ExamplePlugin plugin;

        public ThreadSafeExampleListener(ExamplePlugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            CustomPermissionAttachment permissionAttachment = new CustomPermissionAttachment(plugin, event.getPlayer());

            // Ensure thread safety so we can run this asynchronously
            permissionAttachment.ensureThreadSafety();

            CompletableFuture.runAsync(() -> {
                // Add permission to the attachment
                permissionAttachment.setPermission("rank.mod", true);

                // Add a collection that contains multiple permissions
                permissionAttachment.setPermissions(Arrays.asList("examplepermission", "anotherexample"), true);

                // Apply the permissions to the player.
                permissionAttachment.applyAttachment();
            });
        }
    }
}
```