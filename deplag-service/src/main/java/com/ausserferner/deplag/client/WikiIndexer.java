package com.ausserferner.deplag.client;

import com.ausserferner.deplag.DeplagException;
import com.ausserferner.deplag.Document;
import com.ausserferner.deplag.index.IndexWriter;
import com.ausserferner.deplag.service.IndexService;
import com.caucho.hessian.client.HessianProxyFactory;
import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.mediawiki.MediaWikiDialect;
import org.jsoup.Jsoup;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class WikiIndexer {

    private static long consumedBytes = 0;

    public static void main(String[] args) throws IOException {

        String serviceUrl = "http://localhost:8080/deplag/remoting/IndexService";

        HessianProxyFactory hfactory = new HessianProxyFactory();
        IndexService index = (IndexService) hfactory.create(IndexService.class, serviceUrl);

        String filename = "/home/damoeb/Downloads/dewiki-20120225-pages-articles.xml";

        final int period = 100;

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                System.out.println("thoughput " + (getConsumedBytes() / 1024d / period) + " KB");
                setConsumedBytes(0);
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, period * 1000, period * 1000);

        InputStream in = null;
        XMLStreamReader parser = null;
        int count = 0;
        Document d = new Document();
        MarkupParser wikiParser = new MarkupParser(new MediaWikiDialect());

        long init = System.currentTimeMillis();
        boolean isAlive = true;

        try {
            StringBuilder builder = new StringBuilder(30000);

            in = new FileInputStream(filename);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            parser = factory.createXMLStreamReader(in);
            String tag = null, value = null;

            while (isAlive && parser.hasNext()) {

                switch (parser.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT:
                        tag = parser.getLocalName();
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        if (tag == null) break;
                        if (tag.equalsIgnoreCase("title")) {
                            if (value != null && !value.equals(parser.getText()) && builder.length() > 0) {
                                if (value.length() < 70 && !value.contains(":")) {

                                    try {
                                        count++;

                                        if (count > 10) {
                                            System.out.println(index.status());
                                            isAlive = false;
                                            break;
                                        }

                                        String url = "http://de.wikipedia.org/wiki/" + value.replace(' ', '_');
                                        d.setUrl(url);
                                        System.out.println(count + ": " + url);

                                        StringWriter writer = new StringWriter();

                                        HtmlDocumentBuilder wikiBuilder = new HtmlDocumentBuilder(writer);
                                        wikiBuilder.setEmitAsDocument(false);

                                        wikiParser.setBuilder(wikiBuilder);
                                        wikiParser.parse(builder.toString());
                                        
//                                        String plainText = Jsoup.parse(writer.getBuffer().toString()).text();
//                                        byte[] data = plainText.getBytes();
                                        byte[] data = writer.getBuffer().toString().getBytes();

                                        setConsumedBytes(data.length + getConsumedBytes());
                                        index.add(d, new ByteArrayInputStream(data));

                                    } catch (Exception de) {
                                        de.printStackTrace();
                                    }
                                }
                                builder.delete(0, builder.length() - 1);
                            }
                            value = parser.getText();
                        } else if (tag.equalsIgnoreCase("text")) {
                            builder.append(parser.getText());
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        tag = null;
                        break;
                }
                parser.next();
            }


        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            System.out.println("time consumed: " + (System.currentTimeMillis() - init) / 1000d + " sec");

            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    //
                }
            }
            if (parser != null) {
                try {
                    parser.close();
                } catch (XMLStreamException e) {
                    //
                }
            }
        }

        timer.cancel();
    }

    public static long getConsumedBytes() {
        return consumedBytes;
    }

    public static synchronized void setConsumedBytes(long _consumedBytes) {
        consumedBytes = _consumedBytes;
    }
}
