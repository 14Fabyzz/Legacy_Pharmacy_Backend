package com.legacy.pharmacy.reportes.config;

/**
 * Contexto del usuario usando ThreadLocal
 *
 * Almacena la información del usuario extraída de los headers
 * del Gateway para que esté disponible en toda la aplicación.
 */
public class UserContext {

    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();
    private static final ThreadLocal<String> userRole = new ThreadLocal<>();

    public static void setUserId(Long id) {
        userId.set(id);
    }

    public static void setUsername(String name) {
        username.set(name);
    }

    public static void setUserRole(String role) {
        userRole.set(role);
    }

    public static Long getUserId() {
        return userId.get();
    }

    public static String getUsername() {
        return username.get();
    }

    public static String getUserRole() {
        return userRole.get();
    }

    public static boolean isAdmin() {
        String role = userRole.get();
        return role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ADMINISTRADOR"));
    }

    public static void clear() {
        userId.remove();
        username.remove();
        userRole.remove();
    }
}
