package com.github.veselinazatchepina.books.data

import io.reactivex.Observable


interface BooksDataSource {

    fun isUserAuthenticated(): Boolean

    fun signInAnonymously(): Observable<Boolean?>

    fun isUserExists(): Observable<Boolean?>

    fun saveUserId()
}