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

