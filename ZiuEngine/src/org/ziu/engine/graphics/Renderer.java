package org.ziu.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.ziu.engine.GameObject;
import org.ziu.engine.IHud;
import org.ziu.engine.World;
import org.ziu.engine.WorldLight;
import org.ziu.game.Hud;
import org.ziu.engine.SkyBox;
import org.ziu.engine.Utils;
import org.ziu.engine.Window;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private static final int MAX_POINT_LIGHTS = 5;

    private static final int MAX_SPOT_LIGHTS = 5;

    private final Transformation transformation;

    private ShaderProgram worldShaderProgram;

    private ShaderProgram hudShaderProgram;
    
    private ShaderProgram skyBoxShaderProgram;
    
    private ShaderProgram depthShaderProgram;

    private final float specularPower;

	private ShadowMap shadowMap;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {
    	shadowMap = new ShadowMap();
    	setupDepthShader();
    	setupSkyBoxShader();
        setupWorldShader();
        setupHudShader();
    }
    
    private void setupSkyBoxShader() throws Exception {
        skyBoxShaderProgram = new ShaderProgram();
        skyBoxShaderProgram.createVertexShader(Utils.loadResource("resources/built_in/shaders/sb_vertex.vs"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource("resources/built_in/shaders/sb_fragment.fs"));
        skyBoxShaderProgram.link();

        // Create uniforms for projection matrix
        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");
        skyBoxShaderProgram.createUniform("texture_sampler");
        skyBoxShaderProgram.createUniform("ambientLight");
    }


    private void setupWorldShader() throws Exception {
        // Create shader
        worldShaderProgram = new ShaderProgram();
        worldShaderProgram.createVertexShader(Utils.loadResource("resources/built_in/shaders/vertex.vs"));
        worldShaderProgram.createFragmentShader(Utils.loadResource("resources/built_in/shaders/fragment.fs"));
        worldShaderProgram.link();

        // Create uniforms for modelView and projection matrices and texture
        worldShaderProgram.createUniform("projectionMatrix");
        worldShaderProgram.createUniform("modelViewMatrix");
        worldShaderProgram.createUniform("texture_sampler");
        // Create uniform for material
        worldShaderProgram.createMaterialUniform("material");
        // Create lighting related uniforms
        worldShaderProgram.createUniform("specularPower");
        worldShaderProgram.createUniform("ambientLight");
        worldShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        worldShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        worldShaderProgram.createDirectionalLightUniform("directionalLight");
        
     // Create uniforms for shadow mapping
        worldShaderProgram.createUniform("shadowMap");
        worldShaderProgram.createUniform("orthoProjectionMatrix");
        worldShaderProgram.createUniform("modelLightViewMatrix");
    }

    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.createVertexShader(Utils.loadResource("resources/built_in/shaders/hud_vertex.vs"));
        hudShaderProgram.createFragmentShader(Utils.loadResource("resources/built_in/shaders/hud_fragment.fs"));
        hudShaderProgram.link();

        // Create uniforms for Ortographic-model projection matrix and base colour
        hudShaderProgram.createUniform("projModelMatrix");
        hudShaderProgram.createUniform("colour");
        hudShaderProgram.createUniform("hasTexture");
    }
    
    private void setupDepthShader() throws Exception {
        depthShaderProgram = new ShaderProgram();
        depthShaderProgram.createVertexShader(Utils.loadResource("resources/built_in/shaders/depth_vertex.vs"));
        depthShaderProgram.createFragmentShader(Utils.loadResource("resources/built_in/shaders/depth_fragment.fs"));
        depthShaderProgram.link();

        depthShaderProgram.createUniform("orthoProjectionMatrix");
        depthShaderProgram.createUniform("modelLightViewMatrix");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, World world, IHud hud) {

        clear();

        // Render depth map before view ports has been set up
        renderDepthMap(window, camera, world);

        
        glViewport(0, 0, window.getWidth(), window.getHeight());
        
        
        
     // Update projection and view atrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);
        
        renderWorld(window, camera, world);
        
        renderSkyBox(window,camera,world);

        renderHud(window, hud);
    }
    
    private void renderDepthMap(Window window, Camera camera, World world) {
        // Setup view port to match the texture size
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        DirectionalLight light = world.getWorldLight().getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        if(world.getGameObjects() != null) {
        	
        	for (GameObject gameObject : world.getGameObjects()) {
        		Mesh mesh = gameObject.getMesh(); 
        		Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(gameObject, lightViewMatrix);
        		depthShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
        		
        		worldShaderProgram.setUniform("material", mesh.getMaterial());
        		mesh.render();
        	}
        }
        // Unbind
        depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
    }

    public void renderWorld(Window window, Camera camera, World world) {

        worldShaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        worldShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        worldShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);           
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();
        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix();

        renderLights(viewMatrix,world.getWorldLight());

        worldShaderProgram.setUniform("texture_sampler", 0);
        worldShaderProgram.setUniform("shadowMap", 1);
        
        glEnable(GL_CULL_FACE);
        
        
        if(world.getGameObjects() != null) {
        	
        	for (GameObject gameObject : world.getGameObjects()) {
        		Mesh mesh = gameObject.getMesh();
        		Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameObject, viewMatrix);
        		worldShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);            
        		Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(gameObject, lightViewMatrix);            
        		worldShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
        		worldShaderProgram.setUniform("material", mesh.getMaterial());
        		glActiveTexture(GL_TEXTURE1);
        		glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
        		mesh.render();
        	}
        }

        worldShaderProgram.unbind();
    }
    
    private void renderSkyBox(Window window, Camera camera, World world) {
        skyBoxShaderProgram.bind();

        skyBoxShaderProgram.setUniform("texture_sampler", 0);

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        SkyBox skyBox = world.getSkyBox();
        Matrix4f viewMatrix = transformation.getViewMatrix();
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
        skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        skyBoxShaderProgram.setUniform("ambientLight", world.getWorldLight().getAmbientLight());
                
        glDisable(GL_CULL_FACE);
        world.getSkyBox().getMesh().render();

        skyBoxShaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, WorldLight worldLight) {

        worldShaderProgram.setUniform("ambientLight", worldLight.getAmbientLight());
        worldShaderProgram.setUniform("specularPower", specularPower);

        // Process Point Lights
        PointLight[] pointLightList = worldLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            worldShaderProgram.setUniform("pointLights", currPointLight, i);
        }

        // Process Spot Ligths
        SpotLight[] spotLightList = worldLight.getSpotLightList();
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

            Vector3f lightPos = currSpotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            worldShaderProgram.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(worldLight.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        worldShaderProgram.setUniform("directionalLight", currDirLight);
    }

    private void renderHud(Window window, IHud hud) {
        if (hud != null) {
            hudShaderProgram.bind();

            Matrix4f ortho = transformation.getOrtho2DProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
            for (GameObject gameObject : hud.getGameObjects()) {
                Mesh mesh = gameObject.getMesh();
                Matrix4f projModelMatrix = transformation.buildOrthoProjModelMatrix(gameObject, ortho);
                hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
                hudShaderProgram.setUniform("colour", gameObject.getMesh().getMaterial().getAmbientColour());
                hudShaderProgram.setUniform("hasTexture", gameObject.getMesh().getMaterial().isTextured() ? 1 : 0);

                
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                mesh.render();
            }

            hudShaderProgram.unbind();
        }
    }
    

    public void cleanup(World world, Hud hud) {
    	if (shadowMap != null) {
            shadowMap.cleanup();
        }
        if (depthShaderProgram != null) {
            depthShaderProgram.cleanup();
        }
        if (skyBoxShaderProgram != null) {
            skyBoxShaderProgram.cleanup();
        }
        if (worldShaderProgram != null) {
            worldShaderProgram.cleanup();
        }
        if (hudShaderProgram != null) {
            hudShaderProgram.cleanup();
        }
        for(GameObject gameObject : world.getGameObjects()) {
        	gameObject.getMesh().cleanUp();
        }
        for(GameObject gameObject : hud.getGameObjects()) {
        	gameObject.getMesh().cleanUp();
        }
    }
}