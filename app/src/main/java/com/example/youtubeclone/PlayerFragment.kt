package com.example.youtubeclone

import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubeclone.Adapter.VideoAdapter
import com.example.youtubeclone.Dto.VideosDto
import com.example.youtubeclone.Model.VideoModel
import com.example.youtubeclone.databinding.FragmentPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.DefaultDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private var binding: FragmentPlayerBinding? = null
    private val videoAdapter = VideoAdapter(callback = { url, title ->
        play(url, title)
    })
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)

        initMotionLayoutEvent(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)
        initPlayer(fragmentPlayerBinding)
        initControlButton(fragmentPlayerBinding)
        initClearBtn(fragmentPlayerBinding)
        getVideoListApi()
        binding = fragmentPlayerBinding

    }

    fun play(url: String, title: String) {
        activity?.let {
            it.findViewById<View>(R.id.fragmentContainer).isVisible
        }

        context?.let {
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            player?.let { player ->
                player.setMediaItem(mediaItem)
                player.playWhenReady = playWhenReady
                player.seekTo(currentWindow, playbackPosition)
                player.prepare()
                player.play()
            }
        }

        binding?.let {
            it.playerMotionLayout.visibility = View.VISIBLE
            it.playerMotionLayout.transitionToEnd()
            it.bottomTitleTextview.text = title
            it.bottomPlayerControlBtn.setColorFilter(R.color.black)
        }

    }

    private fun initMotionLayoutEvent(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playerMotionLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                binding?.let {
                    (activity as MainActivity).also { mainActivity ->
                        mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress =
                            abs(progress)
                    }
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
    }

    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.fragmentRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayer(fragmentPlayerBinding: FragmentPlayerBinding) {

        context?.let {
            player = ExoPlayer.Builder(it)
                .build()
                .also { player ->
                    fragmentPlayerBinding.playerView.player = player
                }
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    binding?.let {
                        if (isPlaying) {
                            it.bottomPlayerControlBtn.setImageResource(R.drawable.ic_baseline_pause_24)
                        } else {
                            it.bottomPlayerControlBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                        }
                    }
                }
            })
        }

    }
    private fun initControlButton(fragmentPlayerBinding: FragmentPlayerBinding){
        fragmentPlayerBinding.bottomPlayerControlBtn.setOnClickListener {
            val player = this.player?: return@setOnClickListener
            if (player.isPlaying){
                player.pause()
            }else{
                player.play()
            }
        }
    }

    private fun initClearBtn(fragmentPlayerBinding: FragmentPlayerBinding){
        fragmentPlayerBinding.bottomPlayerClearBtn.setOnClickListener {
            binding?.let {
                it.playerMotionLayout.visibility = View.INVISIBLE
                player?.let {
                    it.release()
                }
            }
        }
    }
    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentMediaItemIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

    private fun getVideoListApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            it.getItemList().enqueue(object : Callback<VideosDto> {
                override fun onResponse(call: Call<VideosDto>, response: Response<VideosDto>) {
                    if (response.isSuccessful) {
                        response.body()?.let { dto ->
                            videoAdapter.submitList(dto.videos)
                        }
                    }
                }

                override fun onFailure(call: Call<VideosDto>, t: Throwable) {
                    Log.d("testt", t.message.toString())
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }
}