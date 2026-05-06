#version 150

uniform sampler2D Sampler0;

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    vec2 texel = 1.0 / vec2(textureSize(Sampler0, 0));
    vec4 center = texture(Sampler0, texCoord0);
    float centerId = center.a;
    bool centerIsPiece = centerId > 0.03;

    /*
     * We treat the two outline cases differently:
     *   • background next to piece  -> draw outer outline on the background side only
     *   • piece next to piece       -> draw a separator on one piece side only
     *
     * Piece ID is stored in alpha (A), background is A=0.
     * This asymmetric test avoids turning close/overlapping silhouettes into one
     * enlarged hull, which happened when both sides of every boundary were drawn.
     * piece<->background edges use the touching piece RGB color.
     * piece<->piece edges use a dark separator for readability.
     *
     * Threshold: piece IDs are spaced by 8/255 ≈ 0.031, and background is 0.
     * Minimum distance from any piece ID (starting at 16/255 ≈ 0.063) to background is ~0.063.
     * Minimum distance between adjacent piece IDs is ~0.031.
     * We use threshold 0.02 to catch any ID difference while avoiding floating-point noise.
     *
     * Search radius: 2 pixels for outer outlines, 1 pixel for piece separators.
     */
    if (centerIsPiece) {
        // Only draw inter-piece separators on one side of the boundary, so the
        // divider stays thin and does not expand the combined silhouette.
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                vec4 neighbor = texture(Sampler0, texCoord0 + vec2(dx, dy) * texel);
                float neighborId = neighbor.a;
                bool neighborIsPiece = neighborId > 0.03;
                if (neighborIsPiece && abs(neighborId - centerId) > 0.02 && centerId > neighborId) {
                    fragColor = vec4(0.0, 0.0, 0.0, 1.0);
                    return;
                }
            }
        }
        discard;
    }

    // Background pixel: draw only the outer outline, using the nearest piece color.
    float bestDistance = 9999.0;
    vec3 bestColor = vec3(0.0);
    bool foundPiece = false;
    for (int dx = -2; dx <= 2; dx++) {
        for (int dy = -2; dy <= 2; dy++) {
            if (dx == 0 && dy == 0) continue;
            vec4 neighbor = texture(Sampler0, texCoord0 + vec2(dx, dy) * texel);
            float neighborId = neighbor.a;
            if (neighborId > 0.03) {
                float distanceSq = float(dx * dx + dy * dy);
                if (distanceSq < bestDistance) {
                    bestDistance = distanceSq;
                    bestColor = neighbor.rgb;
                    foundPiece = true;
                }
            }
        }
    }

    if (foundPiece) {
        fragColor = vec4(bestColor, 1.0);
        return;
    }

    discard;
}
