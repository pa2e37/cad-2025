package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    // Главная страница библиотеки
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "index";
    }

    // Добавление книги
    @PostMapping("/addBook")
    public String addBook(@ModelAttribute Book book) {
        bookRepository.save(book);
        return "redirect:/";
    }

    // Удаление книги (только MODERATOR)
    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/deleteBook/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "redirect:/";
    }

    // Переключение доступности книги (только MODERATOR)
    @PreAuthorize("hasRole('MODERATOR')")
    @PostMapping("/updateBook/{id}")
    public String updateBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElseThrow();
        book.setAvailable(!book.isAvailable());
        bookRepository.save(book);
        return "redirect:/";
    }

    // Отметить книгу как прочитанную
    @PostMapping("/readBook/{id}")
    public String readBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElseThrow();
        book.setRead(true);
        bookRepository.save(book);
        return "redirect:/";
    }

    // Показать только доступные книги
    @GetMapping("/available")
    public String availableBooks(Model model) {
        model.addAttribute("books", bookRepository.findByAvailableTrue());
        return "index";
    }
}
