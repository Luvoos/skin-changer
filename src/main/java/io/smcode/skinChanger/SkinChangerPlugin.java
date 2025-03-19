package io.smcode.skinChanger;

import io.smcode.skinChanger.commands.SkinCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkinChangerPlugin extends JavaPlugin {

    public static SkinChangerPlugin instance;
    public SkinManager skinManager;

    @Override
    public void onEnable() {
        try {
            init();
        } catch (Exception e) {
            getLogger().warning("Something went wrong: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("SkinChanger was enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkinChanger was disabled!");
    }

    private void init() {
        instance = this;
        skinManager = new SkinManager();
        getCommand("skin").setExecutor(new SkinCommand());
    }
}
