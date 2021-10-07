package com.pj.OfferBatch.domain.model;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table
@Getter
@Setter
@ToString
public class Offer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private BigDecimal desconto;
    private Boolean active;

    public Offer(Long id, String descricao, BigDecimal desconto, Boolean active) {
        this.id = id;
        this.descricao = descricao;
        this.desconto = desconto;
        this.active = active;
    }

    public Offer() {
    }
}
