package com.pj.OfferBatch.util;

import com.pj.OfferBatch.domain.model.Offer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class OfferReaderToExport extends JdbcCursorItemReader<Offer> implements ItemReader<Offer> {

    public OfferReaderToExport(@Autowired DataSource dataSource) {
        setDataSource(dataSource);
        setSql("SELECT * FROM offer");
        setFetchSize(100);
        setRowMapper(new OfferRowMapper());
    }

    public class OfferRowMapper implements RowMapper<Offer> {
        @Override
        public Offer mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Offer offer = new Offer();
            offer.setId(resultSet.getLong("id"));
            offer.setDescricao(resultSet.getString("descricao"));
            offer.setDesconto(resultSet.getBigDecimal("desconto"));
            offer.setActive(resultSet.getBoolean("active"));

            return offer;
        }
    }

}
