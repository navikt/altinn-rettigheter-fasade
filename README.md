# Altinn enkeltrettigheter

REST-api som lar deg sjekke status på tilganger og opprette søknader for nye rettigheter.
Kall er alltid i konktesten av en autentisert bruker.

## Mock Authentication
Du må ha naisdevice for å bruke mock-autentisering.
For å lage et mock-token som appen godtar, kan du kjøre følgende kommando:

```shell
TOKEN=$(curl --data-urlencode sub=012345678900 --data-urlencode aud=mockedaudience  https://fakedings.dev-gcp.nais.io/fake/custom)
echo $TOKEN
```

Du kan bytte ut verdien `sub` til et hvilket som helst fødselsnummer.

Dette tokenet må legges ved som en cookie `fakedings` i forespørsler til apiet.
```shell
curl -v --cookie fakedings=$TOKEN http://localhost:8080/api/reportees
```

## Kontakt
Opprett gjerne issue her i github-prosjektet hvis du lurer på noe.

For de med tilgang til NAVs slack, kontakt team *Min side arbeidsgiver* i produktområdet *arbeidsgiver*.