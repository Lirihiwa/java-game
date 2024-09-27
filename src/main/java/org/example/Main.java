package org.example;

import org.joml.Matrix4f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    // The window handle
    private long window;
    private float posX = 0.0f, posY = 0.0f, posZ = -5.0f;
    private float angle = 0.0f;


    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        window = glfwCreateWindow(1920, 1080, "Game", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(action == GLFW_PRESS || action == GLFW_REPEAT){
                switch (key) {
                    case GLFW_KEY_W -> posZ += 0.05f;
                    case GLFW_KEY_S -> posZ -= 0.05f;
                    case GLFW_KEY_D -> posX += 0.05f;
                    case GLFW_KEY_A -> posX -= 0.05f;

                    case GLFW_KEY_SPACE -> posY -= 0.05f;
                    case GLFW_KEY_LEFT_SHIFT -> posY += 0.05f;
                }
            }
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);

        glfwSwapInterval(1);

        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        Matrix4f matrix = new Matrix4f().perspective((float) Math.toRadians(45.0),
                1920.0f / 1080.0f, 1.0f, 100.0f);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matrixBuffer);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glMatrixMode(GL_PROJECTION);
            glLoadMatrixf(matrixBuffer);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            glTranslatef(posX, posY, posZ);
            glRotatef(angle, 0.0f, 1.0f, 0.0f);
            glScalef(0.33f, 0.33f, 0.33f);

            glBegin(GL_QUADS);
            //front side
            glColor3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-0.5f, -0.5f, 0.5f);
            glVertex3f(0.5f, -0.5f, 0.5f);
            glVertex3f(0.5f, 0.5f, 0.5f);
            glVertex3f(-0.5f, 0.5f, 0.5f);

            //back side
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(-0.5f, -0.5f, -0.5f);
            glVertex3f(0.5f, -0.5f, -0.5f);
            glVertex3f(0.5f, 0.5f, -0.5f);
            glVertex3f(-0.5f, 0.5f, -0.5f);

            //left side
            glColor3f(0.0f, 0.502f, 0.0f);
            glVertex3f(-0.5f, 0.5f, -0.5f);
            glVertex3f(-0.5f, 0.5f, 0.5f);
            glVertex3f(-0.5f, -0.5f, 0.5f);
            glVertex3f(-0.5f, -0.5f, -0.5f);

            //right side
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f(0.5f, 0.5f, 0.5f);
            glVertex3f(0.5f, 0.5f, -0.5f);
            glVertex3f(0.5f, -0.5f, -0.5f);
            glVertex3f(0.5f, -0.5f, 0.5f);

            //top side
            glColor3f(1.0f, 0.647f, 0.0f);
            glVertex3f(0.5f, 0.5f, 0.5f);
            glVertex3f(-0.5f, 0.5f, 0.5f);
            glVertex3f(-0.5f, 0.5f, -0.5f);
            glVertex3f(0.5f, 0.5f, -0.5f);

            //bottom side
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(0.5f, -0.5f, 0.5f);
            glVertex3f(-0.5f, -0.5f, 0.5f);
            glVertex3f(-0.5f, -0.5f, -0.5f);
            glVertex3f(0.5f, -0.5f, -0.5f);
            glEnd();

            angle += 0.5f;

            glfwSwapBuffers(window);

            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

}