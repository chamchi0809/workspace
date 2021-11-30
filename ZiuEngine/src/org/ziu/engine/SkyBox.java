package org.ziu.engine;

import org.ziu.engine.graphics.Material;
import org.ziu.engine.graphics.Mesh;
import org.ziu.engine.graphics.OBJLoader;
import org.ziu.engine.graphics.Texture;

public class SkyBox extends GameObject {

	public SkyBox() throws Exception {
		super();
        Mesh skyBoxMesh = OBJLoader.loadMesh("resources/built_in/models/skybox.obj");
        Texture skyBoxtexture = new Texture("resources/built_in/textures/skybox.png");
        skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
        setScale(200);

    }
	
    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxtexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
        setScale(200);
    }
}