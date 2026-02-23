<template>
<div class="flex flex-col gap-2 justify-center items-center w-full">
	<AppCard hover v-for="group in groups" :flex="true" class="max-w-1/2">
		<div class="flex flex-row justify-between items-center w-full">
			<AppText variant="subtitle" tag="h3" class="max-w-fit">
				{{ group.name }}
			</AppText>
			<div class="flex flex-row justify-end gap-1 w-fit">
				<AppButton variant="secondary" @click="">
					Edit
				</AppButton>
				<AppButton variant="cancel" @click="() => deleteGroup(group)">
					Delete
				</AppButton>
			</div>
		</div>
	</AppCard>
	<AppButton variant="cancel" @click="() => createGroup('Test name')">
		Create
	</AppButton>
</div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import type { Group } from "@/model/Group"

import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import AppText from "@/components/AppText.vue"

import { apiFetch } from "@/utilities/apiFetch"

const groups = ref([] as Group[])

getGroups();
function getGroups() {
	apiFetch<Group[]>("/api/group/me", "GET")
		.then((result) => groups.value = result)
		.catch((error) => {
			console.error(error);
		});
}


function createGroup(name: string) {
	apiFetch<Group, Partial<Group>>("/api/group", "POST", { name })
		.then((result) => groups.value.push(result))
		.catch((error) => {
			console.error(error);
		});
}

function deleteGroup(group: Group) {
	apiFetch("/api/group/" + group.id, "DELETE")
		.then((_) => getGroups())
		.catch((error) => {
			console.error(error);
		});
}

</script>