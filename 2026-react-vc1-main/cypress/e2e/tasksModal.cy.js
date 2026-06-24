describe("Task details and completion modal", () => {
  const tasks = [
    {
      id: 301,
      werknemerId: 1,
      datum: "2099-01-12T09:00:00.000Z",
      status: "gepland",
      specificaties:
        "Start en controleer assemblagelijn AL-07 en voer een korte testrun uit.",
      taakTemplate: {
        id: 41,
        type: "Inspectie",
        omschrijving: "Kwaliteitscontrole lijn B",
        duurTijd: 60,
      },
    },
    {
      id: 302,
      werknemerId: 1,
      datum: "2099-01-13T10:30:00.000Z",
      status: "gepland",
      specificaties: "Controleer temperatuurafwijkingen en rapporteer afwijkingen.",
      taakTemplate: {
        id: 42,
        type: "Onderhoud",
        omschrijving: "Preventief onderhoud machine A1",
        duurTijd: 90,
      },
    },
  ];

  const taskDetails = {
    id: 301,
    werknemerId: 1,
    datum: "2099-01-12T09:00:00.000Z",
    status: "gepland",
    tijdGespendeerd: 0,
    specificaties:
      "Start en controleer assemblagelijn AL-07 en voer een korte testrun uit.",
    taakTemplate: {
      id: 41,
      type: "Inspectie",
      omschrijving: "Kwaliteitscontrole lijn B",
      duurTijd: 60,
    },
    machine: {
      machineId: 9,
      machineName: "MC-PLT-001",
      siteName: "Site Gent",
      locationOnSite: "Zone A1",
    },
  };

  beforeEach(() => {
    cy.login("Jonas.VanAert@example.com", "12345678");

    cy.intercept("GET", "**/werknemers/me/taken", { items: tasks }).as("getTasks");
    cy.intercept("GET", "**/taken/301/details", taskDetails).as("getTaskDetails");
    cy.intercept("PUT", "**/taken/*/status", { success: true }).as("updateStatus");

    cy.visit("http://localhost:5173/planning");
    cy.wait("@getTasks");
    cy.get("[data-cy=dateSelector]").clear().type("2099-01-12");
    cy.get("[data-cy=task]").should("have.length", 2);
  });

  it("shows details for the selected task", () => {
    cy.get("[data-cy=detail_button]").first().click();

    cy.wait("@getTaskDetails");
    cy.get("[data-cy=task_modal]").should("exist");
    cy.get("[data-cy=task_modal_title]").should("have.text", "Details taak");
    cy.get("[data-cy=taak_omschrijving]").should("have.text", "Kwaliteitscontrole lijn B");
    cy.get("[data-cy=taak_specificaties]").should(
      "have.text",
      "Start en controleer assemblagelijn AL-07 en voer een korte testrun uit.",
    );
    cy.get("[data-cy=taak_startdatum]").should("contain", "2099-01-12");
    cy.get("[data-cy=taak_starttijd]").should("contain", "09:00");
    cy.get("[data-cy=taak_type]").should("have.text", "Inspectie");
    cy.get("[data-cy=taak_duurtijd]").should("have.text", "60 minuten");
    cy.get("[data-cy=taak_machine]").should("have.text", "MC-PLT-001");
  });

  it("shows completion modal fields", () => {
    cy.get("[data-cy=complete_button]").first().click();

    cy.get("[data-cy=task_modal]").should("exist");
    cy.get("[data-cy=task_modal_title]").should("have.text", "Markeer taak als afgewerkt");
    cy.get("[data-cy=task_complete_input]").should("be.visible");
  });

  it("marks a task as completed", () => {
    cy.get("[data-cy=complete_button]").first().click();
    cy.get("[data-cy=task_complete_input]").type("50");
    cy.get("[data-cy=task_complete_bevestig_button]").click();

    cy.wait("@updateStatus");
    cy.get("[data-cy=complete_button]").first().should("have.text", "x");
  });

  it("allows canceling completion state", () => {
    cy.get("[data-cy=complete_button]").first().click();
    cy.get("[data-cy=task_complete_input]").type("50");
    cy.get("[data-cy=task_complete_bevestig_button]").click();
    cy.wait("@updateStatus");

    cy.get("[data-cy=complete_button]").first().click();
    cy.get("[data-cy=task_modal_title]").should("have.text", "Markeer als onafgewerkt");
    cy.get("[data-cy=task_markeer_onafgewerkt]").click();

    cy.wait("@updateStatus");
    cy.get("[data-cy=complete_button]").first().should("have.text", "");
  });

  it("keeps completion state when canceling undo", () => {
    cy.get("[data-cy=complete_button]").first().click();
    cy.get("[data-cy=task_complete_input]").type("50");
    cy.get("[data-cy=task_complete_bevestig_button]").click();
    cy.wait("@updateStatus");

    cy.get("[data-cy=complete_button]").first().click();
    cy.get("[data-cy=task_cancel_markeer_onafgewerkt]").click();

    cy.get("[data-cy=complete_button]").first().should("have.text", "x");
  });

  it("shows validation for spent time below minimum", () => {
    cy.get("[data-cy=complete_button]").first().click();
    cy.get("[data-cy=task_complete_input]").type("0");
    cy.get("[data-cy=task_complete_bevestig_button]").click();

    cy.contains("Gespendeerde tijd moet minstens 15 minuten zijn.").should("be.visible");
  });

  it("shows validation for spent time above maximum", () => {
    cy.get("[data-cy=complete_button]").first().click();
    cy.get("[data-cy=task_complete_input]").type("500");
    cy.get("[data-cy=task_complete_bevestig_button]").click();

    cy.contains("Gespendeerde tijd kan niet langer dan 480 minuten zijn.").should("be.visible");
  });
});