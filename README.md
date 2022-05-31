# Crypto Investment recommendation service

### _This service was developed to help investors in choosing cryptocurrencies_

## Main functionality
 - Reads all the prices from the csv files
 - Calculates oldest/newest/min/max for each crypto for the whole month
 - Compute the normalized range for each crypto
 - Peek the crypto with the highest normalized range for a
   specific day
> Go to <your_server>/swagger-ui.html to read API specification

### Adding new crypto
 If you want to add new crypto, just add file to `src/main/resources/prices`
 with data and name it like `CRYPTO_NAME_values.csv`.

### Adding new data about existing crypto
If you want to add new data about existing crypto,
just add it to existing file `CRYPTO_NAME_values.csv`.
 
