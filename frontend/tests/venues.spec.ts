import { test, expect, type APIRequestContext } from '@playwright/test';
import { randomUUID } from 'crypto';

let headers: Record<string, string>;
let createdIds: number[];

function uniqueName(label: string) {
  return `E2E ${label} ${randomUUID().slice(0, 8)}`;
}

async function createVenue(request: APIRequestContext, name: string) {
  const response = await request.post('/api/venues', {
    headers,
    data: { name, description: 'Created by an end-to-end test', address: 'Test Street 1' },
  });
  expect(response.ok()).toBeTruthy();
  const venue = await response.json();
  createdIds.push(venue.id);
  return venue;
}

test.beforeEach(async ({ request }) => {
  const response = await request.post('/api/auth/login', {
    data: { email: 'venueadmin2@example.com', password: 'Password123!' },
  });
  const { token } = await response.json();
  headers = { Authorization: `Bearer ${token}` };
  createdIds = [];
});

test.afterEach(async ({ request }) => {
  for (const id of createdIds) {
    await request.delete(`/api/venues/${id}`, { headers });
  }
});

test('creates a venue and opens its detail page', async ({ page }) => {
  const name = uniqueName('created');
  await page.goto('/venues/new');

  await page.getByLabel('Venue name').fill(name);
  await page.getByLabel('Description').fill('A venue created through the form');
  await page.getByLabel('Address').fill('Test Street 2');
  await page.getByRole('button', { name: 'Save' }).click();

  await expect(page).toHaveURL(/\/venues\/\d+$/);
  createdIds.push(Number(page.url().split('/').pop()));
  await expect(page.getByRole('heading', { name })).toBeVisible();
});

test('renames a venue', async ({ page, request }) => {
  const venue = await createVenue(request, uniqueName('original'));
  const newName = uniqueName('renamed');
  await page.goto(`/venues/${venue.id}/edit`);

  await page.getByLabel('Venue name').fill(newName);
  await page.getByRole('button', { name: 'Save' }).click();

  await expect(page).toHaveURL(`/venues/${venue.id}`);
  await expect(page.getByRole('heading', { name: newName })).toBeVisible();
});

test('finds a venue by searching its name', async ({ page, request }) => {
  const venue = await createVenue(request, uniqueName('searchable'));
  await page.goto('/venues');

  await page.getByLabel('Search venues').fill(venue.name);

  await expect(page.locator('app-venue-card')).toHaveCount(1);
  await expect(page.locator('app-venue-card')).toContainText(venue.name);
});

test('deletes a venue from the overview', async ({ page, request }) => {
  const venue = await createVenue(request, uniqueName('deleted'));
  await page.goto('/venues');
  await page.getByLabel('Search venues').fill(venue.name);
  const card = page.locator('app-venue-card').filter({ hasText: venue.name });

  await card.getByRole('button', { name: 'Delete' }).click();
  await page.getByRole('dialog').getByRole('button', { name: 'Delete' }).click();

  await expect(card).toBeHidden();
  await expect(page.getByText(`No venue matches "${venue.name}".`)).toBeVisible();
});
