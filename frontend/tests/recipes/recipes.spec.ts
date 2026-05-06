import { expect, Page } from '@playwright/test';
import { test } from '../setup';

type RecipeIngredientRequest = {
  ingredientName: string;
  amount: number | null;
  unit: string | null;
};

type CreateRecipeRequest = {
  groupId: string | null;
  name: string;
  description: string;
  instructions: string;
  mealType: string | null;
  servings: number | null;
  prepTimeMinutes: number | null;
  imageUrl: string | null;
  lastMade: string | null;
  ingredients: RecipeIngredientRequest[];
};

async function createRecipe(
  request: Page["request"],
  overrides: Partial<CreateRecipeRequest> = {}
) {
    const authToken = (await request.storageState()).origins[0].localStorage.find(a => a.name == "auth_token")?.value;
    const payload: CreateRecipeRequest = {
        groupId: null,
        name: `Test Recipe ${Date.now()}`,
        description: 'A tasty test recipe.',
        instructions: 'Mix ingredients and cook.',
        mealType: 'Dinner',
        servings: 2,
        prepTimeMinutes: 25,
        imageUrl: null,
        lastMade: null,
        ingredients: [{ ingredientName: 'Carrot', amount: 2, unit: 'piece' }],
        ...overrides,
    };

    const response = await request.fetch('/api/recipes', { method: "POST", data: payload, headers: { "Authorization": `Bearer ${authToken}` } });
    if (response.status() < 200 || response.status() >= 300)
        throw Error("API Failed: " + await response.text());
    return await response.json();
}

async function deleteRecipe(
  request: Page["request"],
  name: string
) {
    const authToken = (await request.storageState()).origins[0].localStorage.find(a => a.name == "auth_token")?.value;
    const headers = { "Authorization": `Bearer ${authToken}`, "Accept": "application/json", "Content-Type": "application/json" };

    const getresponse = await request.fetch('/api/recipes', { method: "GET", headers });
    if (getresponse.status() < 200 || getresponse.status() >= 300)
        throw Error("API Failed: " + await getresponse.text());

    const recipes = await getresponse.json();
    const recipe = recipes.find((r: any) => r.name === name);
    if (!recipe) throw Error("Recipe not found: " + name);

    const response = await request.fetch('/api/recipes/' + recipe.id, { method: "DELETE", headers });
    if (response.status() < 200 || response.status() >= 300)
        throw Error("API Failed: " + await response.text());
}

test.describe('Recipes', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/');
        await page.getByRole('link', { name: 'Recipes', exact: true }).click();
        await page.waitForURL("/recipes");
        await page.waitForLoadState("networkidle");
    });

    test('shows recipe details in dialog', async ({ page }) => {

        const recipe = await createRecipe(page.request, {
            name: `Dialog Recipe ${Date.now()}`,
            description: 'Loaded from API.',
            instructions: 'Open the dialog.',
            mealType: 'Lunch',
        });

        await page.reload();
        await expect(page.getByRole('heading', { name: recipe.name })).toBeVisible();
        await page.getByRole('heading', { name: recipe.name }).click();

        await expect(page.getByRole('button', { name: 'Cancel' })).toBeVisible();
        await expect(page.getByText('Loaded from API.', { exact: true })).toBeVisible();
        await expect(page.getByText('Open the dialog.')).toBeVisible();

        await deleteRecipe(page.request, recipe.name);
    });

    test('can create a recipe', async ({ page }) => {
        const recipeName = `Playwright Recipe ${Date.now()}`;

        await page.getByRole('link', { name: 'Create Recipe' }).click();
        await expect(page.getByRole('heading', { name: 'Create Recipe' })).toBeVisible();

        await page.getByLabel('Name').fill(recipeName);
        await page.getByLabel('Description').fill('Created from UI.');
        await page.getByLabel('instructions').fill('Cook with care.');
        await page.getByLabel('Meal type').fill('Dinner');
        await page.getByLabel('Servings').fill('4');
        await page.getByLabel('Prep time (min)').fill('40');

        await page.getByRole('button', { name: 'Create Recipe' }).click();

        await page.waitForURL("/recipes");
        await expect(page.getByRole('heading', { name: 'Recipes' })).toBeVisible();
        await expect(page.getByText(recipeName)).toBeVisible();

        await deleteRecipe(page.request, recipeName)
    });

    test('can edit a recipe', async ({ page }) => {
        const recipe = await createRecipe(page.request, {
            name: `Editable Recipe ${Date.now()}`,
        });

        await page.reload();
        await expect(page.getByRole('heading', { name: recipe.name })).toBeVisible();
        await page.getByRole('heading', { name: recipe.name }).click();
        await expect(page.getByRole('button', { name: 'Cancel' })).toBeVisible();
        await page.getByRole('link', { name: 'Edit' }).click();

        await expect(page.getByRole('heading', { name: 'Edit Recipe' })).toBeVisible();

        const updatedName = `Updated Recipe ${Date.now()}`;
        await page.getByLabel('Name').fill(updatedName);
        await page.getByRole('button', { name: 'Save changes' }).click();
        await page.waitForLoadState("networkidle");

        await page.waitForURL("/recipes");
        await expect(page.getByRole('heading', { name: 'Recipes' })).toBeVisible();
        await expect(page.getByText(updatedName)).toBeVisible();
    });

    test('can delete a recipe', async ({ page }) => {
        const recipe = await createRecipe(page.request, {
            name: `Delete Recipe ${Date.now()}`,
        });

        await page.reload();
        await expect(page.getByRole('heading', { name: recipe.name })).toBeVisible();
        await page.getByRole('heading', { name: recipe.name }).click();
        await expect(page.getByRole('button', { name: 'Cancel' })).toBeVisible();
        await page.getByRole('button', { name: 'Delete' }).click();
        await page.getByRole('button', { name: 'Delete recipe' }).click();

        await page.reload()
        await expect(page.getByText(recipe.name)).not.toBeVisible();
    });
});
