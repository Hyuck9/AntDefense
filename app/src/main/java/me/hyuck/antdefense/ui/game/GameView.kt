package me.hyuck.antdefense.ui.game

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * 게임 화면을 그릴 SurfaceView
 */
class GameView(private val mContext: Context, attrs: AttributeSet) : SurfaceView(mContext, attrs), SurfaceHolder.Callback {

    private var mGameThread: GameThread? = null

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (mGameThread == null) {
            // start
            mWidth = getWidth()
            mHeight = getHeight()
            mGameThread = GameThread(mContext, holder, mWidth, mHeight)
            mGameThread!!.start()
        } else {
            // resume
            mGameThread!!.pauseThread(false)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    /** 게임 종료  */
    fun stopGame() {
        mGameThread!!.stopThread()
    }

    /** 게임 일시중지 OR 게임 재개  */
    fun pauseGame(wait: Boolean) {
        mGameThread!!.pauseThread(wait)
    }

    /** 게임 다시시작  */
    fun restartGame() {
        mGameThread!!.stopThread()
        mGameThread = null
        mGameThread = GameThread(mContext, holder, mWidth, mHeight)
        mGameThread!!.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val x = event.x
        val y = event.y

        when( action and MotionEvent.ACTION_MASK ) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {
                mGameThread!!.touchUp(x, y)
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_POINTER_DOWN -> {

            }
        }
        return true
    }
}
