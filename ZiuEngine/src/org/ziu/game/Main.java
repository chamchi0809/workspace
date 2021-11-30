package org.ziu.game;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import org.lwjgl.Version;
import static org.lwjgl.glfw.Callbacks.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.ziu.engine.GameEngine;
import org.ziu.engine.IGameScene;
import org.ziu.engine.Timer;
import org.ziu.engine.Window;
import org.ziu.engine.graphics.Renderer;
import org.ziu.game.DemoScene01;

public class Main {

    public static void main(String[] args) {
        try {
        	boolean vSync = true;
        	IGameScene gameScene = new DemoScene01();
        	GameEngine gameEng = new  GameEngine("Game", 900, 500, vSync,gameScene);        	
        	gameEng.start();
        }catch(Exception excp) {
        	System.exit(-1);
        }
    }

}