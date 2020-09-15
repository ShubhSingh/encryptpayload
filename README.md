# encryptpayload to hide sensitive info in JSON payload

encryptpayload is a simple spring boot app which encrypts and decrypts JSON fields to hide sensitive info (like password and userName) when the request is sent over network or passes through intermediate channels like some event driven system.

I did this POC when I wanted to encrypt JSON fields while sending the request through a REST api using RestTemplate and decrypt the incoming request when received by another REST api.

I used 2 encryption and decryption mechanism for this POC:

1. Jackson Crypto as mentioned here: https://github.com/meltmedia/jackson-crypto
2. Jasypt as mentioned here: http://www.jasypt.org/

# How to run this spring boot app

It is a maven project just do: `mvn clean install` and then run Spring Boot App in any IDE preferrably Spring Tool Suite.
This app will run at `server.port=9045`

Use POSTMAN to run REST apis for example:

POST http://localhost:9045/api/send-jasypt
```yaml
{
    "envName": "shusime",
    "password": "Welcome@1",
    "userName": "xyz@gmail.com",
    "endDate": null
}
```
OR

POST http://localhost:9045/api/send-crypto
```yaml
{
    "envName": "shusime",
    "password": {
        "salt": "zk6g7KTuHb0GquM26/VDeMANBG4=",
        "iv": "YuVPjgxPJK7jBrQrdSKfIw==",
        "value": "o3ywZaXNlEVpRyKW38D0AQ=="
    },
    "userName": {
        "salt": "zk6g7KTuHb0GquM26/VDeMANBG4=",
        "iv": "YuVPjgxPJK7jBrQrdSKfIw==",
        "value": "yEHxrTNbFKY+YNiLMYBDuDA+pm89l5Fw3iRP9jr4hVI="
    },
    "endDate": null
}
```

Check the application logs to see encrypted JSON payload being sent and received and how it is getting decrypted to be used.
