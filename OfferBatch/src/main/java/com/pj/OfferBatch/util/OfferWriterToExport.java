package com.pj.OfferBatch.util;

import com.pj.OfferBatch.domain.model.Offer;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class OfferWriterToExport extends FlatFileItemWriter<Offer> {


    public OfferWriterToExport() {
        setResource(new FileSystemResource("OfferBatch/src/main/resources/output.csv"));
        setLineAggregator(getDelimitedLineAggregator());
    }

    public DelimitedLineAggregator<Offer> getDelimitedLineAggregator(){
        BeanWrapperFieldExtractor<Offer> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();
        beanWrapperFieldExtractor.setNames(new String[] {"id", "descricao", "desconto", "active"});
        DelimitedLineAggregator<Offer> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(",");
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);
        return delimitedLineAggregator;

    }
}
