package com.pj.OfferBatch.batch.configbatch;

import com.pj.OfferBatch.domain.model.Offer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class OfferReaderToImport {
    @Bean
    public FlatFileItemReader<Offer> itemOfferReaderToImport() {
        FlatFileItemReader<Offer> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("imput.csv"));
        flatFileItemReader.setLineMapper(new DefaultLineMapper<Offer>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {setNames(new String[] {"descricao", "desconto", "active"});}
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Offer>() {
                    {setTargetType(Offer.class);}
                });
            }
        });
        log.info("Reading {}", flatFileItemReader);
        return flatFileItemReader;
    }
}
