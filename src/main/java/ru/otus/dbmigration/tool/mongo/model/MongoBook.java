package ru.otus.dbmigration.tool.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class MongoBook {

    @Id
    private String id;

    private String name;

    @DBRef
    private MongoAuthor author;

    @DBRef
    private MongoGenre genre;

    @Override
    public String toString() {
        return id + ", " + name + ", {" + author + "}, {" + genre + "}";
    }
}
