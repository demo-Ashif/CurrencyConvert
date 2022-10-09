# CurrencyConvert App
This app mainly demonstarate how foreign currency can be converted into another currency. Also you can add your own currency amount. The goal was build 
an app in loosely coupled way and show the usage of libraries in effective way. 

**MVVM** pattern followed. **Dagger-Hilt** was used for dependecy injection. **Room DB** for local DB support. Kotlin couroutines
to run tasks efficiently in threads. **Retrofit & okHttp3** was used for API config and network interception. Followed
repository pattern and provided offline support


To get the data about latest currency rate I have used [Exchange Rates API](https://api.exchangeratesapi.io/).


**Response example**:

```
{
    "success": true,
    "timestamp": 1664976663,
    "base": "EUR",
    "date": "2022-10-05",
    "rates": {
        "AED": 3.629653,
        "AFN": 86.746247,
        "ALL": 116.956017,
        "AMD": 400.434646,
        "ANG": 1.781149,
        "AOA": 429.678496,
        "ARS": 147.14983,
        "AUD": 1.534749,
        "DZD": 139.150144,
        "EGP": 19.42952,
        "ERN": 14.822794,
        "ETB": 52.242481,
        "EUR": 1 
    }
}
```

#### Core App Functionalities
- User Can Add Currency
- Convert to Any Currency
- Can see Currency Conversion Commission Value Change
#### Screenshots

![Screenshot 01](https://i.ibb.co/hFvLtdZ/Screenshot-2022-10-07-at-7-53-05-PM.png) ![Screenshot 02](https://i.ibb.co/CvRSJtG/Screenshot-2022-10-07-at-7-54-31-PM.png) ![Screenshot 03](https://i.ibb.co/NpCNvRZ/Screenshot-2022-10-07-at-7-53-36-PM.png) 

### Future Scope
- Writing all test cases
