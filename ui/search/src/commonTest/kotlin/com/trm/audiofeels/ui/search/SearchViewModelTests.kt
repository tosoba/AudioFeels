package com.trm.audiofeels.ui.search

import app.cash.turbine.test
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.data.test.SuggestionsFakeRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eq
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchViewModelTests {
  @BeforeTest
  fun before() {
    Dispatchers.setMain(StandardTestDispatcher())
  }

  @AfterTest
  fun after() {
    Dispatchers.resetMain()
  }

  @Test
  fun `when no interaction - then query flow emits empty query`() = runTest {
    searchViewModel().query.test {
      assertEquals(expected = SearchViewModel.EMPTY_QUERY, actual = awaitItem())
      expectNoEvents()
    }
  }

  @Test
  fun `given initial query received - when multiple query changes - then query flow emits all queries`() =
    runTest {
      val viewModel = searchViewModel()
      viewModel.query.test {
        skipItems(1)
        repeat(10) { index ->
          val query = Array(size = index + 1) { "a" }.joinToString(separator = "")
          viewModel.onQueryChange(query)
          assertEquals(expected = query, actual = awaitItem())
          ensureAllEventsConsumed()
        }
      }
    }

  @Test
  fun `when no interaction - then playlists flow emits Loading`() = runTest {
    searchViewModel().playlists.test {
      assertEquals(expected = LoadableState.Loading, actual = awaitItem())
      ensureAllEventsConsumed()
    }
  }

  @Test
  fun `given initial playlists Loading received - when query too short - then query playlists does not emit more items`() =
    runTest {
      val viewModel = searchViewModel()
      viewModel.playlists.test {
        skipItems(1)
        repeat(times = SearchViewModel.MIN_QUERY_LENGTH) {
          viewModel.onQueryChange(Array(size = it) { "a" }.joinToString(separator = ""))
        }
        ensureAllEventsConsumed()
      }
    }

  @Test
  fun `given initial playlists Loading received - when query too short after trim - then query playlists does not emit more items`() =
    runTest {
      val viewModel = searchViewModel()
      viewModel.playlists.test {
        skipItems(1)
        repeat(times = SearchViewModel.MIN_QUERY_LENGTH) {
          viewModel.onQueryChange(
            Array(size = it) { "a" }
              .joinToString(separator = "")
              .padEnd(length = SearchViewModel.MIN_QUERY_LENGTH, padChar = ' ')
          )
        }
        ensureAllEventsConsumed()
      }
    }

  @Test
  fun `given initial playlists Loading received - when valid query is repeated multiple times - then searchPlaylists is called only once`() =
    runTest {
      val playlistsRepository =
        mock<PlaylistsRepository> { everySuspend { searchPlaylists(any()) } returns emptyList() }
      val viewModel = searchViewModel(playlistsRepository = playlistsRepository)
      viewModel.playlists.test {
        skipItems(1)
        val query =
          Array(size = SearchViewModel.MIN_QUERY_LENGTH) { "a" }.joinToString(separator = "")
        repeat(times = 10) {
          viewModel.onQueryChange(query)
          advanceTimeBy(delayTimeMillis = 1_000L)
        }
        awaitItem()
        ensureAllEventsConsumed()
        verifySuspend(exactly(1)) { playlistsRepository.searchPlaylists(eq(query)) }
      }
    }

  private fun searchViewModel(playlistsRepository: PlaylistsRepository = mock {}): SearchViewModel =
    SearchViewModel(
      playlistsRepository = playlistsRepository,
      suggestionsRepository = SuggestionsFakeRepository(),
    )
}
