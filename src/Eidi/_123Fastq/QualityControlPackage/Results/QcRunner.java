package Eidi._123Fastq.QualityControlPackage.Results;

import Eidi._123Fastq.QualityControlPackage.Modules.AdapterContent;
import Eidi._123Fastq.QualityControlPackage.Modules.BasicStats;
import Eidi._123Fastq.QualityControlPackage.Modules.DistributionOfMeanQualitiesPerReadLengths;
import Eidi._123Fastq.QualityControlPackage.Modules.DuplicationLevel;
import Eidi._123Fastq.QualityControlPackage.Modules.KmerContent;
import Eidi._123Fastq.QualityControlPackage.Modules.MergeableQCModule;
import Eidi._123Fastq.QualityControlPackage.Modules.NContent;
import Eidi._123Fastq.QualityControlPackage.Modules.OverRepresentedSeqs;
import Eidi._123Fastq.QualityControlPackage.Modules.PerBaseQualityScores;
import Eidi._123Fastq.QualityControlPackage.Modules.PerBaseSequenceContent;
import Eidi._123Fastq.QualityControlPackage.Modules.PerSequenceGCContent;
import Eidi._123Fastq.QualityControlPackage.Modules.PerSequenceQualityScores;
import Eidi._123Fastq.QualityControlPackage.Modules.PerTileQualityScores;
import Eidi._123Fastq.QualityControlPackage.Modules.QCModule;
import Eidi._123Fastq.QualityControlPackage.Modules.SequenceLengthDistribution;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFile;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFormatException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Runs QC analysis using a true producer-consumer parallel architecture.
 *
 * Each worker thread receives its own complete copy of every QC module and
 * processes a chunk of sequences end-to-end (no serial-module bottleneck on
 * the main thread).  After all workers finish, results are merged into the
 * master module set on the main thread.
 */
public class QcRunner {

    private static final int MIN_THREADS = 1;
    /**
     * Number of sequences per work chunk.  Larger values reduce queue
     * overhead; smaller values give finer load-balancing granularity.
     */
    private static final int CHUNK_SIZE = 50_000;

    public static int defaultThreadCount() {
        Integer configured = readConfiguredThreadCount("123fastq.qc.threads");
        if (configured != null) {
            return configured;
        }
        configured = readConfiguredThreadCount("123FASTQ_QC_THREADS");
        if (configured != null) {
            return configured;
        }
        return Math.max(MIN_THREADS, Runtime.getRuntime().availableProcessors());
    }

    private static Integer readConfiguredThreadCount(String key) {
        String value = System.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(key);
        }
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Math.max(MIN_THREADS, Integer.parseInt(value.trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public QcAnalysisResult run(SequenceFile sequenceFile, QCModule[] modules, int requestedThreads, QcProgressListener progressListener)
            throws SequenceFormatException, InterruptedException {
        int threadCount = Math.max(MIN_THREADS, requestedThreads);
        if (threadCount == MIN_THREADS) {
            return runSerial(sequenceFile, modules, progressListener);
        }
        return runParallel(sequenceFile, modules, threadCount, progressListener);
    }

    // -------------------------------------------------------------------------
    // Serial path (single-threaded, unchanged in behaviour)
    // -------------------------------------------------------------------------

    private QcAnalysisResult runSerial(SequenceFile sequenceFile, QCModule[] modules, QcProgressListener progressListener)
            throws SequenceFormatException, InterruptedException {
        int seqCount = 0;
        while (sequenceFile.hasNext()) {
            checkInterrupted();
            Sequence sequence = sequenceFile.next();
            seqCount++;
            processSequence(modules, sequence);
            publishProgress(sequenceFile, progressListener, seqCount);
        }
        return new QcAnalysisResult(modules, seqCount);
    }

    // -------------------------------------------------------------------------
    // Parallel path — producer-consumer with per-worker module instances
    // -------------------------------------------------------------------------

    private QcAnalysisResult runParallel(SequenceFile sequenceFile, QCModule[] modules, int threadCount, QcProgressListener progressListener)
            throws SequenceFormatException, InterruptedException {

        // An empty list used as a poison-pill sentinel (identity comparison).
        final List<Sequence> sentinel = Collections.emptyList();

        // Bounded queue provides natural backpressure so the reader thread
        // does not allocate unbounded memory when workers are slower than I/O.
        BlockingQueue<List<Sequence>> workQueue = new ArrayBlockingQueue<>(threadCount * 4 + 1);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<QCModule[]>> futures = new ArrayList<>(threadCount);

        // Each worker gets its own complete copy of every module — no shared
        // state, so no synchronisation is needed inside processSequence().
        for (int i = 0; i < threadCount; i++) {
            QCModule[] workerModules = createModuleCopies(modules);
            futures.add(executor.submit(new WorkerTask(workerModules, workQueue, sentinel)));
        }

        int seqCount = 0;
        List<Sequence> currentChunk = new ArrayList<>(CHUNK_SIZE);

        try {
            // Main thread: read the file and feed chunks to workers.
            while (sequenceFile.hasNext()) {
                checkInterrupted();
                currentChunk.add(sequenceFile.next());
                seqCount++;

                if (currentChunk.size() == CHUNK_SIZE) {
                    enqueue(workQueue, currentChunk);
                    currentChunk = new ArrayList<>(CHUNK_SIZE);
                }
                publishProgress(sequenceFile, progressListener, seqCount);
            }

            // Flush last (possibly partial) chunk.
            if (!currentChunk.isEmpty()) {
                enqueue(workQueue, currentChunk);
            }

            // Send one sentinel per worker so each exits cleanly.
            for (int i = 0; i < threadCount; i++) {
                enqueue(workQueue, sentinel);
            }

            // Collect worker results and merge into the master module set.
            for (Future<QCModule[]> future : futures) {
                QCModule[] workerModules = future.get();
                mergeAllModules(workerModules, modules);
            }

            return new QcAnalysisResult(modules, seqCount);

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new IllegalStateException("QC worker failed", cause);
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Non-blocking offer loop so that the main thread remains interruptible
     * even when the work queue is full.
     */
    private void enqueue(BlockingQueue<List<Sequence>> queue, List<Sequence> chunk) throws InterruptedException {
        while (!queue.offer(chunk, 100, TimeUnit.MILLISECONDS)) {
            checkInterrupted();
        }
    }

    // -------------------------------------------------------------------------
    // Module copying — creates a fresh, independent instance of every module
    // -------------------------------------------------------------------------

    /**
     * Creates one complete copy of the module array for a single worker.
     *
     * OverRepresentedSeqs and DuplicationLevel are always created as a linked
     * pair (DuplicationLevel derives all its data from OverRepresentedSeqs).
     */
    private QCModule[] createModuleCopies(QCModule[] modules) {
        QCModule[] copies = new QCModule[modules.length];
        OverRepresentedSeqs overRepCopy = null;
        int dupLevelIndex = -1;

        for (int i = 0; i < modules.length; i++) {
            QCModule m = modules[i];
            if (m instanceof OverRepresentedSeqs) {
                overRepCopy = new OverRepresentedSeqs();
                copies[i] = overRepCopy;
            } else if (m instanceof DuplicationLevel) {
                // Must be linked to its OverRepresentedSeqs — defer until
                // we know which OverRepresentedSeqs copy to link to.
                dupLevelIndex = i;
            } else if (m instanceof BasicStats) {
                copies[i] = new BasicStats(((BasicStats) m).getName());
            } else if (m instanceof AdapterContent) {
                copies[i] = new AdapterContent();
            } else if (m instanceof KmerContent) {
                copies[i] = new KmerContent();
            } else if (m instanceof PerTileQualityScores) {
                copies[i] = new PerTileQualityScores();
            } else if (m instanceof PerBaseQualityScores) {
                copies[i] = new PerBaseQualityScores();
            } else if (m instanceof DistributionOfMeanQualitiesPerReadLengths) {
                copies[i] = new DistributionOfMeanQualitiesPerReadLengths();
            } else if (m instanceof PerSequenceQualityScores) {
                copies[i] = new PerSequenceQualityScores();
            } else if (m instanceof PerBaseSequenceContent) {
                copies[i] = new PerBaseSequenceContent();
            } else if (m instanceof PerSequenceGCContent) {
                copies[i] = new PerSequenceGCContent();
            } else if (m instanceof NContent) {
                copies[i] = new NContent();
            } else if (m instanceof SequenceLengthDistribution) {
                copies[i] = new SequenceLengthDistribution();
            } else {
                throw new IllegalArgumentException("Unknown QC module type: " + m.getClass().getName());
            }
        }

        // Wire the deferred DuplicationLevel to its OverRepresentedSeqs copy.
        if (dupLevelIndex >= 0 && overRepCopy != null) {
            copies[dupLevelIndex] = overRepCopy.duplicationLevelModule();
        }

        return copies;
    }

    // -------------------------------------------------------------------------
    // Merging — fold every worker's results into the master module set
    // -------------------------------------------------------------------------

    private void mergeAllModules(QCModule[] source, QCModule[] target) {
        for (int i = 0; i < source.length; i++) {
            if (source[i] instanceof MergeableQCModule) {
                ((MergeableQCModule) target[i]).mergeFrom((MergeableQCModule) source[i]);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Shared helpers
    // -------------------------------------------------------------------------

    private void processSequence(QCModule[] modules, Sequence sequence) {
        for (QCModule module : modules) {
            processModule(module, sequence);
        }
    }

    private static void processModule(QCModule module, Sequence sequence) {
        if (sequence.isFiltered() && module.ignoreFilteredSequences()) {
            return;
        }
        module.processSequence(sequence);
    }

    private void publishProgress(SequenceFile sequenceFile, QcProgressListener progressListener, int sequenceCount) {
        if (sequenceCount % 4000 == 0 && progressListener != null) {
            progressListener.progressUpdated(sequenceCount, sequenceFile.getPercentComplete());
        }
    }

    private void checkInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("QC analysis was interrupted");
        }
    }

    // -------------------------------------------------------------------------
    // Worker task — runs ALL modules on sequences taken from the shared queue
    // -------------------------------------------------------------------------

    private static class WorkerTask implements Callable<QCModule[]> {

        private final QCModule[] modules;
        private final BlockingQueue<List<Sequence>> queue;
        private final List<Sequence> sentinel;

        WorkerTask(QCModule[] modules, BlockingQueue<List<Sequence>> queue, List<Sequence> sentinel) {
            this.modules = modules;
            this.queue = queue;
            this.sentinel = sentinel;
        }

        public QCModule[] call() throws InterruptedException {
            while (true) {
                List<Sequence> chunk = queue.take();
                if (chunk == sentinel) {
                    break;
                }
                for (int s = 0; s < chunk.size(); s++) {
                    Sequence seq = chunk.get(s);
                    for (int m = 0; m < modules.length; m++) {
                        processModule(modules[m], seq);
                    }
                }
            }
            return modules;
        }
    }
}
