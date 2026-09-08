# 123Fastq

123Fastq is a Java desktop application for FASTQ quality control, format conversion, barcode splitting, and read trimming.

## Project Layout

- `src/` - Java source code and bundled adapter/config resources.
- `test/` - lightweight smoke tests that can run without JUnit.
- `lib/` - required third-party jars for local compilation.
- `build.xml` and `nbproject/` - NetBeans/Ant project files.

Generated folders such as `build/` and `dist/` are intentionally ignored by git.

## Build From Source

With a JDK installed, compile the project from the repository root:

```powershell
New-Item -ItemType Directory -Force -Path build\classes | Out-Null
$sources = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -source 8 -target 8 -cp "lib/*" -d build\classes $sources
```

If Ant is installed, the NetBeans project can also be built with:

```powershell
ant clean jar
```

## Adapter Trimming Smoke Test

```powershell
New-Item -ItemType Directory -Force -Path build\test-classes | Out-Null
javac -encoding UTF-8 -source 8 -target 8 -cp "build/classes;lib/*" -d build\test-classes @(Get-ChildItem -Path test -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp "build/test-classes;build/classes;lib/*" Eidi._123Fastq.TrimFactoryPackage.AdapterTrimmerSmokeTest
java -cp "build/test-classes;build/classes;src;lib/*" Eidi._123Fastq.QualityControlPackage.Results.QcRunnerSmokeTest
```

## QC Threads

Quality control asks for a thread count when starting single-file or comparative QC. The default is the available processor count and can also be set with `-D123fastq.qc.threads=<threads>` or the `123FASTQ_QC_THREADS` environment variable.
