package me.bridge.permission.example;

import me.bridge.permission.CustomPermissionAttachment;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

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
