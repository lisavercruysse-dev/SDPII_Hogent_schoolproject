const LOGIN_TOKEN = 'jwt-token';

const USERS_BY_EMAIL = {
  'Jonas.VanAert@example.com': {
    id: 1,
    firstName: 'Jonas',
    lastName: 'Van Aert',
    email: 'Jonas.VanAert@example.com',
    jobTitel: 'werknemer',
    plantId: 1,
  },
  'Pieter.DeBakker@example.com': {
    id: 3,
    firstName: 'Pieter',
    lastName: 'De Bakker',
    email: 'Pieter.DeBakker@example.com',
    jobTitel: 'verantwoordelijke',
    plantId: 1,
  },
};

const getUserByEmail = (email) =>
  USERS_BY_EMAIL[email] ?? {
    id: 999,
    firstName: 'Test',
    lastName: 'User',
    email,
    jobTitel: 'werknemer',
    plantId: 1,
  };

Cypress.Commands.add('login', (email, password) => {
  Cypress.log({
    displayName: 'login',
  });

  cy.intercept('POST', '**/api/sessions', {
    statusCode: 200,
    body: { token: LOGIN_TOKEN },
  }).as('login');

  cy.intercept('GET', '**/api/werknemers/me', getUserByEmail(email)).as(
    'currentUser',
  );

  cy.visit('http://localhost:5173/login');

  cy.get('[data-cy=email_input]').clear();
  cy.get('[data-cy=email_input]').type(email);

  cy.get('[data-cy=password_input]').clear();
  cy.get('[data-cy=password_input]').type(password);

  cy.get('[data-cy=submit_btn]').click();
  cy.wait('@login');
  cy.wait('@currentUser');
  cy.window().its('localStorage.jwtToken').should('eq', LOGIN_TOKEN);
});

Cypress.Commands.add('logout', () => {
  Cypress.log({
    displayName: 'logout',
  });

  cy.visit('http://localhost:5173');
  cy.get('[data-cy=logout_btn]').click();
});
