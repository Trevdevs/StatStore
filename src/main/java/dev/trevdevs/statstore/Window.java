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

            glColor3f(0, 255, 255);
            glBegin(GL_QUADS);
            glVertex2f(0,0);
            glVertex2f(.9f,0);
            glVertex2f(.9f,.9f);
            glVertex2f(0,.9f);
            glEnd();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
}
