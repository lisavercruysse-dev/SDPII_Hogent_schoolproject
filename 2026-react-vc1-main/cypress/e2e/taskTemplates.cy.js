describe("Show list of task templates", () => {
  const templates = [
    { id: 1, omschrijving: "Onderhoud machine", duurTijd: 60, type: "onderhoud" },
    { id: 2, omschrijving: "Machine inspectie", duurTijd: 45, type: "inspectie" },
    { id: 3, omschrijving: "Administratie", duurTijd: 30, type: "algemeen" },
  ];

  const team = {
    id: 10,
    name: "Team B",
    siteId: 1,
    werknemers: [{ id: 2, firstName: "Tom", lastName: "Claes" }],
  };

  const setupPlanningInterceptors = () => {
    cy.intercept("GET", "**/werknemers/me/takenSupervisor", { items: [] }).as(
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

  it("should show a list of all task templates", () => {
    cy.get("[data-cy=taskTemplateOmschrijvingTitel]").should("have.text", "omschrijving");
    cy.get("[data-cy=taskTemplateMinutenTitel]").should("have.text", "geschatte tijd");
    cy.get("[data-cy=taskTemplateTypeTitel]").should("have.text", "type");

    cy.get("[data-cy=taskTemplateOmschrijving]").should("have.length", 3);
    cy.get("[data-cy=taskTemplateOmschrijving]").first().should("have.text", "Onderhoud machine");
    cy.get("[data-cy=taskTemplateMinuten]").first().should("have.text", "60 min");
    cy.get("[data-cy=taskTemplateType]").first().should("have.text", "onderhoud");
  });

  it("should show a filtered list of task templates", () => {
    cy.get("[data-cy=taskTemplateOmschrijving]").should("have.length", 3);

    cy.get("[data-cy=taskTemplateSearch]").type("machine");
    cy.get("[data-cy=taskTemplateOmschrijving]").should("have.length", 2);
    cy.get("[data-cy=taskTemplateOmschrijving]").first().should("have.text", "Onderhoud machine");

    cy.get("[data-cy=taskTemplateSearch]").clear().type("hallo");
    cy.get("[data-cy=taskTemplateOmschrijving]").should("have.length", 0);
  });
});