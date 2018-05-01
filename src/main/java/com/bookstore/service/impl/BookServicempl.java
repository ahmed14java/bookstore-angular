package com.bookstore.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.domain.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;

@Service
public class BookServicempl implements BookService{
	
	@Autowired
	private BookRepository bookRepository ;

	@Override
	public List<Book> findAll() {
		List<Book> books = (List<Book>) bookRepository.findAll();
		
		List<Book> activeBookList = new ArrayList<>();
		for (Book book : books) {
			if(book.isActive()) {
				activeBookList.add(book);
			}
		}
		return activeBookList;
	}

	@Override
	public Book findOne(Long id) {
		Book book = bookRepository.findOne(id);
		return book;
	}

	@Override
	public Book save(Book book) {
		return bookRepository.save(book);
	}

	@Override
	public List<Book> blurrySearch(String title) {
		List<Book> books = bookRepository.findByTitleContaining(title);
		List<Book> activeBookList = new ArrayList<>();
		for (Book book : books) {
			if(book.isActive()) {
				activeBookList.add(book);
			}
		}
		return activeBookList;
	}

	@Override
	public void removeOne(Long id) {
		bookRepository.delete(id);
		
	}

}
