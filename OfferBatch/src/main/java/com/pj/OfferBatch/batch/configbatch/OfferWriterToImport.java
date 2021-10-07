package com.pj.OfferBatch.batch.configbatch;

import com.pj.OfferBatch.domain.model.Offer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableBatchProcessing
public class OfferWriterToImport {

    @Autowired
    public DataSource dataSource;

    @Bean
    public JdbcBatchItemWriter<Offer> offerWriterToImportToDb(){
        JdbcBatchItemWriter<Offer> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO offer (descricao, desconto, active) VALUES (:descricao, :desconto, :active)");
        writer.setDataSource(this.dataSource);
        log.info("Writing {}", writer);
        return writer;
    }

}
