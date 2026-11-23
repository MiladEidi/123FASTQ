# 123Fastq

123Fastq is a Java desktop and command-line application for FASTQ quality control, format conversion, barcode splitting, read trimming, and single-cell RNA-seq counting.

## Project Layout

- `src/` — Java source code and bundled adapter/config resources.
- `test/` — lightweight smoke tests that run without JUnit.
- `lib/` — required third-party JARs (included in the repository).
- `build.xml` and `nbproject/` — NetBeans/Ant project files.

Generated folders (`build/`, `dist/`) are ignored by git.

## Requirements

- Java 8 or later (Java 17+ recommended)

## Build

### With Ant (recommended)

```bash
ant jar
```

Produces `dist/123Fastq.jar` with the bundled Java dependencies.

### With plain javac

```bash
mkdir -p build/classes
find src -name "*.java" > sources.txt
javac -encoding UTF-8 --release 8 -cp "lib/*" -d build/classes @sources.txt
```

## Run from the JAR

```bash
java -jar dist/123Fastq.jar --help
java -jar dist/123Fastq.jar --version
java -jar dist/123Fastq.jar gui
```

With no arguments, the JAR opens the desktop on a graphical system and prints CLI help on a headless system. CLI commands run headlessly even when a display is available.

For classes built directly with `javac`, use the same commands after the main class:

```bash
java -cp "build/classes:src:lib/*" Eidi._123Fastq.Cli.Main --help
```

## Command-line workflows

Run `java -jar dist/123Fastq.jar <command> --help` for the complete options. Commands list output paths on stdout as tab-separated keys and values. Progress and errors go to stderr, and `--quiet` suppresses progress. Exit codes are `0` for success, `1` for a processing or I/O failure, `2` for invalid command usage, and `130` for interruption. Commands protect existing outputs and create output directories as needed.

### Quality control

Repeat `--input` to generate a separate HTML report and ZIP archive for each file. Use `--compare` with exactly two inputs for a comparative report. Inputs can be plain FASTQ, gzip, bzip2, SAM/BAM, or supported single-read FAST5. `--mapped-only` limits SAM/BAM QC to mapped portions of reads.

```bash
java -jar dist/123Fastq.jar qc --input reads.fastq.gz --out-dir qc --threads 4
java -jar dist/123Fastq.jar qc --input R1.fastq.gz --input R2.fastq.gz --compare --out-dir qc
```

### Read trimming

Repeat `--step` in the desired order. The CLI supports adapter clipping, quality thresholds and windows, end cropping, length and GC filters, homopolymer trimming, and Phred conversion. An optional `--adapter-file` uses the application's Illumina adapter trimmer before the steps. Input Phred defaults to 33. Use a `.gz` or `.bz2` suffix for compressed FASTQ output.

```bash
java -jar dist/123Fastq.jar trim --input reads.fastq.gz --output trimmed.fastq.gz \
  --step sliding-window:4:20 --step min-length:35 --report trim-summary.tsv

java -jar dist/123Fastq.jar trim --r1 R1.fastq.gz --r2 R2.fastq.gz \
  --out-r1 R1.paired.fastq.gz --out-r2 R2.paired.fastq.gz \
  --orphans-r1 R1.orphans.fastq.gz --orphans-r2 R2.orphans.fastq.gz \
  --step trailing:20 --step min-length:35
```

Paired output requires synchronized read identifiers. Optional orphan files retain surviving mates of filtered reads. Use `--threads N` for parallel trimming with input order preserved.

### Barcode splitting

Supply a tab-separated sheet with a sample name and an equal-length barcode per line. The CLI matches the specified read end with a configurable substitution limit. Ties and unmatched reads go to `Undetermined.fastq` (or `.fastq.gz` with `--gzip`). `--clip` removes the matched barcode and its quality scores. A TSV summary records counts per sample and assignment outcome.

```bash
java -jar dist/123Fastq.jar split-barcodes --input reads.fastq.gz --barcodes samples.tsv \
  --out-dir demux --end 5 --max-mismatches 1 --clip --gzip
```

### Format conversion

```bash
java -jar dist/123Fastq.jar sam-to-fastq --input reads.bam --output reads.fastq.gz
java -jar dist/123Fastq.jar fast5-to-fastq --input read1.fast5 --input read2.fast5 --output reads.fastq.gz
```

SAM/BAM conversion writes primary reads in file order and restores the original sequencing orientation. Use `--skip-missing` to skip records that lack sequence or quality. FAST5 conversion extracts embedded basecalls from legacy single-read FAST5 files and requires a platform supported by the bundled HDF5 native library.

### Single-cell counting

Provide R1 and R2 in matching lane order. Repeat each flag for the remaining lanes of the same 10x library. `--star` selects the STAR executable when it is outside `PATH`. Chemistry, reference, and disk requirements are described in [Single-cell RNA-seq](#single-cell-rna-seq).

```bash
java -jar dist/123Fastq.jar single-cell \
  --r1 lane1_R1.fastq.gz --r2 lane1_R2.fastq.gz \
  --r1 lane2_R1.fastq.gz --r2 lane2_R2.fastq.gz \
  --fasta genome.fa.gz --gtf genes.gtf.gz --whitelist 3M-february-2018.txt.gz \
  --chemistry v3.1 --expected-cells 5000 --threads 8 --out-dir results
```

This command prints the run path, raw and filtered matrix directories, and the called-cell, gene, and filtered UMI counts.

## QC Threads

The GUI asks for a thread count when starting single-file or comparative QC. The default is the number of available processors. Override with:

```bash
java -D123fastq.qc.threads=4 -jar dist/123Fastq.jar gui
```

or the environment variable `123FASTQ_QC_THREADS`.

The CLI defaults to one QC worker. Use `qc --threads N` to set the worker count.

## Single-cell RNA-seq

Open **Single-cell analysis** in the sidebar. This workflow counts one 10x Chromium 3′ Gene Expression library at a time. It supports v2 (16-base cell barcode and 10-base UMI) and v3/v3.1 (16-base cell barcode and 12-base UMI). Use one FASTQ pair per lane or sequencing chunk from the same library. Run separate libraries in separate jobs because their cell barcodes can overlap.

1. Make [STAR 2.7.11 or a later 2.x release](https://github.com/alexdobin/STAR/releases) available on `PATH` or select its executable in the form. STAR runs locally on Linux or macOS. On Windows, run this workflow in a Linux environment with a graphical session, such as WSL with WSLg.
2. Add the raw, demultiplexed **R1** (cell barcode and UMI) and **R2** (cDNA) FASTQs as pairs. Provide a genome **FASTA**, its matching **GTF** with annotated exons, and the chemistry-specific [barcode whitelist](https://github.com/alexdobin/STAR/blob/2.7.11b/docs/STARsolo.md#running-starsolo-for-10x-chromium-scrna-seq-data). The whitelist is one barcode per line. Inputs can be plain text or gzip-compressed.
3. Select a writable output directory, the chemistry, and the approximate number of expected cells. **Include intronic reads** selects STARsolo `GeneFull`. Clear it for exon-only `Gene` counting. The index RAM setting limits STAR's index build. Mapping also needs RAM, which can reach tens of gigabytes for a mammalian genome.
4. Select **Validate and run**. The app reads every FASTQ record, checks that mates match and have valid sequence and quality lengths, validates whitelist entries, and checks GTF contig names and coordinates against the FASTA. Use a FASTA and GTF distributed for the same reference release. The log reports sampled barcode match rates and flags low matches. If validation succeeds, the app builds the genome index and runs STARsolo alignment, barcode correction, UMI deduplication, and `CellRanger2.2` cell calling.

The app stages normalized, uncompressed inputs and builds a temporary STAR index in the output volume. Allow space for both in addition to the results. Temporary data is removed when a run finishes, fails, or is cancelled. Each run gets its own `single-cell-*` directory with settings, logs, STAR reports when available, and the following output after a successful run:

| Output under `counts/` | Contents |
| --- | --- |
| `filtered_feature_bc_matrix/` | Count matrix for called cells |
| `raw_feature_bc_matrix/` | Unfiltered count matrix, including whitelist barcodes with zero counts |
| `matrix-summary.tsv` | Gene and barcode dimensions, nonzero entries, and UMI totals |

Each matrix is a gzip-compressed Matrix Market `matrix.mtx.gz` with genes in rows and barcodes in columns, plus `features.tsv.gz` and `barcodes.tsv.gz`. Use the filtered folder with tools such as Scanpy `read_10x_mtx` or Seurat `Read10X`. `Summary.csv` and `STAR-Log.final.out` in the run directory report quantification and mapping metrics. A filtered result can contain zero cells.

The single-cell smoke test can run validation and matrix checks on its own. Pass a STAR executable to also run the synthetic FASTQ-to-matrix test.

```bash
ant compile-test
java -cp 'build/classes:build/test/classes:lib/*' Eidi._123Fastq.SingleCellPackage.SingleCellSmokeTest /path/to/STAR
```

To test the packaged CLI in a clean working directory with no library files next to its JAR:

```bash
ant jar compile-test
java -cp build/test/classes Eidi._123Fastq.Cli.CliSmokeTest
```
