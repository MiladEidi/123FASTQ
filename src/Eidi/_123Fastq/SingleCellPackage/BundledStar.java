package Eidi._123Fastq.SingleCellPackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * The STAR executables shipped inside 123Fastq: the official STAR 2.7.11b builds for Linux x86-64
 * (static) and macOS x86-64. Apple Silicon Macs run the macOS build through Rosetta 2. A JAR entry
 * cannot be executed, so the binary is extracted on first use to a per-user cache.
 */
public final class BundledStar {
    public static final String VERSION = "2.7.11b";
    private static final String ROOT = "/Eidi/_123Fastq/SingleCellPackage/native/";

    private enum Platform {
        LINUX_X86_64("linux-x86_64", "36e94b899a56b0ea5de5d65e722f55bc552b6713bd2d1e83a5c2abbd06c9881a"),
        MACOS_X86_64("macos-x86_64", "b460f7b8f0c205f193c9ec4ea360f9ed448ca00bc7194d87206b4ceac9da90d6");

        final String resource;
        final String sha256;

        Platform(String directory, String sha256) {
            this.resource = ROOT + directory + "/STAR";
            this.sha256 = sha256;
        }
    }

    private static final boolean MAC = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("mac");
    private static String unavailable;
    private static boolean checked;

    private BundledStar() {
    }

    /** True when this build has a STAR binary for the operating system and CPU. */
    public static boolean supported() {
        Platform platform = platform();
        return platform != null && BundledStar.class.getResource(platform.resource) != null;
    }

    /** True on an Apple Silicon Mac, where the bundled STAR needs Rosetta 2. */
    public static boolean needsRosetta() {
        return MAC && isArm(System.getProperty("os.arch", ""));
    }

    /** Returns null when the bundled STAR can run here, otherwise the reason it cannot. */
    public static synchronized String unavailableReason() {
        if (!checked) {
            if (!supported()) {
                unavailable = "123Fastq bundles STAR for Linux x86-64 and macOS only.";
            } else if (needsRosetta() && !rosettaInstalled()) {
                unavailable = "The bundled STAR needs Rosetta 2 on Apple Silicon. Install it with "
                        + "'softwareupdate --install-rosetta', or install a native STAR (for example "
                        + "'brew install rna-star') and select it.";
            }
            checked = true;
        }
        return unavailable;
    }

    /** Returns the path of the extracted executable, extracting it if needed. */
    public static synchronized Path executable() throws IOException {
        String reason = unavailableReason();
        if (reason != null) {
            throw new IOException(reason);
        }
        Platform platform = platform();
        Path directory = cacheRoot().resolve("star-" + VERSION + "-" + platform.sha256.substring(0, 12));
        Path target = directory.resolve("STAR");
        if (Files.isRegularFile(target) && Files.isExecutable(target)
                && platform.sha256.equals(sha256(Files.readAllBytes(target)))) {
            return target;
        }
        byte[] binary = read(platform.resource);
        if (!platform.sha256.equals(sha256(binary))) {
            throw new IOException("The bundled STAR executable is corrupt. Reinstall 123Fastq or select a STAR executable.");
        }
        Files.createDirectories(directory);
        Path partial = Files.createTempFile(directory, "STAR-", ".partial");
        try {
            Files.write(partial, binary);
            try {
                Files.setPosixFilePermissions(partial, PosixFilePermissions.fromString("rwxr-xr-x"));
            } catch (UnsupportedOperationException ex) {
                if (!partial.toFile().setExecutable(true, false)) {
                    throw new IOException("Could not mark the bundled STAR as executable: " + partial, ex);
                }
            }
            try {
                Files.move(partial, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException ex) {
                Files.move(partial, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(partial);
        }
        return target;
    }

    private static Platform platform() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
        boolean x86 = arch.equals("amd64") || arch.equals("x86_64");
        if (os.startsWith("linux") && x86) {
            return Platform.LINUX_X86_64;
        }
        if (MAC && (x86 || isArm(arch))) {
            return Platform.MACOS_X86_64;
        }
        return null;
    }

    private static boolean isArm(String arch) {
        arch = arch.toLowerCase(Locale.ROOT);
        return arch.equals("aarch64") || arch.equals("arm64");
    }

    private static boolean rosettaInstalled() {
        try {
            Process process = new ProcessBuilder("/usr/bin/arch", "-x86_64", "/usr/bin/true")
                    .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.to(new java.io.File("/dev/null"))).start();
            if (!process.waitFor(15, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return false;
            }
            return process.exitValue() == 0;
        } catch (IOException ex) {
            return false;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private static Path cacheRoot() {
        Path base;
        String xdg = System.getenv("XDG_CACHE_HOME");
        if (xdg != null && !xdg.trim().isEmpty() && Paths.get(xdg).isAbsolute()) {
            base = Paths.get(xdg);
        } else if (MAC) {
            base = Paths.get(System.getProperty("user.home"), "Library", "Caches");
        } else {
            base = Paths.get(System.getProperty("user.home"), ".cache");
        }
        Path root = base.resolve("123Fastq");
        try {
            Files.createDirectories(root);
            if (Files.isWritable(root)) {
                return root;
            }
        } catch (IOException | SecurityException ignored) {
            // Fall back to the temporary directory below.
        }
        return Paths.get(System.getProperty("java.io.tmpdir"), "123Fastq-" + System.getProperty("user.name", "user"));
    }

    private static byte[] read(String resource) throws IOException {
        try (InputStream in = BundledStar.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException("The bundled STAR executable is missing from this 123Fastq build.");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(8 << 20);
            byte[] buffer = new byte[1 << 16];
            for (int n; (n = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        }
    }

    private static String sha256(byte[] data) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(data);
            StringBuilder hex = new StringBuilder(64);
            for (byte b : digest) {
                hex.append(String.format("%02x", b & 0xff));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
