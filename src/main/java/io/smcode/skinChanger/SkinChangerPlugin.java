package io.smcode.skinChanger;

import io.smcode.skinChanger.commands.SkinCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkinChangerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("skin").setExecutor(new SkinCommand());
    }
}
