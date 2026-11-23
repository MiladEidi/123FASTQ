# Command-line reference

*New in 123Fastq 1.4*

Every 123Fastq workflow except the desktop itself can run without a display. This makes 123Fastq usable on servers, in cluster jobs, and in pipelines such as Snakemake or Nextflow.

```bash
java -jar 123Fastq.jar <command> [options]
java -jar 123Fastq.jar <command> --help
java -jar 123Fastq.jar --version        # prints: 123Fastq 1.4
```

| Command | Purpose |
| --- | --- |
| [`qc`](#qc) | Generate QC HTML and ZIP reports |
| [`trim`](#trim) | Trim single-end or paired-end FASTQ reads |
| [`split-barcodes`](#split-barcodes) | Assign FASTQ reads to samples by end barcodes |
| [`sam-to-fastq`](#sam-to-fastq) | Convert SAM or BAM to FASTQ |
| [`fast5-to-fastq`](#fast5-to-fastq) | Extract embedded basecalls from single-read FAST5 files |
| [`single-cell`](#single-cell) | Generate raw and cell-filtered RNA-seq UMI matrices |
| `gui` | Open the desktop application |

With no arguments, the JAR opens the desktop on a graphical system and prints this help on a headless system. CLI commands run headlessly even when a display is available.

## Conventions

- **Output listing.** On success, each command prints its outputs on stdout as tab-separated `key<TAB>value` lines, so scripts can parse them.
- **Progress and errors** go to stderr. `--quiet` suppresses progress messages.
- **Exit codes:**

  | Code | Meaning |
  | --- | --- |
  | `0` | Success |
  | `1` | Processing or I/O failure |
  | `2` | Invalid command usage |
  | `130` | Interrupted (for example Ctrl-C) |

- **Safe outputs.** Commands do not overwrite existing outputs, and they create output directories as needed.
- **Compression.** FASTQ inputs can be plain, `.gz` or `.bz2`. Name an output `.gz` or `.bz2` to compress it.
- **Repeated options.** Options such as `--input`, `--step`, `--r1` and `--r2` can be given more than once.

## qc

```text
qc --input FILE [--input FILE ...] --out-dir DIR [options]
```

Writes one HTML report and ZIP archive per input. Inputs can be FASTQ (plain, gzip or bzip2), SAM/BAM, or single-read FAST5.

| Option | Description |
| --- | --- |
| `--compare` | Generate one comparative report from exactly two inputs. |
| `--mapped-only` | Analyze mapped SAM/BAM reads only, excluding soft-clipped bases. |
| `--threads N` | QC worker threads (1 by default). |

```bash
java -jar 123Fastq.jar qc --input reads.fastq.gz --out-dir qc --threads 4
java -jar 123Fastq.jar qc --input R1.fastq.gz --input R2.fastq.gz --compare --out-dir qc
```

## trim

```text
trim --input FILE --output FILE [options]                                   # single-end
trim --r1 FILE --r2 FILE --out-r1 FILE --out-r2 FILE [options]              # paired-end
```

Supply at least one `--step` or an `--adapter-file`. Steps run in the order given, after adapter-file clipping. Paired reads must have synchronized read IDs. A pair is kept only when both mates survive.

| Option | Description |
| --- | --- |
| `--step STEP` | A trimming operation. Repeat to chain operations. See the table below. |
| `--orphans-r1 FILE`, `--orphans-r2 FILE` | Save surviving reads whose mate was removed. |
| `--phred 33\|64` | Input quality encoding (33 by default). |
| `--threads N` | Trimming workers (1 by default). Output order is preserved. |
| `--drop-comments` | Clear the text on the `+` line. |
| `--report FILE` | Write tab-separated read or pair totals. |
| `--adapter-file FILE` | Clip with a FASTA of Illumina adapters before any `--step`. |
| `--adapter-seed-mismatches N` | Adapter seed mismatches (2 by default). |
| `--adapter-palindrome N` | Minimum palindrome clipping score (30 by default). |
| `--adapter-simple N` | Minimum simple clipping score (10 by default). |
| `--adapter-min-overlap N` | Minimum palindrome adapter overlap (8 by default). |
| `--keep-both-adapter-reads` | Keep reverse reads in palindrome mode. |

| Step | Effect |
| --- | --- |
| `leading:Q` | Remove leading bases with quality below Q. |
| `trailing:Q` | Remove trailing bases with quality below Q. |
| `mean-quality:Q` | Drop reads with mean quality below Q. |
| `sliding-window:SIZE:Q` | Cut once the mean quality in a SIZE-base window falls below Q. |
| `head-crop:N` | Remove N bases from the start. |
| `end-crop:N[:READ_LENGTH_THRESHOLD]` | Remove N bases from the end, optionally only from reads longer than READ_LENGTH_THRESHOLD. Cropped reads of N bases or fewer are dropped. |
| `min-length:N`, `max-length:N` | Drop reads shorter or longer than N. |
| `poly-g:N` | Trim a trailing poly-G run of at least N bases. |
| `poly-base:N` | Trim a trailing homopolymer of any base of at least N bases. |
| `gc:MIN:MAX` | Keep reads with GC percentage from MIN to MAX. |
| `max-info:TARGET:STRICTNESS` | Adaptive quality trimming toward a TARGET length (1–700). Strictness ranges from 0 to 1, and reads can be up to 1000 bases. |
| `adapter:SEQUENCE` | Short k-mer adapter clipping for one sequence. |
| `to-phred33`, `to-phred64` | Convert quality encoding. |

```bash
java -jar 123Fastq.jar trim --input reads.fastq.gz --output trimmed.fastq.gz \
  --step sliding-window:4:20 --step min-length:35 --report trim-summary.tsv

java -jar 123Fastq.jar trim --r1 R1.fastq.gz --r2 R2.fastq.gz \
  --out-r1 R1.paired.fastq.gz --out-r2 R2.paired.fastq.gz \
  --orphans-r1 R1.orphans.fastq.gz --orphans-r2 R2.orphans.fastq.gz \
  --adapter-file TruSeq3-PE.fa --step trailing:20 --step min-length:35 --threads 4
```

## split-barcodes

```text
split-barcodes --input FILE --barcodes TSV --out-dir DIR [options]
```

The sample sheet is tab-separated, with one sample name and one barcode per row. All barcodes must have the same length and contain only A, C, G and T. Blank lines and lines starting with `#` are skipped.

```text
# sample	barcode
liver	ACGTAC
kidney	TGCATG
```

| Option | Description |
| --- | --- |
| `--end 5\|3` | Match at the 5′ end (default) or the 3′ end. |
| `--max-mismatches N` | Allow up to N substitutions (0 by default). |
| `--clip` | Remove the matched barcode and its quality scores. |
| `--gzip` | Write compressed FASTQs. |

Each sample gets its own FASTQ. Unmatched reads, ambiguous reads (tied best matches), and reads that would be empty after `--clip` go to `Undetermined.fastq`. `barcode-summary.tsv` records counts by sample and by reason.

```bash
java -jar 123Fastq.jar split-barcodes --input reads.fastq.gz --barcodes samples.tsv \
  --out-dir demux --end 5 --max-mismatches 1 --clip --gzip
```

## sam-to-fastq

```text
sam-to-fastq --input FILE --output FILE [options]
```

Writes reads in file order and restores the original sequencing orientation of reverse-strand alignments. By default, only primary records with bases and qualities are exported.

| Option | Description |
| --- | --- |
| `--mapped-only` | Keep mapped reads only. |
| `--all-alignments` | Include secondary and supplementary records. |
| `--skip-missing` | Skip records without bases or qualities instead of failing. |

```bash
java -jar 123Fastq.jar sam-to-fastq --input reads.bam --output reads.fastq.gz
```

## fast5-to-fastq

```text
fast5-to-fastq --input FILE [--input FILE ...] --output FILE
```

Extracts existing embedded basecalls from legacy single-read Oxford Nanopore FAST5 files. It does not basecall raw signal. It requires a platform supported by the bundled HDF5 native library.

```bash
java -jar 123Fastq.jar fast5-to-fastq --input read1.fast5 --input read2.fast5 --output reads.fastq.gz
```

## single-cell

```text
single-cell --r1 R1 --r2 R2 --fasta FASTA --gtf GTF --whitelist FILE --out-dir DIR [options]
```

Counts one 10x Chromium 3′ Gene Expression library with STARsolo. Repeat `--r1` and `--r2` in matching lane order. Plain and gzip inputs are accepted. The full guide is in [single-cell-rna-seq.md](single-cell-rna-seq.md).

| Option | Description |
| --- | --- |
| `--chemistry v2\|v3\|v3.1` | 10x chemistry (v3 by default). |
| `--star FILE` | STAR executable. Defaults to the bundled STAR 2.7.11b on Linux x86-64 and macOS, otherwise STAR on `PATH`. Must be 2.7.11 or a later 2.x release. |
| `--exon-only` | Count exonic reads only. The default also counts intronic reads. |
| `--expected-cells N` | Approximate number of recovered cells, used for cell calling (3000 by default). |
| `--threads N` | STAR threads (number of processors, up to 8, by default). |
| `--index-ram GIB` | STAR index memory limit in GiB (32 by default). |

Output keys: `run`, `filtered_matrix`, `raw_matrix`, `called_cells`, `genes`, `filtered_umis`.

```bash
java -jar 123Fastq.jar single-cell \
  --r1 lane1_R1.fastq.gz --r2 lane1_R2.fastq.gz \
  --r1 lane2_R1.fastq.gz --r2 lane2_R2.fastq.gz \
  --fasta genome.fa.gz --gtf genes.gtf.gz --whitelist 3M-february-2018.txt.gz \
  --chemistry v3.1 --expected-cells 5000 --threads 8 --out-dir results
```

## Using outputs in scripts

Because outputs are listed as tab-separated keys on stdout, a shell script can capture them directly:

```bash
matrix=$(java -jar 123Fastq.jar single-cell --quiet ... | awk -F'\t' '$1=="filtered_matrix"{print $2}')
```
