package me.hyuck.antdefense.utils;

public class Values {

    /** 게임 시작 재화, 생명 */
    public static final int START_MONEY = 5000;
    public static final int LIFE = 5;

    /** 게임 속도 관련 */
    public static final int GAME_SPEED_NORMAL = 1;
    public static final int GAME_SPEED_FAST = 5;

    /** FPS 관련 */
    public static final int FRAME_PER_SECOND = 25;
    public static final int TIME_PER_FRAME = 1000 / FRAME_PER_SECOND;

    /** 스테이지당 웨이브 수 */
    public static final int WAVES_PER_STAGE = 30;

    /** 1Stage 맵 */
    public final static int[][] STAGE1_MAP = {
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0 },
            { 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 },
            { 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };

    /** 개미 타입 */
    public static final int[] ANT_INDEX = { 100, 200, 300, 400 };
    
    /** 개미 스탯 정보 { 사정거리, 데미지, 연사력, 구매가격(합산) } */
    public static final int[] BASIC_ANT_STATS = { 1, 10, 2, 500 };
    public static final int[][] RIFLE_ANT_STATS = { { 2, 30, 2, 1200 }, { 3, 40, 3, 2200 }, { 4, 50, 6, 3500 } };
    public static final int[][] SPLASH_ANT_STATS = { { 1, 20, 1, 1200 }, { 2, 30, 1, 2200 }, { 2, 40, 1, 3500 } };
    public static final int[][] SNIPER_ANT_STATS = { { 4, 30, 1, 1200 }, { 6, 50, 2, 2200 }, { 8, 70, 2, 3500 } };

    /** 적 스탯 정보 { HP, Speed, Price(얻는 돈) } */
    public static final int[][] ENEMY_STATS = { { 20, 10, 10 }, { 10, 40, 40 }, { 20, 30, 30 }, { 50, 20, 100 }, { 1000, 10, 100 } };

    /** 화면 사이즈 관련 */
    public static int Width = 0;
    public static int Height = 0;
    public static int OnePixel = 0;
}
