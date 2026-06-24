import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateNotificatieDto, NotificatieType } from './NotificatieDTO';
import { notificaties } from 'src/drizzle/schema';
import { and, eq, sql } from 'drizzle-orm';
import {
  type DatabaseProvider,
  InjectDrizzle,
} from 'src/drizzle/drizzle.provider';

@Injectable()
export class NotificatieService {
  constructor(
    @InjectDrizzle()
    private readonly db: DatabaseProvider,
  ) {}

  async findAllByWerknemer(
    werknemerId: number,
    filter?: 'unread' | NotificatieType,
  ) {
    const conditions = [eq(notificaties.werknemerId, werknemerId)];

    if (filter === 'unread') {
      conditions.push(eq(notificaties.isRead, false));
    } else if (filter) {
      conditions.push(eq(notificaties.type, filter));
    }

    return this.db.query.notificaties.findMany({
      where: and(...conditions),
      orderBy: (n, { desc }) => [desc(n.id)],
    });
  }

  async findById(id: number) {
    const notificatie = await this.db.query.notificaties.findFirst({
      where: eq(notificaties.id, id),
    });

    if (!notificatie) {
      throw new NotFoundException('Notificatie niet gevonden.');
    }

    return notificatie;
  }

  async markAsRead(id: number) {
    const existing = await this.db.query.notificaties.findFirst({
      where: eq(notificaties.id, id),
    });

    if (!existing) {
      throw new NotFoundException('Notificatie niet gevonden.');
    }

    await this.db
      .update(notificaties)
      .set({ isRead: true })
      .where(eq(notificaties.id, id));

    return { success: true };
  }

  async getUnreadCount(werknemerId: number): Promise<{ count: number }> {
    const result = await this.db
      .select({ count: sql<number>`count(*)` })
      .from(notificaties)
      .where(
        and(
          eq(notificaties.werknemerId, werknemerId),
          eq(notificaties.isRead, false),
        ),
      );

    return { count: Number(result[0]?.count ?? 0) };
  }

  async create(dto: CreateNotificatieDto) {
    await this.db.insert(notificaties).values({
      ...dto,
      type: dto.type ?? 'ALGEMEEN',
      time: dto.time ?? new Date().toISOString(),
    });
    return { success: true };
  }

  async sendNotification(
    werknemerId: number,
    title: string,
    description: string,
    type: NotificatieType,
  ) {
    await this.db.insert(notificaties).values({
      werknemerId,
      title,
      description,
      time: new Date().toISOString(),
      type,
      isRead: false,
    });
  }
}
