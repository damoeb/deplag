package com.ausserferner.deplag.service;

import com.ausserferner.deplag.DeplagException;
import com.ausserferner.deplag.Document;
import com.ausserferner.deplag.store.Fragment;
import com.caucho.hessian.io.HessianInput;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public interface IndexService {

    Map<String, List<Fragment>> match(String text) throws DeplagException;

    Map<String, Object> status();
    
    String add(Document document, InputStream data) throws DeplagException;

}
