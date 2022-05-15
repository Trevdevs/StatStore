#version 330

in vec3 exColor;
out vec4 color;

void main()
{
    color = vec4(exColor, 1.0);
}