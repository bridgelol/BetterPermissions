package me.bridge.permission;

import me.bridge.permission.reflection.BasicReflection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Map;

public class CustomPermissionAttachment extends PermissionAttachment {

    private static final Field PERMISSIONS_FIELD =
            BasicReflection.fetchField(PermissionAttachment.class, "permissions");
    private static final Field PERMISSIBLE_FIELD =
            BasicReflection.fetchField(PermissionAttachment.class, "permissible");


    public CustomPermissionAttachment(Plugin plugin, Permissible Permissible) {
        super(plugin, Permissible);
    }

    public CustomPermissionAttachment(Plugin plugin, Player player) {
        super(plugin, PermissibleUtil.getPermissible(player));
    }

    public void recalculatePermissions() {
        ((Permissible) BasicReflection.invokeField(CustomPermissionAttachment.PERMISSIBLE_FIELD, this))
                .recalculatePermissions();
    }

    @Override
    public void setPermission(String name, boolean value) {
        this.addPermission(name, value);
    }

    @Override
    public void unsetPermission(String name) {
        this.removePermission(name);
    }

    @SuppressWarnings("unchecked")
    public void addPermission(String permission, boolean value) {
        ((Map<String, Boolean>) BasicReflection.invokeField(CustomPermissionAttachment.PERMISSIONS_FIELD, this))
                .put(permission.toLowerCase(), value);
    }

    @SuppressWarnings("unchecked")
    public void removePermission(String permission) {
        ((Map<String, Boolean>) BasicReflection.invokeField(CustomPermissionAttachment.PERMISSIONS_FIELD, this))
                .remove(permission.toLowerCase());
    }

    public void applyAttachment(Player player) {
        PermissibleUtil.applyPermissionAttachment(player, this);
    }
}
