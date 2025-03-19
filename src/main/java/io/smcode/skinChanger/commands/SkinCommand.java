package io.smcode.skinChanger.commands;

import io.smcode.skinChanger.SkinChangerPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkinCommand implements CommandExecutor {

    private final SkinChangerPlugin plugin = SkinChangerPlugin.instance;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage("§cThis command can only be executed by players!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /skin <player>");
            return true;
        }

        plugin.skinManager.setSkinFromName(player, args[0], true);

        return true;
    }


}
