package me.bridge.permission;

import me.bridge.permission.util.BasicReflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class CustomPermissionAttachment extends PermissionAttachment {

    private static final Field PERMISSIONS_FIELD =
            BasicReflection.fetchField(PermissionAttachment.class, "permissions");
    private static final Field PERMISSIBLE_FIELD =
            BasicReflection.fetchField(PermissionAttachment.class, "permissible");

    private static final String CRAFT_SERVER_NAME = Bukkit.getServer().getClass().getPackage().getName();
    private static final String CRAFT_SERVER_VERSION = CRAFT_SERVER_NAME
            .substring(CRAFT_SERVER_NAME.lastIndexOf('.') + 1);
    private static final Class<?> CRAFT_HUMAN_ENTITY =
            BasicReflection.getClass("org.bukkit.craftbukkit." + CRAFT_SERVER_VERSION + ".entity");
    private static final Field PERMISSIBLE_BASE =
            BasicReflection.fetchField(CRAFT_HUMAN_ENTITY, "perm");
    private static final Field ATTACHMENTS =
            BasicReflection.fetchField(PermissibleBase.class, "attachments");
    private static final Field PARENT =
            BasicReflection.fetchField(PermissibleBase.class, "parent");

    private final Player player;

    public CustomPermissionAttachment(Plugin plugin, Player player) {
        super(plugin, CustomPermissionAttachment.getPermissible(player));

        this.player = player;
    }

    public void recalculatePermissions() {
        ((Permissible) BasicReflection.invokeField(PERMISSIBLE_FIELD, this)).recalculatePermissions();
    }

    @Override
    public void setPermission(String name, boolean value) {
        this.addPermission(name, value);
    }

    public void setPermissions(List<String> permissions, boolean value) {
        permissions.forEach(permission -> this.addPermission(permission, value));
    }

    @Override
    public void unsetPermission(String name) {
        this.removePermission(name);
    }

    @SuppressWarnings("unchecked")
    public void addPermission(String permission, boolean value) {
        ((Map<String, Boolean>) BasicReflection.invokeField(PERMISSIONS_FIELD, this))
                .put(permission.toLowerCase(), value);
    }

    @SuppressWarnings("unchecked")
    public void removePermission(String permission) {
        ((Map<String, Boolean>) BasicReflection.invokeField(PERMISSIONS_FIELD, this))
                .remove(permission.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public void applyAttachment() {
        PermissibleBase permissibleBase =
                (PermissibleBase) BasicReflection.invokeField(PERMISSIBLE_BASE, this.player);
        List<PermissionAttachment> attachments = (List<PermissionAttachment>)
                BasicReflection.invokeField(ATTACHMENTS, permissibleBase);

        if (!attachments.contains(this))
            attachments.add(this);

        recalculatePermissions();
    }

    public static Permissible getPermissible(Player player) {
        PermissibleBase permissibleBase =
                (PermissibleBase) BasicReflection.invokeField(PERMISSIBLE_BASE, player);
        return (Permissible) BasicReflection.invokeField(PARENT, permissibleBase);
    }
}
