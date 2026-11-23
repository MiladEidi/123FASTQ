# Changelog

## 1.4 — 2026-11-23

### Added

- **Single-cell RNA-seq counting.** A new **Single Cell › scRNA-seq (STARsolo)** workflow turns raw 10x Chromium 3′ Gene Expression FASTQs into raw and cell-filtered UMI count matrices. It supports v2, v3 and v3.1 chemistry, multiple lanes per library, gzip inputs, exon-only or exon-plus-intron counting, and `CellRanger2.2` cell calling. Every input is validated before STAR starts, runs can be cancelled, and temporary data is removed afterwards. See [docs/single-cell-rna-seq.md](docs/single-cell-rna-seq.md).
- **Bundled STAR 2.7.11b.** The JAR ships the official STAR builds for Linux x86-64 (static) and macOS x86-64 and uses them when no STAR is selected. Intel Macs run STAR natively and Apple Silicon Macs run it through Rosetta 2. Each binary is checked against its SHA-256 checksum when it is extracted.
- **In-app guide.** The single-cell tab has an **Input and output guide** that explains chemistries, whitelists, references, and results.
- **Command-line interface.** `java -jar 123Fastq.jar <command>` runs `qc`, `trim`, `split-barcodes`, `sam-to-fastq`, `fast5-to-fastq`, and `single-cell` without a display. Outputs are listed on stdout, progress goes to stderr, and exit codes are stable. See [docs/command-line.md](docs/command-line.md).
- **CLI trimming features.** Paired-end trimming with orphan outputs, multi-threaded trimming with order preserved, Illumina adapter FASTA clipping, and a tab-separated trimming report.
- **CLI barcode splitting.** Assigns reads to samples by a 5′ or 3′ barcode with a mismatch limit, optional clipping, and a per-sample summary.

### Changed

- The sidebar has a **Single Cell** section styled like Quality Check, Trim Factory and Converters.
- Running the JAR with no arguments opens the desktop on a graphical system and prints CLI help on a headless system.
- `--version` and QC reports show version 1.4.

## 1.3

- Multi-threaded quality control: every QC module runs in parallel worker threads.
