package br.com.caiomoizes.scLogin.commands;

import br.com.caiomoizes.scLogin.SCLogin;
import br.com.caiomoizes.scLogin.api.LoginAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class Login implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) return true;

        if (LoginAPI.isPremium(p)) {
            p.sendMessage(Component.text("Você entrou via conta Original, não precisa de senha.", NamedTextColor.GOLD));
            return true;
        }

        if (!p.hasPermission("login.use")) {
            p.sendMessage(Component.text("Você não tem permissão para usar este comando.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) return false;

        if (LoginAPI.isLoggedIn(p)) {
            p.sendMessage(Component.text("Você já está logado!", NamedTextColor.LIGHT_PURPLE));
            return true;
        }

        String password = args[0];

        CompletableFuture.supplyAsync(() -> LoginAPI.checkPassword(p, password))
                .thenAccept(success -> {
                    Bukkit.getScheduler().runTask(SCLogin.getInstance(), () -> {
                        if (success) {
                            LoginAPI.logIn(p);
                            p.sendMessage(Component.text("Logado com sucesso!", NamedTextColor.GREEN));
                        } else {
                            p.kick(Component.text("Senha incorreta!", NamedTextColor.RED));
                        }
                    });
                });

        return true;
    }
}
