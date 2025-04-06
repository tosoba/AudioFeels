package com.trm.audiofeels.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Mood(val symbol: String) {
  Peaceful("🕊️"),
  Romantic("💘"),
  Sentimental("😢"),
  Tender("😌"),
  Easygoing("🙂"),
  Yearning("👀"),
  Sophisticated("🤓"),
  Sensual("😘"),
  Cool("😎"),
  Gritty("🙎"),
  Melancholy("🌧️"),
  Serious("😐"),
  Brooding("🤔"),
  Fiery("🔥"),
  Defiant("😈"),
  Aggressive("🤬"),
  Rowdy("👺"),
  Excited("🎉"),
  Energizing("💫"),
  Empowering("💪"),
  Stirring("😲"),
  Upbeat("🙌"),
  Other("🤷"),
}
