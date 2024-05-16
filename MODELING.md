# Aktivitetsdiagram:
![Aktivitetsdiagram](./docs/diagrams/ActivityDiagram.png)



# Klassediagram:
![Klassediagram](./docs/diagrams/KlasseDiagram.png)



# Sekvensdiagram:
![Sekvensdiagram](./docs/diagrams/SekvensDiagram.png)



# Use case diagram:
![Use case diagram](./docs/diagrams/UseCaseDiagram.png)



## Tekstlig beskrivelse for use case

**Navn på Use Case:** Søke etter steder og se værinformasjon  
**Aktør:** Bruker (ungdom mellom 18-25 år)

### Prebetingelser
- Brukeren må ha en enhet med Byge-appen installert.
- Brukeren må ha en aktiv internettforbindelse for å hente data fra værtjenester.
- Brukeren må ha gitt nødvendige tillatelser for appen, som tilgang til internett.

### Postbetingelser
- Brukeren har fått vist oppdatert værinformasjon for det ønskede stedet.
- Brukerens innlogging i appen legges inn database som registrerer daglig bruk for "streak"-funksjonen.

### Hovedflyt
1. **Åpne appen:** Brukeren åpner Byge-appen på sin enhet.
2. **Inngangsskjerm:** Appen spør om å bruke brukerens lokasjon.
3. **Vær for brukerens lokasjon:** Vær for brukerens posisjon innhentes.
4. **Visning av resultater:** Appen viser værinformasjon for det angitte stedet, inkludert temperatur, farevarsel, og vær for kommende timer og dager.
5. **Interaksjon med resultatene:** Brukeren kan trykke på forskjellige deler av værinformasjonen for mer detaljert data.
6. **Avslutte bruk:** Brukeren kan enten fortsette å søke etter andre steder eller lukke appen.

### Alternativflyt
- **Bruker vil ikke bruke sin lokasjon:**
  1. **Taste inn stedsnavn:** Brukeren taster inn navnet på stedet de ønsker værinformasjon for i søkefeltet.
  2. **Søk utføres:** Systemet henter data fra den tilkoblede værtjenesten basert på det inntastede stedsnavnet.
  3. **Visning av resultater:** Appen viser værinformasjon for det angitte stedet, inkludert temperatur, farevarsel, og vær for kommende timer og dager.
  4. **Interaksjon med resultatene:** Brukeren kan trykke på forskjellige deler av værinformasjonen for mer detaljert data.
  5. **Avslutte bruk:** Brukeren kan enten fortsette å søke etter andre steder eller lukke appen.

- **Ingen treff på søk:** Hvis ingen resultater finnes for det inntastede stedsnavnet, viser systemet en melding om at ingen data er tilgjengelig og foreslår at brukeren prøver et annet stedsnavn.
- **Tap av internettforbindelse under søk:** Hvis internettforbindelsen forsvinner under søket, viser appen en feilmelding og ber brukeren sjekke sin nettverkstilkobling før de prøver på nytt.
- **Tekniske problemer med værtjenesten:** Hvis det oppstår en feil med værtjenesten eller serveren som leverer værdata, informerer appen brukeren om at tjenesten midlertidig er utilgjengelig og anbefaler å prøve igjen senere.
