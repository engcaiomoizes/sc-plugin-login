package br.com.caiomoizes.scLogin;

import br.com.caiomoizes.scLogin.api.LoginAPI;
import br.com.caiomoizes.scLogin.utils.MojangAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class Events implements Listener {
    private final SCLogin plugin;

    public Events(SCLogin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        String playerName = e.getName();

        boolean isPremium = LoginAPI.checkIsPremium(playerName).join();

        if (isPremium) {
            Bukkit.getConsoleSender().sendMessage("[SC-Login] " + playerName + " identificado como PREMIUM.");

            UUID mojangUUID = plugin.getDatabase().getSavedUUID(playerName);

            if (mojangUUID != null) {
                Bukkit.getConsoleSender().sendMessage("[SC-Login] Aplicando UUID Mojang " + mojangUUID + " para " + playerName + " (modo online).");
                e.setPlayerProfile(Bukkit.createProfile(mojangUUID, playerName));
            } else {
                Bukkit.getConsoleSender().sendMessage("[SC-Login] AVISO: " + playerName + " foi detectado como PREMIUM, mas nenhum UUID foi encontrado no banco.");
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("[SC-Login] " + playerName + " identificado como CRACKED.");
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        e.joinMessage(Component.text(p.getName() + " entrou!", NamedTextColor.GREEN));
        p.sendMessage(Component.text("Bem-Vindo ao SnowCraft!", NamedTextColor.BLUE));
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        LoginAPI.logOut(p);
        e.quitMessage(Component.text(p.getName() + " saiu!", NamedTextColor.RED));
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (!LoginAPI.isLoggedIn(p) && LoginAPI.requireLogin())
            p.teleport(e.getFrom());
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (!LoginAPI.isLoggedIn(p) && LoginAPI.requireLogin())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        Player p = e.getPlayer();

        if (!LoginAPI.isLoggedIn(p) && LoginAPI.requireLogin())
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if (!LoginAPI.isLoggedIn(p) && LoginAPI.requireLogin())
            e.setCancelled(true);
    }
}
