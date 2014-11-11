package com.ausserferner.deplag.store;

import com.ausserferner.deplag.Constant;
import com.ausserferner.deplag.Document;
import com.ausserferner.deplag.index.Hit;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.zip.GZIPInputStream;

@Controller
public class MySqlStore implements Store {

    private static Logger LOGGER = Logger.getLogger(MySqlStore.class);

    private static final int CHUNK_SIZE = 16384;

    @Autowired
    @Qualifier("txManager")
    private DataSourceTransactionManager txManager;

    final LobHandler lobHandler = new DefaultLobHandler();  // reusable object

    private JdbcTemplate jdbcTemplate;
    private final Charset charset;
    private final int chunkSize;
    private boolean useCompression = false;

    public MySqlStore() {
        charset = Charset.forName("UTF-8");
        chunkSize = CHUNK_SIZE;
    }

    public boolean isUseCompression() {
        return useCompression;
    }

    @Override
    public void store(final Document document, Reader reader) {

        final String documentId = document.getId();
        LOGGER.info(String.format("store document '%s'", documentId));

        KeyHolder keyHolder = new GeneratedKeyHolder();

        delete(documentId);

        LOGGER.debug(String.format("persist document '%s'", document.getId()));
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement("INSERT INTO `documents` ( `identifier`, `hash`, `url` ) VALUES ( ?, ?, ? )", new String[]{"id", "hash", "url"});

                        ps.setString(1, documentId);
                        ps.setString(2, document.getHash());
                        ps.setString(3, document.getUrl());
                        return ps;
                    }
                },
                keyHolder);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        txManager.setDataSource(jdbcTemplate.getDataSource());
        TransactionStatus status = txManager.getTransaction(def);

//        ChunkProvider provider = new ChunkProvider(reader, chunkSize);
        try {
            int chunkId = 0;

            if (useCompression) {
                PackedChunkProvider provider = new PackedChunkProvider(reader, chunkSize);
//                long totalBytes = 0;

                while (provider.hasNextChunk()) {
                    PackedChunk chunk = provider.nextChunk();
                    if (chunk == null) {
                        break;
                    }
//                totalBytes += chunk.getLength();
//                storeChunk(chunkId++, keyHolder.getKey().intValue(), chunk, charset);
//                    storeChunk(chunkId++, keyHolder.getKey().intValue(), chunk.getPackedPayload(), charset);
                storeChunk(chunkId++, keyHolder.getKey().intValue(), ((PackedChunk)chunk).getPackedPayload(), charset);
                }
//            LOGGER.info(String.format("compression for document '%s': %s%%", documentId, ((double) totalBytes) / (chunkId * chunkSize)));

            } else {

                ChunkProvider provider = new ChunkProvider(reader, chunkSize);
                while (provider.hasNextChunk()) {
                    Chunk chunk = provider.nextChunk();
                    if (chunk == null) {
                        break;
                    }
                    storeChunk(chunkId++, keyHolder.getKey().intValue(), chunk.getPayload().getBytes(), charset);
                }
            }


            LOGGER.info(String.format("commit %s chunks for document '%s'", chunkId, documentId));

            txManager.commit(status);

        } catch (IOException e) {
            txManager.rollback(status);
            LOGGER.error(String.format("storing chunks for doc %s failed: %s", documentId, e.getMessage()));
            LOGGER.debug(e);

        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //
            }
        }
    }

    @Override
    public void delete(String documentId) {
        LOGGER.debug(String.format("remove document '%s' from store", documentId));
        jdbcTemplate.update("DELETE c.*, d.* FROM `chunks` c, `documents` d WHERE c.`fk_document_id`=d.`pk_id` AND d.`identifier` = ?", documentId);
    }

    protected void storeChunk(final int chunkId, final long fkDocumentId, final byte[] data, final Charset charset) throws IOException {

        LOGGER.debug(String.format("store chunk '%s' with %skb", chunkId, data.length / 1000d));

        // compressed blob
        int contentLength = -1; // fake, will be ignored anyway
        final SqlLobValue sqlLobValue = new SqlLobValue(new ByteArrayInputStream(data),
                contentLength, new DefaultLobHandler() {
            public LobCreator getLobCreator() {
                return new DefaultLobHandler.DefaultLobCreator() {
                    public void setBlobAsBinaryStream(PreparedStatement ps, int paramIndex, InputStream binaryStream, int contentLength) throws SQLException {
                        // The contentLength parameter should be the -1 we provided
                        // earlier.
                        // You now have direct access to the PreparedStatement.
                        // Simply avoid calling setBinaryStream(int, InputStream, int)
                        // in favor of setBinaryStream(int, InputStream).
                        ps.setBinaryStream(paramIndex, binaryStream);
                    }
                };
            }
        });

        jdbcTemplate.update(
                "INSERT INTO `chunks` (`chunk_id_in_document`, `fk_document_id`, `content`) VALUES (?, ?, ?)",
                new Object[]{chunkId, fkDocumentId, sqlLobValue},
                new int[]{Types.INTEGER, Types.INTEGER, Types.BLOB});

    }

    @Override
    public Fragment getFragment(final Hit hit) {

        final Range range = hit.getRangeOnDocument();

        final int length = range.getLength() + Constant.PRE_TEXT_FRAGMENT + Constant.POST_TEXT_FRAGMENT;
//      highlighter: buffer on both sides
        final StringBuilder builder = new StringBuilder(length);
        final int fromPosition = range.getPosition() == 0 ? range.getPosition() : range.getPosition() - Constant.PRE_TEXT_FRAGMENT;
        final int toPosition = (range.getPosition() + range.getLength() + Constant.POST_TEXT_FRAGMENT);

        final int fromChunk = fromPosition / chunkSize;
        final int toChunk = toPosition / chunkSize;

        jdbcTemplate.query("SELECT `chunk_id_in_document`, `content` FROM `chunks` c INNER JOIN `documents` d ON d.`pk_id`=c.`fk_document_id` WHERE d.identifier = ? AND `chunk_id_in_document` BETWEEN ? AND ?",
                new Object[]{hit.getDocumentId(), fromChunk, toChunk},
                new RowCallbackHandler() {

                    @Override
                    public void processRow(ResultSet rs) throws SQLException {

                        Reader reader = null;

                        try {
                            final int chunkId = rs.getInt("chunk_id_in_document");

                            // gzip solution
                            if (useCompression) {
                                reader = new InputStreamReader(new GZIPInputStream(rs.getBinaryStream("content")));
                            } else {
                                reader = new InputStreamReader(rs.getBinaryStream("content"));
                            }

                            int skipPrefix = 0;
                            if (chunkId == fromChunk) {
//                                skipPrefix = range.getPosition() - fromChunk * chunkSize;
                                skipPrefix = fromPosition - fromChunk * chunkSize;
                            }

                            int c;
                            while ((c = reader.read()) != -1) {
                                if (skipPrefix > 0) {
                                    skipPrefix--;
                                    continue;
                                }
                                builder.append((char) c);
//                                if (builder.length() >= range.getLength()) {
                                if (builder.length() >= length) {
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            LOGGER.error(String.format("Cannot stream content of chunk in document '%s': %s", hit.getDocumentId(), e.getMessage()));
                            LOGGER.debug(e);
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    LOGGER.error(e.getMessage());
                                }
                            }
                        }
                    }

                });

        Fragment f = new Fragment();
        f.setDocumentId(hit.getDocumentId());
//        if(range.getLength()<Constant.PRE_TEXT_FRAGMENT) {
//            f.setPre("");
//            f.setPost("");
//            f.setText(builder.toString());
//        } else {
            f.setPre(fromPosition == 0 ? "" : builder.substring(0, Constant.PRE_TEXT_FRAGMENT));
            f.setPost(builder.substring(Math.min(length - Constant.POST_TEXT_FRAGMENT, builder.length()), builder.length()));
            int _fromPosition = fromPosition == 0 ? 0 : Constant.POST_TEXT_FRAGMENT;
            f.setText(builder.substring(_fromPosition, _fromPosition + range.getLength()));
//        }

        f.setHit(hit);
        return f;
    }

    @Override
    public long getDocumentCount() {
        return jdbcTemplate.queryForLong("SELECT COUNT(*) FROM `documents`");
    }

    @Override
    public long getChunkCount() {
        return jdbcTemplate.queryForLong("SELECT COUNT(*) FROM `chunks`");
    }

    @Override
    public Reader getDocumentStream(String documentId) {
        //todo
        return null;
    }

    @Override
    public boolean hasDocumentWithHash(String hash) {
        return !StringUtils.isBlank(hash) && jdbcTemplate.queryForInt("SELECT COUNT(*) from `documents` WHERE `hash`=?", hash)==1;
    }

    @Override
    public boolean hasDocumentWithUrl(String url) {
        return !StringUtils.isBlank(url) && jdbcTemplate.queryForInt("SELECT COUNT(*) from `documents` WHERE `url`=?", url) == 1;
    }
    
    private Long lastDocumentId = null;

    @Override
    public String newDocumentId() {
        if (lastDocumentId == null) {
            lastDocumentId = getLastDocumentId();
        }
        return String.valueOf(lastDocumentId++);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setDataSource(final DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setFetchSize(Integer.MIN_VALUE);
    }

    public Long getLastDocumentId() {
        try {
            return jdbcTemplate.queryForLong("SELECT `pk_id` FROM `documents` ORDER BY `pk_id` ASC LIMIT 1");
        } catch (EmptyResultDataAccessException e) {
            return 1l;
        }
    }

    public DataSourceTransactionManager getTxManager() {
        return txManager;
    }

    public void setTxManager(DataSourceTransactionManager txManager) {
        this.txManager = txManager;
    }
}
