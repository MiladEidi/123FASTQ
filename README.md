# 123Fastq

123Fastq is a Java desktop application for FASTQ quality control, format conversion, barcode splitting, and read trimming.

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
java -cp "build/classes:lib/*" Eidi._123Fastq.GUI.App123Fastq
```

Or with the JAR:

```bash
java -jar dist/123Fastq.jar
```

## QC Threads

Quality control asks for a thread count when starting single-file or comparative QC. The default is the number of available processors. Override with:

```bash
java -D123fastq.qc.threads=4 -jar dist/123Fastq.jar
```

or the environment variable `123FASTQ_QC_THREADS`.
