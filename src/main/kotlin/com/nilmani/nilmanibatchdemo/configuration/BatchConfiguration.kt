package com.nilmani.nilmanibatchdemo.configuration

import com.nilmani.nilmanibatchdemo.llistner.JobCompleteNotificationListener
import com.nilmani.nilmanibatchdemo.model.Person
import com.nilmani.nilmanibatchdemo.processor.PersonItemProcessor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.PathResource
import javax.sql.DataSource


@Configuration
@EnableBatchProcessing
class BatchConfiguration {
    @Autowired
    private lateinit var jobBuilderFactory: JobBuilderFactory
    @Autowired
    private lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun reader(): FlatFileItemReader<Person>? {
        return FlatFileItemReaderBuilder<Person>()
            .name("personItemReader")
            .resource(PathResource("src/main/resources/simple-data.csv"))
            .delimited()
            .names(*arrayOf("firstName", "lastName"))
            .fieldSetMapper(object : BeanWrapperFieldSetMapper<Person?>() {
                init {
                    setTargetType(Person::class.java)
                }
            })
            .build()
    }
    @Bean
    fun processor():PersonItemProcessor{
        return PersonItemProcessor()
    }
    @Bean
    fun writer(dataSource: DataSource?): JdbcBatchItemWriter<Person>? {
        return JdbcBatchItemWriterBuilder<Person>()
            .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider())
            .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
            .dataSource(dataSource!!)
            .build()
    }

    @Bean
    fun importUserJob(listener: JobCompleteNotificationListener, step1: Step): Job? {
        return jobBuilderFactory["importUserJob"]
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build()
    }

    @Bean
    fun step1(writer: JdbcBatchItemWriter<Person>?): Step? {
        return stepBuilderFactory["step1"]
            .chunk<Person, Person>(10)
            .reader(reader()!!)
            .processor(processor())
            .writer(writer!!)
            .build()
    }
}


