package me.hyuck.antdefense.command

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import java.util.*

class RingCommand(private var radius: Int, var elementWidth: Int, var elementHeight: Int, private var expandSpeed: Int) {

    private val ringElements = ArrayList<RingElement>()
    private var ringPosX = 50F
    private var ringPosY = 50F
    var imgBackGround: Bitmap? = null
    var bgHeight = radius * 2
    var bgWidth = radius * 2
    private var bgAlpha = 0

    fun setPos(x: Float, y: Float) {
        ringPosX = x
        ringPosY = y
    }
    /**
     * Add ring command element.
     * @param name Ring command element's name. Use 'checkRingCommandTouched' to get this name in specific position.
     * @param image Ring command element's image(Bitmap)
     */
    fun add(name: String, image: Bitmap) {
        val ringElement = RingElement()
        ringElement.name = name
        ringElement.elementImage = image
        ringElement.width = elementWidth
        ringElement.height = elementHeight

        ringElements.add(ringElement)
    }

    /**
     * 선택한 링커맨드의 이름 체크
     */
    fun checkRingCommandTouched(x: Float, y:Float) : String? {
        ringElements.forEach {
            val posX = ringPosX + it.posX
            val posY = ringPosY + it.posY
            if ( x >= posX - it.width / 2 && x <= posX + it.width / 2 && y >= posY - it.height / 2 && y <= posY + it.height / 2 ) {
                return it.name
            }
        }

        return null
    }


    /**
     * 이미지 리소스 변경
     * */
    fun changeImage(name: String, image: Bitmap) {
        ringElements.forEach {
            if ( it.name.equals(name) ) {
                it.elementImage = image
            }
        }
    }

    /**
     * To recycle RingCommand object.
     * This may reset every ring command element position to 0.
     */
    fun resetAnimation() {
        ringElements.forEach {
            it.curPosX = 0.0
            it.curPosY = 0.0
        }
        bgAlpha = 0
    }

    /**
     * 링커맨드 보이기
     * */
    fun showRing(canvas: Canvas) {
        val numberOfElements = ringElements.size
        val width = canvas.width
        val height = canvas.height

        imgBackGround?.let {
            val paint = Paint()
            bgAlpha += expandSpeed
            if ( bgAlpha <= 100 ) {
                paint.alpha = bgAlpha
            }
            val scaledImage = Bitmap.createScaledBitmap(it, bgWidth, bgHeight, false)
            canvas.drawBitmap(scaledImage, ringPosX - bgWidth / 2, ringPosY - bgHeight / 2, paint)
        }

        /*for (i in 0 .. numberOfElements) {
            var degree = 0
            if ( ringPosY - radius < 0 && ringPosX - radius < 0 ) {
                degree = 90 / (numberOfElements - 1) * i
            } else if ( ringPosY - radius < 0 && ringPosX + radius > width ) {
                degree = 90 + 90 / (numberOfElements - 1) * i
            } else if ( ringPosY + radius > height && ringPosX - radius < 0 ) {
                degree = 90 - 90 / (numberOfElements - 1) * i - 90
            } else if ( ringPosY + radius > height && ringPosX + radius > width ) {
                degree = 90 + 90 / (numberOfElements - 1) * i + 90
            } else if ( ringPosY - radius < 0 ) {
                degree = 180 - 180 / (numberOfElements - 1) * i
            } else if ( ringPosY + radius > height ) {
                degree = 180 + 180 / (numberOfElements - 1) * i
            } else if ( ringPosX - radius < 0 ) {
                degree = 180 / (numberOfElements - 1) * i - 90
            } else if ( ringPosX + radius > width ) {
                degree = 180 - 180 / (numberOfElements - 1) * i + 90
            } else {
                degree = 360 / numberOfElements * (i + 1) + 90
            }

            val radian = degree * (Math.PI / 180)

        }*/

        ringElements.forEachIndexed { i, r ->
            var degree = 360 / numberOfElements * (i + 1) + 90

            if ( ringPosY - radius < 0 && ringPosX - radius < 0 ) {
                degree = 90 / (numberOfElements - 1) * i
            } else if ( ringPosY - radius < 0 && ringPosX + radius > width ) {
                degree = 90 + 90 / (numberOfElements - 1) * i
            } else if ( ringPosY + radius > height && ringPosX - radius < 0 ) {
                degree = 90 - 90 / (numberOfElements - 1) * i - 90
            } else if ( ringPosY + radius > height && ringPosX + radius > width ) {
                degree = 90 + 90 / (numberOfElements - 1) * i + 90
            } else if ( ringPosY - radius < 0 ) {
                degree = 180 - 180 / (numberOfElements - 1) * i
            } else if ( ringPosY + radius > height ) {
                degree = 180 + 180 / (numberOfElements - 1) * i
            } else if ( ringPosX - radius < 0 ) {
                degree = 180 / (numberOfElements - 1) * i - 90
            } else if ( ringPosX + radius > width ) {
                degree = 180 - 180 / (numberOfElements - 1) * i + 90
            }

            val radian = degree * (Math.PI / 180)
            r.posX = (radius * Math.cos(radian)).toFloat()
            r.posY = (radius * Math.sin(radian)).toFloat()

            if (Math.sqrt(Math.pow(r.curPosX, 2.0) + Math.pow(r.curPosY, 2.0)) < radius) {
                r.curPosX += (expandSpeed * Math.cos(radian)).toFloat()
                r.curPosY += (expandSpeed * Math.sin(radian)).toFloat()
            } else {
                r.expanded = true
            }

            val scaledImage = Bitmap.createScaledBitmap(r.elementImage!!, r.width, r.height, false)
            canvas.drawBitmap(scaledImage, (ringPosX + r.curPosX - r.width / 2).toFloat(), (ringPosY + r.curPosY - r.height / 2).toFloat(), null)
        }
    }

    private inner class RingElement {
        internal var name: String? = null
        internal var elementImage: Bitmap? = null
        internal var curPosX: Double = 0.0
        internal var curPosY: Double = 0.0
        internal var posX: Float = 0.toFloat()
        internal var posY: Float = 0.toFloat()
        internal var width: Int = 0
        internal var height: Int = 0
        internal var expanded = false
    }
}