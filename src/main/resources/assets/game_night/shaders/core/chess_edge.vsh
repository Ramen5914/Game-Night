#version 150

in vec3 Position;
in vec2 UV0;

out vec2 texCoord0;

void main() {
    /* Vertices are supplied in NDC space (-1..1), so no matrix needed. */
    gl_Position = vec4(Position.xy, 0.0, 1.0);
    texCoord0 = UV0;
}
