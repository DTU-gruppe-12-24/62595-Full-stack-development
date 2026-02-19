<script setup lang="ts">
import { ref } from "vue"

import AppCard from "../components/AppCard.vue"
import AppButton from "../components/AppButton.vue"
import AppInput from "../components/AppInput.vue"
import AppDialog from "@/components/AppDialog.vue"

const showDialog = ref<boolean>(false)

function handleSave() {
  // alert("Saved!")
  showDialog.value = false
}
</script>

<template>
  <div class="page">

    <h1>Component Preview</h1>

    <!-- Buttons -->
    <section>
      <h2>Buttons</h2>

      <div class="row">
        <AppButton variant="primary">
          Primary Button
        </AppButton>

        <AppButton variant="secondary">
          Secondary Button
        </AppButton>

        <AppButton variant="cancel">
          Cancel Button
        </AppButton>
      </div>
    </section>
    <!-- Dialog -->
    <section>
      <h2>Dialog</h2>
      <AppButton variant="primary" @click="showDialog = true">
        Open Dialog
      </AppButton>
    </section>


    <!-- Inputs -->
    <section>
      <h2>Inputs</h2>

      <AppInput placeholder="Recipe name..." />
    </section>

    <!-- Cards -->
    <section>
      <h2>Cards</h2>

      <AppCard hover>
        <h3>Spaghetti</h3>
        <p>Classic Italian recipe</p>

        <template #footer>
          <AppButton variant="primary">Add to plan</AppButton>
        </template>
      </AppCard>
    </section>

    <!-- Dialog -->
    <AppDialog
        v-model="showDialog"
        title="Example Dialog"
    >
      <p>This is a dialog preview.</p>

      <template #footer>
        <AppButton
            variant="secondary"
            @click="showDialog = false"
        >
          Cancel
        </AppButton>

        <AppButton variant="primary" @click="handleSave">
          Save
        </AppButton>
      </template>
    </AppDialog>

  </div>
</template>

<style scoped>

.page {
  padding: 40px;
}

.row {
  display: flex;
  gap: 10px;
}

section {
  margin-top: 30px;
}

</style>
