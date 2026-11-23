package Eidi._123Fastq.Cli;

import Eidi._123Fastq.ConvertorsPackage.Fast5ToFastq.Fast5FileParser;
import Eidi._123Fastq.ConvertorsPackage.SamToFastq.SamFileParser;
import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import Eidi._123Fastq.TrimFactoryPackage.FastqSerializer;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

final class ConvertCommand {
    static final String SAM_HELP = "Usage: java -jar 123Fastq.jar sam-to-fastq --input FILE --output FILE [options]\n"
            + "\nWrite one FASTQ in alignment-file order, restoring original read orientation."
            + "\nPrimary reads with bases and qualities are exported by default."
            + "\n  --mapped-only      Keep mapped reads."
            + "\n  --all-alignments   Include secondary and supplementary records."
            + "\n  --skip-missing     Skip records without bases or qualities."
            + "\nChoose .gz or .bz2 for compressed FASTQ output.\n";
    static final String FAST5_HELP = "Usage: java -jar 123Fastq.jar fast5-to-fastq --input FILE [--input FILE ...] --output FILE\n"
            + "\nExtract existing embedded basecalls from legacy single-read FAST5 files."
            + "\nChoose .gz or .bz2 for compressed FASTQ output.\n";

    private ConvertCommand() {
    }

    static void sam(String[] options, PrintStream out, PrintStream err) throws Exception {
        Arguments args = Arguments.parse(options, new String[] {"input", "output"},
                new String[] {"mapped-only", "all-alignments", "skip-missing"});
        Path input = Cli.inputFile(args.path("input"));
        Path output = args.path("output");
        long exported = 0;
        long skipped = 0;
        try (StagedOutputs outputs = new StagedOutputs()) {
            try (SAMFileReader sam = new SAMFileReader(input.toFile());
                    FastqSerializer fasta = Cli.writer(outputs.stage(output))) {
                sam.setValidationStringency(SAMFileReader.ValidationStringency.STRICT);
                SAMRecordIterator records = sam.iterator();
                try {
                    while (records.hasNext()) {
                        Cli.checkInterrupted();
                        SAMRecord record = records.next();
                        if ((!args.flag("all-alignments")
                                && (record.getNotPrimaryAlignmentFlag() || record.getSupplementaryAlignmentFlag()))
                                || (args.flag("mapped-only") && record.getReadUnmappedFlag())) {
                            skipped++;
                            continue;
                        }
                        String bases = record.getReadString();
                        String qualities = record.getBaseQualityString();
                        if (bases.isEmpty() || qualities.isEmpty() || bases.equals("*") || qualities.equals("*")) {
                            if (args.flag("skip-missing")) {
                                skipped++;
                                continue;
                            }
                            throw new IOException("Missing bases or qualities on SAM/BAM read " + record.getReadName()
                                    + ". Use --skip-missing to skip such records.");
                        }
                        if (bases.length() != qualities.length()) {
                            throw new IOException("Sequence and quality lengths differ on SAM/BAM read " + record.getReadName());
                        }
                        FastqRecord fastq = SamFileParser.toFastqRecord(record);
                        if (record.getReadPairedFlag() && !fastq.getName().endsWith("/1") && !fastq.getName().endsWith("/2")) {
                            String suffix = record.getFirstOfPairFlag() ? "/1" : record.getSecondOfPairFlag() ? "/2" : "";
                            fastq = new FastqRecord(fastq.getName() + suffix, fastq.getSequence(), "", fastq.getQuality());
                        }
                        fasta.digestedWriteRecord(fastq);
                        exported++;
                    }
                } finally {
                    records.close();
                }
            }
            outputs.publish();
        }
        if (!args.flag("quiet")) {
            err.println("Exported " + exported + " reads. Skipped " + skipped + " records.");
        }
        Cli.output(out, "fastq", output);
    }

    static void fast5(String[] options, PrintStream out, PrintStream err) throws Exception {
        Arguments args = Arguments.parse(options, new String[] {"input", "output"}, Cli.NO_FLAGS, "input");
        List<Path> inputs = args.paths("input");
        for (Path input : inputs) {
            Cli.inputFile(input);
        }
        Path output = args.path("output");
        try (StagedOutputs outputs = new StagedOutputs()) {
            try (FastqSerializer fastq = Cli.writer(outputs.stage(output))) {
                Fast5FileParser parser = new Fast5FileParser();
                for (Path input : inputs) {
                    Cli.checkInterrupted();
                    parser.Fast5FileParse(input.toFile());
                    fastq.writeRecordFromFast5(parser.next());
                }
            }
            outputs.publish();
        }
        if (!args.flag("quiet")) {
            err.println("Exported " + inputs.size() + " embedded FAST5 reads.");
        }
        Cli.output(out, "fastq", output);
    }
}
