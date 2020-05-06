package me.bridge.permission;

import me.bridge.permission.reflection.BasicReflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;

import java.lang.reflect.Field;
import java.util.List;

public class PermissibleUtil {

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

    static {
        PermissibleUtil.PERMISSIBLE_BASE.setAccessible(true);
        PermissibleUtil.ATTACHMENTS.setAccessible(true);
        PermissibleUtil.PARENT.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    public static void applyPermissionAttachment(Player player, CustomPermissionAttachment attachment) {
        PermissibleBase permissibleBase =
                (PermissibleBase) BasicReflection.invokeField(PermissibleUtil.PERMISSIBLE_BASE, player);
        List<PermissionAttachment> attachments = (List<PermissionAttachment>)
                BasicReflection.invokeField(PermissibleUtil.ATTACHMENTS, permissibleBase);

        attachments.add(attachment);
        attachment.recalculatePermissions();
    }

    public static Permissible getPermissible(Player player) {
        PermissibleBase permissibleBase =
                (PermissibleBase) BasicReflection.invokeField(PermissibleUtil.PERMISSIBLE_BASE, player);
        return (Permissible) BasicReflection.invokeField(PermissibleUtil.PARENT, permissibleBase);
    }
}
