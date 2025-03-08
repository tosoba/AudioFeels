package com.trm.audiofeels.core.player.visualizer

import android.media.audiofx.Visualizer

class WaveEngine(
  audioSession: Int,
  private val minValue: Float,
  private val maxValue: Float,
  private val valuesCount: Int,
  private val onDataCapture: (List<Float>) -> Unit,
) : Visualizer.OnDataCaptureListener {
  private var visualizer: Visualizer =
    Visualizer(audioSession).apply {
      setDataCaptureListener(this@WaveEngine, Visualizer.getMaxCaptureRate(), true, false)
      enabled = true
    }

  private val waveParser: WaveParser = WaveParser()

  private var lastTime = 0L

  override fun onWaveFormDataCapture(
    visualizer: Visualizer?,
    waveform: ByteArray?,
    samplingRate: Int,
  ) {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastTime > 360L) {
      lastTime = currentTime
      if (waveform != null && waveform.isNotEmpty()) {
        onDataCapture(
          waveParser.parse(
            toParse = waveform,
            viewMin = minValue,
            viewMax = maxValue,
            chunkNum = valuesCount,
          )
        )
      }
    }
  }

  override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) = Unit

  fun release() {
    visualizer.apply {
      enabled = false
      release()
    }
  }
}
