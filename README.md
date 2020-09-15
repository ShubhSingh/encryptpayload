# encryptpayload to hide sensitive info in JSON payload

encryptpayload is a simple spring boot app which encrypts and decrypts JSON fields to hide sensitive info (like password and userName) when the request is sent over network or passes through intermediate channels like some event driven system.

I did this POC when I wanted to encrypt JSON fields while sending the request through a REST api using RestTemplate and decrypt the incoming request when received by another REST api.

I used 2 encryption and decryption mechanism for this POC:

1. Jackson Crypto as mentioned here: https://github.com/meltmedia/jackson-crypto
2. Jasypt as mentioned here: http://www.jasypt.org/

# How to run this spring boot app

It is a maven project just do: mvn clean install
and then run Spring Boot App in any IDE preferrably Spring Tool Suite.