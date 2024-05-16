# Arkitekturen
Vi har benyttet oss av den anbefalte og moderne app strukturen beskrevet av Android (https://developer.android.com/topic/architecture#recommended-app-arch). Mer spesifikt har app strukturen fulgt MVVM-arkitekturen. 

MVVM-arkitekturen har blitt hovedsakelig ivaretatt når det gjleder mappe- og filstruktur. Filene har høy kohesjon og lav kobling mellom hverandre, som fører til at filene kun avhenger av minst mengde filer. Dette fører til at koden blir mer oversiktlig og enklere å både redigere og vedlikeholde. Et unntak utenfor dette er MainActivity.kt, som bryter med MVVM, siden den egentlig kun er knyttet til View. Dette er fordi MainActivity blir brukt som en repository, som henter inn dataen HomeViewModel trenger, selv om det egentlig kun skal brukes til å vise noe på telefonen. En mulig løsning er å flytte denne metoden ut til en egen fil, som en repository og sende inn dataen til HomeViewModel. 

noe om erm mainactivity 
som sett i modeling, koblingene mellom filene/klassene.

Målet om lav kobling og høy kohesjon ivaretas mye av å følge MVVM modellen. Når vi deler ting i flere lag får hvert lag høyere kohesjon siden ansvaret blir mer fokusert.
Koblingen mellom objekter blir også lav når vi følger modellen siden objektene kun snakker sammen om det de trenger.

Vi startet først med API 24, på grunn av at dette er en standard. Dette har de fleste telefoner tilgang til. Senere oppdaterte vi det til API-nivå 26, hovedsakelig for noen funksjoner som kun er tilgjengelig da og utover. Mengden Android-brukere som ikke har tilgang til appen reduseres minimalt.
