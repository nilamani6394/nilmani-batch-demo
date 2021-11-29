package com.nilmani.nilmanibatchdemo.llistner

import com.nilmani.nilmanibatchdemo.model.Person
import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.util.function.Consumer


@Component
class JobCompleteNotificationListener : JobExecutionListenerSupport()  {

    val log: org.slf4j.Logger? = LoggerFactory.getLogger(JobCompleteNotificationListener::class.java)
    var jdbcTemplate : JdbcTemplate? = null

    @Autowired
    fun JobCompletionNotificationListener(jdbcTemplate: JdbcTemplate?) {
        this.jdbcTemplate = jdbcTemplate!!
    }
    override fun afterJob(jobExecution: JobExecution) {
        if (jobExecution.status === BatchStatus.COMPLETED) {
            log!!.info("!!! JOB FINISHED! Time to verify the results")
            jdbcTemplate?.query(
                "SELECT first_name, last_name FROM people"
            ) { rs: ResultSet, row: Int ->
                Person(
                    rs.getString(1),
                    rs.getString(2)
                )
            }?.forEach(Consumer { person: Person -> log!!.info("Found <$person> in the database.") })
        }
    }
}