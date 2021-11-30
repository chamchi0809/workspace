package org.ziu.engine;

import org.joml.Vector3f;
import org.ziu.engine.graphics.DirectionalLight;
import org.ziu.engine.graphics.PointLight;
import org.ziu.engine.graphics.SpotLight;

public class WorldLight {

    private Vector3f ambientLight;
    
    private PointLight[] pointLightList;
    
    private SpotLight[] spotLightList;
    
    private DirectionalLight directionalLight;
    
    public WorldLight() {
    	setAmbientLight(new Vector3f(.3f, .3f, .3f));    	
    	setPointLightList(null);
    	setSpotLightList(null);
    	setDirectionalLight(new DirectionalLight(new Vector3f(1,1,1),  new Vector3f(1,1,1), 1));
    }
    
    public WorldLight(Vector3f ambientLight, PointLight[] pointLightList, SpotLight[] spotLightList, DirectionalLight directionalLight) {
    	setAmbientLight(ambientLight);
    	setPointLightList(pointLightList);
    	setSpotLightList(spotLightList);
    	setDirectionalLight(directionalLight);
    }

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public PointLight[] getPointLightList() {
        return pointLightList;
    }

    public void setPointLightList(PointLight[] pointLightList) {
        this.pointLightList = pointLightList;
    }

    public SpotLight[] getSpotLightList() {
        return spotLightList;
    }

    public void setSpotLightList(SpotLight[] spotLightList) {
        this.spotLightList = spotLightList;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }
    
}