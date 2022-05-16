package dev.trevdevs.statstore;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Mesh
{
    private int vao;

    private int verticesVBO;
    private int indicesVBO;
    private int texCoordsVBO;

    private int vertexCount;

    public Mesh(float[] vertices, int[] indices, float[] texCoords)
    {
        FloatBuffer verticesBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer texCoordsBuffer = null;
        vertexCount = indices.length;
        try
        {
            verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();

            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();

            texCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            texCoordsBuffer.put(texCoords).flip();

            vao = glGenVertexArrays();
            glBindVertexArray(vao);

            verticesVBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, verticesVBO);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0,3, GL_FLOAT, false, 0, 0);

            indicesVBO = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            texCoordsVBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, texCoordsVBO);
            glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        }
        finally
        {
            if(verticesBuffer != null)
                MemoryUtil.memFree(verticesBuffer);
            if(texCoordsBuffer != null)
                MemoryUtil.memFree(texCoordsBuffer);
            if(indicesBuffer != null)
                MemoryUtil.memFree(indicesBuffer);
        }
    }

    public void draw()
    {
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void clean()
    {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(verticesVBO);
        glDeleteBuffers(texCoordsVBO);

        glBindVertexArray(0);
        glDeleteVertexArrays(vao);
    }
}
