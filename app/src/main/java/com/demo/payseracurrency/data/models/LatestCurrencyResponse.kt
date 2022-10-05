package com.demo.payseracurrency.data.models

import com.google.gson.annotations.SerializedName

data class LatestCurrencyResponse(
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
)

data class Rates(

    @SerializedName("AED")
    val AED: Double,
    @SerializedName("AFN")
    val AFN: Double,
    @SerializedName("ALL")
    val ALL: Double,
    @SerializedName("AMD")
    val AMD: Double,
    @SerializedName("ANG")
    val ANG: Double,
    @SerializedName("AOA")
    val AOA: Double,
    @SerializedName("ARS")
    val ARS: Double,
    @SerializedName("AUD")
    val AUD: Double,
    @SerializedName("AWG")
    val AWG: Double,
    @SerializedName("AZN")
    val AZN: Double,
    @SerializedName("BAM")
    val BAM: Double,
    @SerializedName("BBD")
    val BBD: Double,
    @SerializedName("BDT")
    val BDT: Double,
    @SerializedName("BGN")
    val BGN: Double,
    @SerializedName("BHD")
    val BHD: Double,
    @SerializedName("BIF")
    val BIF: Double,
    @SerializedName("BMD")
    val BMD: Double,
    @SerializedName("BND")
    val BND: Double,
    @SerializedName("BOB")
    val BOB: Double,
    @SerializedName("BRL")
    val BRL: Double,
    @SerializedName("BSD")
    val BSD: Double,
    @SerializedName("BTC")
    val BTC: Double,
    @SerializedName("BTN")
    val BTN: Double,
    @SerializedName("BWP")
    val BWP: Double,
    @SerializedName("BYN")
    val BYN: Double,
    @SerializedName("BYR")
    val BYR: Double,
    @SerializedName("BZD")
    val BZD: Double,
    @SerializedName("CAD")
    val CAD: Double,
    @SerializedName("CDF")
    val CDF: Double,
    @SerializedName("CHF")
    val CHF: Double,
    @SerializedName("CLF")
    val CLF: Double,
    @SerializedName("CLP")
    val CLP: Double,
    @SerializedName("CNY")
    val CNY: Double,
    @SerializedName("COP")
    val COP: Double,
    @SerializedName("CRC")
    val CRC: Double,
    @SerializedName("CUC")
    val CUC: Double,
    @SerializedName("CUP")
    val CUP: Double,
    @SerializedName("CVE")
    val CVE: Double,
    @SerializedName("CZK")
    val CZK: Double,
    @SerializedName("DJF")
    val DJF: Double,
    @SerializedName("DKK")
    val DKK: Double,
    @SerializedName("DOP")
    val DOP: Double,
    @SerializedName("DZD")
    val DZD: Double,
    @SerializedName("EGP")
    val EGP: Double,
    @SerializedName("ERN")
    val ERN: Double,
    @SerializedName("ETB")
    val ETB: Double,
    @SerializedName("EUR")
    val EUR: Int,
    @SerializedName("FJD")
    val FJD: Double,
    @SerializedName("FKP")
    val FKP: Double,
    @SerializedName("GBP")
    val GBP: Double,
    @SerializedName("GEL")
    val GEL: Double,
    @SerializedName("GGP")
    val GGP: Double,
    @SerializedName("GHS")
    val GHS: Double,
    @SerializedName("GIP")
    val GIP: Double,
    @SerializedName("GMD")
    val GMD: Double,
    @SerializedName("GNF")
    val GNF: Double,
    @SerializedName("GTQ")
    val GTQ: Double,
    @SerializedName("GYD")
    val GYD: Double,
    @SerializedName("HKD")
    val HKD: Double,
    @SerializedName("HNL")
    val HNL: Double,
    @SerializedName("HRK")
    val HRK: Double,
    @SerializedName("HTG")
    val HTG: Double,
    @SerializedName("HUF")
    val HUF: Double,
    @SerializedName("IDR")
    val IDR: Double,
    @SerializedName("ILS")
    val ILS: Double,
    @SerializedName("IMP")
    val IMP: Double,
    @SerializedName("INR")
    val INR: Double,
    @SerializedName("IQD")
    val IQD: Double,
    @SerializedName("IRR")
    val IRR: Double,
    @SerializedName("ISK")
    val ISK: Double,
    @SerializedName("JEP")
    val JEP: Double,
    @SerializedName("JMD")
    val JMD: Double,
    @SerializedName("JOD")
    val JOD: Double,
    @SerializedName("JPY")
    val JPY: Double,
    @SerializedName("KES")
    val KES: Double,
    @SerializedName("KGS")
    val KGS: Double,
    @SerializedName("KHR")
    val KHR: Double,
    @SerializedName("KMF")
    val KMF: Double,
    @SerializedName("KPW")
    val KPW: Double,
    @SerializedName("KRW")
    val KRW: Double,
    @SerializedName("KWD")
    val KWD: Double,
    @SerializedName("KYD")
    val KYD: Double,
    @SerializedName("KZT")
    val KZT: Double,
    @SerializedName("LAK")
    val LAK: Double,
    @SerializedName("LBP")
    val LBP: Double,
    @SerializedName("LKR")
    val LKR: Double,
    @SerializedName("LRD")
    val LRD: Double,
    @SerializedName("LSL")
    val LSL: Double,
    @SerializedName("LTL")
    val LTL: Double,
    @SerializedName("LVL")
    val LVL: Double,
    @SerializedName("LYD")
    val LYD: Double,
    @SerializedName("MAD")
    val MAD: Double,
    @SerializedName("MDL")
    val MDL: Double,
    @SerializedName("MGA")
    val MGA: Double,
    @SerializedName("MKD")
    val MKD: Double,
    @SerializedName("MMK")
    val MMK: Double,
    @SerializedName("MNT")
    val MNT: Double,
    @SerializedName("MOP")
    val MOP: Double,
    @SerializedName("MRO")
    val MRO: Double,
    @SerializedName("MUR")
    val MUR: Double,
    @SerializedName("MVR")
    val MVR: Double,
    @SerializedName("MWK")
    val MWK: Double,
    @SerializedName("MXN")
    val MXN: Double,
    @SerializedName("MYR")
    val MYR: Double,
    @SerializedName("MZN")
    val MZN: Double,
    @SerializedName("NAD")
    val NAD: Double,
    @SerializedName("NGN")
    val NGN: Double,
    @SerializedName("NIO")
    val NIO: Double,
    @SerializedName("NOK")
    val NOK: Double,
    @SerializedName("NPR")
    val NPR: Double,
    @SerializedName("NZD")
    val NZD: Double,
    @SerializedName("OMR")
    val OMR: Double,
    @SerializedName("PAB")
    val PAB: Double,
    @SerializedName("PEN")
    val PEN: Double,
    @SerializedName("PGK")
    val PGK: Double,
    @SerializedName("PHP")
    val PHP: Double,
    @SerializedName("PKR")
    val PKR: Double,
    @SerializedName("PLN")
    val PLN: Double,
    @SerializedName("PYG")
    val PYG: Double,
    @SerializedName("QAR")
    val QAR: Double,
    @SerializedName("RON")
    val RON: Double,
    @SerializedName("RSD")
    val RSD: Double,
    @SerializedName("RUB")
    val RUB: Double,
    @SerializedName("RWF")
    val RWF: Double,
    @SerializedName("SAR")
    val SAR: Double,
    @SerializedName("SBD")
    val SBD: Double,
    @SerializedName("SCR")
    val SCR: Double,
    @SerializedName("SDG")
    val SDG: Double,
    @SerializedName("SEK")
    val SEK: Double,
    @SerializedName("SGD")
    val SGD: Double,
    @SerializedName("SHP")
    val SHP: Double,
    @SerializedName("SLL")
    val SLL: Double,
    @SerializedName("SOS")
    val SOS: Double,
    @SerializedName("SRD")
    val SRD: Double,
    @SerializedName("STD")
    val STD: Double,
    @SerializedName("SVC")
    val SVC: Double,
    @SerializedName("SYP")
    val SYP: Double,
    @SerializedName("SZL")
    val SZL: Double,
    @SerializedName("THB")
    val THB: Double,
    @SerializedName("TJS")
    val TJS: Double,
    @SerializedName("TMT")
    val TMT: Double,
    @SerializedName("TND")
    val TND: Double,
    @SerializedName("TOP")
    val TOP: Double,
    @SerializedName("TRY")
    val TRY: Double,
    @SerializedName("TTD")
    val TTD: Double,
    @SerializedName("TWD")
    val TWD: Double,
    @SerializedName("TZS")
    val TZS: Double,
    @SerializedName("UAH")
    val UAH: Double,
    @SerializedName("UGX")
    val UGX: Double,
    @SerializedName("USD")
    val USD: Double,
    @SerializedName("UYU")
    val UYU: Double,
    @SerializedName("UZS")
    val UZS: Double,
    @SerializedName("VND")
    val VND: Double,
    @SerializedName("VUV")
    val VUV: Double,
    @SerializedName("WST")
    val WST: Double,
    @SerializedName("XAF")
    val XAF: Double,
    @SerializedName("XAG")
    val XAG: Double,
    @SerializedName("XAU")
    val XAU: Double,
    @SerializedName("XCD")
    val XCD: Double,
    @SerializedName("XDR")
    val XDR: Double,
    @SerializedName("XOF")
    val XOF: Double,
    @SerializedName("XPF")
    val XPF: Double,
    @SerializedName("YER")
    val YER: Double,
    @SerializedName("ZAR")
    val ZAR: Double,
    @SerializedName("ZMK")
    val ZMK: Double,
    @SerializedName("ZMW")
    val ZMW: Double,
    @SerializedName("ZWL")
    val ZWL: Double
)