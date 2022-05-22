package dev.trevdevs.statstore;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
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
    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
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
        projectionMatrix = getProjectionMatrix(fov, 800, 600, z_near, z_far);
        transformMatrix = new Matrix4f();

        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);


        stbi_set_flip_vertically_on_load(true);
        ByteBuffer texture = stbi_load("assets/tTest.png", width, height, channels, 0);
        System.out.println(String.format("W: %d H: %d C: %d", width.get(0), height.get(0), channels.get(0)));
        if (texture != null)
        {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, texture);
            stbi_image_free(texture);
        }
        else
            System.out.println("Failed to load texture");

        // ================================================
        sp.bind();
        sp.setUniform("projectionMatrix", projectionMatrix);
        sp.setUniform("textureSampler", 0);
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
        };

        int[] indices = new int[] {
                0,1,3,
                3,1,2
        };

        float[] texCoords = new float[] {
                0,0,
                0,1,
                1,1,
                1,0
        };

        Mesh mesh1 = new Mesh(vertices, indices, texCoords);

        GameObject obj = new GameObject(mesh1);
        obj.translate(0,0, -.6f);
        transformMatrix = getTransformMatrix(obj);
        sp.setUniform("transformMatrix", transformMatrix);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);


        while( !glfwWindowShouldClose(window) )
        {
            glClearColor(255, 255, 255, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            transformMatrix = getTransformMatrix(obj);
            sp.setUniform("transformMatrix", transformMatrix);

            obj.getMesh().draw();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        glBindTexture(GL_TEXTURE_2D, 0);
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
