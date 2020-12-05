# ccfd
This application is **C**redit **C**ard **F**raud **D**etector. 


## How to run this application
```$xslt
./runCCFD  [OPTIONS] PRICETHRESHOLD FILENAME

Options:
  -h, --help  Show this message and exit

  PRICETHRESHOLD              (MANDATORY) Enter the price threshold.
  FILENAME                    (MANDATORY) Enter *.csv file with complete
                              location.
  HASHEDCREDITCARDNUMBERSIZE  (OPTIONAL) Size of the hashed credit card number
                              by (default is is 27.)

Eg:
$ ./runCCFD 150 sample.csv
```

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

## Requirement=:
Consider the following credit card fraud detection algorithm:
A credit card transaction comprises the following elements.
1. hashed credit card number
2. timestamp - of format year-month-dayThour:minute:second
amount - of format dollars.cents
3. Transactions are to be received in a file as a comma separated string of elements, one per line,
eg:
```aidl
10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00
```
A credit card will be identified as fraudulent if the sum of amounts for a unique hashed credit
card number over a 24-hour sliding window period exceeds the price threshold.
Write a command line application which takes a price threshold argument and a filename, eg:

```aidl
your-app 150.00 filename.csv
```
The file passed to your app will contain a sequence of transactions in **chronological order**.

## Assumptions:
1. Amount value should have 2 decimal digit. Regex used -> "[0-9]*.[0-9]{2}"
2. Hashed credit card number should be combination of digits and small letter Alphabet.
 **By Default the size excepted to be 27**. But it can changed by setting the third parameter with desired value.
  For example if I want to make sure the **Hashed credit card number** is of Size 32.
  You could run command as:
  ```aidl
$ ./runCCFD 150 sample.csv 32
```



## References:
1. [clikt - Used for Getting Comand Line Argument](https://ajalt.github.io/clikt/) 
2. [kotlin-csv - Used for reading the CSV file](https://github.com/doyaaaaaken/kotlin-csv)
2. [Spek - Used for running the test](https://www.spekframework.org/migration/)
3. [Ktlint - Used for Checking and fixing the Linting issue](https://github.com/pinterest/ktlint)
4. [detekt - Used for checking code quality](https://github.com/detekt/detekt)


