package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.trm.audiofeels.core.ui.compose.theme.Spacing
import com.trm.audiofeels.core.ui.compose.util.currentWindowHeightClass
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

fun LazyGridScope.emptyListItem(primaryText: StringResource, secondaryText: StringResource) {
  item(span = { GridItemSpan(maxLineSpan) }) {
    EmptyListItemContent(
      primaryText = stringResource(primaryText),
      secondaryText = stringResource(secondaryText),
    )
  }
}

@Composable
private fun EmptyListItemContent(primaryText: String, secondaryText: String) {
  if (currentWindowHeightClass() == WindowHeightSizeClass.Compact) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth().padding(Spacing.medium16dp),
    ) {
      EmptyListItemIcon()

      Spacer(modifier = Modifier.width(Spacing.small8dp))

      Column {
        EmptyListItemTextsContent(
          primaryText = primaryText,
          secondaryText = secondaryText,
          textAlign = TextAlign.Start,
        )
      }
    }
  } else {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(Spacing.medium16dp),
    ) {
      EmptyListItemIcon()

      Spacer(modifier = Modifier.height(Spacing.small8dp))

      EmptyListItemTextsContent(
        primaryText = primaryText,
        secondaryText = secondaryText,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
private fun EmptyListItemIcon() {
  Icon(
    imageVector = Icons.AutoMirrored.Outlined.ManageSearch,
    contentDescription = null,
    modifier = Modifier.size(Spacing.extraLarge64dp),
    tint = MaterialTheme.colorScheme.onSurfaceVariant,
  )
}

@Composable
private fun EmptyListItemTextsContent(
  primaryText: String,
  secondaryText: String,
  textAlign: TextAlign,
) {
  Text(text = primaryText, textAlign = textAlign, style = MaterialTheme.typography.titleLarge)
  Spacer(modifier = Modifier.height(Spacing.extraSmall4dp))
  Text(text = secondaryText, textAlign = textAlign, style = MaterialTheme.typography.bodyMedium)
}
