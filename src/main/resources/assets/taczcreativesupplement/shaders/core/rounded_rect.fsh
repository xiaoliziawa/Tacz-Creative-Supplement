#version 150

uniform vec4 ColorModulator;
uniform vec4 Rect;
uniform vec4 FillColorTop;
uniform vec4 FillColorBottom;
uniform float Radius;
uniform float Softness;

in vec2 vertexPos;

out vec4 fragColor;

float roundedBoxSdf(vec2 point, vec2 halfSize, float radius) {
    vec2 delta = abs(point) - (halfSize - vec2(radius));
    return length(max(delta, vec2(0.0))) + min(max(delta.x, delta.y), 0.0) - radius;
}

void main() {
    vec2 center = Rect.xy + Rect.zw * 0.5;
    vec2 halfSize = Rect.zw * 0.5;
    float safeRadius = min(Radius, min(halfSize.x, halfSize.y));
    float distanceToEdge = roundedBoxSdf(vertexPos - center, halfSize, safeRadius);
    float alphaMask = 1.0 - smoothstep(0.0, max(0.001, Softness), distanceToEdge);

    if (alphaMask <= 0.0) {
        discard;
    }

    float gradientFactor = clamp((vertexPos.y - Rect.y) / max(Rect.w, 0.001), 0.0, 1.0);
    vec4 fillColor = mix(FillColorTop, FillColorBottom, gradientFactor) * ColorModulator;
    fragColor = vec4(fillColor.rgb, fillColor.a * alphaMask);
}
