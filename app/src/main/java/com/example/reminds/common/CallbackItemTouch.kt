package com.example.reminds.common

interface CallbackItemTouch {
    fun itemTouchOnMove(oldPosition: Int, newPosition: Int)

    fun itemTouchOnMoveFinish()
}