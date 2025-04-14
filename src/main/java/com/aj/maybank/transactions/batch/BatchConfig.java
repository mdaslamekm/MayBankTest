package com.aj.maybank.transactions.batch;

import com.aj.maybank.transactions.entity.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Transaction> reader() {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionReader")
                .resource(new ClassPathResource("dataSource.txt"))
                .delimited()
                .delimiter("|")
                .names("accountNumber", "trxAmount", "description", "trxDate", "trxTime", "customerId")
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> {
                    Transaction transaction = new Transaction();
                    transaction.setAccountNumber(fieldSet.readString("accountNumber"));
                    transaction.setTrxAmount(fieldSet.readBigDecimal("trxAmount"));
                    transaction.setDescription(fieldSet.readString("description"));

                    // Parse date/time using Java 8 DateTimeFormatter
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                    // Read raw strings and trim whitespace
                    String rawDate = fieldSet.readString("trxDate").trim();
                    String rawTime = fieldSet.readString("trxTime").trim();

                    transaction.setTrxDate(LocalDate.parse(rawDate, dateFormatter));
                    transaction.setTrxTime(LocalTime.parse(rawTime, timeFormatter));
                    transaction.setCustomerId(fieldSet.readString("customerId"));
                    return transaction;
                })
                .build();
    }


    @Bean
    public JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .sql("INSERT INTO transaction (account_number, trx_amount, description, trx_date, trx_time, customer_id) " +
                        "VALUES (:accountNumber, :trxAmount, :description, :trxDate, :trxTime, :customerId)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Job importTransactionsJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importTransactionsJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      FlatFileItemReader<Transaction> reader, JdbcBatchItemWriter<Transaction> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Transaction, Transaction>chunk(10, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }
}