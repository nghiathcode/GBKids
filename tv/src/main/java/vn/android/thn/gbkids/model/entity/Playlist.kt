package vn.android.thn.gbkids.model.entity

import vn.android.thn.gbkids.model.db.VideoTable
import java.util.ArrayList

class Playlist() {
    private var playlist: MutableList<VideoTable>
    private var currentPosition: Int = 0

    init{
        playlist = ArrayList<VideoTable>()
        currentPosition = 0
    }

    /**
     * Clears the videos from the playlist.
     */
    fun clear() {
        playlist.clear()
    }

    /**
     * Adds a video to the end of the playlist.
     *
     * @param video to be added to the playlist.
     */
    fun add(video: VideoTable) {
        playlist.add(video)
    }

    /**
     * Sets current position in the playlist.
     *
     * @param currentPosition
     */
    fun setCurrentPosition(currentPosition: Int) {
        this.currentPosition = currentPosition
    }

    /**
     * Returns the size of the playlist.
     *
     * @return The size of the playlist.
     */
    fun size(): Int {
        return playlist.size
    }

    /**
     * Moves to the next video in the playlist. If already at the end of the playlist, null will
     * be returned and the position will not change.
     *
     * @return The next video in the playlist.
     */
    operator fun next(): VideoTable? {
        if (currentPosition + 1 < size()) {
            currentPosition++
            return playlist[currentPosition]
        }
        return null
    }

    /**
     * Moves to the previous video in the playlist. If the playlist is already at the beginning,
     * null will be returned and the position will not change.
     *
     * @return The previous video in the playlist.
     */
    fun previous(): VideoTable? {
        if (currentPosition - 1 >= 0) {
            currentPosition--
            return playlist[currentPosition]
        }
        return null
    }
}