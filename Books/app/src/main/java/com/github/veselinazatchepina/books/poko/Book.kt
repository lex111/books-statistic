package com.github.veselinazatchepina.books.poko


data class Book(var id: String = "",
                var bookName: String = "",
                val authorsIds: List<String> = emptyList(),
                var category: String = "",
                var section: String = "",
                var startDate: String = "",
                var endDate: String = "",
                var year: String = "",
                var pageCount: Int = 0,
                var repeatCount: Int = 0,
                var rating: Int = 0)