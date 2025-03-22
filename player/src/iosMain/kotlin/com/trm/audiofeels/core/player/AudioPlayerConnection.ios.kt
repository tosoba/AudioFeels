package com.trm.audiofeels.core.player

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.player.util.buildStreamUrl
import com.trm.audiofeels.core.player.util.isPlaying
import com.trm.audiofeels.domain.model.PlaybackState
import com.trm.audiofeels.domain.model.PlayerConstants
import com.trm.audiofeels.domain.model.PlayerError
import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.interop.NSKeyValueObservingProtocol
import io.github.aakira.napier.Napier
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import me.tatarka.inject.annotations.Inject
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.AVFoundation.AVPlayerStatusFailed
import platform.AVFoundation.AVPlayerStatusUnknown
import platform.AVFoundation.addBoundaryTimeObserverForTimes
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.valueWithCMTime
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSKeyValueObservingOptionNew
import platform.Foundation.NSURL.Companion.URLWithString
import platform.Foundation.NSValue
import platform.Foundation.addObserver
import platform.Foundation.removeObserver
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
@ApplicationScope
@Inject
actual class AudioPlayerConnection : PlayerConnection {
  private val player by lazy {
    AVPlayer().apply {
      addObserver(
        observer = timeControlObserver,
        forKeyPath = "timeControlStatus",
        options = NSKeyValueObservingOptionNew,
        context = null,
      )
    }
  }

  private val audioSession: AVAudioSession by lazy {
    AVAudioSession.sharedInstance().apply {
      try {
        setCategory(category = AVAudioSessionCategoryPlayback, error = null)
      } catch (ex: Exception) {
        Napier.e(
          tag = this@AudioPlayerConnection::class.simpleName,
          message = "Failed to set the audio session configuration",
          throwable = ex,
        )
      }
    }
  }

  private var currentItemIndex = 0
  private var tracks = emptyList<Track>()
  private var timeObserverToken: Any? = null
  private var host: String? = null

  private val timeControlObserver: NSObject =
    object : NSObject(), NSKeyValueObservingProtocol {
      override fun observeValueForKeyPath(
        keyPath: String?,
        ofObject: Any?,
        change: Map<Any?, *>?,
        context: COpaquePointer?,
      ) {
        _playerState.update {
          when (it) {
            is PlayerState.Enqueued -> it.copy(isPlaying = player.isPlaying)
            PlayerState.Idle,
            is PlayerState.Error -> enqueuedWithPlaybackState(PlaybackState.READY) ?: it
          }
        }
      }
    }

  private val itemStatusObserver: NSObject =
    object : NSObject(), NSKeyValueObservingProtocol {
      override fun observeValueForKeyPath(
        keyPath: String?,
        ofObject: Any?,
        change: Map<Any?, *>?,
        context: COpaquePointer?,
      ) {
        _playerState.update {
          when (player.currentItem?.status) {
            AVPlayerStatusUnknown -> enqueuedWithPlaybackState(PlaybackState.BUFFERING) ?: it
            AVPlayerItemStatusReadyToPlay -> enqueuedWithPlaybackState(PlaybackState.READY) ?: it
            AVPlayerStatusFailed -> PlayerState.Error(PlayerError.OTHER_ERROR, it)
            else -> it
          }
        }
      }
    }

  private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
  override val playerStateFlow: Flow<PlayerState> = _playerState.asStateFlow()

  override val currentTrackPositionMsFlow: Flow<Long> = flow {
    while (currentCoroutineContext().isActive) {
      player.currentItem?.let { emit((CMTimeGetSeconds(player.currentTime()) * 1000).toLong()) }
      delay(1_000L)
    }
  }

  override val audioDataFlow: Flow<List<Float>> = emptyFlow()

  override fun play() {
    activateAudioSession()
    player.play()
  }

  override fun pause() {
    player.pause()
    deactivateAudioSession()
  }

  override fun playPrevious() {
    currentItemIndex = if (currentItemIndex - 1 >= 0) currentItemIndex - 1 else 0
    play(currentItemIndex)
  }

  override fun playNext() {
    currentItemIndex = if (currentItemIndex + 1 < tracks.size) currentItemIndex + 1 else 0
    play(currentItemIndex)
  }

  override fun playAtIndex(index: Int) {
    currentItemIndex = index
    play(currentItemIndex)
  }

  override fun seekTo(positionMs: Long) {
    player.seekToTime(
      CMTimeMakeWithSeconds(seconds = positionMs.toDouble() / 1_000, preferredTimescale = 1)
    )
  }

  override fun enqueue(input: PlayerInput, startTrackIndex: Int, startPositionMs: Long) {
    tracks = input.tracks
    host = input.host
    play(trackIndex = startTrackIndex, startPositionMs = startPositionMs)
  }

  private fun enqueuedWithPlaybackState(playbackState: PlaybackState): PlayerState.Enqueued? =
    tracks.getOrNull(currentItemIndex)?.let { currentTrack ->
      PlayerState.Enqueued(
        currentTrack = currentTrack,
        currentTrackIndex = currentItemIndex,
        playbackState = playbackState,
        isPlaying = player.isPlaying,
      )
    }

  private fun setCurrentItem(index: Int) {
    currentItemIndex = index
    removeCurrentItemObservers()

    val track = tracks[index]
    AVPlayerItem(URLWithString(track.buildStreamUrl(host = requireNotNull(host)))!!).apply {
      player.replaceCurrentItemWithPlayerItem(this)
      addObserver(
        observer = itemStatusObserver,
        forKeyPath = "status",
        options = NSKeyValueObservingOptionNew,
        context = null,
      )

      // Skip to the next track when the current one ends
      scheduleNextSkipOnEndPlaying(
        duration = CMTimeMake(value = track.duration.toLong(), timescale = 1)
      )
    }
  }

  private fun removeCurrentItemObservers() {
    timeObserverToken?.let { timeObserverToken ->
      player.removeTimeObserver(timeObserverToken)
      this.timeObserverToken = null
      player.currentItem?.removeObserver(observer = itemStatusObserver, forKeyPath = "status")
    }
  }

  private fun scheduleNextSkipOnEndPlaying(duration: CValue<CMTime>) {
    timeObserverToken =
      player.addBoundaryTimeObserverForTimes(
        times = listOf(NSValue.valueWithCMTime(duration)),
        queue = dispatch_get_main_queue(),
      ) {
        currentItemIndex = if (currentItemIndex + 1 < tracks.size) currentItemIndex + 1 else 0
        play(currentItemIndex)
      }
  }

  private fun play(
    trackIndex: Int,
    startPositionMs: Long = PlayerConstants.DEFAULT_START_POSITION_MS,
  ) {
    setCurrentItem(index = trackIndex)
    play()
    startPositionMs.takeUnless { it == PlayerConstants.DEFAULT_START_POSITION_MS }?.let(::seekTo)
  }

  override fun reset() {
    player.pause()
    seekToInitialTime()
    deactivateAudioSession()
    removeCurrentItemObservers()
    tracks = emptyList()
    host = null
  }

  private fun seekToInitialTime() {
    player.seekToTime(CMTimeMakeWithSeconds(seconds = 0.0, preferredTimescale = 1))
  }

  private fun activateAudioSession() {
    updateAudioSessionActive(active = true)
  }

  private fun deactivateAudioSession() {
    updateAudioSessionActive(active = false)
  }

  private fun updateAudioSessionActive(active: Boolean) {
    try {
      audioSession.setActive(
        active = active,
        withOptions = AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation,
        error = null,
      )
    } catch (ex: Exception) {
      Napier.e(
        message = "Failed to update the audio session active to $active",
        tag = this@AudioPlayerConnection::class.simpleName,
        throwable = ex,
      )
    }
  }
}
