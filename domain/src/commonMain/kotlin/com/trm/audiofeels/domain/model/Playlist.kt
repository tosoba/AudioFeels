package com.trm.audiofeels.domain.model

import com.trm.audiofeels.core.base.model.CommonParcelable
import com.trm.audiofeels.core.base.model.CommonParcelize

@CommonParcelize
data class Playlist(
  val id: String,
  val name: String,
  val description: String?,
  val artworkUrl: String?,
  val score: Double,
  val trackCount: Int,
) : CommonParcelable
