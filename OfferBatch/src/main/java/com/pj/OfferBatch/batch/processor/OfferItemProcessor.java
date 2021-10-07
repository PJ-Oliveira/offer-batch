package com.pj.OfferBatch.batch.processor;

import com.pj.OfferBatch.domain.model.Offer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;

@Slf4j
public class OfferItemProcessor implements ItemProcessor<Offer, Offer> {

    @Override
    public Offer process(Offer offer) throws Exception {
        final Long id = offer.getId();
        final String descricao = offer.getDescricao().toUpperCase();
        final BigDecimal desconto = offer.getDesconto();
        final Boolean status = offer.getActive();
        final Offer transformed = new Offer(id, descricao, desconto, status);
        log.info("Converting ( {}",offer," ) into ( {}",transformed," )");
        return transformed;
    }


}
