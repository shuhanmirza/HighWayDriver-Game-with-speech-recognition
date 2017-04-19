package retrorunner.entity;

import java.awt.Graphics;
import retrorunner.Game;
import retrorunnner.gfx.Assets;

public class Player extends Entity
{

    private Game game;

    float x, y;

    public Player(float x, float y, Game game)
    {
        super(x, y);
        this.x = x;
        this.y = y;
        this.game = game;
        game.getSpeech().r = null;
    }

    public void tick()
    {     
        if("left".equals(game.getSpeech().r))
        {
            x = 150;
            game.getSpeech().r = null;
        }
        
        else if("right".equals(game.getSpeech().r))
        {
            x = 250;
            game.getSpeech().r = null;
        }
    }

    public void render(Graphics g)
    {
        g.drawImage(Assets.imgPlayerCar, (int) x, (int) y, null);
        //System.out.println(x);
    }

    public int getX()
    {
       return (int)x;
    }

    public int getY()
    {
        return (int)y;
    }

}
