package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private String title;
    private static Window window = null; // Singleton
    private long glfwWindow = 0; // mem address of window in memory is stored
    private float r, g, b, a;
    private boolean fadeToBlack = false;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        this.r = 1;
        this.g = 1;
        this.b = 1;
        this.a = 1;
    }

    public static Window get(){
        if(Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }

    public void run(){
        System.out.println("Hello LW3GL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW & Free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    /* Get window up and running*/
    public void init(){
        // Set up error callback --> Log errors
        GLFWErrorCallback.createPrint(System.err).set();

        // Init GLFW
        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Config GLFW, use hints to create the window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to create the GLFW window");
        }


        // Forward mousePosCallback to glfw cursorposcallback
        // Basically just overwriting the callbacks from the glfw docs
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make OpenGL context current, so our window is the curretn context
        glfwMakeContextCurrent(glfwWindow);

        // Enable v-sync , no wait time between frames, go as fast as we can when updating frames
        glfwSwapInterval(1);

        // Make window visible
        glfwShowWindow(glfwWindow);

        // Makes sure we can use the bindings to OpenGL
        GL.createCapabilities();

    }

    public void loop(){
        while(!glfwWindowShouldClose(glfwWindow)){
            // Poll Events (mouse, key)
            glfwPollEvents();

            glClearColor(r,g,b,a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(fadeToBlack){
                r = Math.max(r - 0.01f, 0);
                g = Math.max(g - 0.01f, 0);
                b = Math.max(b - 0.01f, 0);
            }
            else{
                r = Math.min(r + 0.01f, 1);
                g = Math.min(g + 0.01f, 1);
                b = Math.min(b + 0.01f, 1);
            }

            if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                fadeToBlack = true;
            }

            if(!KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                fadeToBlack = false;
            }

            glfwSwapBuffers(glfwWindow);

        }
    }
}
