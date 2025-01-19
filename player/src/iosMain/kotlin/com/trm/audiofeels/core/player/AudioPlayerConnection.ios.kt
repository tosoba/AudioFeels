package com.trm.audiofeels.core.player

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.player.util.buildStreamUrl
import com.trm.audiofeels.core.player.util.isPlaying
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.interop.NSKeyValueObservingProtocol
import io.github.aakira.napier.Napier
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import me.tatarka.inject.annotations.Inject
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
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
  private val player by lazy(::AVPlayer)
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

  private val itemStatusObserver: NSObject =
    object : NSObject(), NSKeyValueObservingProtocol {
      override fun observeValueForKeyPath(
        keyPath: String?,
        ofObject: Any?,
        change: Map<Any?, *>?,
        context: COpaquePointer?,
      ) {
        val isLoading = player.currentItem?.status == AVPlayerStatusUnknown
        val hasError = player.currentItem?.status == AVPlayerStatusFailed
      }
    }

  override val playerState: Flow<PlayerState> =
    callbackFlow<PlayerState> {
        val timeControlObserver =
          object : NSObject(), NSKeyValueObservingProtocol {
            override fun observeValueForKeyPath(
              keyPath: String?,
              ofObject: Any?,
              change: Map<Any?, *>?,
              context: COpaquePointer?,
            ) {
              player.isPlaying
            }
          }

        player.addObserver(
          observer = timeControlObserver,
          forKeyPath = "timeControlStatus",
          options = NSKeyValueObservingOptionNew,
          context = null,
        )

        awaitClose {
          player.removeObserver(observer = timeControlObserver, forKeyPath = "timeControlStatus")
        }
      }
      .conflate()

  override val currentTrackPositionMs: Flow<Long> = flow {
    while (currentCoroutineContext().isActive) {
      player.currentItem?.let {
        emit(CMTimeGetSeconds(player.currentTime()).milliseconds.inWholeMilliseconds)
      }
      delay(1_000L)
    }
  }

  override fun play() {
    activateAudioSession()
    player.play()
  }

  override fun pause() {
    player.pause()
    deactivateAudioSession()
  }

  override fun playPrevious() {
    // TODO:
  }

  override fun playNext() {
    // TODO:
  }

  override fun skipTo(positionMs: Long) {
    // TODO:
  }

  override fun skipTo(trackIndex: Int, positionMs: Long) {
    // TODO:
  }

  override fun enqueue(
    tracks: List<Track>,
    host: String,
    autoPlay: Boolean,
    startTrackIndex: Int,
    startPositionMs: Long, // TODO: use that somehow :D
  ) {
    this.tracks = tracks
    this.host = host
    if (autoPlay) {
      play(startTrackIndex)
    }
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
    }
    player.currentItem?.removeObserver(observer = itemStatusObserver, forKeyPath = "status")
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

  private fun play(trackIndex: Int) {
    setCurrentItem(index = trackIndex)
    play()
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
