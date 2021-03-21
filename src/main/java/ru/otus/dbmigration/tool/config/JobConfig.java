package ru.otus.dbmigration.tool.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.otus.dbmigration.tool.mongo.model.MongoAuthor;
import ru.otus.dbmigration.tool.mongo.model.MongoBook;
import ru.otus.dbmigration.tool.mongo.model.MongoGenre;

import java.util.HashMap;

@Configuration
public class JobConfig {
    private static final int CHUNK_SIZE = 1000;
    private final Logger logger = LoggerFactory.getLogger("Batch");
    public static final String IMPORT_JOB_NAME = "IMPORT_DATA_JOB";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @StepScope
    @Bean
    public MongoItemReader<MongoAuthor> authorItemReader() {
        return new MongoItemReaderBuilder<MongoAuthor>()
                .name("authorItemReader")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .targetType(MongoAuthor.class)
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public MongoItemReader<MongoGenre> genreItemReader() {
        return new MongoItemReaderBuilder<MongoGenre>()
                .name("genreItemReader")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .targetType(MongoGenre.class)
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public MongoItemReader<MongoBook> bookItemReader() {
        return new MongoItemReaderBuilder<MongoBook>()
                .name("bookItemReader")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .targetType(MongoBook.class)
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<MongoAuthor> authorWriter() {
        return new JdbcBatchItemWriterBuilder<MongoAuthor>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("insert into authors(id, first_name, second_name) values (:id, :firstName, :secondName)")
                .namedParametersJdbcTemplate(jdbcTemplate)
                .build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<MongoGenre> genreWriter() {
        return new JdbcBatchItemWriterBuilder<MongoGenre>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("insert into genres(id, name) values (:id, :name)")
                .namedParametersJdbcTemplate(jdbcTemplate)
                .build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<MongoBook> bookWriter() {
        return new JdbcBatchItemWriterBuilder<MongoBook>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .itemPreparedStatementSetter((book, ps) -> {
                    ps.setString(1, book.getId());
                    ps.setString(2, book.getName());
                    ps.setString(3, book.getAuthor().getId());
                    ps.setString(4, book.getGenre().getId());
                })
                .sql("insert into books(id, name, id_author, id_genre) values (?, ?, ?, ?)")
                .namedParametersJdbcTemplate(jdbcTemplate)
                .build();
    }

    @Bean
    public Step migrateAuthor(JdbcBatchItemWriter<MongoAuthor> writer, ItemReader<MongoAuthor> reader) {
        return stepBuilderFactory.get("migrateAuthor")
                .<MongoAuthor, MongoAuthor>chunk(CHUNK_SIZE)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Step migrateGenre(JdbcBatchItemWriter<MongoGenre> writer, ItemReader<MongoGenre> reader) {
        return stepBuilderFactory.get("migrateAuthor")
                .<MongoGenre, MongoGenre>chunk(CHUNK_SIZE)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Step migrateBook(JdbcBatchItemWriter<MongoBook> writer, ItemReader<MongoBook> reader) {
        return stepBuilderFactory.get("migrateAuthor")
                .<MongoBook, MongoBook>chunk(CHUNK_SIZE)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importDataJob(Step migrateAuthor, Step migrateGenre, Step migrateBook) {
        return jobBuilderFactory.get(IMPORT_JOB_NAME)
                .flow(migrateAuthor)
                .next(migrateGenre)
                .next(migrateBook)
                .end()
                .build();
    }
}
