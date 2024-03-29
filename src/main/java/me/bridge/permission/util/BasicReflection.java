package me.bridge.permission.util;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class BasicReflection {

    /**
     * Used to invoke a field
     * @param field The field to invoke
     * @param object The object where the field is applicable
     * @exception IllegalArgumentException in case we cannot access the field (Should not happen)
     * @return invoked field
     */
    public static Object invokeField(Field field, Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Used to fetch a field
     * @param clazz Class where field is applicable
     * @param fieldName Name of the field we're trying to fetch
     * @exception IllegalArgumentException in case the field is not found
     * @return Optional field
     */
    @SuppressWarnings("deprecation")
    public static Field fetchField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);

            if (!field.isAccessible())
                field.setAccessible(true);

            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("deprecation")
    public static Field fetchFieldPrivileged(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);

            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                if (!field.isAccessible())
                    field.setAccessible(true);
                return null;
            });

            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static final Field MODIFIERS_FIELD = fetchFieldPrivileged(Field.class, "modifiers");

    @SuppressWarnings("deprecation")
    public static void updateFinalField(Field field, Object object, Object newValue) {
        try {
            if (!field.isAccessible())
                field.setAccessible(true);

            int oldModifiers = field.getModifiers();
            MODIFIERS_FIELD.setInt(field, oldModifiers & ~Modifier.FINAL);
            field.set(object, newValue);
            MODIFIERS_FIELD.setInt(field, oldModifiers);
        } catch (IllegalAccessException e){
            throw new IllegalArgumentException(e);
        }
    }

    public static void updateField(Field field, Object object, Object newValue) {
        try {
            field.set(object, newValue);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Used to fetch a constructor
     * @param clazz Class where the constructor is applicable
     * @param parameters Constructor in the constructor we're trying to fetch
     * @exception IllegalArgumentException in case the constructor is not found
     * @return the fetched constructor
     */
    public static Constructor<?> fetchConstructor(Class<?> clazz, Class<?>... parameters) {
        try {
            return clazz.getConstructor(parameters);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Used to invoke a constructor
     * @param constructor The constructor to invoke
     * @param parameters The parameters we need to use to invoke the constructor
     * @exception IllegalArgumentException in case the constructor is not found
     * @return invoked constructor
     */
    public static Object invokeConstructor(Constructor<?> constructor, Object... parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Used to fetch a method
     * @param clazz Class where the method is applicable
     * @param methodName The name of the method we're fetching
     * @param parameters The parameters of the method
     * @return the fetched method
     */
    public static Method fetchMethod(Class<?> clazz, String methodName, Class<?>... parameters) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameters);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * Used to invoke a method
     * @param method The method to invoke
     * @param object The object which contains the method we need to invoke
     * @param parameters The parameters needed to invoke the method
     * @return The method's returning object
     */
    public static Object invokeMethod(Method method, Object object, Object... parameters) {
        try {
            return method.invoke(object, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Used to find a class by its name
     * @param name Class' name
     * @exception IllegalArgumentException in case the class is not found
     * @return the found class
     */
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Finds a class and calls it back using a {@link BiConsumer}
     * @param name Class' name
     * @param callback callback after class is found
     */
    public static void getClassCallback(String name, BiConsumer<Class<?>, Throwable> callback) {
        CompletableFuture<Class<?>> completableFuture = new CompletableFuture<>();

        completableFuture.complete(BasicReflection.getClass(name));
        completableFuture.whenCompleteAsync(callback);
    }
}
