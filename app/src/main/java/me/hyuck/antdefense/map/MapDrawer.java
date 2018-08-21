package me.hyuck.antdefense.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import org.jetbrains.annotations.Contract;

import me.hyuck.antdefense.utils.Values;

public class MapDrawer {

    /** 맵 ( 현재는 1스테이지만.. ) */
    // { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    // { 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
    // { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0 },
    // { 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0 },
    // { 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0 },
    // { 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0 },
    // { 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 },
    // { 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 },
    // { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1 },
    // { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }
    private int[][] map = Values.STAGE1_MAP;

    /** 현재는 1만 움직일 수 있는 타일, 추후 추가 예정 */
    private int[] movableTiles = { 1 };

    /** 직선 길 */
    private Bitmap tile0;
    /** 커브 길 */
    private Bitmap tile1;

    /** 타일 크기 */
    private int tileSizeX = 50;
    private int tileSizeY = 50;


    public void setMap(int[][] map) {
        this.map = map;
    }

    public void setTile0(Bitmap tile0) {
        this.tile0 = tile0;
    }

    public void setTile1(Bitmap tile1) {
        this.tile1 = tile1;
    }

    public void setMovableTiles(int[] movableTiles) {
        this.movableTiles = movableTiles;
    }

    public void setTileSize(int tileSizeX, int tileSizeY) {
        this.tileSizeX = tileSizeX;
        this.tileSizeY = tileSizeY;
    }


    /**
     * 입력받은 타일이 지나갈 수 있는 길인지 확인
     * input에 해당하는 타일이 지나갈 수 있는 타일이면 true, 아니면 false
     * ex ) Enemy가 지나가는 길 -> 1
     *      Ant가 설치되는 땅 -> 0
     *      movableTiles의 값이 { 1 } 일 때
     *      input 값이 1이면 true, 1이 아니면 false 반환
     * */
    private boolean isMovableTile(int input) {
        boolean movable = false;
        for( int i = 0; i<movableTiles.length; i++ ) {
            if(movableTiles[i] == input) movable = true;
        }
        return movable;
    }

    /**
     * 위쪽 타일이 갈 수 있는 길인지 체크
     */
    private boolean checkUp(int x, int y) {
        return ( y > 0 ) && isMovableTile(map[y - 1][x]);
    }

    /**
     * 아래쪽 타일이 갈 수 있는 길인지 체크
     */
    private boolean checkDown(int x, int y) {
        return ( y < map.length - 1 ) && isMovableTile(map[y + 1][x]);
    }

    /**
     * 왼쪽 타일이 갈 수 있는 길인지 체크
     */
    private boolean checkLeft(int x, int y) {
        return ( x > 0 ) && isMovableTile(map[y][x - 1]);
    }

    /**
     * 오른쪽 타일이 갈 수 있는 길인지 체크
     */
    private boolean checkRight(int x, int y) {
        return ( x < map[0].length - 1 ) && isMovableTile(map[y][x + 1]);
    }

    /** 이미지 회전 및 뒤집어 그리기 */
    private void drawMatrix(Canvas canvas, Bitmap tile, Matrix matrix, int x, int y) {
        Bitmap img = Bitmap.createBitmap(tile, 0, 0, tile.getWidth(), tile.getHeight(), matrix, true);
        Bitmap scaledImage = Bitmap.createScaledBitmap(img, tileSizeX, tileSizeY, false);
        canvas.drawBitmap(scaledImage, tileSizeX * x, tileSizeY * y, null);
    }

    /**
     * 맵 그리기
     * 맨 왼쪽 위칸에서 시작 -> 맨 아래 오른쪽 칸에서 끝 나는 단일 경로 길
     * ( 갈림길 없음 )
     * */
    public Bitmap drawMap(int width, int height) {
        Bitmap straightTile = Bitmap.createScaledBitmap(tile0, tileSizeX, tileSizeY, false);
        Bitmap curveTile = Bitmap.createScaledBitmap(tile1, tileSizeX, tileSizeY, false);
        Bitmap road = Bitmap.createBitmap(width, height, tile0.getConfig());
        Canvas canvas = new Canvas(road);

        int lastX = map[0].length - 1;
        int lastY = map.length - 1;
        Matrix matrix;

        for ( int y = 0; y < map.length; y++ ) {
            for ( int x = 0; x < map[0].length; x++ ) {
                // 이동할 수 있는 타일일 경우 길 그리기
                if ( isMovableTile(map[y][x]) ) {
                    // 한번 Rotate나 Scale을 했을 경우를 위해 매트릭스 초기화
                    matrix = new Matrix();
                    if (
                            ( y == 0 && x == 0 && checkRight(x, y) ) ||                                     // 첫번째 타일에서 오른쪽에 길이 있을 경우
                            ( y == lastY && x == lastX && checkLeft(x, y) ) ||                              // 마지막 타일에서 왼쪽에 길이 있을 경우
                            ( !checkUp(x, y) && !checkDown(x, y) && checkLeft(x, y) && checkRight(x, y) )   // 위아래 길 없고 양옆에 길이 있을 경우
                        ) {
                        // 가로 직선 길
                        canvas.drawBitmap(straightTile, tileSizeX * x, tileSizeY * y, null);
                    }
                    else if (
                            ( y == 0 && x == 0 && checkDown(x, y) ) ||                                      // 첫번째 타일에서 아래쪽에 길이 있을 경우
                            ( y == lastY && x == lastX && checkUp(x, y) ) ||                                // 마지막 타일에서 위쪽에 길이 있을 경우
                            ( checkUp(x, y) && checkDown(x, y) && !checkLeft(x, y) && !checkRight(x, y) )   // 양옆에 길 없고 위아래 길이 있을 경우
                            ) {
                        // 세로 직선 길
                        matrix.postRotate(90.0F);
                        drawMatrix(canvas, tile0, matrix, x, y);
                    }
                    // 첫번째, 마지막 타일 제외됨.
                    else if (
                            ( checkUp(x, y) && !checkDown(x, y) && !checkLeft(x, y) && checkRight(x, y) )   // 아래/왼쪽 길 없고, 위/오른쪽 길이 있을 경우
                            ) {
                        // ( └ ) 모양 커브길
                        canvas.drawBitmap(curveTile, this.tileSizeX * x, this.tileSizeY * y, null);
                    }
                    else if (
                            ( checkUp(x, y) && !checkDown(x, y) && checkLeft(x, y) && !checkRight(x, y) )   // 아래/오른쪽 길 없고, 위/왼쪽 길이 있을 경우
                            ) {
                        // ( ┘ ) 모양 커브길
                        matrix.setScale(-1.0F, 1.0F);
                        drawMatrix(canvas, tile1, matrix, x, y);
                    }
                    else if (
                            ( !checkUp(x, y) && checkDown(x, y) && checkLeft(x, y) && !checkRight(x, y) )   // 아래/오른쪽 길 없고, 위/왼쪽 길이 있을 경우
                            ) {
                        // ( ┐ ) 모양 커브길
                        matrix.setScale(-1.0F, -1.0F);
                        drawMatrix(canvas, tile1, matrix, x, y);
                    }
                    else if (
                            ( !checkUp(x, y) && checkDown(x, y) && !checkLeft(x, y) && checkRight(x, y) )   // 아래/오른쪽 길 없고, 위/왼쪽 길이 있을 경우
                            ) {
                        // ( ┌ ) 모양 커브길
                        matrix.setScale(1.0F, -1.0F);
                        drawMatrix(canvas, tile1, matrix, x, y);
                    }

                }
            }
        }

        return road;
    }

}
