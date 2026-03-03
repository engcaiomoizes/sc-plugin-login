package br.com.caiomoizes.scLogin;

import br.com.caiomoizes.scLogin.commands.Login;
import br.com.caiomoizes.scLogin.commands.Register;
import br.com.caiomoizes.scLogin.commands.SwitchPassword;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

public final class SCLogin extends JavaPlugin {

    private static SCLogin instance;
    private Database database;

    public static SCLogin getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[SC-Login] Ativado!", NamedTextColor.GREEN));

        saveDefaultConfig();

        try {
            database = new Database();

            database.setup(getDataFolder());
            getLogger().info("Conexão com SQLite estabelecida com sucesso!");
        } catch (SQLException e) {
            getLogger().severe("Não foi possível conectar ao SQLite! Desativando o plugin...");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerCommand(
                "register",
                "Registrar sua conta",
                List.of("register", "registrar"),
                "register.use",
                "/register <password> <confirmPassword>",
                new Register()
        );
        registerCommand(
                "login",
                "Logar na sua conta",
                List.of("login", "logar"),
                "login.use",
                "/login <password>",
                new Login()
        );
        registerCommand(
                "switchpassword",
                "Trocar a senha da sua conta",
                List.of("switchpassword", "switch-password", "switch", "switchpass", "switch-pass"),
                "switchpassword.use",
                "/switchpassword <newPassword> <confirmNewPassword>",
                new SwitchPassword()
        );

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[SC-Login] Desativado!", NamedTextColor.RED));
    }

    public void registerCommand(String name, String description, List<String> aliases, String permission, String usage, CommandExecutor executor) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            BukkitCommand command = new BukkitCommand(name) {
                @Override
                public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String @NotNull [] args) {
                    return executor.onCommand(sender, this, label, args);
                }
            };

            command.setDescription(description);
            command.setAliases(aliases);
            command.setPermission(permission);
            command.setUsage(usage);

            commandMap.register("snowcraft", command);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
