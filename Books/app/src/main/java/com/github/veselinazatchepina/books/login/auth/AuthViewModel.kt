package com.github.veselinazatchepina.books.login.auth

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.util.Log
import com.github.veselinazatchepina.books.data.BooksRepository
import com.github.veselinazatchepina.books.data.remote.BooksRemoteDataSource
import com.github.veselinazatchepina.books.login.UserExistenceState
import io.reactivex.disposables.CompositeDisposable


class AuthViewModel : ViewModel() {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val booksDataSource by lazy {
        BooksRepository.getInstance(BooksRemoteDataSource.getInstance())
    }
    private val authUIWrapper: AuthUIWrapper by lazy {
        AuthUIWrapper()
    }
    var liveIntent: MutableLiveData<AnonymouslyLoginState> = MutableLiveData()
    var liveIsUserExists: MutableLiveData<UserExistenceState> = MutableLiveData()

    fun signInWithAuthUI(): Intent {
        return authUIWrapper.getAuthIntent()
    }

    fun signInAnonymously() {
        liveIntent.postValue(AnonymouslyLoginState.AnonymouslyLoad())
        compositeDisposable.add(booksDataSource.signInAnonymously()
                .subscribe({
                    if (it != null && it) {
                        liveIntent.postValue(AnonymouslyLoginState.AnonymouslySuccess())
                    } else {
                        liveIntent.postValue(AnonymouslyLoginState.AnonymouslyError("Please try again!"))
                    }
                }, {
                    liveIntent.postValue(AnonymouslyLoginState.AnonymouslyError("Please try again!"))
                    Log.d("ANONYMOUSLY_LOGIN_STATE", it.message)
                }))

    }

    fun isUserExists() {
        liveIsUserExists.postValue(UserExistenceState.UserExistenceLoad())
        compositeDisposable.add(booksDataSource.isUserExists().subscribe({
            if (it != null && it) {
                liveIsUserExists.postValue(UserExistenceState.UserExistenceSuccess())
            } else {
                liveIsUserExists.postValue(UserExistenceState.UserExistenceError())
            }
        }, {
            liveIsUserExists.postValue(UserExistenceState.UserExistenceError())
        }))
    }

    fun saveUser() {
        booksDataSource.saveUserId()
    }

    fun isUserAuthenticated(): Boolean = booksDataSource.isUserAuthenticated()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
