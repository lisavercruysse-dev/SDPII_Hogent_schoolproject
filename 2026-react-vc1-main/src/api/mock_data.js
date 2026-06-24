// src/api/mock_data.js

const TASK_DATA = [
  {
    id: 1,
    type: "Inspectie",
    omschrijving: "Kwaliteitscontrole lijn B",
    duurtijd: 60,
    startdatum: "2026-03-02T09:00:00", // Maandag
    status: "Afgewerkt",
    specificaties: "Start en controleer assemblagelijn AL-07 en voer een korte testrun uit.",
    machineId: 1,
    machine: "MC-PLT-001",
    memberId: 1
  },
  {
    id: 2,
    type: "Onderhoud",
    omschrijving: "Preventief onderhoud machine A1",
    duurtijd: 120,
    startdatum: "2026-03-04T08:00:00", // Woensdag
    status: "Gepland",
    specificaties: "Controleer en vervang slijtagegevoelige onderdelen, smeer bewegende delen en voer een testcyclus uit.",
    machineId: 2,
    machine: "MC-ASD-023",
    memberId: 4
  },
  {
    id: 3,
    type: "Productie",
    omschrijving: "Productie batch PET flessen 1,5L",
    duurtijd: 240,
    startdatum: "2026-03-05T13:00:00", // Donderdag namiddag
    status: "Gepland",
    specificaties: "Start de productie op lijn C3, controleer de vulmachine, en houd de kwaliteit gedurende de batch in de gaten.",
    machineId: 3,
    machine: "MC-PLT-100",
    memberId: 5
  },
  {
    id: 4,
    type: "Reparatie",
    omschrijving: "Herstellen sensor foutcode E17",
    duurtijd: 90,
    startdatum: "2026-03-06T10:30:00", // Vrijdag voormiddag
    status: "Gepland",
    specificaties: "Diagnosticeer sensor E17, vervang defecte componenten en voer kalibratie uit om correcte werking te garanderen.",
    machineId: 4,
    machine: "MC-HST-003",
    memberId: 6
  },
  {
    id: 5,
    type: "Reparatie",
    omschrijving: "Herstellen sensor foutcode E18",
    duurtijd: 90,
    startdatum: "2026-03-06T13:00:00", // Vrijdag namiddag
    status: "Afgewerkt",
    specificaties: "Controleer foutcode E18, vervang defecte sensoronderdelen en voer een korte functionele test uit.",
    machineId: 3,
    machine: "MC-PLT-100",
    memberId: 7
  },
  {
    id: 6,
    type: "Onderhoud",
    omschrijving: "Onderhoud verpakkingsmachine P2",
    duurtijd: 120,
    startdatum: "2026-03-06T14:30:00", // Vrijdag namiddag
    status: "Gepland",
    specificaties: "Controleer de sensoren en rollen, en voer een proefrun uit om de correcte werking te verifiëren.",
    machineId: 1,
    machine: "MC-PLT-001",
    memberId: 8
  },
  {
    id: 7,
    type: "Inspectie",
    omschrijving: "Veiligheidsinspectie lijn A",
    duurtijd: 45,
    startdatum: "2026-03-22T09:00:00",
    status: "Gepland",
    specificaties: "Controleer veiligheidsvoorzieningen op lijn A, test noodstops en rapporteer eventuele gebreken.",
    machineId: 1,
    machine: "MC-PLT-001",
    memberId: 1
  },
  {
    id: 8,
    type: "Inspectie",
    omschrijving: "Kwaliteitscontrole lijn C",
    duurtijd: 60,
    startdatum: "2026-03-22T10:00:00",
    status: "Afgewerkt",
    specificaties: "Start en controleer assemblagelijn CL-12 en voer een korte testrun uit.",
    machineId: 1,
    machine: "MC-PLT-001",
    memberId: 1
  }
];

const USER_DATA = [
  {
    id: 1,
    firstName: 'Jonas',
    lastName: 'Van Aert',
    email: 'Jonas.VanAert@example.com',
    jobTitel: 'werknemer',
    plantId: 1,
    teamIds: [1, 2]
  },
  {
    id: 2,
    firstName: 'Jan',
    lastName: 'Jansens',
    email: 'Jan.Jansens@example.com',
    jobTitel: 'manager',
  },
  {
    id: 3,
    firstName: 'Pieter',
    lastName: 'De Bakker',
    email: 'Pieter.DeBakker@example.com',
    jobTitel: 'verantwoordelijke',
    plantId: 1,
  },
  {
    id: 4,
    firstName: 'Marie',
    lastName: 'Vermeulen',
    email: 'Marie.Vermeulen@example.com',
    jobTitel: 'werknemer',
    plantId: 1,
    teamIds: [1]
  },
  {
    id: 5,
    firstName: 'Sophie',
    lastName: 'Peeters',
    email: 'Sophie.Peeters@example.com',
    jobTitel: 'werknemer',
    plantId: 1,
    teamIds: [1]
  },
  {
    id: 6,
    firstName: 'Tom',
    lastName: 'Claes',
    email: 'Tom.Claes@example.com',
    jobTitel: 'werknemer',
    plantId: 1,
    teamIds: [2]
  },
  {
    id: 7,
    firstName: 'Lisa',
    lastName: 'Willems',
    email: 'Lisa.Willems@example.com',
    jobTitel: 'werknemer',
    plantId: 1,
    teamIds: [1]
  },
  {
    id: 8,
    firstName: 'Kevin',
    lastName: 'Janssens',
    email: 'Kevin.Janssens@example.com',
    jobTitel: 'werknemer',
    plantId: 1,
    teamIds: [2]
  }
];

const PLANTS = [
  { 
    id: 1,
    name: "Plant A", 
    location: "Eindhoven, Nederland", 
    status: "actief",
    statusProductie: "Waarschuwing",
    lat: 51.44, 
    lng: 5.47
  },
  { 
    id: 2,
    name: "Plant B", 
    location: "Groningen, Nederland", 
    status: "non-actief",
    statusProductie: "Offline",
    lat: 53.22, 
    lng: 6.56 
  },
  {
    id: 3,
    name: "Plant C",
    location: "Gent, België",
    status: "actief",
    statusProductie: "Gezond",
    lat: 51.05, 
    lng: 3.74
  },
  {
    id: 4,
    name: "Plant D",
    location: "Londen, Verenigd Koninkrijk",
    status: "actief",
    statusProductie: "Gezond",
    lat: 51.51, 
    lng: -0.13
  },
  {
    id: 5,
    name: "Plant E",
    location: "New York, Verenigde Staten",
    status: "actief",
    statusProductie: "Gezond",
    lat: 40.71, 
    lng: -74.01
  }
];

const TEAMS = [
  {
    id: 1,
    name: "Team A",
    plantId: 1
  },
  {
    id: 2,
    name: "Team B",
    plantId: 1
  },
  {
    id: 3,
    name: "Team C",
    plantId: 2
  },
  {
    id: 4,
    name: "Team D",
    plantId: 2
  }
];

const MACHINE_DATA = [
  {
    id: 1,
    plantId: 1,
    name: "MC-PLT-001"
  },
  {
    id: 2,
    plantId: 1,
    name: "MC-ASD-023"
  },
  {
    id: 3,
    plantId: 1,
    name: "MC-PLT-100"
  },
  {
    id: 4,
    plantId: 1,
    name: "MC-HST-003"
  },
  {
    id: 5,
    plantId: 2,
    name: "MC-GRN-001"
  }
]

const TASK_TEMPLATE_DATA = [
  {
    id: 1,
    omschrijving: "Onderhoud machine",
    minuten: 60,
    type: "onderhoud",
  },
  {
    id: 2,
    omschrijving: "Herstellen machine",
    minuten: 180,
    type: "reparatie",
  },
    {
    id: 3,
    omschrijving: "Inspectie machine",
    minuten: 30,
    type: "inspectie",
  },
  {
    id: 4,
    omschrijving: "Schoonmaken machine",
    minuten: 45,
    type: "onderhoud",
  },
  {
    id: 5,
    omschrijving: "Onderdelen vervangen",
    minuten: 120,
    type: "reparatie",
  },
  {
    id: 6,
    omschrijving: "Kalibratie machine",
    minuten: 90,
    type: "onderhoud",
  },
  {
    id: 7,
    omschrijving: "Storingsanalyse",
    minuten: 60,
    type: "inspectie",
  },
  {
    id: 8,
    omschrijving: "Veiligheidscontrole",
    minuten: 20,
    type: "inspectie",
  },
  {
    id: 9,
    omschrijving: "Productie goederen",
    minuten: 240,
    type: "productie"
  }
]

export { TASK_DATA, USER_DATA, PLANTS, TEAMS, MACHINE_DATA, TASK_TEMPLATE_DATA };