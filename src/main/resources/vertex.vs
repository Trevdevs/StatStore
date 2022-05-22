#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 inTexCoord;

out vec2 exTexCoord;
uniform mat4 projectionMatrix;
uniform mat4 transformMatrix;

void main()
{
    gl_Position = projectionMatrix * transformMatrix * vec4(position, 1.0);
    exTexCoord = inTexCoord;
}