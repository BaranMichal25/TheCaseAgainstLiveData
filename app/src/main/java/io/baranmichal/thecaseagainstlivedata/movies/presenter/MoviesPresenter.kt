package io.baranmichal.thecaseagainstlivedata.movies.presenter

import io.baranmichal.thecaseagainstlivedata.base.presenter.BasePresenter
import io.baranmichal.thecaseagainstlivedata.base.rx.AppSchedulers
import io.baranmichal.thecaseagainstlivedata.movies.data.MoviesRepository
import io.baranmichal.thecaseagainstlivedata.movies.view.MoviesView
import java.io.IOException
import javax.inject.Inject

class MoviesPresenter @Inject constructor(
    private val repository: MoviesRepository,
    private val schedulers: AppSchedulers,
    private val messageProvider: MoviesMessageProvider
) : BasePresenter<MoviesView>() {

    fun loadMovies() {
        repository.loadMovies()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.main)
            .doOnSubscribe {
                view()?.showLoading()
            }
            .subscribe({
                view()?.showMovies(it)
            }, {
                view()?.showLoadingError(getErrorMessage(it))
            })
            .autoClear()
    }

    fun refreshMovies() {
        repository.refreshMovies()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.main)
            .subscribe({
                view()?.showMovies(it)
            }, {
                view()?.showRefreshError(getErrorMessage(it))
            })
            .autoClear()
    }

    fun retryRefreshClicked() {
        view()?.showRefresh()
        refreshMovies()
    }

    private fun getErrorMessage(error: Throwable): String {
        return when (error) {
            is IOException -> messageProvider.getConnectionErrorMessage()
            else -> messageProvider.getUnknownErrorMessage()
        }
    }
}