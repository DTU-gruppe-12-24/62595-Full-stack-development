<script setup lang="ts">

import WeekCalendar from "@/components/AppCalendar.vue"
import AppContainer from "@/components/AppContainer.vue"
import AppSection from "@/components/AppSection.vue"
import AppText from "@/components/AppText.vue"

function handleCellClick(data: {day: Date, slot: string}) {

  console.log("Clicked cell:", data)

}

</script>

<template>
  <AppContainer class="page">
    <AppSection class="calendar-section">
      <AppText variant="title" tag="h1">Calendar</AppText>
      <WeekCalendar
          @cell-click="handleCellClick"
      />
    </AppSection>
  </AppContainer>

</template>

<style scoped>

.page {
  padding: 30px;
}

.calendar-section {
  margin-top: 0;
}

</style>
