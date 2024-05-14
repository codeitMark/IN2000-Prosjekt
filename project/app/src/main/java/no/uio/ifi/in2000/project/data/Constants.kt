package no.uio.ifi.in2000.project.data

import java.util.Calendar

object Constants {
    val monthMap = mapOf(
        1 to "januar",
        2 to "februar",
        3 to "mars",
        4 to "april",
        5 to "mai",
        6 to "juni",
        7 to "juli",
        8 to "august",
        9 to "september",
        10 to "oktober",
        11 to "november",
        12 to "desember"
    )

    val dayOfWeekMap = mapOf(
        Calendar.MONDAY to "Man",
        Calendar.TUESDAY to "Tir",
        Calendar.WEDNESDAY to "Ons",
        Calendar.THURSDAY to "Tor",
        Calendar.FRIDAY to "Fre",
        Calendar.SATURDAY to "Lør",
        Calendar.SUNDAY to "Søn"
    )

    val timeFormat = mapOf(
        "00:00:00Z" to "00-06",
        "06:00:00Z" to "06-12",
        "12:00:00Z" to "12-18",
        "18:00:00Z" to "18-00"
    )

    fun getWeatherDescription(text: String): String? {
            return when (text) {
                "clearsky_day", "clearsky_night", "clearsky_polartwilight" -> "er klar himmel"
                "fair_day", "fair_night", "fair_polartwilight" -> "er lettskyet"
                "partlycloudy_day", "partlycloudy_night", "partlycloudy_polartwilight" -> "er delvis skyet"
                "cloudy" -> "er overskyet"
                "rainshowers_day", "rainshowers_night", "rainshowers_polartwilight" -> "er regnbyger"
                "rainshowersandthunder_day", "rainshowersandthunder_night", "rainshowersandthunder_polartwilight" -> "er regnbyger og torden"
                "sleetshowers_day", "sleetshowers_night", "sleetshowers_polartwilight" -> "er sluddbyger"
                "snowshowers_day", "snowshowers_night", "snowshowers_polartwilight" -> "er snøbyger"
                "rain" -> "regner"
                "heavyrain" -> "er kraftig regn"
                "heavyrainandthunder" -> "er kraftig regn og torden"
                "sleet" -> "er sludd"
                "snow" -> "snør"
                "snowandthunder" -> "er snø og torden"
                "fog" -> "er tåkete"
                "sleetshowersandthunder_day", "sleetshowersandthunder_night", "sleetshowersandthunder_polartwilight" -> "er sluddbyger og torden"
                "snowshowersandthunder_day", "snowshowersandthunder_night", "snowshowersandthunder_polartwilight" -> "er snøbyger og torden"
                "rainandthunder" -> "er regn og torden"
                "sleetandthunder" -> "er sludd og torden"
                "lightrainshowersandthunder_day", "lightrainshowersandthunder_night", "lightrainshowersandthunder_polartwilight" -> "er lette regnbyger og torden"
                "heavyrainshowersandthunder_day", "heavyrainshowersandthunder_night", "heavyrainshowersandthunder_polartwilight" -> "er kraftige regnbyger og torden"
                "lightsleetshowersandthunder_day", "lightsleetshowersandthunder_night", "lightsleetshowersandthunder_polartwilight" -> "er lette sluddbyger og torden"
                "heavysleetshowersandthunder_day", "heavysleetshowersandthunder_night", "heavysleetshowersandthunder_polartwilight" -> "er kraftige sluddbyger og torden"
                "lightsnowshowersandthunder_day", "lightsnowshowersandthunder_night", "lightsnowshowersandthunder_polartwilight" -> "er lette snøbyger og torden"
                "heavysnowshowersandthunder_day", "heavysnowshowersandthunder_night", "heavysnowshowersandthunder_polartwilight" -> "er kraftige snøbyger og torden"
                "lightrainandthunder" -> "er lett regn og torden"
                "lightsleetandthunder" -> "er lett sludd og torden"
                "heavysleetandthunder" -> "er kraftig sludd og torden"
                "lightsnowandthunder" -> "er lett snø og torden"
                "heavysnowandthunder" -> "er kraftig snø og torden"
                "lightrainshowers_day", "lightrainshowers_night", "lightrainshowers_polartwilight" -> "er lette regnbyger"
                "heavyrainshowers_day", "heavyrainshowers_night", "heavyrainshowers_polartwilight" -> "er kraftige regnbyger"
                "lightsleetshowers_day", "lightsleetshowers_night", "lightsleetshowers_polartwilight" -> "er lette sluddbyger"
                "heavysleetshowers_day", "heavysleetshowers_night", "heavysleetshowers_polartwilight" -> "er kraftige sluddbyger"
                "lightsnowshowers_day", "lightsnowshowers_night", "lightsnowshowers_polartwilight" -> "er lette snøbyger"
                "heavysnowshowers_day", "heavysnowshowers_night", "heavysnowshowers_polartwilight" -> "er kraftige snøbyger"
                "lightrain" -> "er lett regn"
                "lightsleet" -> "er lett sludd"
                "heavysleet" -> "er kraftig sludd"
                "lightsnow" -> "er lett snø"
                "heavysnow" -> "snør kraftig"
                else -> null
            }
    }
}