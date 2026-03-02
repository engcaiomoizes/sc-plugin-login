package br.com.caiomoizes.snowCraftBR.api;

import br.com.caiomoizes.snowCraftBR.SnowCraftBR;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LoginAPI {
    public static SnowCraftBR snowcraft = SnowCraftBR.getInstance();

    private static List<Player> loggedInPlayers = new ArrayList<>();

    public static void disableLogin() {
        snowcraft.getConfig().set("require-login", false);
    }

    public static void enableLogin() {
        snowcraft.getConfig().set("require-login", true);
    }

    public static boolean requireLogin() {
        return snowcraft.getConfig().getBoolean("require-login");
    }

    public static boolean isLoggedIn(Player p) {
        return loggedInPlayers.contains(p);
    }

    public static void logIn(Player p) {
        loggedInPlayers.add(p);
    }

    public static void logOut(Player p) {
        loggedInPlayers.remove(p);
    }

    public static void register(Player p, String password) {
        snowcraft.getUsers().get().set(p.getName().toLowerCase(), password);
        snowcraft.getUsers().save();
    }

    public static void unregister(Player p) {
        snowcraft.getUsers().get().set(p.getName().toLowerCase(), null);
        snowcraft.getUsers().save();
    }

    public static void switchPassword(Player p, String newPassword) {
        register(p, newPassword);
    }

    public static String getPassword(Player p) {
        return snowcraft.getUsers().get().getString(p.getName().toLowerCase());
    }

    public static boolean isRegistered(Player p) {
        return snowcraft.getUsers().get().contains(p.getName().toLowerCase());
    }

    public static List<Player> getLoggedInPlayers() {
        return loggedInPlayers;
    }

    public static void setLoggedInPlayers(List<Player> loggedInPlayers) {
        LoginAPI.loggedInPlayers = loggedInPlayers;
    }
}
