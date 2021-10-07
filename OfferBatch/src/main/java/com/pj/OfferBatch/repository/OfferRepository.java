package com.pj.OfferBatch.repository;


import com.pj.OfferBatch.domain.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OfferRepository extends JpaRepository<Offer, Long> {
}
