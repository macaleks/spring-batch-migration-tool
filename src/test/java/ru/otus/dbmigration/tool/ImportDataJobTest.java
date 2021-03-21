package ru.otus.dbmigration.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
class ImportDataJobTest {

    private static final String IMPORT_DATA_JOB = "IMPORT_DATA_JOB";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testJob() throws Exception {
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(IMPORT_DATA_JOB);

        int authorsCountBefore = jdbcTemplate.queryForObject("select count(*) from authors", Integer.class);
        int genresCountBefore = jdbcTemplate.queryForObject("select count(*) from genres", Integer.class);
        int booksCountBefore = jdbcTemplate.queryForObject("select count(*) from books", Integer.class);

        assertThat(authorsCountBefore)
                .isEqualTo(genresCountBefore)
                .isEqualTo(booksCountBefore)
                .isEqualTo(0);

        JobExecution execution = jobLauncherTestUtils.launchJob();
        assertThat(execution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        int authorsCountAfter = jdbcTemplate.queryForObject("select count(*) from authors", Integer.class);
        int genresCountAfter = jdbcTemplate.queryForObject("select count(*) from genres", Integer.class);
        int booksCountAfter = jdbcTemplate.queryForObject("select count(*) from books", Integer.class);

        assertThat(authorsCountAfter).isEqualTo(6);
        assertThat(genresCountAfter).isEqualTo(9);
        assertThat(booksCountAfter).isEqualTo(3);

    }

}
