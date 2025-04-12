package com.trm.audiofeels.ui.search

import app.cash.turbine.test
import com.trm.audiofeels.data.test.SuggestionsFakeRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import dev.mokkery.mock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

internal class SearchViewModelTests {
  @OptIn(ExperimentalCoroutinesApi::class)
  @BeforeTest
  fun before() {
    Dispatchers.setMain(StandardTestDispatcher())
  }

  @Test
  fun `when no interaction - then query flow emits empty query`() = runTest {
    searchViewModel().query.test {
      assertEquals(expected = SearchViewModel.EMPTY_QUERY, actual = awaitItem())
    }
  }

  @Test
  fun `when multiple query changes - then query flow emits all queries`() = runTest {
    val viewModel = searchViewModel()
    viewModel.query.test {
      skipItems(1)
      repeat(10) { index ->
        val query = Array(5) { index }.joinToString(separator = "")
        viewModel.onQueryChange(query)
        assertEquals(expected = query, actual = awaitItem())
      }
    }
  }

  private fun searchViewModel(
    playlistsRepository: PlaylistsRepository.() -> Unit = {}
  ): SearchViewModel =
    SearchViewModel(
      playlistsRepository = mock(block = playlistsRepository),
      suggestionsRepository = SuggestionsFakeRepository(),
    )
}
