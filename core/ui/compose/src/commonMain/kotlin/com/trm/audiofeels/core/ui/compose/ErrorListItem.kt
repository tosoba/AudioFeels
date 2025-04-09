package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import com.trm.audiofeels.core.ui.compose.theme.ListItemSize
import com.trm.audiofeels.core.ui.compose.theme.RoundedCornerSize
import com.trm.audiofeels.core.ui.compose.theme.Spacing
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.error_occurred
import com.trm.audiofeels.core.ui.resources.refresh
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ErrorListItem(modifier: Modifier = Modifier, onClick: () -> Unit) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Card(modifier = Modifier.width(ListItemSize.medium150dp), onClick = onClick) {
      Image(
        painter = rememberVectorPainter(vectorResource(Res.drawable.refresh)),
        contentDescription = null,
        modifier =
          Modifier.size(ListItemSize.medium150dp)
            .clip(RoundedCornerShape(RoundedCornerSize.medium16dp))
            .background(MaterialTheme.colorScheme.errorContainer),
      )

      Spacer(modifier = Modifier.height(Spacing.small8dp))

      Text(
        text = stringResource(Res.string.error_occurred),
        style = MaterialTheme.typography.labelLarge,
        maxLines = 1,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.small8dp).basicMarquee(),
      )

      Spacer(modifier = Modifier.height(Spacing.small8dp))
    }
  }
}
