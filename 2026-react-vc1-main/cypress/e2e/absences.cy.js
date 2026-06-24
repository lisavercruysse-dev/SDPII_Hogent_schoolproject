describe("Absences list", () => {
  const initialAbsences = {
    absences: [
      {
        id: 11,
        startDate: "2099-06-01",
        endDate: "2099-06-03",
        days: 3,
        type: "Vakantie",
        status: "In behandeling",
        canCancel: true,
      },
      {
        id: 12,
        startDate: "2099-07-10",
        endDate: "2099-07-10",
        days: 1,
        type: "Ziekte",
        status: "In behandeling",
        canCancel: true,
      },
    ],
    stats: {
      totaleZiektedagen: 1,
      totaleVakantiedagen: 3,
    },
  };

  beforeEach(() => {
    cy.login("Jonas.VanAert@example.com", "12345678");
  });

  it("should show the absences overview", () => {
    cy.intercept("GET", "**/afwezigheden/me", initialAbsences).as("getAbsences");

    cy.visit("http://localhost:5173/afwezigheden");
    cy.wait("@getAbsences");

    cy.contains("h1", "Beheer afwezigheden").should("be.visible");
    cy.get("[data-cy=stat-card]").should("have.length", 2);
    cy.get("[data-cy=absence-row]").should("have.length", 2);
    cy.get("[data-cy=absence-row]").first().should("contain", "Vakantie");
    cy.get("[data-cy=absence-row]").eq(1).should("contain", "Ziekte");
  });

  it("should show validation errors when submitting an empty form", () => {
    cy.intercept("GET", "**/afwezigheden/me", initialAbsences).as("getAbsences");

    cy.visit("http://localhost:5173/afwezigheden");
    cy.wait("@getAbsences");

    cy.contains("button", "Vakantie aanvragen").click();
    cy.contains("h2", "Vakantie aanvragen").should("be.visible");
    cy.contains("button", "Aanvragen").click();

    cy.contains("Start datum is verplicht.").should("be.visible");
    cy.contains("Eind datum is verplicht.").should("be.visible");
    cy.contains("Reden is verplicht.").should("be.visible");
  });

  it("should show an error when selecting dates in the past", () => {
    cy.intercept("GET", "**/afwezigheden/me", initialAbsences).as("getAbsences");

    cy.visit("http://localhost:5173/afwezigheden");
    cy.wait("@getAbsences");

    cy.contains("button", "Vakantie aanvragen").click();

    cy.get("input[type='date']").eq(0).type("2000-01-01");
    cy.get("input[type='date']").eq(1).type("2000-01-05");
    cy.get("textarea").type("Test");
    cy.contains("button", "Aanvragen").click();

    cy.contains("Datum mag niet in het verleden liggen.").should("be.visible");
  });

  it("should successfully request a new vacation", () => {
    const updatedAbsences = {
      absences: [
        {
          id: 13,
          startDate: "2099-08-01",
          endDate: "2099-08-05",
          days: 5,
          type: "Vakantie",
          status: "In behandeling",
          canCancel: true,
        },
        ...initialAbsences.absences,
      ],
      stats: {
        totaleZiektedagen: 1,
        totaleVakantiedagen: 8,
      },
    };

    let getCounter = 0;
    cy.intercept("GET", "**/afwezigheden/me", (req) => {
      getCounter += 1;
      req.reply(getCounter === 1 ? initialAbsences : updatedAbsences);
    }).as("getAbsences");

    cy.intercept("POST", "**/afwezigheden", { success: true }).as(
      "createAbsence",
    );

    cy.visit("http://localhost:5173/afwezigheden");
    cy.wait("@getAbsences");

    cy.contains("button", "Vakantie aanvragen").click();
    cy.get("input[type='date']").eq(0).type("2099-08-01");
    cy.get("input[type='date']").eq(1).type("2099-08-05");
    cy.get("textarea").type("Zomervakantie");
    cy.contains("button", "Aanvragen").click();

    cy.wait("@createAbsence");
    cy.wait("@getAbsences");
    cy.get("[data-cy=absence-row]").should("have.length", 3);
    cy.get("[data-cy=absence-row]").first().should("contain", "Vakantie");
    cy.get("[data-cy=absence-row]").first().should("contain", "In behandeling");
  });

  it("should cancel an absence request", () => {
    const updatedAbsences = {
      absences: [
        {
          ...initialAbsences.absences[0],
          status: "Geannuleerd",
          canCancel: false,
        },
        initialAbsences.absences[1],
      ],
      stats: initialAbsences.stats,
    };

    let getCounter = 0;
    cy.intercept("GET", "**/afwezigheden/me", (req) => {
      getCounter += 1;
      req.reply(getCounter === 1 ? initialAbsences : updatedAbsences);
    }).as("getAbsences");

    cy.intercept("PATCH", "**/afwezigheden/*/cancel", { success: true }).as(
      "cancelAbsence",
    );

    cy.visit("http://localhost:5173/afwezigheden");
    cy.wait("@getAbsences");

    cy.get("[data-cy=absence-row]")
      .first()
      .contains("button", "Annuleren")
      .scrollIntoView()
      .should("be.visible")
      .click();

    cy.contains("Afwezigheid annuleren").should("be.visible");
    cy.contains("button", "Ja, annuleren").click();

    cy.wait("@cancelAbsence");
    cy.wait("@getAbsences");
    cy.get("[data-cy=absence-row]").first().should("contain", "Geannuleerd");
    cy.get("[data-cy=absence-row]").first().should("not.contain", "Annuleren");
  });

  it("should show an error modal when trying to report sick while already sick", () => {
    cy.intercept("GET", "**/afwezigheden/me", initialAbsences).as("getAbsences");

    cy.visit("http://localhost:5173/afwezigheden");
    cy.wait("@getAbsences");

    cy.contains("button", "Ziekte melden").click();
    cy.contains("Ziekte melden gefaald").should("be.visible");
    cy.contains(
      "U bent al ziek gemeld. Het is daarom niet mogelijk om opnieuw ziek te melden.",
    ).should("be.visible");

    cy.contains("button", "OK").click();
    cy.contains("Ziekte melden gefaald").should("not.exist");
  });
});