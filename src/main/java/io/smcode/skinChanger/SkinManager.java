package io.smcode.skinChanger;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinManager {

    private final SkinChangerPlugin plugin = SkinChangerPlugin.getPlugin(SkinChangerPlugin.class);
    private static final String PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final Map<String, Collection<ProfileProperty>> cache = new HashMap<>();

    public void setSkin(Player player, String skinName, boolean sendConfirmationMessage) {
        final String targetSkin = skinName;
        final PlayerProfile playerProfile = player.getPlayerProfile();
        playerProfile.setProperties(getTextureProperty(targetSkin));
        player.setPlayerProfile(playerProfile);

        if (sendConfirmationMessage) player.sendMessage("§aYour skin has been changed to: " + targetSkin);
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
            plugin.getLogger().warning("REQUEST ERROR! URL: " + url);
            throw new RuntimeException(e);
        }
    }
}
