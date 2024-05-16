# Byge

En værapp designet for unge voksne som bruker android, fra alderen 18-25.

# Dokumentasjon

Under ser du hva som finnes av dokumentasjon som tilhører Byge. Du finner også hvor du finner den - Brukerdokumentasjon -> Rapporten - Produktdokumentasjon -> Rapporten - Archetecture.md -> root directory - Modelling.md -> root directory

# Hvordan kjøre appen

Du kan få tak i appen på GitHub her: https://github.uio.no/IN2000-V24/team-38.git
For å kjøre appen kan du klone repositoriet fra linken over, for så å kjøre den enten på android enheten din, eller på en emulator.

# Biblioteker

    - ktor og gson
    - viewmodel og coroutine
    - coil
    - datastore
    - google play services (location)
    - junit
    - Jetpack Compose

# Kjente feil

    - Under første lasting må appen restartes etter å ha gitt tillatelse til å bruke brukerlokasjon for at den skal hente brukerlokasjonen. Dette kan også løses ved å åpne Google Maps og angi posisjon før man åpner appen.
    - Under recomposition av skjermen mister en hvilket sted en har valgt
