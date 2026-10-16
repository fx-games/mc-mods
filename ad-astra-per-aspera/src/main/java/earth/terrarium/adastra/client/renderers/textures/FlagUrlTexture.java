package earth.terrarium.adastra.client.renderers.textures;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import earth.terrarium.adastra.AdAstra;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Registers the warning flag under the given id, then downloads the url and replaces it with the downloaded image.
 */
public final class FlagUrlTexture {

    private static final HttpClient CLIENT = HttpClient.newBuilder().build();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier DEFAULT_FLAG = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/block/flag/warning_flag.png");

    private FlagUrlTexture() {}

    public static void load(Identifier id, String url) {
        var manager = Minecraft.getInstance().getTextureManager();
        manager.registerAndLoad(id, new SimpleTexture(DEFAULT_FLAG));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "Ad Astra (Minecraft Mod)")
            .build();

        CompletableFuture.runAsync(() -> {
            try {
                HttpResponse<InputStream> data = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
                if (data.statusCode() / 100 == 2) {
                    NativeImage image = NativeImage.read(data.body());
                    Minecraft.getInstance().execute(() -> manager.register(id, new DynamicTexture(id::toString, image)));
                }
            } catch (Exception e) {
                LOGGER.error("Couldn't download http texture", e);
            }
        }, Util.backgroundExecutor());
    }
}
