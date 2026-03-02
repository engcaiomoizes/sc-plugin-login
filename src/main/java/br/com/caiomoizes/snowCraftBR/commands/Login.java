package br.com.caiomoizes.snowCraftBR.commands;

import br.com.caiomoizes.snowCraftBR.api.LoginAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Login implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player p) {
            if (!p.hasPermission("login.use")) {
                p.sendMessage(Component.text("Você não tem permissão para usar este comando.", NamedTextColor.RED));
                return true;
            }

            if (args.length == 0) return false;

            if (!LoginAPI.isLoggedIn(p)) {
                String password = args[0];

                if (password.equals(LoginAPI.getPassword(p))) {
                    LoginAPI.logIn(p);
                    p.sendMessage(Component.text("Logado com sucesso!", NamedTextColor.GREEN));
                } else {
                    p.kick(Component.text("Senha incorreta!", NamedTextColor.RED));
                }
            } else {
                p.sendMessage(Component.text("Você já está logado!", NamedTextColor.LIGHT_PURPLE));
            }
        }

        return true;
    }
}
