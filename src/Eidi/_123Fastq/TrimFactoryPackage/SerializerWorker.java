package Eidi._123Fastq.TrimFactoryPackage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class SerializerWorker implements Runnable {

    private FastqSerializer serializer;
    private ArrayBlockingQueue<Future<BlockOfRecords>> serializerQueue;
    private int recIndex;
    private AtomicBoolean complete;
    private boolean deleteComments;

    public SerializerWorker(FastqSerializer serializer, ArrayBlockingQueue<Future<BlockOfRecords>> serializerQueue, int recIndex, boolean deleteComments) {
        this.serializer = serializer;
        this.serializerQueue = serializerQueue;
        this.recIndex = recIndex;
        this.deleteComments = deleteComments;
        this.complete = new AtomicBoolean();
    }

    public boolean isComplete() {
        return complete.get();
    }

    @Override
    public void run() {

        try {

            Future<BlockOfRecords> future = serializerQueue.take();
            BlockOfRecords bor = future.get();
            List<FastqRecord> recs = bor.getTrimmedRecs().get(recIndex);

            while (recs != null) {
                for (FastqRecord rec : recs) {
                    if (deleteComments) {
                        serializer.digestedWriteRecord(rec);
                    } else {
                        serializer.writeRecord(rec);
                    }
                }

                future = serializerQueue.take();
                bor = future.get();
                recs = bor.getTrimmedRecs().get(recIndex);
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
        } finally {
            complete.set(true);
        }
    }
}
