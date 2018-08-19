package me.hyuck.antdefense.ui.game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 게임 화면을 그릴 SurfaceView
 * */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread mGameThread;
    private SurfaceHolder mHolder;
    private Context mContext;

    private int mWidth, mHeight;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if ( mGameThread == null ) {
            // start
            mWidth = getWidth();
            mHeight = getHeight();
            mGameThread = new GameThread(mContext, mHolder, mWidth, mHeight);
            mGameThread.start();
        } else {
            // resume
            mGameThread.pauseThread(false);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    /** 게임 종료 */
    public void stopGame() {
        mGameThread.stopThread();
    }

    /** 게임 일시중지 OR 게임 재개 */
    public void pauseGame(boolean wait) {
        mGameThread.pauseThread(wait);
    }

    /** 게임 다시시작 */
    public void restartGame() {
        mGameThread.stopThread();
        mGameThread = null;
        mGameThread = new GameThread(mContext, mHolder, mWidth, mHeight);
        mGameThread.start();
    }


}
