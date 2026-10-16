package earth.terrarium.adastra.client.renderers.textures;

import com.mojang.blaze3d.platform.NativeImage;
import earth.terrarium.adastra.common.blockentities.flag.FlagColor;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class FlagImageTexture extends DynamicTexture {

    public FlagImageTexture(byte[] data) {
        super(() -> "ad_astra flag image", createImage(FlagColor.fromBytes(data)));
    }

    private static NativeImage createImage(FlagColor[] colors) {
        NativeImage nativeImage = new NativeImage(22, 16, false);
        for (int x = 0; x < 22; x++) {
            for (int y = 0; y < 16; y++) {
                FlagColor color = colors[x + y * 22];
                if (color == FlagColor.NONE) {
                    nativeImage.setPixelABGR(x, y, 0x00000000);
                } else {
                    nativeImage.setPixelABGR(x, y, color.color() | 0xFF000000);
                }
            }
        }
        return nativeImage;
    }
}
