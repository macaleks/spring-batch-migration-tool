package ru.otus.dbmigration.tool.shell;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import static ru.otus.dbmigration.tool.config.JobConfig.IMPORT_JOB_NAME;

@ShellComponent
public class ShellCommand {

    private final Job importDataJob;

    private final JobLauncher jobLauncher;
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;

    public ShellCommand(Job importData, JobLauncher jobLauncher, JobOperator jobOperator, JobExplorer jobExplorer) {
        this.importDataJob = importData;
        this.jobLauncher = jobLauncher;
        this.jobOperator = jobOperator;
        this.jobExplorer = jobExplorer;
    }

    @ShellMethod(value = "startWithJobLauncher", key = "jl")
    public void startMigrationWithJobLauncher() throws Exception {
        JobExecution execution = jobLauncher.run(importDataJob,new JobParameters());
        System.out.println(execution);
    }

    @ShellMethod(value = "startWithJobOperator", key = "jo")
    public void startMigrationWithJobOperator() throws Exception {
        Long executionId = jobOperator.start(IMPORT_JOB_NAME, "");
        System.out.println(jobOperator.getSummary(executionId));
    }

    @ShellMethod(value = "showInfo", key = "i")
    public void showInfo() {
        System.out.println(jobExplorer.getJobNames());
        System.out.println(jobExplorer.getLastJobInstance(IMPORT_JOB_NAME));
    }
}
