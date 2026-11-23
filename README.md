# 123Fastq

123Fastq is a Java desktop application for FASTQ quality control, format conversion, barcode splitting, read trimming, and single-cell RNA-seq counting.

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

Produces `dist/123Fastq.jar`.

### With plain javac

```bash
mkdir -p build/classes
find src -name "*.java" > sources.txt
javac -encoding UTF-8 --release 8 -cp "lib/*" -d build/classes @sources.txt
```

## Run

```bash
java -cp "build/classes:src:lib/*" Eidi._123Fastq.GUI.App123Fastq
```

Or with the JAR:

```bash
java -cp "dist/123Fastq.jar:lib/*" Eidi._123Fastq.GUI.App123Fastq
```

## QC Threads

Quality control asks for a thread count when starting single-file or comparative QC. The default is the number of available processors. Override with:

```bash
java -D123fastq.qc.threads=4 -cp "dist/123Fastq.jar:lib/*" Eidi._123Fastq.GUI.App123Fastq
```

or the environment variable `123FASTQ_QC_THREADS`.

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
