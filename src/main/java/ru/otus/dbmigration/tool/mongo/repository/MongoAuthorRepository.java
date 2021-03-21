package ru.otus.dbmigration.tool.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.dbmigration.tool.mongo.model.MongoAuthor;

@Repository
public interface MongoAuthorRepository extends MongoRepository<MongoAuthor, String> {
}
