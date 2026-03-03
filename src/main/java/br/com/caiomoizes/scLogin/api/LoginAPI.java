package br.com.caiomoizes.scLogin.api;

import br.com.caiomoizes.scLogin.SCLogin;
import br.com.caiomoizes.scLogin.utils.MojangAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LoginAPI {
    public static final SCLogin plugin = SCLogin.getInstance();

    private static final Set<UUID> loggedInPlayers = new HashSet<>();

    public static boolean isLoggedIn(Player p) {
        return loggedInPlayers.contains(p.getUniqueId());
    }

    public static void logIn(Player p) {
        loggedInPlayers.add(p.getUniqueId());
    }

    public static void logOut(Player p) {
        loggedInPlayers.remove(p.getUniqueId());
    }

    /* --- Métodos de Persistência (SQLite) --- */

    public static void register(Player p, String password, boolean isPremium) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        plugin.getDatabase().savePlayer(p.getName().toLowerCase(), hashedPassword, isPremium);
    }

    public static void unregister(Player p) {
        plugin.getDatabase().deletePlayer(p.getName().toLowerCase());
    }

    public static boolean isRegistered(Player p) {
        return plugin.getDatabase().playerExists(p.getName().toLowerCase());
    }

    private static String getPassword(Player p) {
        return plugin.getDatabase().getPassword(p.getName().toLowerCase());
    }

    public static void switchPassword(Player p, String newPassword) {
        plugin.getDatabase().updatePassword(p.getName().toLowerCase(), newPassword);
    }

    public static boolean isPremium(Player p) {
        String cleanName = p.getName().trim().toLowerCase();
        Boolean stored = plugin.getDatabase().isPlayerPremium(cleanName);
        Bukkit.getConsoleSender().sendMessage("[SC-Login] isPremium() cache para " + cleanName + ": " + stored);
        return stored != null && stored;
    }

    public static boolean checkPassword(Player p, String typedPassword) {
        String storedHash = getPassword(p);

        if (storedHash == null) return false;

        try {
            return BCrypt.checkpw(typedPassword, storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    public static CompletableFuture<Boolean> checkIsPremium(String playerName) {
        String cleanName = playerName.trim().replace(" ", "");

        return CompletableFuture.supplyAsync(() -> {
            // 1) Tenta usar o cache do banco antes da API
            Boolean storedPremium = plugin.getDatabase().isPlayerPremium(cleanName.toLowerCase());
            if (storedPremium != null) {
                Bukkit.getConsoleSender().sendMessage("[SC-Login] Cache premium encontrado para " + cleanName + ": " + storedPremium);
                return storedPremium;
            }

            // 2) Se não houver cache, consulta a Mojang
            try {
                Bukkit.getConsoleSender().sendMessage("[SC-Login] Consultando Mojang para " + cleanName + "...");
                MojangAPI mojangAPI = new MojangAPI();
                UUID premiumUUID = mojangAPI.getPremiumUUID(cleanName).get();

                boolean isOriginal = (premiumUUID != null);

                plugin.getDatabase().setPremiumStatus(cleanName.toLowerCase(), isOriginal, premiumUUID);

                Bukkit.getConsoleSender().sendMessage("[SC-Login] Resultado Mojang para " + cleanName + ": " + (isOriginal ? "PREMIUM" : "CRACKED"));

                return isOriginal;
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("[SC-Login] Erro ao consultar Mojang para " + cleanName + ": " + e.getMessage());
                return false;
            }
        });
    }

    /* --- Configurações Gerais --- */

    public static boolean requireLogin() {
        return plugin.getConfig().getBoolean("require-login");
    }
}
