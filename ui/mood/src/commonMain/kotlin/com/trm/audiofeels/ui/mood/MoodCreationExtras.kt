package com.trm.audiofeels.ui.mood

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.trm.audiofeels.domain.model.Mood

fun moodCreationExtras(mood: Mood): CreationExtras =
  MutableCreationExtras().also { it[MoodKey] = mood }

internal object MoodKey : CreationExtras.Key<Mood>
