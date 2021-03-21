package ru.otus.dbmigration.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import ru.otus.dbmigration.tool.mongo.model.MongoAuthor;
import ru.otus.dbmigration.tool.mongo.model.MongoBook;
import ru.otus.dbmigration.tool.mongo.model.MongoGenre;
import ru.otus.dbmigration.tool.mongo.repository.MongoAuthorRepository;
import ru.otus.dbmigration.tool.mongo.repository.MongoBookRepository;
import ru.otus.dbmigration.tool.mongo.repository.MongoGenreRepository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SpringBatchMigrationToolApplication {

    @Autowired
    MongoBookRepository bookRepository;
    @Autowired
    MongoAuthorRepository authorRepository;
    @Autowired
    MongoGenreRepository genreRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchMigrationToolApplication.class, args);
    }

    @PostConstruct
    public void init() {
        List<MongoAuthor> authors = Arrays.asList(new MongoAuthor("1", "Neil", "Gaiman"),
                new MongoAuthor("2", "Terry", "Pratchett"),
                new MongoAuthor("3", "Lisa", "Lutz"),
                new MongoAuthor("4", "David", "Hayward"),
                new MongoAuthor("5", "Jodi", "Picoult"),
                new MongoAuthor("6", "Samantha", "Leer")
        );
        authors.forEach(authorRepository::save);

        List<MongoGenre> genres = Arrays.asList(
                new MongoGenre("1", "biography"),
                new MongoGenre("2", "fable"),
                new MongoGenre("3", "realistic"),
                new MongoGenre("4", "fiction"),
                new MongoGenre("5", "poetry"),
                new MongoGenre("6", "science fiction"),
                new MongoGenre("7", "drama"),
                new MongoGenre("8", "fantasy"),
                new MongoGenre("9", "mystery")
        );
        genres.forEach(genreRepository::save);

        List<MongoBook> books = Arrays.asList(
                new MongoBook("1", "Good Omens",
                        authorRepository.findById("1").get(),
                        genreRepository.findById("7").get()),
                new MongoBook("2", "Heads You Lose",
                        authorRepository.findById("4").get(),
                        genreRepository.findById("9").get()),
                new MongoBook("3", "Between the Lines",
                        authorRepository.findById("6").get(),
                        genreRepository.findById("1").get())
        );
        books.forEach(bookRepository::save);
    }
}
