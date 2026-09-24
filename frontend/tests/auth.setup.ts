import { test as setup, expect } from '@playwright/test';
import path from 'path';

const authFile = path.join(__dirname, '../playwright/.auth/user.json');

setup('authenticate', async ({ page }) => {
  await page.goto('/login');
  await page.getByLabel('Email Address').fill('venueadmin2@example.com');
  await page.getByLabel('Password').fill('Password123!');
  await page.getByRole('checkbox').check();
  await page.getByRole('button', { name: 'Sign In' }).click();

  await page.waitForURL('/dashboard');
  await expect(page.getByText('Welcome to BeeThere')).toBeVisible();

  await page.context().storageState({ path: authFile });
});
