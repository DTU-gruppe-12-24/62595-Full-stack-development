<template>

  <div class="page">

    <h1>Calendar</h1>

    <AppCalendar @select="selectDate" />

    <p v-if="selectedDate">
      Selected: {{ selectedDate.toDateString() }}
    </p>

  </div>

</template>

<script setup lang="ts">

import { ref } from 'vue'
import AppCalendar from '@/components/AppCalendar.vue'

const selectedDate = ref(null as Date | null)

function selectDate(date: Date) {
  selectedDate.value = date
}

</script>

<style scoped>

.page {
  padding: 20px;
}

</style>
