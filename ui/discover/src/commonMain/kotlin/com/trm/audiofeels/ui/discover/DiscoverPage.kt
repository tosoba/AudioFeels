package com.trm.audiofeels.ui.discover

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.discover
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiscoverPage(modifier: Modifier = Modifier) {
  Box(modifier = modifier) { Text(stringResource(Res.string.discover)) }
}
