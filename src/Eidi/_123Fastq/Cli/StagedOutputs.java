package Eidi._123Fastq.Cli;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class StagedOutputs implements AutoCloseable {
    private final Map<Path, Path> directories = new LinkedHashMap<>();
    private final Map<Path, Path> files = new LinkedHashMap<>();

    static Path directory(Path path) throws IOException {
        return Files.createDirectories(path.toAbsolutePath().normalize()).toRealPath();
    }

    Path stage(Path path) throws IOException {
        Path target = path.toAbsolutePath().normalize();
        if (target.getFileName() == null) {
            throw new IOException("Specify a file as the output: " + target);
        }
        Path parent = directory(target.getParent());
        target = parent.resolve(target.getFileName());
        if (Files.exists(target, LinkOption.NOFOLLOW_LINKS) || files.containsKey(target)) {
            throw new IOException("Output already exists or was specified twice: " + target);
        }
        Path scratch = directories.get(parent);
        if (scratch == null) {
            scratch = Files.createTempDirectory(parent, ".123fastq-");
            directories.put(parent, scratch);
        }
        Path staged = scratch.resolve(target.getFileName());
        files.put(target, staged);
        return staged;
    }

    void publish() throws IOException, InterruptedException {
        for (Map.Entry<Path, Path> file : files.entrySet()) {
            if (!Files.isRegularFile(file.getValue())) {
                throw new IOException("The command did not produce " + file.getKey());
            }
        }
        Cli.checkInterrupted();
        List<Path> reserved = new ArrayList<>();
        try {
            // Reserve destinations exclusively before moving any staged output.
            for (Path target : files.keySet()) {
                Files.createFile(target);
                reserved.add(target);
            }
            for (Map.Entry<Path, Path> file : files.entrySet()) {
                try {
                    Files.move(file.getValue(), file.getKey(), StandardCopyOption.ATOMIC_MOVE,
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException ex) {
                    // Keep the reserved destination in place on volumes without atomic moves.
                    try (OutputStream destination = Files.newOutputStream(file.getKey(), StandardOpenOption.WRITE)) {
                        Files.copy(file.getValue(), destination);
                    }
                }
            }
        } catch (IOException ex) {
            for (Path target : reserved) {
                try {
                    Files.deleteIfExists(target);
                } catch (IOException rollback) {
                    ex.addSuppressed(rollback);
                }
            }
            throw ex;
        }
    }

    @Override
    public void close() throws IOException {
        IOException failure = null;
        for (Path scratch : directories.values()) {
            try {
                Files.walkFileTree(scratch, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException error) throws IOException {
                        if (error != null) {
                            throw error;
                        }
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException ex) {
                if (failure == null) {
                    failure = ex;
                } else {
                    failure.addSuppressed(ex);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }
}
