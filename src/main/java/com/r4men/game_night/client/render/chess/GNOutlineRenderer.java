package com.r4men.game_night.client.render.chess;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.entity.renderer.GNChessBlockEntityRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * Renders a screen-space silhouette outline around each individual chess piece.
 * <p>
 * Algorithm (fired at AFTER_BLOCK_ENTITIES):
 * <p>
 *  Pass 1 – Combined silhouette
 *    • Clear pieceTarget once.
 *    • Render all pieces into pieceTarget.
 *      - RGB stores the desired outline colour for each piece.
 *      - A stores a unique piece ID (background A=0).
 * <p>
 *  Pass 2 – Edge detection
 *    • Full-screen NDC quad with chess_edge shader.
 *    • Fragment shader compares the A channel of the center pixel against all
 *      neighbours. Any difference (background↔piece) triggers an outline pixel.
 *    • Outline colour is sourced from the piece pixel's RGB.
 *    • Alpha-blended over the main framebuffer.
 * <p>
 * Rendering all pieces in one target allows piece-vs-piece boundaries to be
 * detected when silhouettes overlap on screen.
 */
@EventBusSubscriber(modid = GameNight.ID, value = Dist.CLIENT)
public class GNOutlineRenderer {

    /**
     * Custom RenderTarget that uses an explicit RGBA8 internal format for the color
     * attachment, ensuring full 8-bit precision per channel for per-piece ID encoding.
     */
    private static final class PieceIdTarget extends RenderTarget {
        private int colorTextureId = -1;

        PieceIdTarget(int width, int height) {
            super(true); // useDepth = true
            this.resize(width, height, Minecraft.ON_OSX);
        }

        @Override
        public void createBuffers(int width, int height, boolean clearError) {
            RenderSystem.assertOnRenderThreadOrInit();

            this.viewWidth = width;
            this.viewHeight = height;
            this.width = width;
            this.height = height;

            // Create framebuffer
            this.frameBufferId = GlStateManager.glGenFramebuffers();

            // Create color texture with explicit RGBA8 format for precise ID storage
            this.colorTextureId = GlStateManager._genTexture();
            GlStateManager._bindTexture(this.colorTextureId);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
            // RGBA8 internal format: 8 bits per channel, exactly what we need for piece IDs
            GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA8, width, height, 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null);

            // Create depth texture
            this.depthBufferId = GlStateManager._genTexture();
            GlStateManager._bindTexture(this.depthBufferId);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
            GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT24, width, height, 0,
                    GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, null);

            // Attach textures to framebuffer
            GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBufferId);
            GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                    GL11.GL_TEXTURE_2D, this.colorTextureId, 0);
            GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                    GL11.GL_TEXTURE_2D, this.depthBufferId, 0);

            this.checkStatus();
            this.unbindRead();
        }

        @Override
        public void destroyBuffers() {
            RenderSystem.assertOnRenderThreadOrInit();
            this.unbindRead();
            this.unbindWrite();
            if (this.colorTextureId > -1) {
                GlStateManager._deleteTexture(this.colorTextureId);
                this.colorTextureId = -1;
            }
            if (this.depthBufferId > -1) {
                GlStateManager._deleteTexture(this.depthBufferId);
                this.depthBufferId = -1;
            }
            if (this.frameBufferId > -1) {
                GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
                GlStateManager._glDeleteFramebuffers(this.frameBufferId);
                this.frameBufferId = -1;
            }
        }

        int colorTexId() { return this.colorTextureId; }
    }

    private static PieceIdTarget pieceTarget;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;

        List<OutlineJob> jobs = GNChessBlockEntityRenderer.PENDING_OUTLINES;
        if (jobs.isEmpty()) return;

        if (GNShaders.CHESS_OUTLINE_SHADER == null || GNShaders.CHESS_EDGE_SHADER == null) {
            jobs.clear();
            return;
        }

        try {
            Minecraft mc = Minecraft.getInstance();
            int w = mc.getMainRenderTarget().width;
            int h = mc.getMainRenderTarget().height;

            // Lazily create or resize the piece silhouette target.
            if (pieceTarget == null || pieceTarget.width != w || pieceTarget.height != h) {
                if (pieceTarget != null) pieceTarget.destroyBuffers();
                pieceTarget = new PieceIdTarget(w, h);
            }

            // Note: We intentionally do NOT blit the scene depth buffer. Instead we
            // clear pieceTarget's depth and let pieces depth-test against each other.
            // This ensures each piece retains its unique ID even where they overlap.
            // (Blitting scene depth caused precision mismatches that dropped pieces.)

            pieceTarget.bindWrite(false);
            GlStateManager._clearColor(0f, 0f, 0f, 0f);
            GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

            renderSilhouettes(jobs, event.getProjectionMatrix());

            mc.getMainRenderTarget().bindWrite(false);
            drawEdges();

        } finally {
            jobs.clear();
        }
    }

    // -------------------------------------------------------------------------
    // Pass 1 — Silhouette render with per-piece color IDs
    // -------------------------------------------------------------------------

    private static void renderSilhouettes(List<OutlineJob> jobs, Matrix4f projMat) {
        RandomSource rng = RandomSource.create();
        Vector3f scratch = new Vector3f();

        // Depth test ensures front-most piece writes its ID at each pixel.
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(true);

        for (int i = 0; i < jobs.size(); i++) {
            OutlineJob job = jobs.get(i);
            Matrix4f mat = job.matrix();

            // Use spaced alpha IDs to keep robust separation over sampling noise.
            int pieceId = 16 + (i * 7);
            if (pieceId > 255) pieceId = 255;

            BufferBuilder bb = Tesselator.getInstance()
                    .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            List<BakedQuad> quads = new ArrayList<>();
            for (Direction dir : Direction.values()) {
                quads.addAll(job.model().getQuads(null, dir, rng, ModelData.EMPTY, RenderType.cutoutMipped()));
            }
            quads.addAll(job.model().getQuads(null, null, rng, ModelData.EMPTY, RenderType.cutoutMipped()));

            for (BakedQuad quad : quads) {
                int[] verts = quad.getVertices(); // BLOCK format: 8 ints / vertex
                for (int v = 0; v < 4; v++) {
                    int base = v * 8;
                    float x = Float.intBitsToFloat(verts[base]);
                    float y = Float.intBitsToFloat(verts[base + 1]);
                    float z = Float.intBitsToFloat(verts[base + 2]);
                    mat.transformPosition(x, y, z, scratch);
                    bb.addVertex(scratch.x, scratch.y, scratch.z)
                      .setColor(job.outlineRed(), job.outlineGreen(), job.outlineBlue(), pieceId);
                }
            }

            MeshData mesh = bb.build();
            if (mesh != null) {
                GNShaders.CHESS_OUTLINE_SHADER.getUniform("ModelViewMat").set(new Matrix4f());
                GNShaders.CHESS_OUTLINE_SHADER.getUniform("ProjMat").set(projMat);
                RenderSystem.setShader(() -> GNShaders.CHESS_OUTLINE_SHADER);
                BufferUploader.drawWithShader(mesh);
            }
        }

        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }

    // -------------------------------------------------------------------------
    // Pass 2 — Edge detection & composite
    // -------------------------------------------------------------------------

    private static void drawEdges() {
        // A simple full-screen NDC quad.  The vertex shader passes the positions
        // straight through (no matrix needed) and the fragment shader samples
        // Sampler0 (the piece silhouette texture) to find edge pixels.
        BufferBuilder bb = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bb.addVertex(-1f, -1f, 0f).setUv(0f, 0f);
        bb.addVertex( 1f, -1f, 0f).setUv(1f, 0f);
        bb.addVertex( 1f,  1f, 0f).setUv(1f, 1f);
        bb.addVertex(-1f,  1f, 0f).setUv(0f, 1f);
        MeshData mesh = bb.buildOrThrow();

        // No depth test — the outline is a 2-D overlay; depth was already
        // respected during the silhouette pass.
        RenderSystem.disableDepthTest();

        // The shader JSON declares alpha blending, but ensure it is active.
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Slot 0 → Sampler0 in the edge shader.
        RenderSystem.setShaderTexture(0, pieceTarget.colorTexId());
        RenderSystem.setShader(() -> GNShaders.CHESS_EDGE_SHADER);
        BufferUploader.drawWithShader(mesh);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
}
