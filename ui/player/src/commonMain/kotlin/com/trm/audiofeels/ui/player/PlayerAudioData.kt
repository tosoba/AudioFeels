package com.trm.audiofeels.ui.player

sealed interface PlayerAudioData {
  data object Disabled : PlayerAudioData

  data class Enabled(val data: List<Float>) : PlayerAudioData
}
