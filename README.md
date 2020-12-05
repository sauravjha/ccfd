# ccfd
This application is to detect Credit Card Fraud. 

## How to run this application ?
```$xslt
./runCCFD  [OPTIONS] PRICETHRESHOLD FILENAME

Options:
  -h, --help  Show this message and exit

Arguments:
  PRICETHRESHOLD  Enter the price threshold argument
  FILENAME        Enter file with complete location( if the file exist in
                  current dir just enter the name)

```
Example:
```$xslt
./runCCFD 150 sample.csv
```
First argument should be Int or Double/Float.
Second argument should be filename that exists.

## How to run Test
```$xslt
./gradlew test 
```

## Code Quality Check task/script
:star2: Kotlin
```$xslt
Check the lint
./gradlew ktlintCheck
Fix the lint
./gradlew ktlintFormat
```
:sparkles: Linting is taken care. :smiley:

Static code analysis

:dizzy: Detekt
```$xslt
./gradlew detekt
```

## References:
1. [clikt - Used for Getting Comand Line Argument](https://ajalt.github.io/clikt/) 
2. [kotlin-csv - Used for reading the CSV file](https://github.com/doyaaaaaken/kotlin-csv)
2. [Spek - Used for running the test](https://www.spekframework.org/migration/)
3. [Ktlint - Used for Checking and fixing the Linting issue](https://github.com/pinterest/ktlint)
4. [detekt - Used for checking code quality](https://github.com/detekt/detekt)


