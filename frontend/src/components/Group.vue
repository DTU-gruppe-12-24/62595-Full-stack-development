<template>
Group: {{ group.name }}
</template>

<script setup lang="ts">
import type { Group } from "@/model/Group"

defineProps<{
	group: Group
}>()
</script>