package org.ziu.game;

import org.joml.Vector4f;
import org.ziu.engine.*;
import org.ziu.engine.graphics.*;
import java.awt.Font;

public class Hud implements IHud {

	private static final Font FONT = new Font("Arial", Font.PLAIN, 20);	
	
    private static final String CHARSET = "ISO-8859-1";

    private final GameObject[] gameItems;

    private final TextObject statusTextItem;
    
    public Hud(String statusText) throws Exception {

    	FontTexture fontTexture = new FontTexture(FONT,CHARSET);
        this.statusTextItem = new TextObject(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));
        gameItems = new GameObject[]{statusTextItem};
    }
    
    @Override
    public GameObject[] getGameObjects() {
        return gameItems;
    }

}