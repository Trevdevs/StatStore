package dev.trevdevs.statstore;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window
{
    private long window;

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
        if( !glfwInit() )
            throw new IllegalStateException("Unable to create GLFW context");

        window = glfwCreateWindow(800, 600, "Stat Store", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
    }

    /**
     * Draws the window
     */
    private void draw()
    {
        GL.createCapabilities();

        while( !glfwWindowShouldClose(window) )
        {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
}
