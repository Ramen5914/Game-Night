package com.r4men.game_night.client.render.chess;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Wraps a vanilla BakedModel and carries the outline color parsed from the
 * model JSON's "outline_color" field. All BakedModel methods delegate to the
 * inner model so rendering is identical to the base model.
 */
public class ChessPieceBakedModel implements BakedModel {

    private final BakedModel delegate;
    /** Packed RGB outline color: 0xRRGGBB (no alpha). */
    private final int outlineColor;

    public ChessPieceBakedModel(BakedModel delegate, int outlineColor) {
        this.delegate = delegate;
        this.outlineColor = outlineColor;
    }

    /** Returns the packed RGB outline color (0xRRGGBB). */
    public int getOutlineColor() {
        return outlineColor;
    }

    // -------------------------------------------------------------------------
    // Full delegation
    // -------------------------------------------------------------------------

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state,
                                    @Nullable Direction side,
                                    RandomSource rand) {
        return delegate.getQuads(state, side, rand);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state,
                                    @Nullable Direction side,
                                    RandomSource rand,
                                    ModelData data,
                                    @Nullable RenderType renderType) {
        return delegate.getQuads(state, side, rand, data, renderType);
    }

    @Override
    public boolean useAmbientOcclusion() { return delegate.useAmbientOcclusion(); }

    @Override
    public boolean isGui3d() { return delegate.isGui3d(); }

    @Override
    public boolean usesBlockLight() { return delegate.usesBlockLight(); }

    @Override
    public boolean isCustomRenderer() { return delegate.isCustomRenderer(); }

    @Override
    public TextureAtlasSprite getParticleIcon() { return delegate.getParticleIcon(); }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        return delegate.getParticleIcon(data);
    }

    @Override
    public ItemTransforms getTransforms() { return delegate.getTransforms(); }

    @Override
    public ItemOverrides getOverrides() { return delegate.getOverrides(); }
}
