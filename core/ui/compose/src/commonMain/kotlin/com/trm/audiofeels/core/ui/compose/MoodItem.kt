package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.trm.audiofeels.core.ui.compose.theme.Spacing

@Composable
fun MoodItem(symbol: String, name: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
  ElevatedCard(onClick = onClick, modifier = modifier, shape = CircleShape) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(modifier = Modifier.height(Spacing.small8dp))

      Text(text = symbol, style = MaterialTheme.typography.headlineLarge)

      Text(
        text = name,
        style = MaterialTheme.typography.labelLarge,
        maxLines = 1,
        modifier = Modifier.padding(horizontal = Spacing.small8dp).basicMarquee(),
      )

      Spacer(modifier = Modifier.height(Spacing.small8dp))
    }
  }
}
