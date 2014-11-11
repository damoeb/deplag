package com.ausserferner.deplag.store;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;

public class ChunkProvider<T extends Chunk> {

    private final Reader reader;
    protected final int chunkSize;

    protected String nextChunk = null;
    private int cursor = 0;
    protected int nextChunkPosition = 0;

    public ChunkProvider(Reader reader, int chunkSize) {
        this.reader = reader;
        this.chunkSize = chunkSize;
    }

    public Chunk nextChunk() {
        Chunk chunk = new Chunk(nextChunk, nextChunkPosition);
        nextChunk = null;
        return chunk;
    }

    public boolean hasNextChunk() throws IOException {

        if(nextChunk==null) {

            CharBuffer cb = CharBuffer.allocate(chunkSize);
            int read = reader.read(cb);
            cb.flip();
            if(read>0) {
                nextChunk = cb.toString();
                nextChunkPosition = cursor;
                cursor += nextChunk.length();
            }
        }

        return nextChunk!=null;
    }

    public int getChunkSize() {
        return chunkSize;
    }


    /*
    public static void main(String[] args) throws IOException {

        String text = "Nathalie Bonvin versucht gerade elf Millionen Menschen zu helfen. Sie arbeitet in Senegals Hauptstadt Dakar, als Beauftragte für Ernährungssicherheit des internationalen Roten Kreuzes in der Sahelzone. Elf Millionen Menschen haben dort derzeit deutlich zu wenig zu essen, schätzt das Rote Kreuz. Bald könnten es 23 Millionen sein. \"Wenn wir nicht bald etwas tun, dann wird das Sterben beginnen\", sagt Bonvin. Bereits im Herbst 2011 warnten zahlreiche Hilfsorganisationen, dass der Region eine Hungersnot drohe, wenn nicht genügend Hilfe eintreffe. Das Rote Kreuz bat damals um etwa vier Millionen Euro. Eine hat es bisher bekommen. Viele Bewohner der Sahelzone müssen jedes Jahr eine Zeitlang hungern, meist etwa zwei Monate, wenn die letzte Ernte verbraucht ist und die nächste noch nicht eingebracht. Heuer könnten es fünf Monate werden - zu lang für ohnehin geschwächte Menschen.";

        ChunkProvider cp = new ChunkProvider(new StringReader(text), 3);
//        ChunkProvider cp = new PackedChunkProvider(new StringReader(text), 3);

        StringBuilder b = new StringBuilder(text.length());

        while(cp.hasNextChunk()) {
            Chunk c = cp.nextChunk();
            b.append(c.getPayload());
            System.out.printf("%s", c.getPayload());
            if(!text.substring(c.getPosition(), c.getPosition()+c.getLength()).equals(c.getPayload())) {
                throw new IllegalArgumentException("wefwef");
            }
        }
        if(!b.toString().equals(text)) {
            throw new IllegalArgumentException("wefwef");
        }

    }
    */

}