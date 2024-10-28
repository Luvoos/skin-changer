package io.smcode.skinChanger.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinCommand implements CommandExecutor {
    private static final String PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final Map<String, Collection<ProfileProperty>> cache = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /skin <player>");
            return true;
        }

        final String targetSkin = args[0];
        final PlayerProfile playerProfile = player.getPlayerProfile();
        playerProfile.setProperties(getTextureProperty(targetSkin));
        player.setPlayerProfile(playerProfile);

        player.sendMessage("§aYour skin has been changed!");
        return true;
    }

    private Collection<ProfileProperty> getTextureProperty(String targetSkin) {
        if (cache.containsKey(targetSkin))
            return cache.get(targetSkin);

        final String profileResponse = makeRequest(PROFILE_URL + targetSkin);
        final JsonObject profileObject = JsonParser.parseString(profileResponse).getAsJsonObject();
        final String uuid = profileObject.get("id").getAsString();

        final String skinResponse = makeRequest(SKIN_URL.formatted(uuid));
        final JsonObject skinObject = JsonParser.parseString(skinResponse).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        final String value = skinObject.get("value").getAsString();
        final String signature = skinObject.get("signature").getAsString();
        final ProfileProperty profileProperty = new ProfileProperty("textures", value, signature);

        cache.put(targetSkin, List.of(profileProperty));

        return List.of(profileProperty);
    }

    private String makeRequest(String url) {

        try (final HttpClient client = HttpClient.newBuilder().build()) {
            final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
