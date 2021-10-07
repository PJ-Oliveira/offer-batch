package com.pj.OfferBatch.util;

import com.pj.OfferBatch.domain.model.Offer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info(" Job Finished! Please, verify the results! ");

            List<Offer> results = this.jdbcTemplate.query("SELECT id, descricao, desconto, active FROM offer",
                    (resultSet, row) -> new Offer(resultSet.getLong("id"), resultSet.getString("descricao"),
                            resultSet.getBigDecimal("desconto"), resultSet.getBoolean("active")));

            for (Offer offer : results) {
                log.info("Found <" + offer.toString() + "> in the database.");
            }

        }
    }
}
