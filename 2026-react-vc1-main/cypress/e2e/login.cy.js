describe('Login page', () => {
  it('shows required field validation on empty submit', () => {
    cy.visit('http://localhost:5173/login');

    cy.get('[data-cy=email_input]').clear();
    cy.get('[data-cy=password_input]').clear();
    cy.get('[data-cy=submit_btn]').click();

    cy.contains('Email is required').should('be.visible');
    cy.contains('Password is required').should('be.visible');
  });

  it('logs in and follows redirect query parameter', () => {
    cy.intercept('POST', '**/api/sessions', {
      statusCode: 200,
      body: { token: 'jwt-token' },
    }).as('login');
    cy.intercept('GET', '**/api/werknemers/me', {
      statusCode: 200,
      body: {
        id: 1,
        firstName: 'Jonas',
        lastName: 'Van Aert',
        email: 'Jonas.VanAert@example.com',
        jobTitel: 'werknemer',
      },
    }).as('currentUser');

    cy.visit('http://localhost:5173/login?redirect=%2Fplanning');
    cy.get('[data-cy=email_input]').clear().type('Jonas.VanAert@example.com');
    cy.get('[data-cy=password_input]').clear().type('12345678');
    cy.get('[data-cy=submit_btn]').click();

    cy.wait('@login');
    cy.wait('@currentUser');
    cy.url().should('include', '/planning');
    cy.window().its('localStorage.jwtToken').should('eq', 'jwt-token');
  });

  it('shows login error on invalid credentials', () => {
    cy.intercept('POST', '**/api/sessions', {
      statusCode: 401,
      body: { message: 'Unauthorized' },
    }).as('loginFail');

    cy.visit('http://localhost:5173/login');
    cy.get('[data-cy=email_input]').clear().type('wrong@example.com');
    cy.get('[data-cy=password_input]').clear().type('wrong-password');
    cy.get('[data-cy=submit_btn]').click();

    cy.wait('@loginFail');
    cy.contains('Ongeldig wachtwoord of email adres.').should('be.visible');
    cy.url().should('include', '/login');
  });
});