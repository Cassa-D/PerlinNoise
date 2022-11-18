package br.pucpr.cg;

import br.pucpr.mage.*;
import br.pucpr.mage.camera.Camera;
import br.pucpr.mage.phong.DirectionalLight;
import br.pucpr.mage.phong.Material;
import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class LitCube implements Scene {
    private static final String PATH = "C:\\Users\\trice\\OneDrive\\Documentos\\Atividades\\opengl05-04-master\\opengl05-04-master\\assets";

    private Keyboard keys = Keyboard.getInstance();

    private static final float SPEED = (float) Math.toRadians(180);

    private Shader shader;
    private Mesh mesh;
    private float angleX = 0.0f;
    private float angleY = 0.5f;
    private Camera camera = new Camera();
    private DirectionalLight light;
    private Material material;

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        shader = Shader.loadProgram("phong");

        int size = 550;

        mesh = MeshFactory.createTerrain(shader, size + 100, size + 100, 0.2f, 0.012f);
        camera.getPosition().y = size;
        camera.getPosition().z = -size;

        light = new DirectionalLight()
                .setAmbient(0.1f)
                .setColor(1.0f, 1.0f, 0.8f);

        material = new Material()
                .setColor(1.0f)
                .setPower(32.0f);
    }

    @Override
    public void update(float secs) {
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), true);
            return;
        }

        if (keys.isDown(GLFW_KEY_A)) {
            angleY += SPEED * secs;
        } else if (keys.isDown(GLFW_KEY_D)) {
            angleY -= SPEED * secs;
        }

        if (keys.isDown(GLFW_KEY_W)) {
            angleX += SPEED * secs;
        } else if (keys.isDown(GLFW_KEY_S)) {
            angleX -= SPEED * secs;
        }

        if (keys.isDown(GLFW_KEY_R)) {
            MeshFactory.refreshSeed();
            mesh = MeshFactory.createTerrain(shader, 250, 250, 0.2f, 0.012f);
        }
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.bind()
            .set(camera)
            .set(light)
        .unbind();

        mesh
            .setUniform("uWorld", new Matrix4f().rotateY(angleY).rotateX(angleX))
            .setItem("material", material)
        .draw(shader);
    }

    @Override
    public void deinit() {
    }

    public static void main(String[] args) {
        new Window(new LitCube(), "Cube with lights", 800, 600).show();
    }
}