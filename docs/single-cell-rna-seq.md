# Single-cell RNA-seq counting

*New in 123Fastq 1.4*

123Fastq turns raw FASTQs from one 10x Chromium 3′ Gene Expression library into gene-by-cell UMI count matrices, ready for Scanpy, Seurat, or any tool that reads 10x Matrix Market folders. Alignment, barcode correction, UMI deduplication, and cell calling are done by [STARsolo](https://github.com/alexdobin/STAR/blob/2.7.11b/docs/STARsolo.md), which ships with 123Fastq.

- [What you need](#what-you-need)
- [STAR: bundled and custom builds](#star-bundled-and-custom-builds)
- [Running from the desktop](#running-from-the-desktop)
- [Running from the command line](#running-from-the-command-line)
- [What the run does](#what-the-run-does)
- [Results](#results)
- [Settings reference](#settings-reference)
- [Troubleshooting](#troubleshooting)

## What you need

| Input | Notes |
| --- | --- |
| **R1 FASTQ** | Cell barcode + UMI read. Raw and demultiplexed by sample, not trimmed. |
| **R2 FASTQ** | cDNA read. |
| **Genome FASTA** | Genomic (not transcriptome) sequences for the organism. |
| **Annotation GTF** | Must contain `exon` features and come from the same reference release as the FASTA, for example both from the same Ensembl or GENCODE release. |
| **Barcode whitelist** | One barcode per line, for your chemistry: `737K-august-2016.txt` for v2 and `3M-february-2018.txt.gz` for v3/v3.1. These files ship with 10x Genomics Cell Ranger. |
| **Output directory** | An existing, writable directory. |

All inputs can be plain text or gzip-compressed. Add one R1/R2 pair per lane or sequencing chunk. All pairs must come from the **same library**. Run separate libraries as separate jobs, because their cell barcodes can overlap.

Supported chemistries:

| Chemistry | Cell barcode | UMI |
| --- | --- | --- |
| 10x 3′ v2 | 16 bases | 10 bases |
| 10x 3′ v3 / v3.1 | 16 bases | 12 bases |

**Resources.** The run stages uncompressed copies of the reads and builds a temporary STAR genome index on the output volume, so leave room for both as well as the results. For a mammalian genome, plan for about 32 GB of RAM for the index build and for alignment, and tens of gigabytes of free disk space. Small genomes need much less.

## STAR: bundled and custom builds

123Fastq includes the official, unmodified [STAR 2.7.11b](https://github.com/alexdobin/STAR/releases/tag/2.7.11b) release binaries (MIT license; license files are in `src/Eidi/_123Fastq/SingleCellPackage/native/`). When the STAR field is blank, or `--star` is not given, 123Fastq picks STAR as follows:

| Computer | STAR used |
| --- | --- |
| Linux x86-64 | Bundled static Linux build |
| Intel Mac (macOS 12 or later) | Bundled macOS build, running natively |
| Apple Silicon Mac with Rosetta 2 | Bundled macOS build, running through Rosetta 2 |
| Apple Silicon Mac without Rosetta 2 | `STAR` on `PATH`. If none is found, the error explains how to install Rosetta 2 or a native STAR. |
| Windows, Linux on ARM, other | `STAR` on `PATH` |

The bundled binary is extracted on first use, checked against its SHA-256 checksum, and reused for later runs:

- Linux: `~/.cache/123Fastq/` (or `$XDG_CACHE_HOME/123Fastq/`)
- macOS: `~/Library/Caches/123Fastq/`

The run log names the STAR that was used.

**Using your own STAR.** Select it in the **STAR executable** field or pass `--star /path/to/STAR`. It must report version 2.7.11 or a later 2.x release. A selected STAR always takes priority over the bundled one.

**Apple Silicon.** Install Rosetta 2 once with:

```bash
softwareupdate --install-rosetta --agree-to-license
```

macOS 15 or later is recommended on Apple Silicon. For a native build instead of Rosetta, install STAR with Homebrew (`brew install rna-star`) and select it.

**Windows.** Run 123Fastq inside a Linux environment with a graphical session, such as WSL with WSLg. The bundled Linux STAR is then used.

## Running from the desktop

1. Open the app and, in the sidebar under **Single Cell**, select **scRNA-seq (STARsolo)**. The single-cell tab opens. Only one single-cell tab is open at a time.
2. Select **Add FASTQ pair** for each lane and choose its R1 and R2 files. Use **Remove pair** to delete a selected pair.
3. Choose the **Genome FASTA**, **Annotation GTF**, **Barcode whitelist**, and **Output directory**. Leave **STAR executable** blank to use the bundled STAR.
4. Choose the **Chemistry**, and keep or clear **Include intronic reads**.
5. Set **Expected cells**, **Threads**, and **Index RAM (GiB)**. See [Settings reference](#settings-reference).
6. Select **Validate and run**. Progress appears in the log.
7. When the run finishes, select **Open run folder**.

**Cancel** stops STAR and its child processes and removes the temporary files. Closing 123Fastq during a run asks for confirmation first, then cancels the run. **Input and output guide** opens a summary of this page inside the app.

## Running from the command line

```bash
java -jar 123Fastq.jar single-cell \
  --r1 lane1_R1.fastq.gz --r2 lane1_R2.fastq.gz \
  --r1 lane2_R1.fastq.gz --r2 lane2_R2.fastq.gz \
  --fasta genome.fa.gz --gtf genes.gtf.gz --whitelist 3M-february-2018.txt.gz \
  --chemistry v3.1 --expected-cells 5000 --threads 8 --out-dir results
```

Repeat `--r1` and `--r2` in matching lane order. All options are listed in the [command-line reference](command-line.md#single-cell). On success, stdout lists:

```text
run              <run directory>
filtered_matrix  <run>/counts/filtered_feature_bc_matrix
raw_matrix       <run>/counts/raw_feature_bc_matrix
called_cells     <number>
genes            <number>
filtered_umis    <number>
```

The fields are tab-separated.

## What the run does

1. **Find STAR** and confirm that it is version 2.7.11 or a later 2.x release.
2. **Validate and stage inputs** before any alignment work:
   - Every FASTQ record is read. Mates must pair up and have valid sequence and quality lengths. R1 must be long enough for the chemistry's barcode and UMI, and at least one R2 read must be 30 bases or longer.
   - Whitelist entries must be valid barcodes of the right length.
   - GTF contig names and coordinates must match the FASTA.
   - A sample of barcodes is checked against the whitelist. The log reports match rates and warns when they are low, which usually means the wrong chemistry or whitelist.
3. **Build a STAR genome index** from the FASTA and GTF, sized for the genome and read length.
4. **Run STARsolo**. 123Fastq uses Cell Ranger–style settings:
   - `--soloType CB_UMI_Simple` with the chemistry's barcode and UMI lengths, `--soloStrand Forward`
   - `--soloFeatures GeneFull` when intronic reads are included, otherwise `Gene`
   - `--soloCBmatchWLtype 1MM_multi_Nbase_pseudocounts`, `--soloUMIdedup 1MM_CR`, `--soloUMIfiltering MultiGeneUMI_CR`
   - `--clipAdapterType CellRanger4` and `--outFilterScoreMin 30`
   - `--soloCellFilter CellRanger2.2 <expected cells> 0.99 10`
5. **Export matrices** in 10x format, and copy STAR's reports into the run folder.
6. **Clean up** the staged reads and index whether the run succeeds, fails, or is cancelled.

## Results

Each run gets its own `single-cell-*` folder inside the output directory:

```text
single-cell-XXXX/
├── run.log                      progress and STAR output
├── run-settings.txt             inputs, settings and STAR version
├── Summary.csv                  STARsolo quantification summary
├── STAR-Log.final.out           STAR mapping statistics
└── counts/
    ├── filtered_feature_bc_matrix/   called cells
    │   ├── matrix.mtx.gz
    │   ├── features.tsv.gz
    │   └── barcodes.tsv.gz
    ├── raw_feature_bc_matrix/        all whitelist barcodes, including zero counts
    └── matrix-summary.tsv            dimensions, nonzero entries and UMI totals
```

STAR reports appear only when STAR produced them. Matrices are gzip-compressed Matrix Market files with genes in rows and barcodes in columns. Load the filtered folder with:

```python
import scanpy as sc
adata = sc.read_10x_mtx("single-cell-XXXX/counts/filtered_feature_bc_matrix")
```

```r
library(Seurat)
counts <- Read10X("single-cell-XXXX/counts/filtered_feature_bc_matrix")
```

A filtered matrix can contain zero cells. Check `Summary.csv` and the barcode match rates in `run.log` when it does.

## Settings reference

| Desktop | CLI | Default | Meaning |
| --- | --- | --- | --- |
| Chemistry | `--chemistry v2\|v3\|v3.1` | v3 | Sets the UMI length (10 for v2, 12 for v3/v3.1). |
| Include intronic reads | `--exon-only` turns it off | on | On: STARsolo `GeneFull` (exons and introns, common for single-nucleus RNA-seq). Off: `Gene` (exons only). |
| Expected cells | `--expected-cells N` | 3000 | Approximate number of recovered cells. Used for cell calling. |
| Threads | `--threads N` | number of processors, up to 8 | STAR worker threads. |
| Index RAM (GiB) | `--index-ram GIB` | 32 | Memory limit for the genome index build. Alignment needs memory too. |
| STAR executable | `--star FILE` | bundled STAR | See [STAR: bundled and custom builds](#star-bundled-and-custom-builds). |

## Troubleshooting

| Symptom | What to check |
| --- | --- |
| *Could not run STAR* | On Apple Silicon, install Rosetta 2. On other platforms, put STAR on `PATH` or select it. |
| *STAR 2.7.11 or a later STAR 2.x release is required* | The selected or `PATH` STAR is too old. Leave the field blank to use the bundled one, if your platform has it. |
| *Illegal instruction* when STAR starts | The CPU or Rosetta 2 lacks AVX2 support. Use a computer from about 2015 or later, update macOS to 15 or later on Apple Silicon, or build STAR from source. |
| Low barcode match rate warning | The chemistry or whitelist does not match the library, or R1 and R2 are swapped. |
| GTF contig error | The FASTA and GTF come from different sources or releases, for example `chr1` in one file and `1` in the other. |
| STAR runs out of memory | Raise **Index RAM**, use a computer with more memory, or test on a smaller genome first. |
| Zero cells called | Check `Summary.csv`, the mapping rate in `STAR-Log.final.out`, and **Expected cells**. |

### Testing the workflow

The smoke test builds a tiny synthetic genome and library and checks the counts STAR produces:

```bash
ant jar compile-test
java -cp 'build/classes:build/test/classes:lib/*' Eidi._123Fastq.SingleCellPackage.SingleCellSmokeTest --bundled
```

Replace `--bundled` with a path to test another STAR. With no argument, only the input-validation and matrix checks run.
