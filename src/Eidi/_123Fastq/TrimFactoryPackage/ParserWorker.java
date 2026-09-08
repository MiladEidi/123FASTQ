package Eidi._123Fastq.TrimFactoryPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JProgressBar;

public class ParserWorker implements Runnable {

    public static final int BLOCKSIZE = 1000;

    private javax.swing.JProgressBar progbar;
    private FastqParser parser;
    private ArrayBlockingQueue<List<FastqRecord>> parserQueue;
    private AtomicBoolean complete;
    private int progressM;
    private boolean blahblah;

    public ParserWorker(JProgressBar progBar, FastqParser parser, ArrayBlockingQueue<List<FastqRecord>> parserQueue, boolean BlahBlah) {
        this.progbar = progBar;
        this.parser = parser;
        this.parserQueue = parserQueue;
        this.complete = new AtomicBoolean();
        this.blahblah = BlahBlah;
    }

    public boolean isComplete() {
        return complete.get();
    }

    public void progressBarSetter(int progress) {
        if (progress > progressM) {
            progressM = progress;
        }
        progbar.setValue(progressM);
    }

    @Override
    public void run() {
        try {
            List<FastqRecord> recs = new ArrayList<>(BLOCKSIZE);
            while (parser.hasNext()) {
                if (blahblah) {
                    int progress = parser.getProgress();
                    if (progress % 5 == 0) {
                        progressBarSetter(progress);
                    }
                }
                recs.add(parser.next());
                if (recs.size() >= BLOCKSIZE) {
                    parserQueue.put(recs);
                    recs = new ArrayList<>();
                }
            }
            if (recs.size() > 0) {
                parserQueue.put(recs);
            }
        } catch (IOException | InterruptedException | RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            complete.set(true);
            try {
                parserQueue.put(new ArrayList<>());
            } catch (InterruptedException | RuntimeException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
