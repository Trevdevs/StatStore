package dev.trevdevs.statstore;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window
{
    private long window;
    private ShaderProgram sp;

    private float fov;
    private float z_near;
    private float z_far;

    private Matrix4f projectionMatrix;

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
                -0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
        };

        int[] indices = new int[] {
                0,1,2,2,3,0
        };

        float[] color = new float[] {
                1f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f
        };

        Mesh mesh1 = new Mesh(vertices, indices, color);

        GameObject obj = new GameObject(mesh1);

        while( !glfwWindowShouldClose(window) )
        {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

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
}
