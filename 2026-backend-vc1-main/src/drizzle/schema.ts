import { relations } from 'drizzle-orm';
import {
  primaryKey,
  datetime,
  varchar,
  mysqlTable,
  int,
  date,
  boolean,
  decimal,
} from 'drizzle-orm/mysql-core';

//java en web

export const sites = mysqlTable('sites', {
  id: int('id', { unsigned: true }).primaryKey().autoincrement(),
  name: varchar('name', { length: 255 }).notNull(),
  capaciteit: int('capaciteit', { unsigned: true }),
  operationeleStatus: varchar('operationeleStatus', { length: 255 }),
  siteProductieStatus: varchar('siteProductieStatus', { length: 255 }),
  locatie: varchar('locatie', { length: 255 }),
  land: varchar('land', { length: 255 }),
  breedtegraad: decimal('breedtegraad', {
    precision: 10,
    scale: 8,
    mode: 'number',
  }).notNull(),
  lengtegraad: decimal('lengtegraad', {
    precision: 11,
    scale: 8,
    mode: 'number',
  }).notNull(),
  isDeleted: boolean('is_deleted').default(false).notNull(),
});

export const werknemers = mysqlTable('werknemers', {
  id: int('id', { unsigned: true }).primaryKey().autoincrement(),
  firstName: varchar('firstName', { length: 255 }).notNull(),
  lastName: varchar('lastName', { length: 255 }).notNull(),
  jobTitel: varchar('jobTitel', { length: 255 }).notNull(),
  passwordHash: varchar('password_hash', { length: 255 }).notNull(),
  email: varchar('email', { length: 255 }).notNull(),
  telefoon: varchar('telefoon', { length: 255 }),
  geboortedatum: date('geboortedatum', { mode: 'string' }).notNull(),
  land: varchar('land', { length: 255 }).notNull(),
  postcode: varchar('postcode', { length: 255 }).notNull(),
  stad: varchar('stad', { length: 255 }).notNull(),
  straat: varchar('straat', { length: 255 }).notNull(),
  huisnummer: int('huisnummer', { unsigned: true }),
  bus: int('bus'),
  status: varchar('status', { length: 255 }).notNull(),
});

export const teams = mysqlTable('teams', {
  id: int('ID', { unsigned: true }).primaryKey().autoincrement(),
  name: varchar('NAAM', { length: 255 }).notNull(),
  siteId: int('SITE_ID', { unsigned: true })
    .references(() => sites.id, { onDelete: 'cascade' })
    .notNull(),
  supervisorId: int('verantwoordelijkeId', { unsigned: true })
    .references(() => werknemers.id)
    .notNull(),
  status: varchar('status', { length: 255 }).notNull(),
});

export const team_werknemers = mysqlTable(
  'team_werknemers',
  {
    teamId: int('teamId', { unsigned: true })
      .references(() => teams.id)
      .notNull(),
    werknemerId: int('werknemerId', { unsigned: true })
      .references(() => werknemers.id)
      .notNull(),
  },
  (table) => [primaryKey({ columns: [table.teamId, table.werknemerId] })],
);

export const teamWerknemersRelations = relations(
  team_werknemers,
  ({ one }) => ({
    team: one(teams, {
      fields: [team_werknemers.teamId],
      references: [teams.id],
    }),
    werknemer: one(werknemers, {
      fields: [team_werknemers.werknemerId],
      references: [werknemers.id],
    }),
  }),
);

export const sitesRelations = relations(sites, ({ many }) => ({
  teams: many(teams),
}));

export const teamsRelations = relations(teams, ({ one, many }) => ({
  site: one(sites, {
    fields: [teams.siteId],
    references: [sites.id],
  }),
  supervisor: one(werknemers, {
    fields: [teams.supervisorId],
    references: [werknemers.id],
  }),
  teamWerknemers: many(team_werknemers),
}));

//enkel web

export const notificaties = mysqlTable('notificaties', {
  id: int('id', { unsigned: true }).primaryKey().autoincrement(),
  title: varchar('title', { length: 255 }).notNull(),
  description: varchar('description', { length: 500 }).notNull(),
  type: varchar('type', { length: 50 }).notNull(),
  time: varchar('time', { length: 255 }).notNull(),
  isRead: boolean('is_read').default(false).notNull(),
  werknemerId: int('werknemer_id', { unsigned: true })
    .notNull()
    .references(() => werknemers.id),
});

export const afwezigheden = mysqlTable('afwezigheden', {
  id: int('id', { unsigned: true }).primaryKey().autoincrement(),
  startDate: date('start_date', { mode: 'string' }).notNull(),
  endDate: date('end_date', { mode: 'string' }).notNull(),
  days: int('days').notNull(),
  type: varchar('type', { length: 50 }).notNull(),
  reason: varchar('reason', { length: 500 }).notNull(),
  status: varchar('status', { length: 50 }).default('In behandeling'),
  canCancel: boolean('can_cancel').default(true).notNull(),
  werknemerId: int('werknemer_id', { unsigned: true }).notNull(),
});

export const taken = mysqlTable('taken', {
  id: int('id', { unsigned: true }).primaryKey().autoincrement(),
  werknemerId: int('werknemer_id', { unsigned: true })
    .notNull()
    .references(() => werknemers.id, { onDelete: 'cascade' }),
  templateId: int('template_id', { unsigned: true })
    .notNull()
    .references(() => taakTemplates.id),
  machineId: int('machine_id', { unsigned: true })
    .notNull()
    .references(() => machines.id),
  datum: datetime('date').notNull(),
  status: varchar('status', { length: 255 }).notNull(),
  tijdGespendeerd: int('tijdGespendeerd', { unsigned: true }),
  specificaties: varchar('specificaties', { length: 500 }).notNull(),
});

export const taakTemplates = mysqlTable('taakTemplates', {
  id: int('id', { unsigned: true }).primaryKey().autoincrement(),
  type: varchar('type', { length: 255 }).notNull(),
  omschrijving: varchar('omschrijving', { length: 255 }).notNull(),
  duurTijd: int('duurTijd', { unsigned: true }).notNull(),
  status: varchar('status', { length: 255 }).notNull(),
});

export const machines = mysqlTable('machines', {
  id: int('id', { unsigned: true }).primaryKey().autoincrement(),
  name: varchar('name', { length: 255 }).notNull(),
  status: varchar('status', { length: 255 }).notNull(),
  productieStatus: varchar('productieStatus', { length: 255 })
    .notNull()
    .default('GEZOND'),
  productinfo: varchar('productinfo', { length: 500 }),
  upTime: int('upTime', { unsigned: true }).notNull(),
  datumLaatsteOnderhoud: date('datumLaatsteOnderhoud', { mode: 'date' }),
  isDeleted: boolean('is_deleted').default(false).notNull(),
});

export const afwezigheidRelations = relations(afwezigheden, ({ one }) => ({
  werknemer: one(werknemers, {
    fields: [afwezigheden.werknemerId],
    references: [werknemers.id],
  }),
}));

export const site_machines = mysqlTable(
  'site_machines',
  {
    siteId: int('siteId', { unsigned: true })
      .references(() => sites.id, { onDelete: 'cascade' })
      .notNull(),
    machineId: int('machineId', { unsigned: true })
      .references(() => machines.id, { onDelete: 'cascade' })
      .notNull(),
    location: varchar('location', { length: 255 })
      .default('Geen locatie')
      .notNull(),
  },
  (table) => [primaryKey({ columns: [table.machineId, table.siteId] })],
);

export const werknemer_machines = mysqlTable(
  'werknemer_Machines',
  {
    werknemerId: int('werknemerId', { unsigned: true })
      .references(() => werknemers.id, { onDelete: 'cascade' })
      .notNull(),

    machineId: int('machineId', { unsigned: true })
      .references(() => machines.id, { onDelete: 'cascade' })
      .notNull(),
  },
  (table) => [primaryKey({ columns: [table.werknemerId, table.machineId] })],
);

export const takenRelations = relations(taken, ({ one }) => ({
  werknemer: one(werknemers, {
    fields: [taken.werknemerId],
    references: [werknemers.id],
  }),
  taakTemplate: one(taakTemplates, {
    fields: [taken.templateId],
    references: [taakTemplates.id],
  }),
  machine: one(machines, {
    fields: [taken.machineId],
    references: [machines.id],
  }),
}));

export const werknemer_machinesRelations = relations(
  werknemer_machines,
  ({ one }) => ({
    werknemer: one(werknemers, {
      fields: [werknemer_machines.werknemerId],
      references: [werknemers.id],
    }),
    machine: one(machines, {
      fields: [werknemer_machines.machineId],
      references: [machines.id],
    }),
  }),
);

export const site_machinesRelations = relations(site_machines, ({ one }) => ({
  site: one(sites, {
    fields: [site_machines.siteId],
    references: [sites.id],
  }),
}));
