import common.NesConstants;
import hardware.NES;
import hardware.rom.Rom;
import hardware.rom.RomLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

class NesteratorForDesktop {

    private GLFWErrorCallback mErrorCallback;
    private GLFWKeyCallback mKeyCallback;

    // The mWindow handle
    private long mWindow;

    private final NES mNES;
    private int mFPS;
    private double mLastFPS;

    private NesteratorForDesktop() throws IOException {
        Rom rom = RomLoader.loadRom(NesteratorForDesktop.class.getResourceAsStream("vram_access.nes"));
        mNES = new NES(rom);
    }

    private void run() {
        try {
            init();
            loop();

            // Release mWindow and mWindow callbacks
            glfwDestroyWindow(mWindow);
            mKeyCallback.release();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            mErrorCallback.release();
        }
    }

    private void init() {
        glfwSetErrorCallback(mErrorCallback = errorCallbackPrint(System.err));

        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        mWindow = glfwCreateWindow(NesConstants.WIDTH * 4, NesConstants.HEIGHT * 4, "Nesterator", NULL, NULL);
        if ( mWindow == NULL )
            throw new RuntimeException("Failed to create the GLFW mWindow");

        glfwSetKeyCallback(mWindow, mKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, GL_TRUE);
            }
        });

        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our mWindow
        glfwSetWindowPos(
                mWindow,
                (GLFWvidmode.width(vidmode) - NesConstants.WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - NesConstants.HEIGHT) / 2
        );

        glfwMakeContextCurrent(mWindow);

        // Enable v-sync
        glfwSwapInterval(1);
        glfwShowWindow(mWindow);
        GLContext.createFromCurrent();
    }

    private void loop() {
        GL.createCapabilities(true);

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glPixelZoom(8.0f, 8.0f);

        double lastRenderedTime = glfwGetTime();
        mLastFPS = glfwGetTime();
        mNES.getPpu().initializeBuffer(BufferUtils.createByteBuffer(NesConstants.WIDTH * NesConstants.HEIGHT * 3));
        glEnable(GL_TEXTURE_2D);

        while ( glfwWindowShouldClose(mWindow) == GL_FALSE ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glShadeModel(GL_FLAT);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glfwPollEvents();

            double time = glfwGetTime();
            if (time - lastRenderedTime >= NesConstants.FRAME_INTERVAL_SECONDS) {
                renderFrame(time - lastRenderedTime);
                lastRenderedTime = time;
            }

            glfwSwapBuffers(mWindow);
        }
    }

    private void renderFrame(double elapsedTimeSec) {
        if (glfwGetTime() - mLastFPS > 1) {
            mFPS = 0; //reset the FPS counter
            mLastFPS += 1; //add one second
        }
        mFPS++;
        mNES.advanceTime(elapsedTimeSec);
        ByteBuffer data = mNES.getPpu().renderFrame();
        data.rewind();
        glDrawPixels(NesConstants.WIDTH, NesConstants.HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, data);
        glFlush();
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
        new NesteratorForDesktop().run();
    }

}