package com.r4men.game_night.client.render.chess;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Custom model geometry loader registered as "game_night:chess_piece".
 * <p>
 * Reads "outline_color" (hex string, e.g. "#1a6bb5") from the model JSON and
 * stores it alongside the normally-baked model in a ChessPieceBakedModel wrapper.
 * All standard Blockbench/vanilla element baking is performed identically to the
 * default BlockModel bake path using FaceBakery.
 */
public class ChessPieceGeometryLoader
        implements IGeometryLoader<ChessPieceGeometryLoader.Geometry> {

    public static final ChessPieceGeometryLoader INSTANCE = new ChessPieceGeometryLoader();

    @Override
    public @NotNull Geometry read(JsonObject json, JsonDeserializationContext ctx) {
        int outlineColor = 0x000000; // default: black
        if (json.has("outline_color")) {
            String hex = json.get("outline_color").getAsString(); // "#rrggbb"
            // Strip leading '#' before parsing
            outlineColor = Integer.parseInt(hex.startsWith("#") ? hex.substring(1) : hex, 16);
        }
        return new Geometry(outlineColor);
    }

    // -------------------------------------------------------------------------

    public record Geometry(int outlineColor) implements IUnbakedGeometry<Geometry> {

        private static final FaceBakery FACE_BAKERY = new FaceBakery();

        @Override
        public BakedModel bake(
                IGeometryBakingContext ctx,
                ModelBaker baker,
                Function<Material, TextureAtlasSprite> spriteGetter,
                ModelState modelState,
                ItemOverrides overrides) {

            // In NeoForge 1.21.1, IGeometryBakingContext is implemented by
            // BlockModel, which is the only way to access getElements().
            // This cast is safe because NeoForge always passes the owning
            // BlockModel as the context when baking a geometry loader model.
            BlockModel blockModel = (BlockModel) ctx;

            TextureAtlasSprite particle =
                spriteGetter.apply(ctx.getMaterial("particle"));

            SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(
                ctx.useAmbientOcclusion(),
                ctx.isGui3d(),
                ctx.useBlockLight(),
                ctx.getTransforms(),
                overrides
            ).particle(particle);

            for (BlockElement element : blockModel.getElements()) {
                for (Direction dir : element.faces.keySet()) {
                    BlockElementFace face = element.faces.get(dir);

                    // face.texture() returns "#0", "#1", etc. — strip the '#'
                    String textureName = face.texture();
                    if (textureName.startsWith("#")) {
                        textureName = textureName.substring(1);
                    }
                    TextureAtlasSprite sprite = spriteGetter.apply(ctx.getMaterial(textureName));

                    // Resolve cull direction in rotated model space
                    @Nullable Direction cullDir = null;
                    if (face.cullForDirection() != null) {
                        cullDir = Direction.rotate(
                            modelState.getRotation().getMatrix(),
                            face.cullForDirection());
                    }

                    BakedQuad quad = FACE_BAKERY.bakeQuad(
                        element.from, element.to,
                        face, sprite, dir,
                        modelState,
                        element.rotation,
                        element.shade);

                    if (cullDir == null) {
                        builder.addUnculledFace(quad);
                    } else {
                        builder.addCulledFace(cullDir, quad);
                    }
                }
            }

            return new ChessPieceBakedModel(builder.build(), outlineColor);
        }
    }
}
