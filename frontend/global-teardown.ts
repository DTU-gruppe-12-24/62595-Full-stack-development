import * as fs from "fs";

const TEST_USER_EMAIL = 'playwright@test.dk';

async function globalTeardown() {
  console.log("Running global teardown…");

  try {
    const response = await fetch(
      `http://localhost:8080/api/test/user/email/${encodeURIComponent(TEST_USER_EMAIL)}`,
      { method: 'DELETE' }
    );

    if (response.ok) {
      console.log("Teardown: Test user deleted successfully");
    } else if (response.status === 403) {
      console.log("Teardown: User already deleted (expected in parallel runs)");
    } else {
      console.warn("Teardown: Unexpected backend status:", response.status);
    }
  } catch (err) {
    console.warn("Teardown: Could not reach backend to delete test user");
  }

  // Remove the correct storage state file
  try {
    fs.unlinkSync('./storageState.json');
    console.log("Teardown: storageState.json removed");
  } catch {
    console.log("Teardown: No storageState.json to remove");
  }
}

export default globalTeardown;