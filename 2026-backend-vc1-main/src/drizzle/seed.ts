import { drizzle } from 'drizzle-orm/mysql2';
import * as mysql from 'mysql2/promise';
import * as schema from './schema';
import * as argon2 from 'argon2';

const connection = mysql.createPool({
  uri: process.env.DATABASE_URL,
  connectionLimit: 5,
});

const db = drizzle(connection, {
  schema,
  mode: 'default',
});

async function resetDatabase() {
  console.log('🗑️ Resetting database...');

  await db.delete(schema.notificaties);
  await db.delete(schema.team_werknemers);
  await db.delete(schema.teams);
  await db.delete(schema.afwezigheden);
  await db.delete(schema.taken);
  await db.delete(schema.werknemers);
  await db.delete(schema.site_machines);
  await db.delete(schema.machines);
  await db.delete(schema.taakTemplates);
  await db.delete(schema.sites);

  console.log('✅ Database reset completed\n');
}

async function main() {
  console.log('🌱 Starting database seeding...\n');

  await resetDatabase();
  await seedSites();
  await seedWerknemers();
  await seedTeams();
  await seedTeam_Werknemers();
  await seedMachines();
  await seedSiteMachines();
  await seedTaakTemplates();
  await seedTaken();

  console.log('🎉 Database seeding completed successfully!');
}

async function hashPassword(password: string): Promise<string> {
  return argon2.hash(password, {
    type: argon2.argon2id,
    hashLength: 32,
    timeCost: 2,
    memoryCost: 2 ** 16,
  });
}

main()
  .then(async () => {
    await connection.end();
  })
  .catch(async (e) => {
    console.error(e);
    await connection.end();
    process.exit(1);
  });

async function seedWerknemers() {
  console.log('Seeding werknemers...');

  const password = await hashPassword('12345678');

  await db.insert(schema.werknemers).values([
    {
      id: 1,
      firstName: 'Jonas',
      lastName: 'Van Aert',
      jobTitel: 'WERKNEMER',
      email: 'Jonas.VanAert@example.com',
      passwordHash: password,
      telefoon: '0485123456',
      geboortedatum: '1995-03-14',
      land: 'België',
      postcode: '9000',
      stad: 'Gent',
      straat: 'Vlaanderenstraat',
      huisnummer: 12,
      bus: null,
      status: 'ACTIEF',
    },
    {
      id: 2,
      firstName: 'Sofie',
      lastName: 'Peeters',
      jobTitel: 'WERKNEMER',
      email: 'Sofie.Peeters@example.com',
      passwordHash: password,
      telefoon: '0472345678',
      geboortedatum: '1992-07-22',
      land: 'België',
      postcode: '2000',
      stad: 'Antwerpen',
      straat: 'Belgiëlei',
      huisnummer: 45,
      bus: 3,
      status: 'ACTIEF',
    },
    {
      id: 3,
      firstName: 'Lars',
      lastName: 'De Smet',
      jobTitel: 'WERKNEMER',
      email: 'Lars.DeSmet@example.com',
      passwordHash: password,
      telefoon: '0496234567',
      geboortedatum: '1998-11-05',
      land: 'België',
      postcode: '3000',
      stad: 'Leuven',
      straat: 'Naamsestraat',
      huisnummer: 78,
      bus: null,
      status: 'ACTIEF',
    },
    {
      id: 4,
      firstName: 'Emma',
      lastName: 'Claes',
      jobTitel: 'WERKNEMER',
      email: 'Emma.Claes@example.com',
      passwordHash: password,
      telefoon: '0479456789',
      geboortedatum: '1997-04-30',
      land: 'België',
      postcode: '8000',
      stad: 'Brugge',
      straat: 'Langestraat',
      huisnummer: 5,
      bus: null,
      status: 'INACTIEF',
    },
    {
      id: 5,
      firstName: 'Jan',
      lastName: 'Jansens',
      jobTitel: 'MANAGER',
      email: 'Jan.Jansens@example.com',
      passwordHash: password,
      telefoon: '0468567890',
      geboortedatum: '1980-09-18',
      land: 'België',
      postcode: '1000',
      stad: 'Brussel',
      straat: 'Louisalaan',
      huisnummer: 200,
      bus: 7,
      status: 'ACTIEF',
    },
    {
      id: 6,
      firstName: 'Pieter',
      lastName: 'De Bakker',
      jobTitel: 'VERANTWOORDELIJKE',
      email: 'Pieter.DeBakker@example.com',
      passwordHash: password,
      telefoon: '0491678901',
      geboortedatum: '1978-02-11',
      land: 'België',
      postcode: '2018',
      stad: 'Antwerpen',
      straat: 'Frankrijklei',
      huisnummer: 33,
      bus: null,
      status: 'ACTIEF',
    },
    {
      id: 7,
      firstName: 'Hannah',
      lastName: 'Peeters',
      jobTitel: 'VERANTWOORDELIJKE',
      email: 'Hannah.Peeters@example.com',
      passwordHash: password,
      telefoon: '0487789012',
      geboortedatum: '1985-06-25',
      land: 'België',
      postcode: '9820',
      stad: 'Merelbeke',
      straat: 'Hundelgemsesteenweg',
      huisnummer: 14,
      bus: 2,
      status: 'ACTIEF',
    },
    {
      id: 8,
      firstName: 'Pascal',
      lastName: 'Hermans',
      jobTitel: 'VERANTWOORDELIJKE',
      email: 'Pascal.Hermans@example.com',
      passwordHash: password,
      telefoon: '0476890123',
      geboortedatum: '1982-12-03',
      land: 'België',
      postcode: '3500',
      stad: 'Hasselt',
      straat: 'Aldestraat',
      huisnummer: 6,
      bus: null,
      status: 'ACTIEF',
    },
    {
      id: 9,
      firstName: 'Jonas',
      lastName: 'Maas',
      jobTitel: 'WERKNEMER',
      email: 'Jonas.Maas@example.com',
      passwordHash: password,
      telefoon: '0494901234',
      geboortedatum: '2000-08-17',
      land: 'België',
      postcode: '8500',
      stad: 'Kortrijk',
      straat: 'Doorniksestraat',
      huisnummer: 3,
      bus: null,
      status: 'ACTIEF',
    },
    {
      id: 10,
      firstName: 'Katrien',
      lastName: 'Vandromme',
      jobTitel: 'WERKNEMER',
      email: 'Katrien.Vandromme@example.com',
      passwordHash: password,
      telefoon: '0494901234',
      geboortedatum: '2000-08-17',
      land: 'België',
      postcode: '8500',
      stad: 'Kortrijk',
      straat: 'Doorniksestraat',
      huisnummer: 3,
      bus: null,
      status: 'INACTIEF',
    },
    {
      id: 11,
      firstName: 'Ad',
      lastName: 'Min',
      jobTitel: 'ADMINISTRATOR',
      email: 'Ad.Min@example.com',
      passwordHash: password,
      telefoon: '0494901235',
      geboortedatum: '2000-09-16',
      land: 'België',
      postcode: '8500',
      stad: 'Kortrijk',
      straat: 'Doorniksestraat',
      huisnummer: 4,
      bus: null,
      status: 'ACTIEF',
    },
    {
      id: 12,
      firstName: 'Vera',
      lastName: 'Inactief',
      jobTitel: 'VERANTWOORDELIJKE',
      email: 'Vera.Inactief@example.com',
      passwordHash: password,
      telefoon: '0494901235',
      geboortedatum: '2000-09-16',
      land: 'België',
      postcode: '9200',
      stad: 'Zelzate',
      straat: 'Schansstraat',
      huisnummer: 14,
      bus: null,
      status: 'INACTIEF',
    },
    {
      id: 13,
      firstName: 'Manager',
      lastName: 'Inactief',
      jobTitel: 'MANAGER',
      email: 'Manager.Inactief@example.com',
      passwordHash: password,
      telefoon: '0494901230',
      geboortedatum: '1999-09-16',
      land: 'België',
      postcode: '9160',
      stad: 'Lokeren',
      straat: 'Kerkstraat',
      huisnummer: 10,
      bus: null,
      status: 'INACTIEF',
    },
    {
      id: 14,
      firstName: 'Lien',
      lastName: 'Vermeersch',
      jobTitel: 'WERKNEMER',
      email: 'Lien.Vermeersch@example.com',
      passwordHash: password,
      telefoon: '0472123456',
      geboortedatum: '1996-05-12',
      land: 'België',
      postcode: '9000',
      stad: 'Gent',
      straat: 'Korenmarkt',
      huisnummer: 8,
      bus: null,
      status: 'ACTIEF',
    },
    {
      id: 15,
      firstName: 'Bram',
      lastName: 'Declercq',
      jobTitel: 'WERKNEMER',
      email: 'Bram.Declercq@example.com',
      passwordHash: password,
      telefoon: '0485987654',
      geboortedatum: '1993-09-03',
      land: 'België',
      postcode: '8500',
      stad: 'Kortrijk',
      straat: 'Groeningestraat',
      huisnummer: 21,
      bus: 1,
      status: 'ACTIEF',
    },
    {
      id: 16,
      firstName: 'Nathalie',
      lastName: 'Wouters',
      jobTitel: 'WERKNEMER',
      email: 'Nathalie.Wouters@example.com',
      passwordHash: password,
      telefoon: '0496543210',
      geboortedatum: '1999-01-17',
      land: 'België',
      postcode: '3000',
      stad: 'Leuven',
      straat: 'Brusselsestraat',
      huisnummer: 44,
      bus: null,
      status: 'ACTIEF',
    },
    {
      id: 17,
      firstName: 'Tibo',
      lastName: 'Janssen',
      jobTitel: 'WERKNEMER',
      email: 'Tibo.Janssen@example.com',
      passwordHash: password,
      telefoon: '0479321654',
      geboortedatum: '2001-03-28',
      land: 'België',
      postcode: '2000',
      stad: 'Antwerpen',
      straat: 'Mechelsesteenweg',
      huisnummer: 67,
      bus: null,
      status: 'ACTIEF',
    },
    {
      id: 18,
      firstName: 'Elien',
      lastName: 'Bogaert',
      jobTitel: 'WERKNEMER',
      email: 'Elien.Bogaert@example.com',
      passwordHash: password,
      telefoon: '0468765432',
      geboortedatum: '1994-11-09',
      land: 'België',
      postcode: '9200',
      stad: 'Dendermonde',
      straat: 'Oude Vest',
      huisnummer: 3,
      bus: 2,
      status: 'INACTIEF',
    },
    {
      id: 19,
      firstName: 'Hanne',
      lastName: 'Bogaert',
      jobTitel: 'ADMINISTRATOR',
      email: 'Hanne.Bogaert@example.com',
      passwordHash: password,
      telefoon: '0468765432',
      geboortedatum: '1994-11-09',
      land: 'België',
      postcode: '9200',
      stad: 'Dendermonde',
      straat: 'Oude Vest',
      huisnummer: 5,
      bus: 3,
      status: 'ACTIEF',
    },
  ]);

  console.log('✅ Werknemers seeded successfully\n');
}

async function seedTeams() {
  console.log('Seeding teams...');

  await db.insert(schema.teams).values([
    {
      id: 1,
      name: 'Team A',
      siteId: 1,
      supervisorId: 6,
      status: 'ACTIEF',
    },
    {
      id: 2,
      name: 'Team B',
      siteId: 1,
      supervisorId: 6,
      status: 'ACTIEF',
    },
    {
      id: 3,
      name: 'Team C',
      siteId: 2,
      supervisorId: 7,
      status: 'ACTIEF',
    },
    {
      id: 4,
      name: 'Team D',
      siteId: 2,
      supervisorId: 7,
      status: 'ACTIEF',
    },
    {
      id: 5,
      name: 'Team E',
      siteId: 2,
      supervisorId: 8,
      status: 'ACTIEF',
    },
  ]);
}

async function seedTeam_Werknemers() {
  console.log('Seeding teamWerknemers...');

  await db.insert(schema.team_werknemers).values([
    { teamId: 1, werknemerId: 1 },
    { teamId: 2, werknemerId: 1 },
    { teamId: 1, werknemerId: 2 },
    { teamId: 2, werknemerId: 2 },
    { teamId: 3, werknemerId: 3 },
    { teamId: 4, werknemerId: 3 },
    { teamId: 3, werknemerId: 4 },
    { teamId: 4, werknemerId: 4 },
    { teamId: 5, werknemerId: 9 },
    { teamId: 1, werknemerId: 10 },
    { teamId: 2, werknemerId: 14 },
    { teamId: 1, werknemerId: 16 },
    { teamId: 2, werknemerId: 17 },
    { teamId: 3, werknemerId: 15 },
    { teamId: 5, werknemerId: 15 },
    { teamId: 4, werknemerId: 18 },
    { teamId: 5, werknemerId: 18 },
  ]);

  console.log('✅ teamWerknemers seeded successfully\n');
}

async function seedSites() {
  console.log('Seeding sites...');

  await db.insert(schema.sites).values([
    {
      id: 1,
      name: 'Site Noord',
      locatie: 'Antwerpen',
      siteProductieStatus: 'GEZOND',
      operationeleStatus: 'ACTIEF',
      capaciteit: 100,
      land: 'België',
      breedtegraad: 51.13,
      lengtegraad: 4.24,
      isDeleted: false,
    },
    {
      id: 2,
      name: 'Site A',
      locatie: 'Aalst',
      siteProductieStatus: 'PROBLEMEN',
      operationeleStatus: 'ACTIEF',
      capaciteit: 80,
      land: 'België',
      breedtegraad: 50.938323,
      lengtegraad: 4.039215,
      isDeleted: false,
    },
    {
      id: 3,
      name: 'Site B',
      locatie: 'Beringen',
      siteProductieStatus: 'PROBLEMEN',
      operationeleStatus: 'INACTIEF',
      capaciteit: 60,
      land: 'België',
      breedtegraad: 51.04954,
      lengtegraad: 5.22606,
      isDeleted: false,
    },
    {
      id: 4,
      name: 'Site C',
      locatie: 'Gent',
      siteProductieStatus: 'OFFLINE',
      operationeleStatus: 'INACTIEF',
      capaciteit: 90,
      land: 'België',
      breedtegraad: 51.0543,
      lengtegraad: 3.7174,
      isDeleted: false,
    },
    {
      id: 5,
      name: 'Site D',
      locatie: 'Brussel',
      siteProductieStatus: 'GEZOND',
      operationeleStatus: 'ACTIEF',
      capaciteit: 50,
      land: 'België',
      breedtegraad: 50.8503,
      lengtegraad: 4.3517,
      isDeleted: false,
    },
    {
      id: 6,
      name: 'Site E',
      locatie: 'Eupen',
      siteProductieStatus: 'OFFLINE',
      operationeleStatus: 'INACTIEF',
      capaciteit: 50,
      land: 'België',
      breedtegraad: 50.63,
      lengtegraad: 6.03,
      isDeleted: false,
    },
    {
      id: 7,
      name: 'Site F',
      locatie: 'Leuven',
      siteProductieStatus: 'PROBLEMEN',
      operationeleStatus: 'ACTIEF',
      capaciteit: 50,
      land: 'België',
      breedtegraad: 50.8798,
      lengtegraad: 4.7005,
      isDeleted: false,
    },
    {
      id: 8,
      name: 'Site G',
      locatie: 'Mechelen',
      siteProductieStatus: 'PROBLEMEN',
      operationeleStatus: 'INACTIEF',
      capaciteit: 50,
      land: 'België',
      breedtegraad: 51.0257,
      lengtegraad: 4.4776,
      isDeleted: false,
    },
    {
      id: 9,
      name: 'Site NL1',
      locatie: 'Eindhoven',
      siteProductieStatus: 'GEZOND',
      operationeleStatus: 'ACTIEF',
      capaciteit: 50,
      land: 'Nederland',
      breedtegraad: 51.44,
      lengtegraad: 5.47,
      isDeleted: false,
    },
    {
      id: 10,
      name: 'Site NL2',
      locatie: 'Groningen',
      siteProductieStatus: 'PROBLEMEN',
      operationeleStatus: 'ACTIEF',
      capaciteit: 50,
      land: 'Nederland',
      breedtegraad: 53.22,
      lengtegraad: 6.56,
      isDeleted: false,
    },
    {
      id: 11,
      name: 'Site UK',
      locatie: 'Londen',
      siteProductieStatus: 'GEZOND',
      operationeleStatus: 'ACTIEF',
      capaciteit: 50,
      land: 'Verenigd Koninkrijk',
      breedtegraad: 51.51,
      lengtegraad: -0.13,
      isDeleted: false,
    },
    {
      id: 12,
      name: 'Site USA',
      locatie: 'New York',
      siteProductieStatus: 'PROBLEMEN',
      operationeleStatus: 'INACTIEF',
      capaciteit: 50,
      land: 'Verenigde Staten',
      breedtegraad: 40.7128,
      lengtegraad: -74.006,
      isDeleted: false,
    },
  ]);
}

async function seedMachines() {
  console.log('Seeding machines...');

  await db.insert(schema.machines).values([
    {
      id: 1,
      name: 'MC-PLT-001',
      status: 'DRAAIT',
      productieStatus: 'GEZOND',
      productinfo: 'Spijkers type A',
      upTime: 240,
      datumLaatsteOnderhoud: new Date('2026-03-02T09:00:00'),
      isDeleted: false,
    },
    {
      id: 2,
      name: 'MC-PLT-002',
      status: 'NOOD_AAN_ONDERHOUD',
      productieStatus: 'FALEND',
      productinfo: 'Bouten type B',
      upTime: 800,
      datumLaatsteOnderhoud: new Date('2026-02-18T14:30:00'),
      isDeleted: false,
    },
    {
      id: 3,
      name: 'MC-PLT-003',
      status: 'DRAAIT',
      productieStatus: 'GEZOND',
      productinfo: 'Moeren type C',
      upTime: 120,
      datumLaatsteOnderhoud: new Date('2026-03-10T08:15:00'),
      isDeleted: false,
    },
    {
      id: 4,
      name: 'MC-PLT-004',
      status: 'GESTOPT',
      productieStatus: 'OFFLINE',
      productinfo: 'Schroeven type D',
      upTime: 0,
      datumLaatsteOnderhoud: new Date('2026-03-20T11:45:00'),
      isDeleted: false,
    },
    {
      id: 5,
      name: 'MC-PLT-005',
      status: 'DRAAIT',
      productieStatus: 'GEZOND',
      productinfo: 'Spijkers type B',
      upTime: 360,
      datumLaatsteOnderhoud: new Date('2026-01-25T07:00:00'),
      isDeleted: false,
    },
    {
      id: 6,
      name: 'MC-PLT-006',
      status: 'NOOD_AAN_ONDERHOUD',
      productieStatus: 'GEZOND',
      productinfo: 'Bouten type A',
      upTime: 30,
      datumLaatsteOnderhoud: new Date('2026-03-15T16:20:00'),
      isDeleted: false,
    },
    {
      id: 7,
      name: 'MC-PLT-007',
      status: 'NOOD_AAN_ONDERHOUD',
      productieStatus: 'FALEND',
      productinfo: 'Moeren type A',
      upTime: 480,
      datumLaatsteOnderhoud: new Date('2026-02-10T10:00:00'),
      isDeleted: false,
    },
    {
      id: 8,
      name: 'MC-PLT-008',
      status: 'GESTOPT',
      productieStatus: 'PROBLEMEN',
      productinfo: 'Schroeven type C',
      upTime: 200,
      datumLaatsteOnderhoud: new Date('2026-03-05T13:30:00'),
      isDeleted: false,
    },
    {
      id: 9,
      name: 'MC-PLT-009',
      status: 'DRAAIT',
      productieStatus: 'GEZOND',
      productinfo: 'Spijkers type C',
      upTime: 150,
      datumLaatsteOnderhoud: new Date('2026-02-28T09:45:00'),
      isDeleted: false,
    },
    {
      id: 10,
      name: 'MC-PLT-010',
      status: 'NOOD_AAN_ONDERHOUD',
      productieStatus: 'FALEND',
      productinfo: 'Bouten type C',
      upTime: 600,
      datumLaatsteOnderhoud: new Date('2026-01-30T15:00:00'),
      isDeleted: false,
    },
    {
      id: 11,
      name: 'MC-PLT-011',
      status: 'DRAAIT',
      productieStatus: 'GEZOND',
      productinfo: 'Moeren type B',
      upTime: 90,
      datumLaatsteOnderhoud: new Date('2026-03-12T12:00:00'),
      isDeleted: false,
    },
  ]);
}

async function seedSiteMachines() {
  console.log('Seeding siteMachines...');

  await db.insert(schema.site_machines).values([
    {
      siteId: 1,
      machineId: 1,
      location: 'A005',
    },
    {
      siteId: 1,
      machineId: 2,
      location: 'A006',
    },
    {
      siteId: 2,
      machineId: 3,
      location: 'B101',
    },
    {
      siteId: 2,
      machineId: 4,
      location: 'B102',
    },
    {
      siteId: 3,
      machineId: 5,
      location: 'C201',
    },
    {
      siteId: 3,
      machineId: 6,
      location: 'C202',
    },
    {
      siteId: 1,
      machineId: 7,
      location: 'A005',
    },
    {
      siteId: 1,
      machineId: 8,
      location: 'A005',
    },
    {
      siteId: 1,
      machineId: 9,
      location: 'A007',
    },
    {
      siteId: 1,
      machineId: 10,
      location: 'A008',
    },
    {
      siteId: 1,
      machineId: 11,
      location: 'A009',
    },
  ]);
}

async function seedTaakTemplates() {
  console.log('Seeding taakTemplates...');

  await db.insert(schema.taakTemplates).values([
    {
      id: 1,
      type: 'ONDERHOUD',
      omschrijving: 'Dagelijks onderhoud',
      duurTijd: 60,
      status: 'ACTIEF',
    },
    {
      id: 2,
      type: 'HERSTELLING',
      omschrijving: 'Herstelling machine',
      duurTijd: 120,
      status: 'ACTIEF',
    },
    {
      id: 3,
      type: 'PRODUCTIE',
      omschrijving: 'Productie product x',
      duurTijd: 180,
      status: 'ACTIEF',
    },
  ]);
}

async function seedTaken() {
  console.log('Seeding taken...');

  await db.insert(schema.taken).values([
    // ── AFGEWERKT ──────────────────────────────────────────
    {
      id: 1,
      werknemerId: 1,
      templateId: 1,
      machineId: 1,
      datum: new Date('2026-05-27T08:00:00'),
      status: 'afgewerkt',
      tijdGespendeerd: 60,
      specificaties: 'Onderhoud uitvoeren',
    },
    {
      id: 15,
      werknemerId: 2,
      templateId: 1,
      machineId: 1,
      datum: new Date('2026-05-25T08:00:00'),
      status: 'afgewerkt',
      tijdGespendeerd: 45,
      specificaties: 'Preventief onderhoud',
    },
    {
      id: 16,
      werknemerId: 3,
      templateId: 2,
      machineId: 1,
      datum: new Date('2026-05-26T09:00:00'),
      status: 'afgewerkt',
      tijdGespendeerd: 90,
      specificaties: 'Herstelling lager',
    },
    {
      id: 17,
      werknemerId: 17,
      templateId: 3,
      machineId: 1,
      datum: new Date('2026-05-27T08:00:00'),
      status: 'afgewerkt',
      tijdGespendeerd: 30,
      specificaties: 'Smering onderdelen',
    },
    {
      id: 18,
      werknemerId: 4,
      templateId: 1,
      machineId: 1,
      datum: new Date('2026-05-25T11:00:00'),
      status: 'afgewerkt',
      tijdGespendeerd: 120,
      specificaties: 'Grote revisie assemblagelijn',
    },
    {
      id: 22,
      werknemerId: 1,
      templateId: 2,
      machineId: 1,
      datum: new Date('2026-05-25T10:00:00'),
      status: 'afgewerkt',
      tijdGespendeerd: 75,
      specificaties: 'Vervanging filters',
    },

    // ── BEZIG – allemaal op 27-05 ────────────────────────────────────────
    {
      id: 19,
      werknemerId: 10,
      templateId: 2,
      machineId: 1,
      datum: new Date('2026-05-27T09:30:00'),
      status: 'bezig',
      tijdGespendeerd: 0,
      specificaties: 'Inspectie koelsysteem',
    },
    {
      id: 20,
      werknemerId: 3,
      templateId: 3,
      machineId: 1,
      datum: new Date('2026-05-27T12:00:00'),
      status: 'bezig',
      tijdGespendeerd: 0,
      specificaties: 'Kalibratie sensoren',
    },
    {
      id: 21,
      werknemerId: 2,
      templateId: 1,
      machineId: 1,
      datum: new Date('2026-05-27T10:00:00'),
      status: 'bezig',
      tijdGespendeerd: 0,
      specificaties: 'Noodonderhoud assemblagelijn',
    },

    // ── GEPLAND – 28-05 ──────────────────────────────────────────────────
    //
    // Machine 1: id14 (08:00–09:00) → id10 (09:00–10:00) → id13 (11:30–12:30)
    // Machine 2: id5  (08:30–11:30) → id6  (13:00–16:00)   [werknemer 1]
    // Machine 3: id12 (08:00–11:00) → id9  (11:00–14:00) → id24 (14:00–15:00)
    // Machine 4: id8  (08:00–10:00)

    {
      id: 14,
      werknemerId: 9,
      templateId: 1, // ONDERHOUD – 60 min → eindigt 09:00
      machineId: 1,
      datum: new Date('2026-05-28T08:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Onderhoud uitvoeren',
    },
    {
      id: 10,
      werknemerId: 14,
      templateId: 1, // ONDERHOUD – 60 min → eindigt 10:00
      machineId: 1,
      datum: new Date('2026-05-28T09:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Dagelijks onderhoud machine MC-PLT-001',
    },
    {
      id: 5,
      werknemerId: 1,
      templateId: 3, // PRODUCTIE – 180 min → eindigt 11:30
      machineId: 2,
      datum: new Date('2026-05-28T08:30:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Productie op assemblagelijn.',
    },
    {
      id: 6,
      werknemerId: 1,
      templateId: 3, // PRODUCTIE – 180 min → eindigt 16:00
      machineId: 2,
      datum: new Date('2026-05-28T13:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Toekomstige taak herstelling.',
    },
    {
      id: 8,
      werknemerId: 2,
      templateId: 2, // HERSTELLING – 120 min → eindigt 10:00
      machineId: 4,
      datum: new Date('2026-05-28T08:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Herstelling machine MC-PLT-004',
    },
    {
      id: 12,
      werknemerId: 4,
      templateId: 3, // PRODUCTIE – 180 min → eindigt 11:00
      machineId: 3,
      datum: new Date('2026-05-28T08:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Productie van product X op MC-PLT-003',
    },
    {
      id: 9,
      werknemerId: 14,
      templateId: 3, // PRODUCTIE – 180 min → eindigt 14:00
      machineId: 3,
      datum: new Date('2026-05-28T11:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Productie van product X op machine MC-PLT-003',
    },
    {
      id: 24,
      werknemerId: 2,
      templateId: 1, // ONDERHOUD – 60 min → eindigt 15:00
      machineId: 3,
      datum: new Date('2026-05-28T14:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Maandelijks onderhoud',
    },

    // ── GEPLAND – 29-05 ──────────────────────────────────────────────────
    //
    // Machine 1: id2 (08:00–09:00) → id3 (09:00–10:00) → id23 (10:00–13:00)
    // Machine 2: id7 (08:00–09:00) → id4 (10:30–12:30) → id11 (13:30–15:30)

    {
      id: 2,
      werknemerId: 1,
      templateId: 1, // ONDERHOUD – 60 min → eindigt 09:00
      machineId: 1,
      datum: new Date('2026-05-29T08:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Onderhoud uit te voeren',
    },
    {
      id: 3,
      werknemerId: 1,
      templateId: 1, // ONDERHOUD – 60 min → eindigt 10:00
      machineId: 1,
      datum: new Date('2026-05-29T09:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Nog een onderhoudje om uit te voeren',
    },
    {
      id: 23,
      werknemerId: 4,
      templateId: 3, // PRODUCTIE – 180 min → eindigt 13:00
      machineId: 1,
      datum: new Date('2026-05-29T10:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Geplande inspectie',
    },
    {
      id: 7,
      werknemerId: 2,
      templateId: 1, // ONDERHOUD – 60 min → eindigt 09:00
      machineId: 2,
      datum: new Date('2026-05-29T08:00:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Dagelijks onderhoud machine MC-PLT-002',
    },
    {
      id: 4,
      werknemerId: 1,
      templateId: 2, // HERSTELLING – 120 min → eindigt 12:30
      machineId: 2,
      datum: new Date('2026-05-29T10:30:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Herstelling assemblagelijn. Momenteel veel problemen.',
    },
    {
      id: 11,
      werknemerId: 4,
      templateId: 2, // HERSTELLING – 120 min → eindigt 15:30
      machineId: 2,
      datum: new Date('2026-05-29T13:30:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Herstelling assemblagelijn MC-PLT-002',
    },
    // ── HERINPLANNING NODIG ──────────────────────────────────────────────────
    {
      id: 13,
      werknemerId: 1,
      templateId: 1, // ONDERHOUD – 60 min → eindigt 14:30
      machineId: 1,
      datum: new Date('2026-05-25T13:30:00'),
      status: 'gepland',
      tijdGespendeerd: 0,
      specificaties: 'Toekomstige onderhoudstaak machine MC-PLT-001',
    },
  ]);
}
