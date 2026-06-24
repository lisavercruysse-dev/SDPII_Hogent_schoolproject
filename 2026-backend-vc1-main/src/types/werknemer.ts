import { werknemers } from '../drizzle/schema';

export type Werknemer = typeof werknemers.$inferInsert;
