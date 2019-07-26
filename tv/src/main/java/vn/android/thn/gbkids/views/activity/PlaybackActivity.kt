/*
 * Copyright (c) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.android.thn.gbkids.views.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.MotionEvent
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.views.fragment.PlaybackFragment


/**
 * Loads PlaybackFragment and delegates input from a game controller.
 * <br></br>
 * For more information on game controller capabilities with leanback, review the
 * [docs.
](https://developer.android.com/training/game-controllers/controller-input.html) */
class PlaybackActivity : LeanbackActivity() {
    private var gamepadTriggerPressed = false
    private var mPlaybackFragment: PlaybackFragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        val fragment = supportFragmentManager.findFragmentByTag(getString(R.string.playback_tag))
        if (fragment is PlaybackFragment) {
            mPlaybackFragment = fragment
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
            mPlaybackFragment!!.skipToNext()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
            mPlaybackFragment!!.skipToPrevious()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L2) {
            mPlaybackFragment!!.rewind()
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R2) {
            mPlaybackFragment!!.fastForward()
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        // This method will handle gamepad events.
        if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) > GAMEPAD_TRIGGER_INTENSITY_ON && !gamepadTriggerPressed) {
            mPlaybackFragment!!.rewind()
            gamepadTriggerPressed = true
        } else if (event.getAxisValue(MotionEvent.AXIS_RTRIGGER) > GAMEPAD_TRIGGER_INTENSITY_ON && !gamepadTriggerPressed) {
            mPlaybackFragment!!.fastForward()
            gamepadTriggerPressed = true
        } else if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) < GAMEPAD_TRIGGER_INTENSITY_OFF && event.getAxisValue(
                MotionEvent.AXIS_RTRIGGER
            ) < GAMEPAD_TRIGGER_INTENSITY_OFF
        ) {
            gamepadTriggerPressed = false
        }
        return super.onGenericMotionEvent(event)
    }

    companion object {
        private val GAMEPAD_TRIGGER_INTENSITY_ON = 0.5f
        // Off-condition slightly smaller for button debouncing.
        private val GAMEPAD_TRIGGER_INTENSITY_OFF = 0.45f
    }
}
