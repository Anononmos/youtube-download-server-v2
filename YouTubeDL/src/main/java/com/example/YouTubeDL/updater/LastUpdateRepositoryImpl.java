package com.example.YouTubeDL.updater;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public class LastUpdateRepositoryImpl implements LastUpdateRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 
     * @param date
     * @return
     */
    public int updateDate() {
        String cmd = "UPDATE last_update SET date=?";

        return jdbcTemplate.update(cmd, LocalDate.now());
    }    

    /**
     * 
     * @return
     */
    public boolean canUpdate() {
        // Able to update if the date stored is more than 2 weeks ago
        // Check if the query returns 1 row

        RowCountCallbackHandler countCallbackHandler = new RowCountCallbackHandler();

        String cmd = "SELECT date FROM last_update WHERE date <= now() - interval '14 days'";
        jdbcTemplate.query(cmd, countCallbackHandler);

        return countCallbackHandler.getRowCount() > 0;
    }
}
