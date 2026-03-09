import { test } from '../setup';
import { expect, Page } from '@playwright/test';

test.describe('Groups', () => {

    test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.getByRole('link', { name: 'Groups' }).click();
  });

  async function createGroup(page: Page, name: string) {
    await page.getByRole('button', { name: 'Create' }).click();
    await page.getByRole('textbox', { name: 'Group name...' }).fill(name);
    await page.locator('footer').getByRole('button', { name: 'Create' }).click();
    await expect(page.getByText(name)).toBeVisible();
  }

  async function deleteGroup(page: Page, name: string) {
    const row = page.getByText(name).locator('..');
    await row.getByRole('button', { name: 'Delete' }).click();
    await page.getByRole('button', { name: 'Yes' }).click();
    await expect(page.getByText(name)).not.toBeVisible();
  }



  test('can create a group', async ({ page }) => {
    const name = `MyFavGroup-${Date.now()}`;

    await createGroup(page, name);
  });


  test('can edit a group', async ({ page }) => {
    const originalName = `MyFavGroup-${Date.now()}`;
    const updatedName = `Foodies4Life-${Date.now()}`;

    await createGroup(page, originalName);

    const row = page.getByText(originalName).locator('..');
    await row.getByRole('button', { name: 'Edit' }).click();
    await page.getByRole('textbox', { name: 'Group name...' }).fill(updatedName);
    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.getByText(updatedName)).toBeVisible();

  });


  test('can delete a group', async ({ page }) => {
    const name = `MyFavGroup-${Date.now()}`;

    await createGroup(page, name);
    await deleteGroup(page, name);
  });


});