# Arkitekturen
Vi har benyttet oss av den anbefalte og moderne app strukturen beskrevet av Android (https://developer.android.com/topic/architecture#recommended-app-arch). Mer spesifikt har app strukturen fulgt MVVM-arkitekturen. 

MVVM-arkitekturen har blitt hovedsakelig ivaretatt når det gjelder mappe- og filstruktur. Målet om lav kobling og høy kohesjon ivaretas mye av å følge MVVM modellen. Når vi deler ting i flere lag får hvert lag høyere kohesjon siden ansvaret blir mer fokusert. Koblingen mellom objekter blir også lav når vi følger modellen siden objektene kun snakker sammen om det de trenger.

Dette fører til at koden blir mer oversiktlig og enklere å både redigere og vedlikeholde. Et unntak til dette er MainActivity.kt, som bryter med MVVM, siden den egentlig kun er knyttet til View. Dette er fordi MainActivity blir brukt som en repository, som henter inn dataen HomeViewModel trenger, selv om det egentlig kun skal brukes til å vise noe på telefonen. En mulig løsning er å flytte denne metoden ut til en egen fil, som en repository og sende inn dataen til HomeViewModel. 

Dette prosjektet bruker teknologiene Android Studio og Kotlin. For UI har vi brukt Jetpack Compose.
Vi startet først med API 24, på grunn av at dette er en standard. Dette har de fleste telefoner tilgang til. Senere oppdaterte vi det til API-nivå 26, hovedsakelig for noen funksjoner som kun er tilgjengelig da og utover. Mengden Android-brukere som ikke har tilgang til appen reduseres minimalt.

Oversikt over strukturen er tilgjengelig i MODELING.md, samt hvordan de ulike klassene og filene interagerer med hverandre. For vedlikehold og videreutvikling, referer til koden. Den er dokumentert med kommentarer over hva de ulike funksjonene gjør.
