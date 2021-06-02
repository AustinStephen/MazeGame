import java.util.TimerTask;

public class gameTimerTask extends TimerTask {
  private GameWindow targetGame;
  
  public gameTimerTask(GameWindow game)
  {
    targetGame = game;
  }

  @Override
  public void run() {
    if(targetGame.isGameRunning()) targetGame.incTimeSeconds();
  }

}
