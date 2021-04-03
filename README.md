
# Steps for create Heap file

Ran a script to find the larges byte sizes of each column 


# Task 0: DbService Script
For testing the databases I decided to use Typescript as it the language I am most comfortable with. There are four key aspects to the script.
- CLI Interface
  - This is responsible for defining the cli interface for user to use the script
- DataFile
  - This is responsible for connecting to the file and loading the file with a stream
- DbServices
  - This is responsible for taking the loaded entities and saving them to the database
- Processor
  - This is responsible for batching the limiting the stream before forwarding the entities on to the DbService

I created three DBServices on for each of the database types and a mock DbService for testing the rest of the script.

## Usage:
Install Packages: `yarn install` or `npm install`

Build Project: `yarn build` or `npm run build`


Running Script: `node ./bin/main.js write -h`

## Script CLI
```sh
Options:
      --version    Show version number                                 [boolean]
  -h, --help       Show help                                           [boolean]
  -s, --service    service to use for loading
                           [choices: "mock", "mongo", "derby"] [default: "mock"]
  -f, --file       csv file to load the data from        [default: "./data.csv"]
  -l, --limit      The total number of row you want to run 0 == all [default: 0]
  -b, --batchSize  The number of items you want to run in each batch 0 == all
                                                                    [default: 0]
```

# Task 1: Derby
`./src/services/derby.ts`

In order to load the data into derby using my script I had to find a npm library that supported JBCD this script will load the select JDBC driver into a tmp jvm and will then use that to interface to. Once I had this setup I just needed to create a DBService for derby. This initialized the connection and created a client for the Processor to use.

By extending the base entity I was able to create Derby specific Entity function that the DerbyDbService need. These include generating an sql string for bulk write, write and query using the Entities Id.

```sql
CREATE TABLE TESTING (
    id int,
    dateTime TIMESTAMP,
    year int,
    mDate int,
    month VARCHAR(9),
    day VARCHAR(9),
    sensorId int,
    sensorName VARCHAR(39),
    hourlyCount int
);
```

Test Results:
### Optimizations

Test Results:


# Task 2: MongoDB
`./src/services/mongo.ts`

MongoDB has better support for typescript than Derby using the package `mongodb` I was able to connect directly to the mongo db. After creating the `MongoDBService` I was able to run my script using the new service.

Test Results:

### Optimizations

Test Results:
# Task 3: Java Heap File

## Design
In designing the heap I decided to go with fixed length for all fields in order to create a simpler work flow for reading and writing.
After writing a script that could can over all the rows in the csv file. I was able to reduce the file to find the max byte lengths required for each field. With these values I was able to create an entity class that defined each column and the required byte length. I then created a serialize and deserialize function in the class. These functions convert the row into binary and convert binary back into the row. Once I had these methods working I could then start on the paging. I need to add the page breaker to the end of each entity so when scanning the algorithm can check if this is the last entity in the page and continue onto the next page. 


## Testing
I tested reading an writing on several page sizes listed bellow.

| PageSize |   512   |  1024   |  2048   |  4096   |  8192   |  16384  |  32768  |  65536  | 131072  |
| :------: | :-----: | :-----: | :-----: | :-----: | :-----: | :-----: | :-----: | :-----: | :-----: |
|  Pages   | 893649  | 397178  | 198589  |  96611  |  48306  |  24153  |  12036  |  6008   |  3002   |
|  Write   | 22516ms | 18738ms | 17065ms | 17688ms | 15709ms | 16418ms | 16386ms | 18478ms | 18771ms |
|   Read   | 6914ms  | 3223ms  | 1745ms  | 1198ms  |  749ms  |  601ms  |  440ms  |  485ms  |  292ms  |

