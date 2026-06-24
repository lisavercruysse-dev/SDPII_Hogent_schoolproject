export function berekenTaakStatsPerSite(taken, machines, plantId) {
  // 1. Alle machines van deze site
  const machineIds = machines
    .filter((m) => m.plantId === plantId)
    .map((m) => m.id);

  let openTaken = 0;
  let toegewezenTaken = 0;
  let totaalDuurtijdAfgewerkt = 0;
  let aantalAfgewerkteTaken = 0;
  let afgewerktVandaag = 0;
  let geplandVandaag = 0; 

  const vandaag = new Date().toISOString().split('T')[0];

  // 2. Loop door taken
  for (const taak of taken) {
    if (!machineIds.includes(taak.machineId)) continue;

    const status = taak.status.toLowerCase();

    if (status === "gepland") {
      openTaken++;

      // Controleren of de taak VANDAAG is gepland 
      if (taak.startdatum && taak.startdatum.startsWith(vandaag)) {
        geplandVandaag++;
      }
    } else {
      toegewezenTaken++;

      // Alleen afgewerkte taken meetellen voor gemiddelde voltooiingstijd
      if (status === "afgewerkt" && typeof taak.duurtijd === "number") {
        totaalDuurtijdAfgewerkt += taak.duurtijd;
        aantalAfgewerkteTaken++;

        // Controleren of de taak VANDAAG is afgewerkt
        if (taak.startdatum && taak.startdatum.startsWith(vandaag)) {
          afgewerktVandaag++;
        }
      }
    }
  }

  const totaal = openTaken + toegewezenTaken;

  // Bereken gemiddelde voltooiingstijd (in minuten)
  const gemiddeldeVoltooiingstijd = 
    aantalAfgewerkteTaken > 0 
      ? Math.round(totaalDuurtijdAfgewerkt / aantalAfgewerkteTaken) 
      : null;

  return {
    openTaken,
    toegewezenTaken,
    totaal,
    percentageOpen: totaal ? (openTaken / totaal) * 100 : 0,
    percentageToegewezen: totaal ? (toegewezenTaken / totaal) * 100 : 0,
    ratio: toegewezenTaken ? openTaken / toegewezenTaken : null,

    aantalAfgewerkteTaken,
    gemiddeldeVoltooiingstijd, // in minuten
    afgewerktVandaag,
    geplandVandaag
  };
}