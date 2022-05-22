#version 330

in vec2 exTexCoord;
out vec4 color;

uniform sampler2D textureSampler;

void main()
{
    color = texture(textureSampler, exTexCoord);
}