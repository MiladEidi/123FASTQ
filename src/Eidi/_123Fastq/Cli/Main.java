package Eidi._123Fastq.Cli;

import Eidi._123Fastq.GUI.App123Fastq;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        boolean requestedGui = args.length == 1 && "gui".equals(args[0]);
        if (requestedGui || (args.length == 0 && !GraphicsEnvironment.isHeadless())) {
            if (GraphicsEnvironment.isHeadless()) {
                System.err.println("The GUI needs a graphical session. Use --help for command-line usage.");
                System.exit(2);
            }
            App123Fastq.main(new String[0]);
            return;
        }

        System.setProperty("java.awt.headless", "true");
        Thread command = Thread.currentThread();
        CountDownLatch finished = new CountDownLatch(1);
        Thread cancellation = new Thread(() -> {
            command.interrupt();
            try {
                finished.await(15, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }, "123Fastq shutdown");
        Runtime.getRuntime().addShutdownHook(cancellation);
        int status;
        try {
            status = Cli.run(args, System.out, System.err);
        } finally {
            finished.countDown();
            try {
                Runtime.getRuntime().removeShutdownHook(cancellation);
            } catch (IllegalStateException ex) {
                // The operating system initiated shutdown while the command was active.
            }
        }
        if (status != 0) {
            System.exit(status);
        }
    }
}
