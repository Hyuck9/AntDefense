package me.hyuck.antdefense.ui.game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import me.hyuck.antdefense.R;
import me.hyuck.antdefense.command.RingCommand;
import me.hyuck.antdefense.map.MapDrawer;
import me.hyuck.antdefense.map.path.PathMaker;
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

    /** 게임 상태 */
    private boolean isRun = true;
    private boolean isWait = false;

    /** 리소스 */
    private Bitmap imgBackGround;
    private Bitmap imgRoad;
    private Bitmap imgRcAttackRange;
    private Bitmap imgRcBasic;
    private Bitmap imgRcCancel;
    private Bitmap imgRcLevelMax;
    private Bitmap[] imgRcSell = new Bitmap[4];
    private Bitmap[] imgRcRifle = new Bitmap[3];
    private Bitmap[] imgRcSniper = new Bitmap[3];
    private Bitmap[] imgRcSplash = new Bitmap[3];

    /** 링커맨드 */
    private boolean isRingCommandShowing = false;
    private int ringCommandIndex = 0;
    private RingCommand[] ringCommands = new RingCommand[4];

    /** 터치 관련 */
    private int touchX, touchY;
    private int touchMapX, touchMapY, touchMapType;
    private float lastTouchX, lastTouchY;

    PathMaker pathMaker;

    /** Constructor */
    GameThread(Context context, SurfaceHolder holder, int width, int height) {
        mContext = context;
        mHolder = holder;
        mWidth = width;
        mHeight = height;

        pathMaker = new PathMaker(width, height);

        createBitmap();
        drawMap();
        createRingCommand();
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

        // 링커맨드 관련 이미지 초기화
        imgRcBasic = BitmapFactory.decodeResource(resources, R.drawable.rc_basic);
        imgRcCancel = BitmapFactory.decodeResource(resources, R.drawable.rc_cancel);
        imgRcLevelMax = BitmapFactory.decodeResource(resources, R.drawable.rc_levmax);
        for ( int i = 0; i < 3; i++ ) {
            imgRcRifle[i] = BitmapFactory.decodeResource(resources, R.drawable.rc_rifle_01 + i);
            imgRcSplash[i] = BitmapFactory.decodeResource(resources, R.drawable.rc_splash_01 + i);
            imgRcSniper[i] = BitmapFactory.decodeResource(resources, R.drawable.rc_sniper_01 + i);
        }
        for ( int i = 0; i < 4; i++ ) {
            imgRcSell[i] = BitmapFactory.decodeResource(resources, R.drawable.rc_sell_01 + i);
        }
        imgRcAttackRange = BitmapFactory.decodeResource(resources, R.drawable.rc_attackrange);

    }

    /** 이미지 리소스 해제 */
    private void recycleBitmap() {
        // 백그라운드 이미지 해제
        imgBackGround.recycle();
        imgBackGround = null;
        imgRoad.recycle();
        imgRoad = null;
        imgRcBasic.recycle();
        imgRcBasic = null;
        imgRcCancel.recycle();
        imgRcCancel = null;
        imgRcLevelMax.recycle();
        imgRcLevelMax = null;
        for ( int i = 0; i < 3; i++ ) {
            imgRcRifle[i].recycle();
            imgRcRifle[i] = null;
            imgRcSplash[i].recycle();
            imgRcSplash[i] = null;
            imgRcSniper[i].recycle();
            imgRcSniper[i] = null;
        }
        for ( int i = 0; i < 4; i++ ) {
            imgRcSell[i].recycle();
            imgRcSell[i] = null;
        }
        imgRcAttackRange.recycle();
        imgRcAttackRange = null;

    }

    /** 링커맨드 셋팅 */
    private void createRingCommand() {
        int w = mWidth / 10;
        int distance = mWidth / 15;

        for ( int i = 0; i < 4; i++ ) {
            ringCommands[i] = new RingCommand(distance, w, w, 15);
        }
        ringCommands[0].setElementWidth((int) (w * 1.5D));
        ringCommands[0].add("basic", imgRcBasic);
        ringCommands[0].add("cancel", imgRcCancel);

        ringCommands[1].add("rifle", imgRcRifle[0]);
        ringCommands[1].add("splash", imgRcSplash[0]);
        ringCommands[1].add("sniper", imgRcSniper[0]);
        ringCommands[1].setElementWidth((int) (w * 1.5D));
        ringCommands[1].add("sell", imgRcSell[0]);

        ringCommands[2].add("level_up", imgRcRifle[1]);
        ringCommands[2].setElementWidth((int) (w * 1.5D));
        ringCommands[2].add("sell", imgRcSell[1]);

        ringCommands[3].setElementWidth((int) (w * 1.5D));
        ringCommands[3].add("max", imgRcLevelMax);
        ringCommands[3].add("sell", imgRcSell[3]);
    }

    /** 링커맨드 활성화 시 타일 구분선 그리기 */
    private void drawMapTileLine(Canvas canvas) {
        for ( int x = 1; x < 20; x++ ) {
            Paint paint = new Paint();
            canvas.drawLine(x * pathMaker.getTileW(), 0, x * pathMaker.getTileW(), mHeight, paint);
        }
        for ( int y = 1; y < 10; y++ ) {
            Paint paint = new Paint();
            canvas.drawLine(0, y * pathMaker.getTileH(), mWidth, y * pathMaker.getTileH(), paint);
        }
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
        mapDrawer.setTileWidth(pathMaker.getTileW());
        mapDrawer.setTileHeight(pathMaker.getTileH());
        imgRoad = mapDrawer.drawMap(mWidth, mHeight);
        tile0.recycle();
        tile1.recycle();
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

        if ( isRingCommandShowing ) {
            drawMapTileLine(canvas);
            ringCommands[ringCommandIndex].showRing(canvas);
        }

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

    /** 터치업 이벤트 */
    public void touchUp(float x, float y) {
        Log.d("lhg", "touch - x : "+x+", y : "+y);
        /* 링커맨드가 보여져 있을 때 */
        if ( isRingCommandShowing ) {

            String command = ringCommands[ringCommandIndex].checkRingCommandTouched(x, y);

            switch ( ringCommandIndex ) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
            ringCommands[ringCommandIndex].resetAnimation();
            isRingCommandShowing = false;
        } else {
            /* 터치한 타일 계산 */
            for (int tileX = 0; tileX < pathMaker.getMapX(); tileX++ ) {
                for (int tileY = 0; tileY < pathMaker.getMapY(); tileY++ ) {
                    if ( pathMaker.getTileRect() != null &&
                            pathMaker.getTileRect()[tileX][tileY]
                                    .contains((int) x, (int) y) ) {
                        touchX = tileX * pathMaker.getTileW() + pathMaker.getTileW() / 2;
                        touchY = tileY * pathMaker.getTileH() + pathMaker.getTileH() / 2;
                        touchMapX = tileX;
                        touchMapY = tileY;
                        touchMapType = pathMaker.getMap()[touchMapY][touchMapX];
                    }
                }
            }

            /* 빌드 가능한 땅일 때 */
            if ( touchMapType == 0 ) {
                ringCommandIndex = 0;
                isRingCommandShowing = true;
                Log.d("lhg", "touchX : "+touchX);
                Log.d("lhg", "touchY : "+touchY);
                ringCommands[0].setPos(touchX, touchY);
            }

        }
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
