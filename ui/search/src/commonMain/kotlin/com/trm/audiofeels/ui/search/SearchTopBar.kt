package com.trm.audiofeels.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.search
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchTopBar(
  hazeState: HazeState,
  suggestions: List<String>,
  onQueryChange: (String) -> Unit,
) {
  var query by rememberSaveable { mutableStateOf("") }
  var expanded by rememberSaveable { mutableStateOf(false) }

  fun updateQuery(newQuery: String) {
    query = newQuery
    onQueryChange(query)
  }

  val boxHazeStyle =
    HazeStyle(
      backgroundColor = TopAppBarDefaults.topAppBarColors().containerColor,
      tint =
        HazeTint(
          TopAppBarDefaults.topAppBarColors()
            .run { copy(containerColor = containerColor.copy(alpha = .85f)) }
            .containerColor
        ),
    )

  Box(
    modifier =
      Modifier.fillMaxWidth().hazeEffect(hazeState) {
        style = boxHazeStyle
        blurRadius = 10.dp
      }
  ) {
    val searchBarHazeStyle =
      HazeStyle(backgroundColor = SearchBarDefaults.colors().containerColor, tint = null)
    DockedSearchBar(
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            top =
              with(LocalDensity.current) { TopAppBarDefaults.windowInsets.getTop(this).toDp() } +
                16.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
          )
          .clip(SearchBarDefaults.dockedShape)
          .hazeEffect(hazeState) {
            style = searchBarHazeStyle
            blurRadius = 10.dp
          },
      colors =
        SearchBarDefaults.colors(
          containerColor = SearchBarDefaults.colors().containerColor.copy(alpha = .95f)
        ),
      inputField = {
        SearchBarDefaults.InputField(
          modifier = Modifier.fillMaxWidth(),
          query = query,
          onQueryChange = ::updateQuery,
          onSearch = { expanded = false },
          expanded = expanded,
          onExpandedChange = { expanded = it },
          placeholder = { Text(stringResource(Res.string.search)) },
          leadingIcon = {
            IconButton(onClick = { expanded = false }) {
              Crossfade(expanded) {
                if (it) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Collapse search",
                  )
                } else {
                  Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(Res.string.search),
                  )
                }
              }
            }
          },
          trailingIcon = {
            AnimatedVisibility(query.isNotBlank()) {
              IconButton(onClick = { updateQuery("") }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
              }
            }
          },
        )
      },
      expanded = expanded && suggestions.isNotEmpty(),
      onExpandedChange = {},
      content = {
        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          items(suggestions) { Text(text = it, modifier = Modifier.fillMaxWidth().animateItem()) }
        }
      },
    )
  }
}
