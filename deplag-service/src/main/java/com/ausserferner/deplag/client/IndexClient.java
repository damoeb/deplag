package com.ausserferner.deplag.client;

import com.ausserferner.deplag.DeplagException;
import com.ausserferner.deplag.Document;
import com.ausserferner.deplag.service.IndexService;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianInput;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.util.Scanner;

public class IndexClient {

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException, DeplagException {
        String url = "http://localhost:8080/deplag/remoting/IndexService";

        HessianProxyFactory factory = new HessianProxyFactory();
        IndexService index = (IndexService) factory.create(IndexService.class, url);

        for (int i = 1; i < 2; i++) {
            Document d = new Document();
            index.add(d, new BufferedInputStream(new FileInputStream("/home/damoeb/Downloads/diss" + i)));
        }
    }
}
