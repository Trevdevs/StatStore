package dev.trevdevs.statstore;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram
{
    private int id;

    private int vertexShaderId;
    private int fragShaderId;

    private Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception
    {
        uniforms = new HashMap<>();

        id = glCreateProgram();
        if(id == 0)
            throw new Exception("Could not initialize shader program");

        createVertexShader(read("vertex.vs"));
        createFragShader(read("fragment.fs"));

        linkShaders();

        createUniform("projectionMatrix");
        createUniform("transformMatrix");
    }

    private int createShader(String code, int type) throws Exception
    {
        int shaderId = glCreateShader(type);
        if(shaderId == 0)
            throw new Exception("Error creating shader type: " + type);

        glShaderSource(shaderId, code);
        glCompileShader(shaderId);
        glAttachShader(id, shaderId);
        return shaderId;
    }

    private void createVertexShader(String shader) throws Exception
    {
        createShader(shader, GL_VERTEX_SHADER);
    }

    private void createFragShader(String shader) throws Exception
    {
        createShader(shader, GL_FRAGMENT_SHADER);
    }

    private void linkShaders() throws Exception
    {
        glLinkProgram(id);

        if(vertexShaderId != 0)
            glDetachShader(id, vertexShaderId);

        if(fragShaderId != 0)
            glDetachShader(id, fragShaderId);

        glValidateProgram(id);
    }

    public void bind()
    {
        glUseProgram(id);
    }

    public void unbind()
    {
        glUseProgram(0);
    }

    public void clean()
    {
        glUseProgram(0);
        if(id != 0)
            glDeleteProgram(id);
    }

    private String read(String name)
    {
        Scanner s = new Scanner(getClass().getClassLoader().getResourceAsStream(name)).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private void createUniform(String name) throws Exception
    {
        int uniformLoc = glGetUniformLocation(id, name);

        if(uniformLoc < 0)
            throw new Exception("Failed to find uniform of name: " + name);

        uniforms.put(name, uniformLoc);
    }

    public void setUniform(String name, Matrix4f value)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(name), false, fb);
        }
    }
}
