import { test, expect } from '@playwright/test';

test.use({ storageState: { cookies: [], origins: [] } });

test.beforeEach(async ({ page }) => {
  await page.goto('/register');
});

test('shows required messages when submitted empty', async ({ page }) => {
  await page.getByRole('button', { name: 'Create Account' }).click();

  await expect(page.getByText('Name is required.')).toBeVisible();
  await expect(page.getByText('Phone is required.')).toBeVisible();
  await expect(page.getByText('Email is required.')).toBeVisible();
  await expect(page.getByText('Password is required.')).toBeVisible();
  await expect(page.getByText('Please confirm your password.')).toBeVisible();
});

test('rejects an invalid email address', async ({ page }) => {
  await page.getByLabel('Email Address').fill('not-an-email');
  await page.getByLabel('Email Address').blur();

  await expect(page.getByText('Enter a valid email address.')).toBeVisible();
});

test('rejects a phone number with fewer than four digits', async ({ page }) => {
  await page.getByLabel('Phone').fill('123');
  await page.getByLabel('Phone').blur();

  await expect(page.getByText('Enter a valid phone number')).toBeVisible();
});

test('accepts a phone number with exactly four digits', async ({ page }) => {
  await page.getByLabel('Phone').fill('1234');
  await page.getByLabel('Phone').blur();

  await expect(page.getByText('Enter a valid phone number')).toBeHidden();
  await expect(page.getByText('Phone is required.')).toBeHidden();
});

test('rejects a confirmation that does not match the password', async ({ page }) => {
  await page.getByLabel('Password', { exact: true }).fill('Password123!');
  await page.getByLabel('Confirm Password').fill('Password124!');
  await page.getByLabel('Confirm Password').blur();

  await expect(page.getByText('Passwords do not match.')).toBeVisible();
});
