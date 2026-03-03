package br.com.caiomoizes.scLogin.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MojangAPI {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public CompletableFuture<UUID> getPremiumUUID(String playerName) {
        String cleanName = playerName.trim().replace(" ", "");
        String url = "https://api.mojang.com/users/profiles/minecraft/" + cleanName;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Envia a requisição de forma assíncrona
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("[SC-Login][MojangAPI] Requisição para " + url +
                            " | status=" + response.statusCode() +
                            " | body=" + response.body());

                    if (response.statusCode() == 200) {
                        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                        String id = json.get("id").getAsString();

                        return parseUUID(id);
                    }
                    return null;
                });
    }

    private UUID parseUUID(String idWithoutDashes) {
        // Formata a String 8-4-4-4-12
        return UUID.fromString(idWithoutDashes.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        ));
    }
}
