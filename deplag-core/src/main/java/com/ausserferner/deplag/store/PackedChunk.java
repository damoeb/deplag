package com.ausserferner.deplag.store;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

public class PackedChunk extends Chunk {

    private byte[] packed = null;

    public PackedChunk(String payload, int position) {
        super(payload, position);
    }

    public byte[] getPackedPayload() throws IOException {

        if (packed==null) {
            try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
                 GZIPOutputStream gout = new GZIPOutputStream(bout) ) {
                gout.write(getPayload().getBytes());
                packed = bout.toByteArray();
            }

//            byte[] buffer = getPayload().getBytes();
//            Deflater def = new Deflater(Deflater.BEST_SPEED);
//            try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
//                 DeflaterOutputStream gout = new DeflaterOutputStream(bout,def, buffer.length) ) {
//                gout.write(buffer);
//                packed = bout.toByteArray();
//            }
        }

        return packed;
    }

}