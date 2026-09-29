import { test, expect } from '@playwright/test';

test.describe('signed out', () => {
  test.use({ storageState: { cookies: [], origins: [] } });

  test('signs in with valid credentials and lands on the dashboard', async ({ page }) => {
    await page.goto('/login');

    await page.getByLabel('Email Address').fill('venueadmin2@example.com');
    await page.getByLabel('Password').fill('Password123!');
    await page.getByRole('button', { name: 'Sign In' }).click();

    await expect(page).toHaveURL('/dashboard');
    await expect(page.getByText('Welcome to BeeThere')).toBeVisible();
  });

  test('shows an error for a wrong password', async ({ page }) => {
    await page.goto('/login');

    await page.getByLabel('Email Address').fill('venueadmin2@example.com');
    await page.getByLabel('Password').fill('WrongPassword1!');
    await page.getByRole('button', { name: 'Sign In' }).click();

    await expect(page.getByText('Invalid email or password.')).toBeVisible();
    await expect(page).toHaveURL('/login');
  });

  test('shows required messages when signing in with an empty form', async ({ page }) => {
    await page.goto('/login');

    await page.getByRole('button', { name: 'Sign In' }).click();

    await expect(page.getByText('Email is required.')).toBeVisible();
    await expect(page.getByText('Password is required.')).toBeVisible();
  });

  test('redirects to the login page when opening the dashboard', async ({ page }) => {
    await page.goto('/dashboard');

    await expect(page).toHaveURL('/login');
  });
});

test.describe('signed in', () => {
  test('signs out and can no longer open the dashboard', async ({ page }) => {
    await page.goto('/dashboard');

    await page.getByRole('button', { name: 'Log out' }).click();
    await page.goto('/dashboard');

    await expect(page).toHaveURL('/login');
  });

  test('is sent to the dashboard when opening the login page', async ({ page }) => {
    await page.goto('/login');

    await expect(page).toHaveURL('/dashboard');
  });
});
