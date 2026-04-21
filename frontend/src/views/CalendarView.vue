<script setup lang="ts">

import WeekCalendar from "@/components/AppCalendar.vue"

function handleCellClick(data: {day: Date, slot: string}) {

  console.log("Clicked cell:", data)

}

</script>

<template>

  <div class="page">

    <WeekCalendar
        @cell-click="handleCellClick"
    />

  </div>

</template>

<style scoped>

.page {
  padding: 30px;
}

</style>