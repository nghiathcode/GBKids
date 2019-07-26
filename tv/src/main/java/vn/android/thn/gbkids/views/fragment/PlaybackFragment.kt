/*
 * Copyright (C) 2017 The Android Open Source Project
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

package vn.android.thn.gbkids.views.fragment

import android.annotation.TargetApi
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v17.leanback.app.VideoSupportFragment
import android.support.v17.leanback.app.VideoSupportFragmentGlueHost
import android.support.v17.leanback.widget.*

import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import vn.android.thn.gbfilm.views.listener.YoutubeStreamListener
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.model.entity.Playlist
import vn.android.thn.gbkids.model.entity.StreamEntity
import vn.android.thn.gbkids.player.VideoPlayerGlue
import vn.android.thn.gbkids.presenter.CardPresenter
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.gbkids.utils.Utils
import vn.android.thn.gbkids.views.services.YoutubeStreamService
import vn.android.thn.library.utils.GBUtils

import java.util.ArrayList


/**
 * Plays selected video, loads playlist and related videos, and delegates playback to [ ].
 */
class PlaybackFragment : VideoSupportFragment(), YoutubeStreamListener {

    private var mPlayerGlue: VideoPlayerGlue? = null
    private var mPlayerAdapter: LeanbackPlayerAdapter? = null
    private var mPlayer: SimpleExoPlayer? = null
    private var mTrackSelector: TrackSelector? = null
    private var mPlaylistActionListener: PlaylistActionListener? = null

    //    private Video mVideo;
    private var mPlaylist: Playlist? = null
    private var videoId: String? = null
    //    private VideoLoaderCallbacks mVideoLoaderCallbacks;
    private val mVideoCursorAdapter: CursorObjectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getInstance().mYoutubeStreamListener = this
        videoId = activity!!.intent.getStringExtra("videoId")
        //        mVideo = getActivity().getIntent().getParcelableExtra(VideoDetailsActivity.VIDEO);
        mPlaylist = Playlist()

        //        mVideoLoaderCallbacks = new VideoLoaderCallbacks(mPlaylist);

        // Loads the playlist.
        //        Bundle args = new Bundle();
        //        args.putString(VideoContract.VideoEntry.COLUMN_CATEGORY, mVideo.category);
        //        getLoaderManager()
        //                .initLoader(VideoLoaderCallbacks.QUEUE_VIDEOS_LOADER, args, mVideoLoaderCallbacks);
        //load play list
        //        mVideoCursorAdapter = setupRelatedVideosCursor();
        activity!!.stopService(Intent(activity, YoutubeStreamService::class.java))
        var intentServer = Intent(activity, YoutubeStreamService::class.java)
        intentServer.putExtra("videoId",videoId)
        activity!!.startService(intentServer)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || mPlayer ==
            null
        ) {
//            initializePlayer()
        }
    }

    /** Pauses the player.  */
    @TargetApi(Build.VERSION_CODES.N)
    override fun onPause() {
        super.onPause()

        if (mPlayerGlue != null && mPlayerGlue!!.isPlaying) {
            mPlayerGlue!!.pause()
        }
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        mTrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        mPlayer = ExoPlayerFactory.newSimpleInstance(activity, mTrackSelector)

        mPlayerAdapter = LeanbackPlayerAdapter(activity, mPlayer, UPDATE_DELAY)
        mPlaylistActionListener = PlaylistActionListener(mPlaylist!!)
        mPlayerGlue = VideoPlayerGlue(activity!!, mPlayerAdapter!!, mPlaylistActionListener!!)
        mPlayerGlue!!.host = VideoSupportFragmentGlueHost(this)
        mPlayerGlue!!.playWhenPrepared()

        //        play("");

//        val mRowsAdapter = initializeRelatedVideosRow()
//        adapter = mRowsAdapter
    }

    private fun releasePlayer() {
        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayer = null
            mTrackSelector = null
            mPlayerGlue = null
            mPlayerAdapter = null
            mPlaylistActionListener = null
        }
    }

    private fun play(video: VideoTable,url:String? = null) {
        if (GBUtils.isEmpty(url)) return
        mPlayerGlue!!.title = video.title
        mPlayerGlue!!.subtitle = video.description
        prepareMediaForPlaying(Uri.parse(url))
        mPlayerGlue!!.play()
    }

    private fun prepareMediaForPlaying(mediaSourceUri: Uri) {
        val userAgent = Util.getUserAgent(activity, "VideoPlayerGlue")
        val mediaSource = ExtractorMediaSource(
            mediaSourceUri,
            DefaultDataSourceFactory(activity!!, userAgent),
            DefaultExtractorsFactory(), null, null
        )

        mPlayer!!.prepare(mediaSource)
    }

    private fun initializeRelatedVideosRow(): ArrayObjectAdapter {
        /*
         * To add a new row to the mPlayerAdapter and not lose the controls row that is provided by the
         * glue, we need to compose a new row with the controls row and our related videos row.
         *
         * We start by creating a new {@link ClassPresenterSelector}. Then add the controls row from
         * the media player glue, then add the related videos row.
         */
        val presenterSelector = ClassPresenterSelector()
        presenterSelector.addClassPresenter(
            mPlayerGlue!!.controlsRow.javaClass, mPlayerGlue!!.playbackRowPresenter
        )
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())

        val rowsAdapter = ArrayObjectAdapter(presenterSelector)

        rowsAdapter.add(mPlayerGlue!!.controlsRow)

        val header = HeaderItem(getString(R.string.related_movies))
        val row = ListRow(header, mVideoCursorAdapter)
        rowsAdapter.add(row)

        setOnItemViewClickedListener(ItemViewClickedListener())

        return rowsAdapter
    }

    //    private CursorObjectAdapter setupRelatedVideosCursor() {
    //        CursorObjectAdapter videoCursorAdapter = new CursorObjectAdapter(new CardPresenter());
    //        videoCursorAdapter.setMapper(new VideoCursorMapper());
    //
    //        Bundle args = new Bundle();
    //        args.putString(VideoContract.VideoEntry.COLUMN_CATEGORY, mVideo.category);
    //        getLoaderManager().initLoader(RELATED_VIDEOS_LOADER, args, mVideoLoaderCallbacks);
    //
    //        return videoCursorAdapter;
    //    }

    fun skipToNext() {
        mPlayerGlue!!.next()
    }

    fun skipToPrevious() {
        mPlayerGlue!!.previous()
    }

    fun rewind() {
        mPlayerGlue!!.rewind()
    }

    fun fastForward() {
        mPlayerGlue!!.fastForward()
    }

    override fun onStartStream() {

    }

    override fun onStream(list_stream: ArrayList<StreamEntity>) {
        var video= VideoTable()
        var density = Utils.getScreen(activity!!)
        video.title = "aaaa"
        video.description = "bbbb"
        if (list_stream.size>0){
            for (stream in list_stream){
                if (stream.quality == density){
                    LogUtils.info("Play_URL_density:",stream.quality.toString())
                    initializePlayer()
                    play(video,stream.url)
                    return
                }
            }
            var obj_steam = StreamEntity("",-1)
            for (stream in list_stream){
                if (stream.quality!= -1 && stream.quality < density){
                    if(obj_steam.quality < stream.quality) {
                        obj_steam = stream
                    }
                }
            }
            LogUtils.info("Play_URL:",obj_steam.quality.toString())
//            if (videoPlay!= null){
//                if (videoPlay is VideoTable){
//                    (videoPlay as VideoTable).save()
//                }
//
//            }
            initializePlayer()
            play(video,obj_steam.url)
        }

    }

    override fun onStreamError() {

    }

    /** Opens the video details page when a related video has been clicked.  */
    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {

            //            if (item instanceof VideoTable) {
            //                VideoTable video = (VideoTable) item;
            //
            //                Intent intent = new Intent(getActivity(), VideoDetailsActivity.class);
            //                intent.putExtra(VideoDetailsActivity.VIDEO, video);
            //
            //                Bundle bundle =
            //                        ActivityOptionsCompat.makeSceneTransitionAnimation(
            //                                        getActivity(),
            //                                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
            //                                        VideoDetailsActivity.SHARED_ELEMENT_NAME)
            //                                .toBundle();
            //                getActivity().startActivity(intent, bundle);
            //            }
        }
    }

    /** Loads a playlist with videos from a cursor and also updates the related videos cursor.  */
    //    protected class VideoLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
    //
    //        static final int RELATED_VIDEOS_LOADER = 1;
    //        static final int QUEUE_VIDEOS_LOADER = 2;
    //
    //        private final VideoCursorMapper mVideoCursorMapper = new VideoCursorMapper();
    //
    //        private final Playlist playlist;
    //
    //        private VideoLoaderCallbacks(Playlist playlist) {
    //            this.playlist = playlist;
    //        }
    //
    //        @Override
    //        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    //            // When loading related videos or videos for the playlist, query by category.
    //            String category = args.getString(VideoContract.VideoEntry.COLUMN_CATEGORY);
    //            return new CursorLoader(
    //                    getActivity(),
    //                    VideoContract.VideoEntry.CONTENT_URI,
    //                    null,
    //                    VideoContract.VideoEntry.COLUMN_CATEGORY + " = ?",
    //                    new String[] {category},
    //                    null);
    //        }
    //
    //        @Override
    //        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    //            if (cursor == null || !cursor.moveToFirst()) {
    //                return;
    //            }
    //            int id = loader.getId();
    //            if (id == QUEUE_VIDEOS_LOADER) {
    //                playlist.clear();
    //                do {
    //                    Video video = (Video) mVideoCursorMapper.convert(cursor);
    //
    //                    // Set the current position to the selected video.
    //                    if (video.id == mVideo.id) {
    //                        playlist.setCurrentPosition(playlist.size());
    //                    }
    //
    //                    playlist.add(video);
    //
    //                } while (cursor.moveToNext());
    //            } else if (id == RELATED_VIDEOS_LOADER) {
    //                mVideoCursorAdapter.changeCursor(cursor);
    //            }
    //        }
    //
    //        @Override
    //        public void onLoaderReset(Loader<Cursor> loader) {
    //            mVideoCursorAdapter.changeCursor(null);
    //        }
    //    }

    internal inner class PlaylistActionListener(private val mPlaylist: Playlist) :
        VideoPlayerGlue.OnActionClickedListener {

        override fun onPrevious() {
            play(mPlaylist.previous()!!)
        }

        override fun onNext() {
            play(mPlaylist.next()!!)
        }
    }

    companion object {

        private val UPDATE_DELAY = 16
    }
}
