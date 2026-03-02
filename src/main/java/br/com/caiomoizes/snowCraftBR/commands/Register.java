package br.com.caiomoizes.snowCraftBR.commands;

import br.com.caiomoizes.snowCraftBR.api.LoginAPI;
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
        if (sender instanceof Player p) {
            if (!p.hasPermission("register.use")) {
                p.sendMessage(Component.text("Você não tem permissão para usar este comando.", NamedTextColor.RED));
                return true;
            }

            if (args.length == 0) return false;

            if (!LoginAPI.isRegistered(p)) {
                String password = args[0];

                if (args.length < 2)
                    p.sendMessage(Component.text("Digite a confirmação da senha.", NamedTextColor.GOLD));
                else {
                    String confirmaSenha = args[1];

                    if (password.equals(confirmaSenha)) {
                        LoginAPI.register(p, password);
                        p.sendMessage(Component.text("Você se registrou com sucesso!", NamedTextColor.GREEN));
                        LoginAPI.logIn(p);
                    } else {
                        p.sendMessage(Component.text("As senhas não batem!", NamedTextColor.RED));
                    }
                }
            } else {
                p.sendMessage(Component.text("Você já está registrado!", NamedTextColor.LIGHT_PURPLE));
            }
        }

        return true;
    }
}
