package br.com.caiomoizes.scLogin.commands;

import br.com.caiomoizes.scLogin.api.LoginAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Register implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) return true;

        if (LoginAPI.checkIsPremium(p.getName()).join()) {
            p.sendMessage(Component.text("Contas originais não precisam de registro.", NamedTextColor.GREEN));
            return true;
        }

        if (!p.hasPermission("register.use")) {
            p.sendMessage(Component.text("Você não tem permissão para usar este comando.", NamedTextColor.RED));
            return true;
        }

        if (LoginAPI.isRegistered(p)) {
            p.sendMessage(Component.text("Você já está registrado!", NamedTextColor.LIGHT_PURPLE));
            return true;
        }

        if (args.length < 2) {
            p.sendMessage(Component.text("Use /register <password> <confirmPassword>", NamedTextColor.RED));
            return false;
        }

        String password = args[0];
        String confirmPassword = args[1];

        if (password.equals(confirmPassword)) {
            LoginAPI.register(p, password, false);
            p.sendMessage(Component.text("Você se registrou com sucesso!", NamedTextColor.GREEN));
            LoginAPI.logIn(p);
        } else {
            p.sendMessage(Component.text("As senhas não batem!", NamedTextColor.RED));
        }

        return true;
    }
}
