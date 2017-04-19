package retrorunner;

import java.awt.Graphics;
import java.awt.image.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import retrorunner.display.Display;
import retrorunner.input.KeyManager;
import retrorunner.speech.Speech;
import retrorunner.state.*;
import retrorunnner.gfx.*;

public class Game implements Runnable
{

    private Display display;
    Thread thread;

    public int width, height;
    int x;
    int r;
    public String title;

    private BufferStrategy bs;
    private Graphics g;

    private State gameState;
    private State menuState;

    //input
    KeyManager keyManager;
    
    //speech
    private Speech speech;

    boolean running = false;

    public Game(String title, int width, int height)
    {
        this.width = width;
        this.height = height;
        this.title = title;

        keyManager = new KeyManager();
    }

    private void init()
    {
        display = new Display(title, width, height);
        display.getFrame().addKeyListener(keyManager);
        Assets.init();

        menuState = new MenuState(this);
        State.setState(menuState);
        speech = new Speech();
    }

    private void tick()
    {
        keyManager.tick();
        if (State.getState() != null) {
            State.getState().tick();
        }
    }

    private void render()
    {
        bs = display.getCanvas().getBufferStrategy();
        if (bs == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }

        g = bs.getDrawGraphics();

        //clearing screen
        g.clearRect(0, 0, width, height);

        //drawing start
        if (State.getState() != null) {
            State.getState().render(g);
        }

        //drawing end
        bs.show();
        g.dispose();

    }

    public void run()
    {
        init();

        int fps = 60;
        double timePerTick = 1000000000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();

        while (running) {
            now = System.nanoTime();
            delta += (now - lastTime) / timePerTick;
            lastTime = now;
            if (delta >= 1) {
                tick();
                render();
                delta--;
            }

        }
        stop();
    }

    public KeyManager getKeyManager()
    {
        return keyManager;
    }
    
    public Speech getSpeech()
    {
        return speech;
    }

    public synchronized void start()
    {
        if (running) {
            return;
        }

        running = true;
        thread = new Thread(this, "runner");
        thread.start();
    }

    public synchronized void stop()
    {
        if (running == false) {
            return;
        }

        running = false;
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
