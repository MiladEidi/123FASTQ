package Eidi._123Fastq.GUI;

import javax.swing.JPanel;

public abstract class MyJPanel extends JPanel{

    private boolean shouldRun;
    private Thread thread;

    public MyJPanel() {
        super();
    }

    public Thread getThread(){
        return thread;
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }
}
