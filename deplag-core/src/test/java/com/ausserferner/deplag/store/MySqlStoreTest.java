package com.ausserferner.deplag.store;

import com.ausserferner.deplag.Document;
import com.ausserferner.deplag.index.Hit;
import com.ausserferner.deplag.index.MongoIndex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Scanner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:application-core.xml"})
public class MySqlStoreTest {

    @Autowired
    @Qualifier("store")
    private MySqlStore store;

    @Autowired
    @Qualifier("index")
    private MongoIndex index;

    @Test
    @Rollback(true)
    public void testStore() throws FileNotFoundException {
        Scanner s = new Scanner(new FileInputStream("/home/damoeb/Downloads/diss2"));
        StringBuilder b = new StringBuilder(400);
        while(s.hasNextLine()) {
            b.append(s.nextLine());
            b.append('\n');
        }
        String data = b.toString();
        String id = store.newDocumentId();
//        Document d = new Document();
//        d.setData(data);
        Document d = new Document(new StringReader(data));
        d.setId("1");
        d.setHash("xxx");
        store.store(d, new StringReader(data));
        Hit hit = new Hit();
        hit.setDocumentId(id);
        hit.setRangeOnDocument(new Range(0,data.length()));
        Fragment f = store.getFragment(hit);
        assertEquals(data, f.getText());
        store.delete(id);

    }
}
