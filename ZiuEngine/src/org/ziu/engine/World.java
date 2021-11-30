package org.ziu.engine;

public class World {

    private GameObject[] gameObjects;
    
    private SkyBox skyBox;
    
    private WorldLight worldLight;

    public World(GameObject[] gameObjects, SkyBox skyBox, WorldLight worldLight) {
    	setGameObjects(gameObjects);
    	setSkyBox(skyBox);
    	setWorldLight(worldLight);
    }
    
    public GameObject[] getGameObjects() {
        return gameObjects;
    }

    public void setGameObjects(GameObject[] gameObjects) {
        this.gameObjects = gameObjects;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public WorldLight getWorldLight() {
        return worldLight;
    }

    public void setWorldLight(WorldLight worldLight) {
        this.worldLight = worldLight;
    }
    
}