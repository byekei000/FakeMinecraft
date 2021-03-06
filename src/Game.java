import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;
    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;
    private Robot robot;
    private ArrayList<GameItem> gameItems;
    private static final float CAMERA_POS_STEP = 0.05f;
    private float dy = 0;
    private final float g = -0.02f;
    private boolean leftButtonPressed;
    private CameraBoxSelectionDetector selectDetector;
    private int h = 0;

    public Game() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        // Create the Mesh
        float[] positions = new float[]{
            // V0
            -0.5f, 0.5f, 0.5f,
            // V1
            -0.5f, -0.5f, 0.5f,
            // V2
            0.5f, -0.5f, 0.5f,
            // V3
            0.5f, 0.5f, 0.5f,
            // V4
            -0.5f, 0.5f, -0.5f,
            // V5
            0.5f, 0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,
            // For text coords in top face
            // V8: V4 repeated
            -0.5f, 0.5f, -0.5f,
            // V9: V5 repeated
            0.5f, 0.5f, -0.5f,
            // V10: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V11: V3 repeated
            0.5f, 0.5f, 0.5f,
            // For text coords in right face
            // V12: V3 repeated
            0.5f, 0.5f, 0.5f,
            // V13: V2 repeated
            0.5f, -0.5f, 0.5f,
            // For text coords in left face
            // V14: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V15: V1 repeated
            -0.5f, -0.5f, 0.5f,
            // For text coords in bottom face
            // V16: V6 repeated
            -0.5f, -0.5f, -0.5f,
            // V17: V7 repeated
            0.5f, -0.5f, -0.5f,
            // V18: V1 repeated
            -0.5f, -0.5f, 0.5f,
            // V19: V2 repeated
            0.5f, -0.5f, 0.5f,};
        float[] textCoords = new float[]{
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,
            0.0f, 0.0f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            // For text coords in top face
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.0f, 1.0f,
            0.5f, 1.0f,
            // For text coords in right face
            0.0f, 0.0f,
            0.0f, 0.5f,
            // For text coords in left face
            0.5f, 0.0f,
            0.5f, 0.5f,
            // For text coords in bottom face
            0.5f, 0.0f,
            1.0f, 0.0f,
            0.5f, 0.5f,
            1.0f, 0.5f,};
        int[] indices = new int[]{
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            8, 10, 11, 9, 8, 11,
            // Right face
            12, 13, 7, 5, 12, 7,
            // Left face
            14, 15, 6, 4, 14, 6,
            // Bottom face
            16, 18, 19, 17, 16, 19,
            // Back face
            4, 6, 7, 5, 4, 7,
        };
        Texture texture = new Texture("/res/grassblock.png");
        Mesh mesh = new Mesh(positions, textCoords, indices, texture);
        gameItems = new ArrayList<GameItem>();
        for(int i = 0; i < 50; i++){
            for(int j = 0; j < 50; j++){
                    gameItems.add(new GameItem(mesh));
                    gameItems.get(i*50+j).setScale(1f);
                    gameItems.get(i*50+j).setPosition(((float)i),(int)(Math.random()*2),((float)j));
            }
        }
        selectDetector = new CameraBoxSelectionDetector();
        camera.setPosition(-0.5f, 10f, -0.5f);
        camera.setRotation(0f,90f,0f);
//        GameItem gameItem1 = new GameItem(mesh);
//        gameItem1.setScale(0.5f);
//        gameItem1.setPosition(0, 0, -2);
//        GameItem gameItem2 = new GameItem(mesh);
//        gameItem2.setScale(0.5f);
//        gameItem2.setPosition(0.5f, 0.5f, -2);
//        GameItem gameItem3 = new GameItem(mesh);
//        gameItem3.setScale(0.5f);
//        gameItem3.setPosition(0, 0, -2.5f);
//        GameItem gameItem4 = new GameItem(mesh);
//        gameItem4.setScale(0.5f);
//        gameItem4.setPosition(0.5f, 0, -2.5f);
        try{
            robot = new Robot();
        } catch(AWTException e){ }
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            if(window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)){
                renderer.setFOV(60.0f);
                cameraInc.z = -6;
            } else{
                renderer.setFOV(60.0f);
                cameraInc.z = -4;
            }
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 2;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -2;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 2;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -2;
        } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            if(camera.getPosition().y == getItemWithPos(camera.getPosition().x, camera.getPosition().z).getPosition().y+2){
                cameraInc.y = 1;
                dy += 0.3f;
            }
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

        if(camera.getPosition().y > getItemWithPos(camera.getPosition().x, camera.getPosition().z).getPosition().y+2){
            dy += g;
            camera.movePosition(0,dy,0);
        } else {
            if(camera.getPosition().y < getItemWithPos(camera.getPosition().x, camera.getPosition().z).getPosition().y+2){
                camera.setYPosition(getItemWithPos(camera.getPosition().x, camera.getPosition().z).getPosition().y+2);
            }
            dy = 0;
        }
//        boolean aux = mouseInput.isLeftButtonPressed();
//        if (aux && !this.leftButtonPressed && this.selectDetector.selectGameItem(gameItems, camera)) {
//            h++;
//            System.out.println("hey" + h);
//        }
//        this.leftButtonPressed = aux;
//        System.out.println(camera.getPosition().z + " " + getItemWithPos(camera.getPosition().x, camera.getPosition().z).getPosition().z);
        selectDetector.selectGameItem(gameItems, camera);
        int index = 0;
        for(GameItem gameItem : gameItems){
            if(gameItem.isSelected()){
                System.out.println("yuh" + index);
            }
            index++;
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }

    public GameItem getItemWithPos(float x, float z){
        GameItem gameItem = gameItems.get(((int)((Math.round(x))*50)+(int)((Math.round(z)))));
//        System.out.println(((int)((Math.round(x)+25)*50)+(int)((Math25.round(z)+25))));
        return gameItem;
    }
}
