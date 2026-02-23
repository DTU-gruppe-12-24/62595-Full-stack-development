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
const groups = ref([] as Group[])

fetch("/api/group/me", {
	headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
})
	.then(async (response) => {
		if (response.status < 200 || response.status > 299) throw new Error(await response.text());
		return response.json();
	})
	.then((result) => groups.value = result as Group[])
	.catch((error) => {
		console.error(error);
	});

function createGroup(name: string) {
	fetch("/api/group", {
		method: "POST",
		headers: {
			'Accept': 'application/json',
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ name })
	})
		.then(async (response) => {
			if (response.status < 200 || response.status > 299) throw new Error(await response.text());
			return response.json();
		})
		.then((result) => {
			console.log(result)
			groups.value.push(result as Group)
		})
		.catch((error) => {
			console.error(error);
		});
}

</script>