package com.emysilva.bookstore.service.impl;

import com.emysilva.bookstore.exception.BookStoreException;
import com.emysilva.bookstore.model.Book;
import com.emysilva.bookstore.repository.BookRepository;
import com.emysilva.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getABook(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookStoreException("book with "+ id + "not found"));
    }
}
