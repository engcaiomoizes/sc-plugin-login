package br.com.caiomoizes.scLogin.commands;

import br.com.caiomoizes.scLogin.api.LoginAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SwitchPassword implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player p) {
            if (!p.hasPermission("switchpassword.user")) {
                p.sendMessage(Component.text("Você não tem permissão para usar este comando.", NamedTextColor.RED));
                return true;
            }

            if (args.length == 0) return false;

            if (!LoginAPI.isLoggedIn(p)) {
                p.sendMessage(Component.text("Você deve estar logado para alterar a sua senha.", NamedTextColor.GOLD));
            } else {
                String newPassword = args[0];

                if (args.length < 2) {
                    p.sendMessage(Component.text("Digite a confirmação da nova senha.", NamedTextColor.GOLD));
                } else {
                    String confirmNewPassword = args[1];

                    if (!newPassword.equals(confirmNewPassword)) {
                        p.sendMessage(Component.text("As senhas não são equivalentes.", NamedTextColor.RED));
                    } else {
                        LoginAPI.switchPassword(p, newPassword);
                        p.sendMessage(Component.text("Senha alterada com sucesso!", NamedTextColor.GREEN));
                    }
                }
            }
        }

        return true;
    }
}
