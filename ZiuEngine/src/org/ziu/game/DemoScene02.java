package org.ziu.game;

import org.joml.*;
import static org.lwjgl.glfw.GLFW.*;

import java.awt.Color;

import org.ziu.engine.*;
import org.ziu.engine.audio.*;
import org.ziu.engine.graphics.*;
import org.ziu.game.Hud;

public class DemoScene02 implements IGameScene {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Renderer renderer;

    private final Camera camera;
    
    private Hud hud;    
    
    private World world;

    private Vector3f inputVec;
    
    private AudioManager audioMgr;

    public DemoScene02() {
        renderer = new Renderer();
        camera = new Camera();       
        audioMgr = new AudioManager();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        audioMgr.init();

        try {
			hud = new Hud("Demo Scene02");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        SkyBox skyBox = new SkyBox();
        WorldLight worldLight = new WorldLight();
        worldLight.getDirectionalLight().setShadowPosMult(3);
        GameObject swordObject = new GameObject();
        Mesh swordMesh = OBJLoader.loadMesh("resources/built_in/models/sword.obj");
        swordMesh.setMaterial(new Material(new Texture("resources/built_in/textures/sword.png")));
        swordObject.setMesh(swordMesh);
        swordObject.setPosition(0, 0, -5);
        swordObject.setScale(.2f);
        swordObject.setRotation(40, 0, 30);
        
        GameObject[] gameObjects = {swordObject};
        
        world = new World(gameObjects, skyBox, worldLight);
        
        AudioBuffer bgmBuffer = new AudioBuffer("resources/built_in/sounds/background.ogg");
        audioMgr.addAudioBuffer(bgmBuffer);
        audioMgr.addAudioSource("bgmSource", new AudioSource(true, true));
        audioMgr.getAudioSource("bgmSource").setPosition(new Vector3f(0,0,0));
        audioMgr.getAudioSource("bgmSource").setBuffer(bgmBuffer.getBufferId());
        audioMgr.getAudioSource("bgmSource").play();
        audioMgr.setListener(new AudioListener());
    }

    @Override
    public void input(Window window, MouseInput mouseInput, GameEngine gameEngine) {
    	if(mouseInput.isRightButtonPressed()) {
    		camera.moveRotation(mouseInput.getDeltaPos().x * MOUSE_SENSITIVITY, mouseInput.getDeltaPos().y * MOUSE_SENSITIVITY,0);
    	}
    	inputVec = new Vector3f(0,0,0);
    	if(window.isKeyPressed(GLFW_KEY_W))
    		inputVec.z -= 1;
    	if(window.isKeyPressed(GLFW_KEY_S))
    		inputVec.z += 1;
    	if(window.isKeyPressed(GLFW_KEY_A))
    		inputVec.x -= 1;
    	if(window.isKeyPressed(GLFW_KEY_D))
    		inputVec.x += 1;
    	if(window.isKeyPressed(GLFW_KEY_Q))
    		inputVec.y -= 1;
    	if(window.isKeyPressed(GLFW_KEY_E))
    		inputVec.y += 1;
    }

    @Override
    public void update(float deltaTime, MouseInput mouseInput, GameEngine gameEngine) {
    	camera.movePosition(inputVec.x*5*deltaTime, inputVec.y*5*deltaTime, inputVec.z*5*deltaTime);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, world, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup(world, hud);
        audioMgr.cleanup();
    }

}