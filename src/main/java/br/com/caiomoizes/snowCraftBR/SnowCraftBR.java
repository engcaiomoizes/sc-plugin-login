package br.com.caiomoizes.snowCraftBR;

import br.com.caiomoizes.snowCraftBR.commands.Login;
import br.com.caiomoizes.snowCraftBR.commands.Register;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class SnowCraftBR extends JavaPlugin {

    private static SnowCraftBR instance;

    private CustomConfig users;

    public static SnowCraftBR getInstance() {
        return instance;
    }

    public static void setInstance(SnowCraftBR instance) {
        SnowCraftBR.instance = instance;
    }

    public CustomConfig getUsers() {
        return this.users;
    }

    @Override
    public void onEnable() {
        setInstance(this);

        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[SnowCraft] Ativado!", NamedTextColor.GREEN));

        saveDefaultConfig();

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

        PluginManager pm = Bukkit.getPluginManager();

        this.users = new CustomConfig("users.yml");
        this.users.get().options().copyDefaults(true);
        this.users.save();
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[SnowCraft] Desativado!", NamedTextColor.RED));
    }
}
