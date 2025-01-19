package com.trm.audiofeels.core.player.util

import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.timeControlStatus

internal val AVPlayer.isPlaying: Boolean
  get() = timeControlStatus == AVPlayerTimeControlStatusPlaying
