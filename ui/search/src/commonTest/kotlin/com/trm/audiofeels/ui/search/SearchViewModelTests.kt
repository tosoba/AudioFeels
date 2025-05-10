package com.trm.audiofeels.ui.search

import app.cash.turbine.test
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.test.stubPlaylist
import com.trm.audiofeels.data.test.SuggestionsFakeRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.repository.SuggestionsRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eq
import dev.mokkery.mock
import dev.mokkery.spy
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verify.VerifyMode.Companion.not
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
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
          val query = queryOf(length = index + 1)
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
  fun `when query too short - then query playlists does not emit more items`() = runTest {
    val viewModel = searchViewModel()
    viewModel.playlists.test {
      skipItems(1)

      repeat(times = SearchViewModel.MIN_QUERY_LENGTH) {
        viewModel.onQueryChange(queryOf(length = it))
      }

      ensureAllEventsConsumed()
    }
  }

  @Test
  fun `when query too short after trim - then query playlists does not emit more items`() =
    runTest {
      val viewModel = searchViewModel()

      viewModel.playlists.test {
        skipItems(1)

        repeat(times = SearchViewModel.MIN_QUERY_LENGTH) {
          viewModel.onQueryChange(
            queryOf(length = it).padEnd(length = SearchViewModel.MIN_QUERY_LENGTH, padChar = ' ')
          )
        }

        ensureAllEventsConsumed()
      }
    }

  @Test
  fun `when multiple same query changes - then searchPlaylists is called only once`() = runTest {
    val playlistsRepository =
      mock<PlaylistsRepository> { everySuspend { searchPlaylists(any()) } returns emptyList() }
    val viewModel = searchViewModel(playlistsRepository = playlistsRepository)

    viewModel.playlists.test {
      skipItems(1)

      val query = queryOf()
      repeat(times = 10) {
        viewModel.onQueryChange(query)
        advanceTimeBy(delayTimeMillis = SearchViewModel.QUERY_DEBOUNCE_TIMEOUT_MILLIS * 2)
      }

      skipItems(1)
      ensureAllEventsConsumed()

      verifySuspend(exactly(1)) { playlistsRepository.searchPlaylists(eq(query)) }
    }
  }

  @Test
  fun `when multiple different valid query changes within debounce timeout - then searchPlaylists is called only once`() =
    runTest {
      val playlistsRepository =
        mock<PlaylistsRepository> { everySuspend { searchPlaylists(any()) } returns emptyList() }
      val viewModel = searchViewModel(playlistsRepository = playlistsRepository)

      viewModel.playlists.test {
        skipItems(1)

        val start = currentTime
        var current = start
        var latestQuery = ""
        while (current - start < SearchViewModel.QUERY_DEBOUNCE_TIMEOUT_MILLIS) {
          viewModel.onQueryChange(
            queryOf(length = SearchViewModel.MIN_QUERY_LENGTH + latestQuery.length).also {
              latestQuery = it
            }
          )
          advanceTimeBy(delayTimeMillis = 50L)
          current = currentTime
        }

        skipItems(1)
        ensureAllEventsConsumed()

        verifySuspend(exactly(1)) { playlistsRepository.searchPlaylists(eq(latestQuery)) }
      }
    }

  @Test
  fun `when valid query and empty searchPlaylists response - then suggestion is not saved`() =
    runTest {
      val suggestionsRepository = mock<SuggestionsRepository> {}

      searchViewModel(
          playlistsRepository =
            mock { everySuspend { searchPlaylists(any()) } returns emptyList() },
          suggestionsRepository = suggestionsRepository,
        )
        .testPlaylistsWithQuery(queryOf())

      verifyNoMoreCalls(suggestionsRepository)
    }

  @Test
  fun `when valid query and non-empty searchPlaylists response - then suggestion is saved`() =
    runTest {
      val suggestionsRepository = SuggestionsFakeRepository()
      val query = queryOf()

      searchViewModel(
          playlistsRepository =
            mock { everySuspend { searchPlaylists(any()) } returns listOf(stubPlaylist()) },
          suggestionsRepository = suggestionsRepository,
        )
        .testPlaylistsWithQuery(query)

      assertContentEquals(
        expected = listOf(query),
        actual =
          suggestionsRepository
            .getSuggestionsFlow(limit = SearchViewModel.QUERY_SUGGESTIONS_LIMIT)
            .firstOrNull(),
      )
    }

  @Test
  fun `given valid query and non-empty searchPlaylists response - when no interaction - then valid query suggestion is not returned`() =
    runTest {
      val suggestionsRepository = SuggestionsFakeRepository()
      val viewModel =
        searchViewModel(
          playlistsRepository =
            mock { everySuspend { searchPlaylists(any()) } returns listOf(stubPlaylist()) },
          suggestionsRepository = suggestionsRepository,
        )
      val query = queryOf()
      viewModel.testPlaylistsWithQuery(query)

      viewModel.suggestions.test {
        assertContentEquals(expected = emptyList(), actual = awaitItem())
        ensureAllEventsConsumed()
      }
    }

  @Test
  fun `given valid query and non-empty searchPlaylists response - when query change - then valid query suggestion is returned`() =
    runTest {
      val suggestionsRepository = SuggestionsFakeRepository()
      val viewModel =
        searchViewModel(
          playlistsRepository =
            mock { everySuspend { searchPlaylists(any()) } returns listOf(stubPlaylist()) },
          suggestionsRepository = suggestionsRepository,
        )
      val query = queryOf()
      viewModel.testPlaylistsWithQuery(query)

      viewModel.suggestions.test {
        skipItems(1)
        viewModel.onQueryChange(SearchViewModel.EMPTY_QUERY)
        assertContentEquals(expected = listOf(query), actual = awaitItem())
        ensureAllEventsConsumed()
      }
    }

  @Test
  fun `given valid query and non-empty searchPlaylists response - when shuffle - then playlists are shuffled`() =
    runTest {
      val playlists = spy(listOf(stubPlaylist()))
      val viewModel =
        searchViewModel(
          playlistsRepository = mock { everySuspend { searchPlaylists(any()) } returns playlists }
        )

      viewModel.playlists.test {
        skipItems(1)
        viewModel.onQueryChange(queryOf())
        skipItems(1)

        viewModel.onShuffleClick()
        skipItems(1)
        ensureAllEventsConsumed()
      }

      verify(exactly(1)) { playlists.shuffled() }
    }

  @Test
  fun `given shuffled searchPlaylists response - when query change - then new playlists are not shuffled`() =
    runTest {
      val firstQueryPlaylists = spy(listOf(stubPlaylist(id = "1"), stubPlaylist(id = "2")))
      val secondQueryPlaylists = spy(listOf(stubPlaylist(id = "3"), stubPlaylist(id = "4")))

      val viewModel =
        searchViewModel(
          playlistsRepository =
            mock {
              everySuspend { searchPlaylists(any()) } sequentiallyReturns
                listOf(firstQueryPlaylists, secondQueryPlaylists)
            }
        )

      viewModel.playlists.test {
        skipItems(1)
        viewModel.onQueryChange(queryOf(repeatedChar = '0'))
        skipItems(1)
        viewModel.onShuffleClick()
        skipItems(1)

        viewModel.onQueryChange(queryOf(repeatedChar = '1'))
        skipItems(2)
        ensureAllEventsConsumed()
      }

      verify(exactly(1)) { firstQueryPlaylists.shuffled() }
      verify(not) { secondQueryPlaylists.shuffled() }
    }

  private suspend fun SearchViewModel.testPlaylistsWithQuery(query: String) {
    playlists.test {
      skipItems(1)
      onQueryChange(query)
      awaitItem()
      ensureAllEventsConsumed()
    }
  }

  private fun queryOf(
    length: Int = SearchViewModel.MIN_QUERY_LENGTH,
    repeatedChar: Char = 'a',
  ): String = Array(size = length) { repeatedChar }.joinToString(separator = "")

  private fun searchViewModel(
    playlistsRepository: PlaylistsRepository = mock {},
    suggestionsRepository: SuggestionsRepository = SuggestionsFakeRepository(),
  ): SearchViewModel =
    SearchViewModel(
      playlistsRepository = playlistsRepository,
      suggestionsRepository = suggestionsRepository,
    )
}
