package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoodItem(symbol: String, name: String, onClick: () -> Unit) {
  ElevatedCard(onClick = onClick, modifier = Modifier.size(90.dp), shape = CircleShape) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(modifier = Modifier.height(8.dp))

      Text(text = symbol, style = MaterialTheme.typography.headlineLarge)

      Text(
        text = name,
        style = MaterialTheme.typography.labelLarge,
        maxLines = 1,
        modifier = Modifier.padding(horizontal = 8.dp).basicMarquee(),
      )

      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}
