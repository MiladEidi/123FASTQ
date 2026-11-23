package Eidi._123Fastq.SingleCellPackage;

import Eidi._123Fastq.GUI.App123Fastq;
import Eidi._123Fastq.GUI.MyJPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

public final class SingleCellPanel extends MyJPanel {
    private final JTextField fasta = new JTextField();
    private final JTextField gtf = new JTextField();
    private final JTextField whitelist = new JTextField();
    private final JTextField output = new JTextField();
    private final JTextField executable = new JTextField();
    private final JComboBox<SingleCellRequest.Chemistry> chemistry =
            new JComboBox<>(SingleCellRequest.Chemistry.values());
    private final JCheckBox includeIntrons = new JCheckBox("Include intronic reads", true);
    private final int defaultThreads = Math.min(8, Runtime.getRuntime().availableProcessors());
    private final JSpinner threads = new JSpinner(new SpinnerNumberModel(defaultThreads, 1, 256, 1));
    private final JSpinner expectedCells = new JSpinner(new SpinnerNumberModel(3000, 1, 1_000_000, 100));
    private final JSpinner indexRam = new JSpinner(new SpinnerNumberModel(32, 1, 1024, 1));
    private final List<SingleCellRequest.ReadPair> pairs = new ArrayList<>();
    private final PairsTableModel pairsModel = new PairsTableModel();
    private final JTable table = new JTable(pairsModel);
    private final JButton removePair = new JButton("Remove pair");
    private final JButton start = new JButton("Validate and run");
    private final JButton cancel = new JButton("Cancel");
    private final JButton open = new JButton("Open run folder");
    private final JLabel status = new JLabel("Ready");
    private final JProgressBar progress = new JProgressBar();
    private final JTextArea log = new JTextArea(5, 20);
    private final List<Component> inputs = new ArrayList<>();
    private AnalysisWorker running;
    private Path resultFolder;
    private Runnable afterRun;

    public SingleCellPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JPanel form = new JPanel(new BorderLayout(0, 6));
        form.add(buildReads(), BorderLayout.NORTH);
        form.add(buildSettings(), BorderLayout.CENTER);
        add(form, BorderLayout.NORTH);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.getAccessibleContext().setAccessibleName("Single-cell analysis log");
        JScrollPane logScroll = new JScrollPane(log);
        logScroll.setBorder(BorderFactory.createTitledBorder("Run log"));
        add(logScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.add(start);
        actions.add(cancel);
        actions.add(open);
        start.addActionListener(event -> startAnalysis());
        cancel.addActionListener(event -> cancelAnalysis());
        open.addActionListener(event -> openFolder());
        cancel.setEnabled(false);
        open.setEnabled(false);
        progress.setPreferredSize(new Dimension(76, 12));
        progress.setVisible(false);
        actions.add(progress);
        actions.add(status);
        add(actions, BorderLayout.SOUTH);
        chemistry.setSelectedItem(SingleCellRequest.Chemistry.THREE_PRIME_V3);
    }

    private JPanel buildReads() {
        JPanel panel = new JPanel(new BorderLayout(6, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Paired FASTQs for one 10x 3′ library"));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getAccessibleContext().setAccessibleName("Paired FASTQ files");
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(21);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(710, 86));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton addPair = new JButton("Add FASTQ pair");
        addPair.addActionListener(event -> addReadPair());
        removePair.addActionListener(event -> {
            int[] selected = table.getSelectedRows();
            for (int i = selected.length - 1; i >= 0; i--) {
                pairs.remove(selected[i]);
            }
            pairsModel.fireTableDataChanged();
            removePair.setEnabled(false);
        });
        removePair.setEnabled(false);
        table.getSelectionModel().addListSelectionListener(event ->
                removePair.setEnabled(!isRunning() && table.getSelectedRowCount() > 0));
        buttons.add(addPair);
        buttons.add(removePair);
        panel.add(buttons, BorderLayout.SOUTH);
        inputs.add(table);
        inputs.add(addPair);
        return panel;
    }

    private JPanel buildSettings() {
        JPanel panel = new JPanel(new GridBagLayout());
        pathRow(panel, 0, "Genome FASTA", fasta, false, "Select genome FASTA");
        pathRow(panel, 1, "Annotation GTF", gtf, false, "Select GTF from the same reference assembly");
        pathRow(panel, 2, "Barcode whitelist", whitelist, false, "Select the whitelist for your 10x chemistry");
        pathRow(panel, 3, "Output directory", output, true, "Select a writable results directory");
        pathRow(panel, 4, "STAR executable", executable, false, "Select the STAR executable");
        executable.setToolTipText("Leave blank to use STAR on PATH. Requires STAR 2.7.11 or later 2.x.");

        GridBagConstraints label = constraints(0, 5);
        JLabel chemistryLabel = new JLabel("Chemistry");
        chemistryLabel.setLabelFor(chemistry);
        panel.add(chemistryLabel, label);
        JPanel assay = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        assay.add(chemistry);
        assay.add(includeIntrons);
        GridBagConstraints options = constraints(1, 5);
        options.gridwidth = 2;
        options.anchor = GridBagConstraints.WEST;
        panel.add(assay, options);

        JPanel resources = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        addOption(resources, "Expected cells", expectedCells, "Approximate cells for STARsolo cell calling.");
        addOption(resources, "Threads", threads, "CPU threads for STAR.");
        addOption(resources, "Index RAM (GiB)", indexRam, "STAR genome-index memory limit. Mapping also needs RAM.");
        GridBagConstraints resourcesRow = constraints(0, 6);
        resourcesRow.gridwidth = 3;
        panel.add(resources, resourcesRow);
        inputs.add(chemistry);
        inputs.add(includeIntrons);
        return panel;
    }

    private void pathRow(JPanel panel, int row, String name, JTextField field, boolean directory, String title) {
        JLabel label = new JLabel(name);
        label.setLabelFor(field);
        panel.add(label, constraints(0, row));
        GridBagConstraints entry = constraints(1, row);
        entry.fill = GridBagConstraints.HORIZONTAL;
        entry.weightx = 1;
        panel.add(field, entry);
        JButton browse = new JButton("Browse");
        browse.setToolTipText(title);
        browse.getAccessibleContext().setAccessibleName(title);
        browse.addActionListener(event -> {
            Path path = choose(title, directory);
            if (path != null) {
                field.setText(path.toString());
            }
        });
        panel.add(browse, constraints(2, row));
        inputs.add(field);
        inputs.add(browse);
    }

    private static GridBagConstraints constraints(int column, int row) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.insets = new Insets(2, 4, 2, 4);
        constraints.anchor = GridBagConstraints.WEST;
        return constraints;
    }

    private void addOption(JPanel panel, String name, JSpinner spinner, String help) {
        JLabel label = new JLabel(name);
        label.setLabelFor(spinner);
        spinner.setToolTipText(help);
        panel.add(label);
        panel.add(spinner);
        inputs.add(spinner);
    }

    private Path choose(String title, boolean directory) {
        JFileChooser chooser = new JFileChooser(App123Fastq.lastDirectory);
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(directory ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File file = chooser.getSelectedFile();
        App123Fastq.lastDirectory = chooser.getCurrentDirectory().getPath();
        return file.toPath();
    }

    private void addReadPair() {
        Path r1 = choose("Select R1 FASTQ (cell barcode and UMI)", false);
        if (r1 == null) {
            return;
        }
        Path r2 = choose("Select matching R2 FASTQ (cDNA)", false);
        if (r2 == null) {
            return;
        }
        int index = pairs.size();
        pairs.add(new SingleCellRequest.ReadPair(r1, r2));
        pairsModel.fireTableRowsInserted(index, index);
        table.setRowSelectionInterval(index, index);
        table.scrollRectToVisible(table.getCellRect(index, 0, true));
    }

    private void startAnalysis() {
        try {
            SingleCellRequest request = new SingleCellRequest(pairs, path(fasta, "genome FASTA"),
                    path(gtf, "annotation GTF"), path(whitelist, "barcode whitelist"),
                    path(output, "output directory"),
                    executable.getText().trim().isEmpty() ? null : path(executable, "STAR executable"),
                    (SingleCellRequest.Chemistry) chemistry.getSelectedItem(), includeIntrons.isSelected(),
                    number(expectedCells), number(threads), number(indexRam));
            log.setText("");
            resultFolder = null;
            running = new AnalysisWorker(request);
            setRunning(true);
            status.setText("Running");
            running.execute();
        } catch (IllegalArgumentException | java.text.ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Check single-cell inputs", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static Path path(JTextField field, String label) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Select the " + label + ".");
        }
        return Paths.get(text);
    }

    private static int number(JSpinner spinner) throws java.text.ParseException {
        spinner.commitEdit();
        return (Integer) spinner.getValue();
    }

    private void setRunning(boolean busy) {
        for (Component input : inputs) {
            input.setEnabled(!busy);
        }
        removePair.setEnabled(!busy && table.getSelectedRowCount() > 0);
        start.setEnabled(!busy);
        cancel.setEnabled(busy);
        open.setEnabled(!busy && resultFolder != null);
        progress.setVisible(busy);
        progress.setIndeterminate(busy);
    }

    public boolean isRunning() {
        return running != null;
    }

    @Override
    public boolean canClose() {
        if (isRunning()) {
            log.append("Use Cancel to stop the current run before closing this tab.\n");
            return false;
        }
        return true;
    }

    public void cancelAndThen(Runnable onFinished) {
        afterRun = onFinished;
        cancelAnalysis();
    }

    private void cancelAnalysis() {
        if (running != null) {
            cancel.setEnabled(false);
            status.setText("Cancelling…");
            running.requestStop();
        }
    }

    private void openFolder() {
        try {
            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                throw new IOException("Open the run folder in your file manager.");
            }
            Desktop.getDesktop().open(resultFolder.toFile());
        } catch (IOException | UnsupportedOperationException | SecurityException ex) {
            JOptionPane.showMessageDialog(this, resultFolder + "\n" + ex.getMessage(),
                    "Run folder", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private final class AnalysisWorker extends SwingWorker<SingleCellRunner.Result, String> {
        private final SingleCellRequest request;
        private boolean stopRequested;
        private Thread workerThread;

        AnalysisWorker(SingleCellRequest request) {
            this.request = request;
        }

        synchronized void requestStop() {
            stopRequested = true;
            if (workerThread != null) {
                workerThread.interrupt();
            }
        }

        @Override
        protected SingleCellRunner.Result doInBackground() throws IOException, InterruptedException {
            synchronized (this) {
                if (stopRequested) {
                    throw new InterruptedException();
                }
                workerThread = Thread.currentThread();
            }
            try {
                return SingleCellRunner.run(request, this::publish);
            } finally {
                synchronized (this) {
                    workerThread = null;
                    Thread.interrupted();
                }
            }
        }

        @Override
        protected void process(List<String> messages) {
            for (String message : messages) {
                log.append(message + "\n");
            }
            log.setCaretPosition(log.getDocument().getLength());
        }

        @Override
        protected void done() {
            try {
                SingleCellRunner.Result result = get();
                resultFolder = result.directory;
                status.setText(String.format(Locale.ROOT, "Complete · %,d cells", result.cells));
                log.append("Filtered matrix: " + result.filteredMatrix + "\n");
                log.append("Raw matrix: " + result.rawMatrix + "\n");
            } catch (ExecutionException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof InterruptedException) {
                    status.setText("Cancelled");
                } else {
                    status.setText("Failed");
                    if (cause instanceof SingleCellRunner.RunFailure) {
                        resultFolder = ((SingleCellRunner.RunFailure) cause).directory;
                    } else {
                        log.append(cause.getMessage() + "\n");
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                status.setText("Interrupted");
            } finally {
                running = null;
                setRunning(false);
                log.setCaretPosition(log.getDocument().getLength());
                if (afterRun != null) {
                    Runnable action = afterRun;
                    afterRun = null;
                    action.run();
                }
            }
        }
    }

    private final class PairsTableModel extends AbstractTableModel {
        @Override
        public String getColumnName(int column) {
            return column == 0 ? "R1 · Cell barcode + UMI" : "R2 · cDNA";
        }

        @Override
        public int getRowCount() {
            return pairs.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int row, int column) {
            SingleCellRequest.ReadPair pair = pairs.get(row);
            return (column == 0 ? pair.barcodeRead : pair.cdnaRead).toString();
        }
    }
}
