
## Steps for create Heap file

Ran a script to find the larges byte sizes of each column 




➜ javac dbload.java && java dbload -p 1024 ../../data.csv && java dbload -p 2048 ../../data.csv && java dbload -p 4096 ../../data.csv && java dbload -p 8192 ../../data.csv
[-p, 1024, ../../data.csv]
Page total: 397178
Record total: 3574594
Load time: 20606ms
[-p, 2048, ../../data.csv]
Page total: 198589
Record total: 3574594
Load time: 18371ms
[-p, 4096, ../../data.csv]
Page total: 96611
Record total: 3574594
Load time: 16817ms
[-p, 8192, ../../data.csv]
Page total: 48306
Record total: 3574594
Load time: 15811ms

➜ javac dbload.java && java dbload -p 1024 ../../data.csv && java dbload -p 2048 ../../data.csv && java dbload -p 4096 ../../data.csv && java dbload -p 8192 ../../data.csv     
[-p, 1024, ../../data.csv]
Page total: 397178
Record total: 3574594
Load time: 19955ms
[-p, 2048, ../../data.csv]
Page total: 198589
Record total: 3574594
Load time: 15639ms
[-p, 4096, ../../data.csv]
Page total: 96611
Record total: 3574594
Load time: 16569ms
[-p, 8192, ../../data.csv]
Page total: 48306
Record total: 3574594
Load time: 15808ms

➜ javac dbload.java && java dbload -p 16384 ../../data.csv && java dbload -p 32768 ../../data.csv && java dbload -p 65536 ../../data.csv && java dbload -p 131072 ../../data.csv
[-p, 16384, ../../data.csv]
Page total: 24153
Record total: 3574594
Load time: 16214ms
[-p, 32768, ../../data.csv]
Page total: 12036
Record total: 3574594
Load time: 15819ms
[-p, 65536, ../../data.csv]
Page total: 6008
Record total: 3574594
Load time: 15928ms
[-p, 131072, ../../data.csv]
Page total: 3002
Record total: 3574594
Load time: 16458ms

➜ javac dbload.java && java dbload -p 16384 ../../data.csv && java dbload -p 32768 ../../data.csv && java dbload -p 65536 ../../data.csv && java dbload -p 131072 ../../data.csv
[-p, 16384, ../../data.csv]
Page total: 24153
Record total: 3574594
Load time: 15996ms
[-p, 32768, ../../data.csv]
Page total: 12036
Record total: 3574594
Load time: 16531ms
[-p, 65536, ../../data.csv]
Page total: 6008
Record total: 3574594
Load time: 16738ms
[-p, 131072, ../../data.csv]
Page total: 3002
Record total: 3574594
Load time: 15562ms