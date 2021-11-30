
package org.ziu.engine;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 75;

    public static final int TARGET_UPS = 30;
    
    private MouseInput mouseInput;

    private final Window window;

    private Timer timer;

    private IGameScene gameScene;
    
    private Thread gameLoopThread;

    public GameEngine(String windowTitle, int width, int height, boolean vSync, IGameScene gameScene) throws Exception {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
    	window = new Window(windowTitle, width, height, vSync);
    	mouseInput = new MouseInput();
        this.gameScene = gameScene;
        timer = new Timer();        
    }
    
    public void start() {
    	gameLoopThread.start();
    }
    
    public void cleanup() {    	
    	gameScene.cleanup();
    }
    
    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        }finally {
        	cleanup();
        }
    }
    
    public void MoveScene(IGameScene gameScene) {    	    	
    	gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
    	mouseInput = new MouseInput();
        this.gameScene = gameScene;
        timer = new Timer();      
        start();
    }
    
    protected void init() throws Exception {
        window.init();
        mouseInput.init(window);
        timer.init();
        gameScene.init(window);
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float deltaTime = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input(); //input은 항상 받아옴

            //한UPS 마다 update 실행
            while (accumulator >= deltaTime) {
                update(deltaTime);
                accumulator -= deltaTime;
            }

            render();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    protected void input() {
    	mouseInput.input(window);
        gameScene.input(window, mouseInput, this);
    }

    protected void update(float interval) {
        gameScene.update(interval, mouseInput, this);
    }

    protected void render() {
        gameScene.render(window);
        window.update();
    }
}