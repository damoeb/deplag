package com.ausserferner.deplag.service;

import com.ausserferner.deplag.DeplagException;
import com.ausserferner.deplag.Document;
import com.ausserferner.deplag.index.IndexWriter;
import com.ausserferner.deplag.store.Fragment;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.server.HessianServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Controller
public class DefaultIndexService extends HessianServlet implements IndexService {

    @Autowired
    @Qualifier("indexWriter")
    private IndexWriter index;

    @Override
    public String add(Document document, InputStream data) throws DeplagException {
        document.setReader(new InputStreamReader(data));
        return index.add(document);
    }

    @Override
    public Map<String, List<Fragment>> match(String text) throws DeplagException {
        return index.match(text);
    }

    @Override
    public Map<String, Object> status() {
        return index.getStatus();
    }

    public IndexWriter getIndex() {
        return index;
    }

    public void setIndex(IndexWriter index) {
        this.index = index;
    }
}
