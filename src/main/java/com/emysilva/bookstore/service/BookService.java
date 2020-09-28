package com.emysilva.bookstore.service;


import com.emysilva.bookstore.model.Book;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();

    Book getABook(Long id);
}
