package com.forum.frontend.presentation.search

import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.repository.SearchRepository
import com.forum.frontend.presentation.core.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var previewLimit = PREVIEW_LIMIT

    fun useCompactPreview() {
        previewLimit = COMPACT_LIMIT
    }

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value.take(MAX_QUERY_LENGTH)) }
        search(page = 1, debounce = true)
    }

    fun clear() {
        searchJob?.cancel()
        _state.update { SearchUiState(tab = it.tab) }
    }

    fun selectTab(tab: SearchTab) {
        if (tab == _state.value.tab) return
        _state.update {
            it.copy(tab = tab, posts = emptyList(), people = emptyList(), page = 0, hasMore = false, hasSearched = false)
        }
        search(page = 1, debounce = false)
    }

    fun retry() {
        search(page = 1, debounce = false)
    }

    fun loadMore() {
        val current = _state.value
        if (current.tab == SearchTab.ALL || current.isLoading || current.isLoadingMore || !current.hasMore) return
        search(page = current.page + 1, debounce = false)
    }

    private fun search(page: Int, debounce: Boolean) {
        searchJob?.cancel()
        val current = _state.value
        val term = current.query.trim()
        if (term.length < SearchUiState.MIN_QUERY_LENGTH) {
            _state.update {
                it.copy(
                    posts = emptyList(),
                    people = emptyList(),
                    isLoading = false,
                    isLoadingMore = false,
                    error = null,
                    page = 0,
                    hasMore = false,
                    hasSearched = false,
                )
            }
            return
        }
        val tab = current.tab
        searchJob = scope.launch {
            if (debounce) delay(DEBOUNCE_MS)
            _state.update { if (page == 1) it.copy(isLoading = true, error = null) else it.copy(isLoadingMore = true) }
            when (tab) {
                SearchTab.ALL -> resultOf {
                    coroutineScope {
                        val people = async { searchRepository.searchPeople(term, 1, previewLimit) }
                        val posts = async { searchRepository.searchPosts(term, 1, previewLimit) }
                        people.await() to posts.await()
                    }
                }
                    .onSuccess { (people, posts) ->
                        _state.update {
                            it.copy(
                                people = people.items,
                                posts = posts.items,
                                page = 1,
                                hasMore = false,
                                isLoading = false,
                                isLoadingMore = false,
                                hasSearched = true,
                            )
                        }
                    }
                    .onFailure { showError(it) }

                SearchTab.POSTS -> resultOf { searchRepository.searchPosts(term, page) }
                    .onSuccess { result ->
                        _state.update { s ->
                            s.copy(
                                posts = if (page == 1) result.items else s.posts + result.items,
                                page = result.page,
                                hasMore = result.hasMore,
                                isLoading = false,
                                isLoadingMore = false,
                                hasSearched = true,
                            )
                        }
                    }
                    .onFailure { showError(it) }

                SearchTab.PEOPLE -> resultOf { searchRepository.searchPeople(term, page) }
                    .onSuccess { result ->
                        _state.update { s ->
                            s.copy(
                                people = if (page == 1) result.items else s.people + result.items,
                                page = result.page,
                                hasMore = result.hasMore,
                                isLoading = false,
                                isLoadingMore = false,
                                hasSearched = true,
                            )
                        }
                    }
                    .onFailure { showError(it) }
            }
        }
    }

    private fun showError(error: Throwable) {
        _state.update {
            it.copy(isLoading = false, isLoadingMore = false, hasSearched = true, error = error.toUserMessage())
        }
    }

    private companion object {
        const val DEBOUNCE_MS = 300L
        const val MAX_QUERY_LENGTH = 100
        const val PREVIEW_LIMIT = 6
        const val COMPACT_LIMIT = 4
    }
}
