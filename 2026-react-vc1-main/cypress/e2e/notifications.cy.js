describe("Meldingen list", () => {
  const notifications = [
    {
      id: 101,
      werknemerId: 1,
      title: "Nieuwe taak toegewezen",
      description: "Er is een nieuwe taak aan u toegewezen.",
      time: "14:00 - 17:00 (3 uur)",
      type: "TAAK_TOEGEWEZEN",
      isRead: false,
    },
    {
      id: 102,
      werknemerId: 1,
      title: "Taak gewijzigd",
      description: "Een van uw toegewezen taken is gewijzigd.",
      time: "09:30",
      type: "TAAK_GEWIJZIGD",
      isRead: true,
    },
  ];

  beforeEach(() => {
    cy.login("Jonas.VanAert@example.com", "12345678");
  });

  it("should show the notifications overview", () => {
    cy.intercept("GET", "**/notificaties/werknemer/*", notifications).as(
      "getNotifications",
    );

    cy.visit("http://localhost:5173/meldingen");
    cy.wait("@getNotifications");

    cy.contains("h1", "Meldingen").should("be.visible");
    cy.contains("Meldingen (2)").should("be.visible");
    cy.get("[data-cy=notification-card]").should("have.length", 2);
    cy.get("[data-cy=notification-card]").first().should("contain", "Nieuwe taak toegewezen");
    cy.get("[data-cy=notification-card]").first().should("contain", "14:00 - 17:00 (3 uur)");
  });

  it("should mark a notification as read", () => {
    cy.intercept("GET", "**/notificaties/werknemer/*", notifications).as(
      "getNotifications",
    );
    cy.intercept("PATCH", "**/notificaties/*/read", { success: true }).as(
      "markAsRead",
    );

    cy.visit("http://localhost:5173/meldingen");
    cy.wait("@getNotifications");

    cy.contains("Je hebt 1 ongelezen melding").should("be.visible");
    cy.contains("button", "Markeren als gelezen").click();

    cy.wait("@markAsRead");
    cy.contains("Je hebt 0 ongelezen melding").should("not.exist");
    cy.contains("Je hebt 0 ongelezen meldingen").should("not.exist");
  });
});