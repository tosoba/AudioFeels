package com.trm.audiofeels.ui.favourites

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.favourites
import org.jetbrains.compose.resources.stringResource

@Composable
fun FavouritesPage(modifier: Modifier = Modifier) {
  Box(modifier = modifier) { Text(stringResource(Res.string.favourites)) }
}
