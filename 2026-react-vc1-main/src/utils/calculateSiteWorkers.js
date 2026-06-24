// src/utils/calculateSiteWorkers.js
export function calculateSiteWorkers(siteId, userData, mockAfwezigheden) {
  // Alle werknemers van deze site
  const siteWorkers = userData.filter(
    (u) => u.plantId === siteId && u.jobTitel === "werknemer"
  );

  const workerCount = siteWorkers.length;
  const siteWorkerIds = new Set(siteWorkers.map((u) => u.id));

  const today = new Date();

  // Eén keer alle relevante afwezigheden ophalen
  const relevantAbsences = mockAfwezigheden.filter((afwezigheid) => {
    const start = new Date(afwezigheid.startDate);
    const end = new Date(afwezigheid.endDate);

    return (
      siteWorkerIds.has(afwezigheid.werknemerId) &&
      start <= today &&
      end >= today &&
      (afwezigheid.status === "Goedgekeurd" || afwezigheid.status === "In behandeling")
    );
  });

  // Unieke tellingen
  const absentWorkerIds = new Set(relevantAbsences.map((a) => a.werknemerId));

  const ziekteWorkerIds = new Set(
    relevantAbsences
      .filter((a) => a.type === "Ziekte")
      .map((a) => a.werknemerId)
  );

  const vakantieWorkerIds = new Set(
    relevantAbsences
      .filter((a) => a.type === "Vakantie")
      .map((a) => a.werknemerId)
  );

  const afwezigheden = absentWorkerIds.size;
  const ziekteAfwezigheden = ziekteWorkerIds.size;
  const vakantieAfwezigheden = vakantieWorkerIds.size;

  const availableWorkers = workerCount - afwezigheden;

  return {
    workerCount,
    afwezigheden,
    ziekteAfwezigheden,
    vakantieAfwezigheden,
    availableWorkers,
  };
}