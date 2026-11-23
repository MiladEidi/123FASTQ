package Eidi._123Fastq.Cli;

import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.itadaki.bzip2.BZip2InputStream;

final class FastqInput implements Closeable {
    private static final Pattern BASES = Pattern.compile("[ACGTURYSWKMBDHVNacgturyswkmbdhvn]+");
    private final BufferedReader reader;
    private final Path path;
    private final int phred;
    private long records;

    FastqInput(Path path, int phred) throws IOException {
        this.path = path;
        this.phred = phred;
        InputStream stream = new BufferedInputStream(Files.newInputStream(path), 64 * 1024);
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        try {
            if (name.endsWith(".gz")) {
                stream = new GZIPInputStream(stream, 64 * 1024);
            } else if (name.endsWith(".bz2")) {
                stream = new BZip2InputStream(stream, false);
            }
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8.newDecoder()), 64 * 1024);
        } catch (IOException | RuntimeException ex) {
            stream.close();
            throw ex;
        }
    }

    FastqRecord next() throws IOException, InterruptedException {
        Cli.checkInterrupted();
        String header = reader.readLine();
        if (header == null) {
            return null;
        }
        records++;
        String sequence = reader.readLine();
        String separator = reader.readLine();
        String quality = reader.readLine();
        if (header.length() < 2 || header.charAt(0) != '@' || Character.isWhitespace(header.charAt(1))
                || sequence == null || !BASES.matcher(sequence).matches()
                || separator == null || !separator.startsWith("+")
                || quality == null || quality.length() != sequence.length()
                || !quality.chars().allMatch(ch -> ch >= phred && ch <= 126)) {
            throw new IOException("Malformed FASTQ record " + records + " in " + path
                    + ". Check the four record lines and Phred encoding.");
        }
        return new FastqRecord(header.substring(1), sequence.toUpperCase(Locale.ROOT), separator.substring(1),
                quality, phred, null);
    }

    static String id(FastqRecord record) {
        String id = record.getName().substring(0, idEnd(record.getName()));
        return id.endsWith("/1") || id.endsWith("/2") ? id.substring(0, id.length() - 2) : id;
    }

    static boolean mateMatches(FastqRecord record, char expected) {
        String name = record.getName();
        int end = idEnd(name);
        String id = name.substring(0, end);
        if ((id.endsWith("/1") || id.endsWith("/2")) && id.charAt(id.length() - 1) != expected) {
            return false;
        }
        String description = name.substring(end).trim();
        boolean casavaMate = description.startsWith("1:Y:") || description.startsWith("1:N:")
                || description.startsWith("2:Y:") || description.startsWith("2:N:");
        return !casavaMate || description.charAt(0) == expected;
    }

    private static int idEnd(String name) {
        int end = 0;
        while (end < name.length() && !Character.isWhitespace(name.charAt(end))) {
            end++;
        }
        return end;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
