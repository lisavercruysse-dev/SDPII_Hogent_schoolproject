import {
  Injectable,
  ForbiddenException,
  NotFoundException,
} from '@nestjs/common';
import { CreateAfwezigheidDto } from './AfwezigheidDTO';
import { afwezigheden, werknemers } from '../drizzle/schema';
import { eq, inArray, desc } from 'drizzle-orm';
import {
  type DatabaseProvider,
  InjectDrizzle,
} from '../drizzle/drizzle.provider';
import { NotificatieService } from '../notificaties/NotificatieService';

const MANAGER_JOB_TITELS = ['manager', 'supervisor', 'verantwoordelijke'];

@Injectable()
export class AfwezigheidService {
  constructor(
    @InjectDrizzle()
    private readonly db: DatabaseProvider,
    private readonly notificatieService: NotificatieService,
  ) {}

  async findAllByWerknemer(werknemerId: number) {
    return this.db.query.afwezigheden.findMany({
      where: eq(afwezigheden.werknemerId, werknemerId),
    });
  }

  async getMyAbsences(werknemerId: number) {
    const absences = await this.db.query.afwezigheden.findMany({
      where: eq(afwezigheden.werknemerId, werknemerId),
      orderBy: [desc(afwezigheden.id)],
    });

    const currentYear = new Date().getFullYear();
    const relevantAbsences = absences.filter(
      (a) =>
        new Date(a.startDate).getFullYear() === currentYear &&
        a.status !== 'Geannuleerd' &&
        a.status !== 'Geweigerd',
    );

    const totaleZiektedagen = relevantAbsences
      .filter((a) => a.type === 'Ziekte')
      .reduce((sum, a) => sum + a.days, 0);

    const totaleVakantiedagen = relevantAbsences
      .filter((a) => a.type === 'Vakantie')
      .reduce((sum, a) => sum + a.days, 0);

    return {
      absences,
      stats: { totaleZiektedagen, totaleVakantiedagen },
    };
  }

  async findAll(requesterId: number) {
    const requester = await this.db.query.werknemers.findFirst({
      where: eq(werknemers.id, requesterId),
    });

    if (
      !requester ||
      !MANAGER_JOB_TITELS.includes(requester.jobTitel.toLowerCase())
    ) {
      throw new ForbiddenException(
        'Alleen managers kunnen alle afwezigheden bekijken.',
      );
    }

    const results = await this.db
      .select({
        id: afwezigheden.id,
        startDate: afwezigheden.startDate,
        endDate: afwezigheden.endDate,
        days: afwezigheden.days,
        type: afwezigheden.type,
        reason: afwezigheden.reason,
        status: afwezigheden.status,
        canCancel: afwezigheden.canCancel,
        werknemerId: afwezigheden.werknemerId,
        firstName: werknemers.firstName,
        lastName: werknemers.lastName,
      })
      .from(afwezigheden)
      .leftJoin(werknemers, eq(afwezigheden.werknemerId, werknemers.id))
      .orderBy(desc(afwezigheden.id));

    return results.map((r) => ({
      id: r.id,
      startDate: r.startDate,
      endDate: r.endDate,
      days: r.days,
      type: r.type,
      reason: r.reason,
      status: r.status,
      canCancel: r.canCancel,
      werknemerId: r.werknemerId,
      werknemer: r.firstName ? `${r.firstName} ${r.lastName}` : 'Onbekend',
    }));
  }

  async create(dto: CreateAfwezigheidDto) {
    await this.db.insert(afwezigheden).values({
      werknemerId: dto.werknemerId,
      startDate: dto.startDate,
      endDate: dto.endDate,
      days: dto.days,
      type: dto.type,
      reason: dto.reason,
      status: 'In behandeling',
      canCancel: true,
    });

    const notificatieType =
      dto.type.toLowerCase().includes('vakantie') ||
      dto.type.toLowerCase().includes('verlof')
        ? 'VAKANTIEAANVRAAG'
        : 'ZIEKMELDING';

    const employee = await this.db.query.werknemers.findFirst({
      where: eq(werknemers.id, dto.werknemerId),
    });

    const employeeName = employee
      ? `${employee.firstName} ${employee.lastName}`
      : `werknemer ${dto.werknemerId}`;

    const title =
      notificatieType === 'VAKANTIEAANVRAAG'
        ? 'Nieuwe vakantieaanvraag'
        : 'Ziekmelding teamlid';

    const description =
      notificatieType === 'VAKANTIEAANVRAAG'
        ? `${employeeName} heeft een vakantieaanvraag ingediend van ${dto.startDate} tot ${dto.endDate}.`
        : `${employeeName} heeft zich ziek gemeld van ${dto.startDate} tot ${dto.endDate}.`;

    await this.notifyManagers(title, description, notificatieType);

    return { success: true };
  }

  async cancel(id: number, requesterId: number) {
    const existing = await this.db.query.afwezigheden.findFirst({
      where: eq(afwezigheden.id, id),
    });

    if (!existing) {
      throw new NotFoundException('Afwezigheid niet gevonden.');
    }

    if (existing.werknemerId !== requesterId) {
      throw new ForbiddenException(
        'U kunt alleen uw eigen afwezigheid annuleren.',
      );
    }

    await this.db
      .update(afwezigheden)
      .set({ status: 'Geannuleerd', canCancel: false })
      .where(eq(afwezigheden.id, id));

    const employee = await this.db.query.werknemers.findFirst({
      where: eq(werknemers.id, existing.werknemerId),
    });

    const employeeName = employee
      ? `${employee.firstName} ${employee.lastName}`
      : `werknemer ${existing.werknemerId}`;

    await this.notifyManagers(
      'Afwezigheid geannuleerd',
      `${employeeName} heeft een afwezigheid geannuleerd (${existing.startDate} tot ${existing.endDate}).`,
      'AFWEZIGHEID_GEANNULEERD',
    );

    return { success: true };
  }

  async approve(id: number, managerId: number) {
  const manager = await this.db.query.werknemers.findFirst({
    where: eq(werknemers.id, managerId),
  });


  if (!manager || !MANAGER_JOB_TITELS.includes(manager.jobTitel?.toLowerCase())) {
    throw new ForbiddenException(
      'Alleen managers kunnen afwezigheden goedkeuren.',
    );
  }

    const existing = await this.db.query.afwezigheden.findFirst({
      where: eq(afwezigheden.id, id),
    });

    if (!existing) {
      throw new NotFoundException('Afwezigheid niet gevonden.');
    }

    await this.db
      .update(afwezigheden)
      .set({ status: 'Goedgekeurd', canCancel: false })
      .where(eq(afwezigheden.id, id));

    await this.notificatieService.sendNotification(
      existing.werknemerId,
      'Afwezigheid goedgekeurd',
      `Uw aanvraag van ${existing.startDate} tot ${existing.endDate} is goedgekeurd.`,
      'VAKANTIEAANVRAAG',
    );

    return { success: true };
  }

  async reject(id: number, managerId: number) {
  const manager = await this.db.query.werknemers.findFirst({
    where: eq(werknemers.id, managerId),
  });

  if (!manager || !MANAGER_JOB_TITELS.includes(manager.jobTitel?.toLowerCase())) {
    throw new ForbiddenException(
      'Alleen managers kunnen afwezigheden weigeren.',
    );
  }

    const existing = await this.db.query.afwezigheden.findFirst({
      where: eq(afwezigheden.id, id),
    });

    if (!existing) {
      throw new NotFoundException('Afwezigheid niet gevonden.');
    }

    await this.db
      .update(afwezigheden)
      .set({ status: 'Geweigerd', canCancel: false })
      .where(eq(afwezigheden.id, id));

    await this.notificatieService.sendNotification(
      existing.werknemerId,
      'Afwezigheid geweigerd',
      `Uw aanvraag van ${existing.startDate} tot ${existing.endDate} is geweigerd.`,
      'VAKANTIEAANVRAAG',
    );

    return { success: true };
  }

  private async notifyManagers(
    title: string,
    description: string,
    type: 'ZIEKMELDING' | 'VAKANTIEAANVRAAG' | 'AFWEZIGHEID_GEANNULEERD',
  ) {
    const managers = await this.db.query.werknemers.findMany({
      where: inArray(werknemers.jobTitel, MANAGER_JOB_TITELS),
    });

    await Promise.all(
      managers.map((manager) =>
        this.notificatieService.sendNotification(
          manager.id,
          title,
          description,
          type,
        ),
      ),
    );
  }
}
