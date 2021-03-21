package ru.otus.dbmigration.tool.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "genres")
public class MongoGenre {

    @Id
    private String id;

    private String name;

    @Override
    public String toString() {
        return id + ", " + name;
    }
}
