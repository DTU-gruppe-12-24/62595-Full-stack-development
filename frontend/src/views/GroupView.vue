<template>
<div>
<div v-for="group in groups">
	<GroupComponent :group="group" />
</div>
<button @click="() => createGroup('Test name')">Create</button>
</div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Group } from "@/model/Group"
import GroupComponent from "@/components/Group.vue"
import { apiFetch } from "@/utilities/apiFetch"
const groups = ref([] as Group[])

apiFetch<Group[]>("/api/group/me", "GET")
	.then((result) => groups.value = result)
	.catch((error) => {
		console.error(error);
	});

function createGroup(name: string) {
	apiFetch<Group, Partial<Group>>("/api/group", "POST", { name })
		.then((result) => groups.value.push(result))
		.catch((error) => {
			console.error(error);
		});
}

</script>