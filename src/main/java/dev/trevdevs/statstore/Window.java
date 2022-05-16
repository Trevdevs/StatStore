package dev.trevdevs.statstore;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.system.MemoryUtil.*;

public class Window
{
    private long window;
    private ShaderProgram sp;

    private float fov;
    private float z_near;
    private float z_far;

    private Matrix4f projectionMatrix;
    private Matrix4f transformMatrix;

    private int textureID;
    private int textureWidth, textureHeight;

    public Window()
    {
        init();
        draw();
    }

    /**
     * GLFW Context and Window Creation
     */
    private void init()
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if( !glfwInit() )
            throw new IllegalStateException("Unable to create GLFW context");

        window = glfwCreateWindow(800, 600, "Stat Store", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        GL.createCapabilities();

        try {
            sp = new ShaderProgram();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fov = 60.0f;
        z_near = 0.01f;
        z_far = 1000f;

        projectionMatrix = new Matrix4f();
        projectionMatrix = getProjectionMatrix(fov, 800,600,z_near, z_far);

        transformMatrix = new Matrix4f();

        // LOAD TEXTURE
        InputStream is = ClassLoader.getSystemResourceAsStream("tTest.png");
        BufferedImage image;
        ByteArrayOutputStream baos;
        ByteBuffer texture;

        try {
            image = ImageIO.read(is);
            textureWidth = image.getWidth();
            textureHeight = image.getHeight();
            baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            is.close();
            texture = ByteBuffer.wrap(baos.toByteArray());
            baos.close();
            texture.flip();

            textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureWidth, textureHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);

            glGenerateMipmap(GL_TEXTURE_2D);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // ================================================
        sp.bind();
        sp.setUniform("projectionMatrix", projectionMatrix);

        glEnable(GL_DEPTH_TEST);
    }

    /**
     * Draws the window
     */
    private void draw()
    {
        float[] vertices = new float[] {
                // VO
                -0.5f,  0.5f,  0.5f,
                // V1
                -0.5f, -0.5f,  0.5f,
                // V2
                0.5f, -0.5f,  0.5f,
                // V3
                0.5f,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f,  0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };

        int[] indices = new int[] {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };

        float[] color = new float[] {
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };

        Mesh mesh1 = new Mesh(vertices, indices, color);

        GameObject obj = new GameObject(mesh1);
        obj.translate(0,0, -1f);
        transformMatrix = getTransformMatrix(obj);
        sp.setUniform("transformMatrix", transformMatrix);

        while( !glfwWindowShouldClose(window) )
        {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            obj.rotate(0.4f,0.5f,0);

            transformMatrix = getTransformMatrix(obj);
            sp.setUniform("transformMatrix", transformMatrix);

            obj.getMesh().draw();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        sp.unbind();
        sp.clean();
        mesh1.clean();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar)
    {
        float aspectRatio = width/height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public final Matrix4f getTransformMatrix(GameObject object)
    {
        Vector3f rotation = object.getRotation();

        transformMatrix.identity().translate(object.getPosition()).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(object.getScale());
        return transformMatrix;
    }
}
