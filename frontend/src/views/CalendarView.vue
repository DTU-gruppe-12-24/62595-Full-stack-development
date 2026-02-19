<template>

  <div class="page">

    <h1>Calendar</h1>

    <AppCalendar @select="selectDate" />

    <p v-if="selectedDate">
      Selected: {{ selectedDate.toDateString() }}
    </p>

  </div>

</template>

<script setup>

import { ref } from 'vue'
import AppCalendar from '@/components/AppCalendar.vue'

const selectedDate = ref(null)

function selectDate(date) {
  selectedDate.value = date
}

</script>

<style scoped>

.page {
  padding: 20px;
}

</style>
