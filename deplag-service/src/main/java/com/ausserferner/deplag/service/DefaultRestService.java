package com.ausserferner.deplag.service;

import com.ausserferner.deplag.DeplagException;
import com.ausserferner.deplag.Document;
import com.ausserferner.deplag.ErrorCode;
import com.ausserferner.deplag.index.IndexWriter;
import com.ausserferner.deplag.store.Fragment;
import com.ausserferner.deplag.store.Store;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/deplag")
public class DefaultRestService extends AbstractRestService {

    @Autowired
    @Qualifier("indexWriter")
    private IndexWriter index;

    @RequestMapping(value="/index", method = RequestMethod.POST)
    public
    @ResponseBody
    RestResponse
    index(
            @RequestBody String document
    ) {
        try {
            final long beginOfOperation = System.currentTimeMillis();
    
            Document d = new Document();
            d.setHash(IndexWriter.toMd5Hash(document));
            d.setReader(new StringReader(document));
            
            String documentId = index.add(d);
    
            RestResponse response = new RestResponse();
            response.put("documentId", documentId);
            response.put("operationTime", System.currentTimeMillis() - beginOfOperation);
    
            return response;

        } catch(Exception e) {
            return new RestResponse(e);
        }
    }


    @RequestMapping(value = "/match", method = RequestMethod.POST)
    public
    @ResponseBody
    RestResponse
    match(
            @RequestBody String text
    ) throws DeplagException {

        final long beginOfOperation = System.currentTimeMillis();

        RestResponse response = new RestResponse();
        response.put("fragments", index.match(text));
        response.put("operationTime", System.currentTimeMillis() - beginOfOperation);

        return response;
    }

    @RequestMapping(value = "/match-file", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse matchFile (
            @RequestParam("file") MultipartFile file
    ) throws IOException, DeplagException {

        final long beginOfOperation = System.currentTimeMillis();

        RestResponse response = new RestResponse();
//        switch(file.getContentType()) {
//            case "":
//                response.put("fragments", index.match(new String(file.getBytes())));
//                break;
//            default:
//                break;
//        }
        response.put("operationTime", System.currentTimeMillis() - beginOfOperation);

        return response;

    }
    
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public
    @ResponseBody
    RestResponse
    status() {
        return new RestResponse(index.getStatus());
    }

}
