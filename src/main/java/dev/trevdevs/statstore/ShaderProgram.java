package dev.trevdevs.statstore;

import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram
{
    private int id;

    private int vertexShaderId;
    private int fragShaderId;

    public ShaderProgram() throws Exception
    {
        id = glCreateProgram();
        if(id == 0)
            throw new Exception("Could not initialize shader program");

        createVertexShader(read("vertex.vs"));
        createFragShader(read("fragment.fs"));

        linkShaders();
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
}
