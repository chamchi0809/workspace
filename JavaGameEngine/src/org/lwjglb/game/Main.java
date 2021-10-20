package org.lwjglb.game;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import org.lwjgl.Version;
import static org.lwjgl.glfw.Callbacks.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjglb.engine.IGameLogic;
import org.lwjglb.engine.GameEngine;
import org.lwjglb.engine.Window;
import org.lwjglb.game.Renderer;
import org.lwjglb.engine.Timer;

import org.lwjglb.game.DummyGame;

public class Main {

    public static void main(String[] args) {
        try {
        	boolean vSync = true;
        	IGameLogic gameLogic = new DummyGame();
        	GameEngine gameEng = new  GameEngine("Game", 600, 480, vSync,gameLogic);
        	gameEng.start();
        }catch(Exception excp) {
        	System.exit(-1);
        }
    }

}