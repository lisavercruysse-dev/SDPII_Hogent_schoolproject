describe("Edit task", () => {
  const templates = [
    { id: 1, omschrijving: "Onderhoud machine", duurTijd: 60, type: "onderhoud" },
  ];

  const team = {
    id: 10,
    name: "Team B",
    siteId: 1,
    werknemers: [
      { id: 2, firstName: "Tom", lastName: "Claes" },
      { id: 3, firstName: "Jonas", lastName: "Van Aert" },
    ],
  };

  const tasks = [
    {
      id: 200,
      werknemerId: 2,
      datum: "2099-01-15T10:30:00.000Z",
      status: "gepland",
      specificaties:
        "Diagnosticeer sensor E17, vervang defecte componenten en voer kalibratie uit om correcte werking te garanderen.",
      taakTemplate: {
        id: 21,
        type: "Inspectie",
        omschrijving: "Sensorcontrole",
        duurTijd: 90,
      },
    },
    {
      id: 201,
      werknemerId: 3,
      datum: "2024-01-10T09:00:00.000Z",
      status: "gepland",
      specificaties:
        "Start en controleer assemblagelijn AL-07 en voer een korte testrun uit.",
      taakTemplate: {
        id: 22,
        type: "Inspectie",
        omschrijving: "Kwaliteitscontrole lijn B",
        duurTijd: 60,
      },
    },
  ];

  const setupPlanningInterceptors = () => {
    cy.intercept("GET", "**/werknemers/me/takenSupervisor", { items: tasks }).as(
      "getTasks",
    );
    cy.intercept("GET", "**/sites", { items: [{ id: 1, name: "Site Gent" }] }).as(
      "getSites",
    );
    cy.intercept("GET", "**/teams/mine", { items: [team] }).as("getTeams");
    cy.intercept("GET", "**/taakTemplates", { items: templates }).as(
      "getTemplates",
    );
  };

  beforeEach(() => {
    cy.login("Pieter.DeBakker@example.com", "12345678");
    setupPlanningInterceptors();
    cy.visit("http://localhost:5173/planning");
    cy.wait(["@getTasks", "@getSites", "@getTeams", "@getTemplates"]);
  });

  it("should show a modal to edit a task", () => {
    cy.get("[data-cy=teamSelector]").select("Team B");
    cy.get("[data-cy=dateSelector]").clear().type("2099-01-15");

    cy.get("[data-cy=editTaskButton]")
      .first()
      .scrollIntoView()
      .should("be.visible")
      .click({ force: true });

    cy.get("[data-cy=taakWijzigenTitel]").should("have.text", "Taak Wijzigen");
    cy.get("[data-cy=taakBewerkenWerknemer] option:selected").should("have.text", "Tom Claes");
    cy.get("[data-cy=taakBewerkenDatum]").should("have.value", "2099-01-15");
    cy.get("[data-cy=taakBewerkenStarttijd]").should("have.value", "10:30");
    cy.get("[data-cy=taakBewerkenEindtijd]").should("have.value", "12:00");
    cy.get("[data-cy=taakBewerkenSpecificaties]").should(
      "have.value",
      "Diagnosticeer sensor E17, vervang defecte componenten en voer kalibratie uit om correcte werking te garanderen.",
    );
    cy.get("[data-cy=taakBewerkenSubmit]").should("have.text", "Bewerken");
  });

  it("should show modal to assign a task template", () => {
    cy.get("[data-cy=taskTemplateToewijzen]")
      .first()
      .scrollIntoView()
      .should("be.visible")
      .click();

    cy.get("[data-cy=taakWijzigenTitel]").should("have.text", "Taak Toewijzen");
    cy.get("[data-cy=taakBewerkenDatum]").should("have.value", "");
    cy.get("[data-cy=taakBewerkenStarttijd]").should("have.value", "");
    cy.get("[data-cy=taakBewerkenEindtijd]").should("have.value", "");
    cy.get("[data-cy=taakBewerkenSpecificaties]").should("have.value", "");
    cy.get("[data-cy=taakBewerkenSubmit]").should("have.text", "Toewijzen");
  });

  it("should show a modal to reassign an uncompleted task", () => {
    cy.get("[data-cy=unCompletedTaskToewijzen]")
      .first()
      .scrollIntoView()
      .should("be.visible")
      .click();

    cy.get("[data-cy=taakWijzigenTitel]").should("have.text", "Taak Wijzigen");
    cy.get("[data-cy=taakBewerkenWerknemer] option:selected").should("have.text", "Jonas Van Aert");
    cy.get("[data-cy=taakBewerkenDatum]").should("have.value", "2024-01-10");
    cy.get("[data-cy=taakBewerkenStarttijd]").should("have.value", "09:00");
    cy.get("[data-cy=taakBewerkenEindtijd]").should("have.value", "10:00");
    cy.get("[data-cy=taakBewerkenSpecificaties]").should(
      "have.value",
      "Start en controleer assemblagelijn AL-07 en voer een korte testrun uit.",
    );
    cy.get("[data-cy=taakBewerkenSubmit]").should("have.text", "Bewerken");
  });
});