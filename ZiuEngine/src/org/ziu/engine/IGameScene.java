package org.ziu.engine;

public interface IGameScene {

    void init(Window window) throws Exception;

    void input(Window window, MouseInput mouseInput, GameEngine gameEngine);

    void update(float deltaTime, MouseInput mouseInput, GameEngine gameEngine);
    
    void render(Window window);
    
    void cleanup();
}