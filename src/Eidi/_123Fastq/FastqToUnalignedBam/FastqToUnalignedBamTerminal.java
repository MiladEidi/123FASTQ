//package Eidi._123Fastq.FastqToUnalignedBam;
//
//import htsjdk.samtools.ReservedTagConstants;
//import net.sf.samtools.SAMException;
//import net.sf.samtools.SAMFileHeader;
//import net.sf.samtools.SAMFileHeader.SortOrder;
//import net.sf.samtools.SAMFileWriter;
//import net.sf.samtools.SAMFileWriterFactory;
//import net.sf.samtools.SAMReadGroupRecord;
//import net.sf.samtools.SAMRecord;
//import net.sf.samtools.SAMUtils;
//import htsjdk.samtools.fastq.FastqConstants.FastqExtensions;
//import htsjdk.samtools.fastq.FastqReader;
//import htsjdk.samtools.fastq.FastqRecord;
//import htsjdk.samtools.util.FastqQualityFormat;
//import htsjdk.samtools.util.IOUtil;
//import htsjdk.samtools.util.Iso8601Date;
//import htsjdk.samtools.util.Log;
//import htsjdk.samtools.util.ProgressLogger;
//import htsjdk.samtools.util.QualityEncodingDetector;
//import htsjdk.samtools.util.SequenceUtil;
//import htsjdk.samtools.util.SolexaQualityConverter;
//import net.sf.samtools.util.StringUtil;
//import org.broadinstitute.barclay.argparser.Argument;
//import org.broadinstitute.barclay.argparser.CommandLineProgramProperties;
//import org.broadinstitute.barclay.help.DocumentedFeature;
//import picard.PicardException;
//import picard.cmdline.CommandLineProgram;
//import picard.cmdline.StandardOptionDefinitions;
//import picard.cmdline.programgroups.ReadDataManipulationProgramGroup;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import picard.sam.FastqToSam;
//
//public class FastqToUnalignedBamTerminal {
//
//    public File FASTQ;
//    public File FASTQ2;
//    public boolean USE_SEQUENTIAL_FASTQS = false;
//    public FastqQualityFormat QUALITY_FORMAT;
//    public File OUTPUT;
//    public String READ_GROUP_NAME = "A";
//    public String SAMPLE_NAME;
//    public String LIBRARY_NAME;
//    public String PLATFORM_UNIT;
//    public String PLATFORM;
//    public String SEQUENCING_CENTER;
//    public Integer PREDICTED_INSERT_SIZE;
//    public String PROGRAM_GROUP;
//    public String PLATFORM_MODEL;
//    public List<String> COMMENT = new ArrayList<>();
//    public String DESCRIPTION;
//    public Iso8601Date RUN_DATE;
//    public SortOrder SORT_ORDER = SortOrder.queryname;
//    private static final SolexaQualityConverter solexaQualityConverter = SolexaQualityConverter.getSingleton();
//    public Boolean ALLOW_AND_IGNORE_EMPTY_LINES = false;
//
//    protected int doWork() {
//        IOUtil.assertFileIsReadable(FASTQ);
//        if (FASTQ2 != null) {
//            IOUtil.assertFileIsReadable(FASTQ2);
//        }
//        IOUtil.assertFileIsWritable(OUTPUT);
//
//        final SAMFileHeader header = createSamFileHeader();
//        final SAMFileWriter writer = new SAMFileWriterFactory().makeSAMOrBAMWriter(header, false, OUTPUT);
//
//        // Set the quality format
//        QUALITY_FORMAT = FastqToSam.determineQualityFormat(fileToFastqReader(FASTQ),
//                (FASTQ2 == null) ? null : fileToFastqReader(FASTQ2),
//                QUALITY_FORMAT);
//
//        // Lists for sequential files, but also used when not sequential
//        final List<FastqReader> readers1 = new ArrayList<>();
//        final List<FastqReader> readers2 = new ArrayList<>();
//
//        if (USE_SEQUENTIAL_FASTQS) {
//            // Get all the files
//            for (final File fastq : getSequentialFileList(FASTQ)) {
//                readers1.add(fileToFastqReader(fastq));
//            }
//            if (null != FASTQ2) {
//                for (final File fastq : getSequentialFileList(FASTQ2)) {
//                    readers2.add(fileToFastqReader(fastq));
//                }
//                if (readers1.size() != readers2.size()) {
//                    throw new PicardException(String.format("Found %d files for FASTQ and %d files for FASTQ2.", readers1.size(), readers2.size()));
//                }
//            }
//        } else {
//            readers1.add(fileToFastqReader(FASTQ));
//            if (FASTQ2 != null) {
//                readers2.add(fileToFastqReader(FASTQ2));
//            }
//        }
//
//        // Loop through the FASTQs
//        for (int idx = 0; idx < readers1.size(); idx++) {
//            makeItSo(readers1.get(idx),
//                    (readers2.isEmpty()) ? null : readers2.get(idx),
//                    writer);
//        }
//
//        // Close all the things
//        for (final FastqReader reader : readers1) {
//            reader.close();
//        }
//        for (final FastqReader reader : readers2) {
//            reader.close();
//        }
//        writer.close();
//
//        return 0;
//    }
//
//    protected static List<File> getSequentialFileList(final File baseFastq) {
//        final List<File> files = new ArrayList<>();
//        files.add(baseFastq);
//
//        // Find the correct extension used in the base FASTQ
//        FastqExtensions fastqExtensions = null;
//        String suffix = null; // store the suffix including the extension
//        for (final FastqExtensions ext : FastqExtensions.values()) {
//            suffix = "_001" + ext.getExtension();
//            if (baseFastq.getAbsolutePath().endsWith(suffix)) {
//                fastqExtensions = ext;
//                break;
//            }
//        }
//        if (null == fastqExtensions) {
//            throw new PicardException(String.format("Could not parse the FASTQ extension (expected '_001' + '%s'): %s", FastqExtensions.values().toString(), baseFastq));
//        }
//
//        // Find all the files
//        for (int idx = 2; true; idx++) {
//            String fastq = baseFastq.getAbsolutePath();
//            fastq = String.format("%s_%03d%s", fastq.substring(0, fastq.length() - suffix.length()), idx, fastqExtensions.getExtension());
//            try {
//                IOUtil.assertFileIsReadable(new File(fastq));
//            } catch (final SAMException e) { // the file is not readable, so do not continue
//                break;
//            }
//            files.add(new File(fastq));
//        }
//
//        return files;
//    }
//
//    public void makeItSo(final FastqReader reader1, final FastqReader reader2, final SAMFileWriter writer) {
//        final int readCount = (reader2 == null) ? doUnpaired(reader1, writer) : doPaired(reader1, reader2, writer);
//        LOG.info("Processed " + readCount + " fastq reads");
//    }
//
//    private htsjdk.samtools.SAMRecord createSamRecord(final SAMFileHeader header, final String baseName, final FastqRecord frec, final boolean paired) {
//        final htsjdk.samtools.SAMRecord srec = new htsjdk.samtools.SAMRecord(header);
//        srec.setReadName(baseName);
//        srec.setReadString(frec.getReadString());
//        srec.setReadUnmappedFlag(true);
//        srec.setAttribute(ReservedTagConstants.READ_GROUP_ID, READ_GROUP_NAME);
//        final byte[] quals = htsjdk.samtools.util.StringUtil.stringToBytes(frec.getBaseQualityString());
//        convertQuality(quals, QUALITY_FORMAT);
//        for (final byte qual : quals) {
//            final int uQual = qual & 0xff;
//            if (uQual < MIN_Q || uQual > MAX_Q) {
//                throw new PicardException("Base quality " + uQual + " is not in the range " + MIN_Q + ".."
//                        + MAX_Q + " for read " + frec.getReadHeader());
//            }
//        }
//        srec.setBaseQualities(quals);
//
//        if (paired) {
//            srec.setReadPairedFlag(true);
//            srec.setMateUnmappedFlag(true);
//        }
//        return srec;
//    }
//
//    public SAMFileHeader createSamFileHeader() {
//        final htsjdk.samtools.SAMReadGroupRecord rgroup = new htsjdk.samtools.SAMReadGroupRecord(this.READ_GROUP_NAME);
//        rgroup.setSample(this.SAMPLE_NAME);
//        if (this.LIBRARY_NAME != null) {
//            rgroup.setLibrary(this.LIBRARY_NAME);
//        }
//        if (this.PLATFORM != null) {
//            rgroup.setPlatform(this.PLATFORM);
//        }
//        if (this.PLATFORM_UNIT != null) {
//            rgroup.setPlatformUnit(this.PLATFORM_UNIT);
//        }
//        if (this.SEQUENCING_CENTER != null) {
//            rgroup.setSequencingCenter(SEQUENCING_CENTER);
//        }
//        if (this.PREDICTED_INSERT_SIZE != null) {
//            rgroup.setPredictedMedianInsertSize(PREDICTED_INSERT_SIZE);
//        }
//        if (this.DESCRIPTION != null) {
//            rgroup.setDescription(this.DESCRIPTION);
//        }
//        if (this.RUN_DATE != null) {
//            rgroup.setRunDate(this.RUN_DATE);
//        }
//        if (this.PLATFORM_MODEL != null) {
//            rgroup.setPlatformModel(this.PLATFORM_MODEL);
//        }
//        if (this.PROGRAM_GROUP != null) {
//            rgroup.setProgramGroup(this.PROGRAM_GROUP);
//        }
//
//        final SAMFileHeader header = new SAMFileHeader();
//        header.addReadGroup(rgroup);
//
//        for (final String comment : COMMENT) {
//            header.addComment(comment);
//        }
//
//        header.setSortOrder(this.SORT_ORDER);
//        return header;
//    }
//
//    void convertQuality(final byte[] quals, final FastqQualityFormat version) {
//        switch (version) {
//            case Standard:
//                htsjdk.samtools.SAMUtils.fastqToPhred(quals);
//                break;
//            case Solexa:
//                solexaQualityConverter.convertSolexaQualityCharsToPhredBinary(quals);
//                break;
//            case Illumina:
//                solexaQualityConverter.convertSolexa_1_3_QualityCharsToPhredBinary(quals);
//                break;
//        }
//    }
//
//    String getBaseName(final String readName1, final String readName2, final FastqReader freader1, final FastqReader freader2) {
//        String[] toks = getReadNameTokens(readName1, 1, freader1);
//        final String baseName1 = toks[0];
//        final String num1 = toks[1];
//
//        toks = getReadNameTokens(readName2, 2, freader2);
//        final String baseName2 = toks[0];
//        final String num2 = toks[1];
//
//        if (!baseName1.equals(baseName2)) {
//            throw new PicardException(String.format("In paired mode, read name 1 (%s) does not match read name 2 (%s)", baseName1, baseName2));
//        }
//
//        final boolean num1Blank = htsjdk.samtools.util.StringUtil.isBlank(num1);
//        final boolean num2Blank = htsjdk.samtools.util.StringUtil.isBlank(num2);
//        if (num1Blank || num2Blank) {
//            if (!num1Blank) {
//                throw new PicardException(error(freader1, "Pair 1 number is missing (" + readName1 + "). Both pair numbers must be present or neither."));       //num1 != blank and num2   == blank
//            } else if (!num2Blank) {
//                throw new PicardException(error(freader2, "Pair 2 number is missing (" + readName2 + "). Both pair numbers must be present or neither.")); //num1 == blank and num =2 != blank
//            }
//        } else {
//            if (!num1.equals("1")) {
//                throw new PicardException(error(freader1, "Pair 1 number must be 1 (" + readName1 + ")"));
//            }
//            if (!num2.equals("2")) {
//                throw new PicardException(error(freader2, "Pair 2 number must be 2 (" + readName2 + ")"));
//            }
//        }
//
//        return baseName1;
//    }
//
//    private String[] getReadNameTokens(final String readName, final int pairNum, final FastqReader freader) {
//        if (readName.equals("")) {
//            throw new PicardException(error(freader, "Pair read name " + pairNum + " cannot be empty: " + readName));
//        }
//
//        final int idx = readName.lastIndexOf('/');
//        final String[] result = new String[2];
//
//        if (idx == -1) {
//            result[0] = readName;
//            result[1] = null;
//        } else {
//            result[1] = readName.substring(idx + 1, readName.length()); // should be a 1 or 2
//
//            if (!result[1].equals("1") && !result[1].equals("2")) {    //if not a 1 or 2 then names must be identical
//                result[0] = readName;
//                result[1] = null;
//            } else {
//                result[0] = readName.substring(0, idx); // baseName
//            }
//        }
//
//        return result;
//    }
//
//    private String error(final FastqReader freader, final String str) {
//        return str + " at line " + freader.getLineNumber() + " in file " + freader.getFile().getAbsolutePath();
//    }
//
//    public int SE_mode(final FastqReader freader, final SAMFileWriter writer) {
//        int readCount = 0;
//        final ProgressLogger progress = new ProgressLogger(LOG);
//        for (; freader.hasNext(); readCount++) {
//            final FastqRecord frec = freader.next();
//            final htsjdk.samtools.SAMRecord srec = createSamRecord(writer.getFileHeader(), SequenceUtil.getSamReadNameFromFastqHeader(frec.getReadHeader()), frec, false);
//            srec.setReadPairedFlag(false);
//            writer.addAlignment(srec);
//            progress.record(srec);
//        }
//
//        return readCount;
//    }
//
//    public int PE_mode(final FastqReader freader1, final FastqReader freader2, final SAMFileWriter writer) {
//        int readCount = 0;
//        final ProgressLogger progress = new ProgressLogger(LOG);
//        for (; freader1.hasNext() && freader2.hasNext(); readCount++) {
//            final FastqRecord frec1 = freader1.next();
//            final FastqRecord frec2 = freader2.next();
//
//            final String frec1Name = SequenceUtil.getSamReadNameFromFastqHeader(frec1.getReadHeader());
//            final String frec2Name = SequenceUtil.getSamReadNameFromFastqHeader(frec2.getReadHeader());
//            final String baseName = getBaseName(frec1Name, frec2Name, freader1, freader2);
//
//            final htsjdk.samtools.SAMRecord srec1 = createSamRecord(writer.getFileHeader(), baseName, frec1, true);
//            srec1.setFirstOfPairFlag(true);
//            srec1.setSecondOfPairFlag(false);
//            writer.addAlignment(srec1);
//            progress.record(srec1);
//
//            final htsjdk.samtools.SAMRecord srec2 = createSamRecord(writer.getFileHeader(), baseName, frec2, true);
//            srec2.setFirstOfPairFlag(false);
//            srec2.setSecondOfPairFlag(true);
//            writer.addAlignment(srec2);
//            progress.record(srec2);
//        }
//
//        if (freader1.hasNext() || freader2.hasNext()) {
//            throw new PicardException("Input paired fastq files must be the same length");
//        }
//
//        return readCount;
//    }
//
//    public static FastqQualityFormat determineQualityFormat(final FastqReader reader1, final FastqReader reader2, final FastqQualityFormat expectedQuality) {
//        final QualityEncodingDetector detector = new QualityEncodingDetector();
//
//        if (reader2 == null) {
//            detector.add(QualityEncodingDetector.DEFAULT_MAX_RECORDS_TO_ITERATE, reader1);
//        } else {
//            detector.add(QualityEncodingDetector.DEFAULT_MAX_RECORDS_TO_ITERATE, reader1, reader2);
//            reader2.close();
//        }
//
//        reader1.close();
//
//        final FastqQualityFormat qualityFormat = detector.generateBestGuess(QualityEncodingDetector.FileContext.FASTQ, expectedQuality);
//        if (detector.isDeterminationAmbiguous()) {
//            LOG.warn("Making ambiguous determination about fastq's quality encoding; more than one format possible based on observed qualities.");
//        }
//        LOG.info(String.format("Auto-detected quality format as: %s.", qualityFormat));
//
//        return qualityFormat;
//    }
//
//    private FastqReader fileToFastqReader(final File file) {
//        return new FastqReader(file, ALLOW_AND_IGNORE_EMPTY_LINES);
//    }
//}
