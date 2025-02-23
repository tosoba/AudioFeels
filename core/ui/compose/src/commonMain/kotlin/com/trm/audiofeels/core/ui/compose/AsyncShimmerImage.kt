package com.trm.audiofeels.core.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.artwork_placeholder
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AsyncShimmerImage(
  model: Any?,
  contentDescription: String?,
  placeholder: Painter? = rememberVectorPainter(vectorResource(Res.drawable.artwork_placeholder)),
  modifier: @Composable (Boolean) -> Modifier,
) {
  var showShimmer by remember { mutableStateOf(false) }
  AsyncImage(
    model = model,
    contentDescription = contentDescription,
    contentScale = ContentScale.Crop,
    error = placeholder,
    onLoading = { showShimmer = true },
    onSuccess = { showShimmer = false },
    onError = { showShimmer = false },
    modifier = modifier(showShimmer),
  )
}
