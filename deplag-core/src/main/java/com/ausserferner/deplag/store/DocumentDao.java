package com.ausserferner.deplag.store;

import com.ausserferner.deplag.Document;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;


@Repository("documentDao")
public class DocumentDao {

    private static final Logger LOGGER = Logger.getLogger(DocumentDao.class);

    private JdbcTemplate jdbcTemplate;

    public void onInit() {
    }

    public void store(final String feedUrl, final Document document) {

        LOGGER.debug("store article " + feedUrl);

        // store in db
        int seedId = jdbcTemplate.queryForInt("SELECT `pk_seed_id` FROM `seeds` WHERE `url`=?", new Object[]{feedUrl});
        jdbcTemplate.update("UPDATE `seeds` SET `last_update` = NOW() WHERE `pk_seed_id`=?", new Object[]{seedId});
//    for (Article article : articles) {
//      jdbcTemplate.update(
//          "INSERT IGNORE INTO `articles` (`fk_seed_id`, `url`, `title`, `text`, `html`, `last_update` ) VALUES (?, ?, ?, ?, ?, NOW())",
//          new Object[] { seedId, article.uri(), article.content().title(), article.content().text(), article.content().html() });
//    }
    }

    public void setDataSource(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

}
