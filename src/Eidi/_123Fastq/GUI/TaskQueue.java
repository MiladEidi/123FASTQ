package Eidi._123Fastq.GUI;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskQueue implements Runnable {

    private BlockingQueue<Thread> Queue = new ArrayBlockingQueue<>(20);
    private Thread currentTask;

    public TaskQueue() {
        Thread t = new Thread(this);
        t.start();
    }

    public void AddToQueue(Thread task) {
        Queue.add(task);
    }

    public void StopRunningTask() throws InterruptedException {
        currentTask.interrupt();
    }

    @Override
    public void run() {
        while (true) {
            if (!Queue.isEmpty()) {
                currentTask = Queue.poll();
                currentTask.start();
                try {
                    currentTask.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(TaskQueue.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                Thread.sleep(555);
            } catch (InterruptedException e) {
            }
        }
    }
}
