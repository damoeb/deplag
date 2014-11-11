package com.ausserferner.deplag.index;

import com.ausserferner.deplag.analysis.Consumer;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * Multiplex will forward read characters to several consumers. It is geared by a active reading master
 */
public class ReaderMultiplexer extends Reader implements ConsumerProvider<Character> {

    private final Reader reader;
    private Set<Consumer<Character>> consumers = new HashSet<Consumer<Character>>(1);

    public ReaderMultiplexer(Reader reader) {
        if(reader==null) {
            throw new IllegalArgumentException("reader is null");
        }
        this.reader = reader;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public int read(CharBuffer target) throws IOException {
        return super.read(target);
    }

    @Override
    public long skip(long n) throws IOException {
        return super.skip(n);
    }

    @Override
    public boolean ready() throws IOException {
        return super.ready();
    }


    @Override
    public void reset() throws IOException {
        super.reset();
    }

    @Override
    public int read(char[] cbuf) throws IOException {
        return super.read(cbuf);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int i = 0;
        for (; i < len; i++) {
            int c = read();
            if(c==-1) {
                return i-1;
            }
            cbuf[off + i] = (char) c;
        }
        return i;
    }

    @Override
    public int read() throws IOException {
        int c = reader.read();
        if (c == -1) {
            return c;
        }
        for (Consumer<Character> consumer : consumers) {
            consumer.consume((char) c);
        }
        return c;
    }

    @Override
    public void addConsumer(Consumer<Character> consumer) {
        consumers.add(consumer);
    }

    @Override
    public Character getLastConsumed() {
        return null;
    }

}
