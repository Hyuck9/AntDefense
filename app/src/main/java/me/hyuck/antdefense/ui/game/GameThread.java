package me.hyuck.antdefense.ui.game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import me.hyuck.antdefense.R;
import me.hyuck.antdefense.map.MapDrawer;
import me.hyuck.antdefense.utils.Values;

public class GameThread extends Thread {

    private Context mContext;
    private final SurfaceHolder mHolder;

    /** 화면 가로/세로 */
    private int mWidth, mHeight;

    /** 게임 속도 */
    private int gameSpeed;

    /** FPS 설정 관련 */
    private long lastTime;

    private boolean isRun = true;
    private boolean isWait = false;

    /** 리소스 */
    private Bitmap imgBackGround;
    private Bitmap imgRoad;

    /** Constructor */
    GameThread(Context context, SurfaceHolder holder, int width, int height) {
        mContext = context;
        mHolder = holder;
        mWidth = width;
        mHeight = height;

        createBitmap();
        drawMap();
        lastTime = System.currentTimeMillis();
    }

    /** 이미지 리소스 초기화 */
    private void createBitmap() {
        Resources resources = mContext.getResources();
        Bitmap image;

        // 백그라운드 이미지 초기화
        image = BitmapFactory.decodeResource(resources, R.drawable.game_back);
        imgBackGround = Bitmap.createScaledBitmap(image, mWidth, mHeight, true);
        image.recycle();
    }

    /** 지도 그리기 */
    private void drawMap() {
        MapDrawer mapDrawer = new MapDrawer();
        mapDrawer.setMap(Values.STAGE1_MAP);
        mapDrawer.setMovableTiles(new int[] { 1 });
        Bitmap tile0 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.tile0);
        Bitmap tile1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.tile1);
        mapDrawer.setTile0(tile0);
        mapDrawer.setTile1(tile1);
        mapDrawer.setTileWidth(mWidth / Values.STAGE1_MAP[0].length);
        mapDrawer.setTileHeight(mHeight / Values.STAGE1_MAP.length);
        imgRoad = mapDrawer.drawMap(mWidth, mHeight);
        tile0.recycle();
        tile1.recycle();

    }

    /** 이미지 리소스 해제 */
    private void recycleBitmap() {
        // 백그라운드 이미지 해제
        imgBackGround.recycle();
        imgBackGround = null;
        imgRoad.recycle();
        imgRoad = null;

    }

    /**
     * FPS 조정
     * Frame Per Second = 25        ( 1초에 25프레임 )
     * Time Per Frame = 1000 / 25   ( 40ms에 한번씩 그리기 )
     * (현재 실행 시간 - 이전 실행 시간)이 40ms보다 작으면 Sleep
     * */
    private void adjustFPS() {
        int frameTime = Values.TIME_PER_FRAME;
        long currentTime = System.currentTimeMillis();
        long timeGap = currentTime - lastTime;
        long sleepTime = frameTime - timeGap;
        lastTime = currentTime;

        if ( sleepTime > 0 ) {
            try {
                Thread.sleep(sleepTime);
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        } else {
            for ( int skippedFrame = 0; skippedFrame < 5; skippedFrame++ ) {
                for ( int i = 0; i < gameSpeed; i++ ) {
                    doMake();
                }
                sleepTime += frameTime;
            }
        }
        /*int skippedFrame = 0;
        while ( sleepTime < 0 && skippedFrame++ < 5 ) {
            for ( int i = 0; i < gameSpeed; i++ ) {
                doMake();
            }
            sleepTime += frameTime;
        }*/
    }

    /** 객체 자동 생성 */
    private void makeAll() {

    }

    /** 객체들의 모든 움직임 및 라이프사이클 */
    private void moveAll() {

    }

    /** 공격 범위 및 타격 범위 계산 */
    private void attackedAll() {

    }

    /** 그림을 그리기 전에 모든 객체를 생성, 움직임, 계산 등 설정 */
    private void doMake() {
        makeAll();
        moveAll();
        attackedAll();
    }

    /** Surface에서 받아온 Canvas에 모든 객체 그리기 */
    private void drawAll(Canvas canvas) {
        canvas.save();

        canvas.drawBitmap(imgBackGround, 0, 0, null);
        canvas.drawBitmap(imgRoad, 0, 0, null);

        canvas.restore();

    }

    @Override
    public void run() {
        Canvas canvas;
        while ( isRun ) {
            canvas = mHolder.lockCanvas();   // canvas에 그리기 위해 lockCanvas를 얻어옴
            try {
                synchronized ( mHolder ) {
                    // SurfaceHolder가 잡고있는 Surface에 동기식으로 업데이트가 진행되도록 그림을 그림
                    for ( int i = 0; i < gameSpeed; i++ ) {
                        doMake();
                    }
                    adjustFPS();
                    drawAll(canvas);
                }
            } finally {
                if ( canvas != null ) {
                    // Surface의 수정작업을 마무리.
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }

            // Pause
            synchronized ( this ) {
                if ( isWait ) {
                    try {
                        wait();
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }

        }

        recycleBitmap();
    }

    /** 스레드 종료 -> 리소스 해제 */
    public void stopThread() {
        isRun = false;
        synchronized ( this ) {
            this.notify();
        }
    }

    /** 스레드 일시중지 OR 재개 */
    public void pauseThread(boolean wait) {
        isWait = wait;
        synchronized ( this ) {
            this.notify();
        }
    }

}
