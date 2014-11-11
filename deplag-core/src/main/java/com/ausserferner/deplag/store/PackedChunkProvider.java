package com.ausserferner.deplag.store;

import java.io.Reader;

public class PackedChunkProvider extends ChunkProvider<PackedChunk> {

    public PackedChunkProvider(Reader reader, int chunkSize) {
        super(reader,chunkSize);
    }

    @Override
    public PackedChunk nextChunk() {
        if(nextChunk==null) {
            return null;
        }
        PackedChunk chunk = new PackedChunk(nextChunk, nextChunkPosition);
        nextChunk = null;
        return chunk;
    }
}