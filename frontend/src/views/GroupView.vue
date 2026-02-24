<template>
<div class="flex flex-col gap-2 justify-center items-center w-full">
	<AppCard hover v-for="group in groups" :flex="true" class="max-w-1/2">
		<div class="flex flex-row justify-between items-center w-full">
			<AppText variant="subtitle" tag="h3" class="max-w-fit">
				{{ group.name }}
			</AppText>
			<div class="flex flex-row justify-end gap-1 w-fit">
				<AppButton variant="secondary" @click="() => openEditDialog(group)">
					Edit
				</AppButton>
				<AppButton variant="cancel" @click="() => deleteGroup(group)">
					Delete
				</AppButton>
			</div>
		</div>
	</AppCard>
	<AppButton variant="primary" @click="() => showCreateDialog = true">
		Create
	</AppButton>
	<AppDialog v-model="showCreateDialog" title="Create group">
		<AppInput
			v-model="newGroupName"
			placeholder="Group name..."
			@keyup.enter="submitCreateGroup"
		/>
		<template #footer>
			<AppButton variant="cancel" @click="() => showCreateDialog = false">
				Cancel
			</AppButton>
			<AppButton variant="secondary" @click="submitCreateGroup">
				Create
			</AppButton>
		</template>
	</AppDialog>

	<AppDialog v-model="showEditDialog" title="Edit group">
		<AppInput
			v-model="editGroupName"
			placeholder="Group name..."
			@keyup.enter="submitEditGroup"
		/>
		<template #footer>
			<AppButton variant="cancel" @click="closeEditDialog">
				Cancel
			</AppButton>
			<AppButton variant="secondary" @click="submitEditGroup">
				Save
			</AppButton>
		</template>
	</AppDialog>
</div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import type { Group } from "@/model/Group"

import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import AppText from "@/components/AppText.vue"
import AppDialog from "@/components/AppDialog.vue"
import AppInput from "@/components/AppInput.vue"

import { apiFetch } from "@/utilities/apiFetch"

const groups = ref([] as Group[])
const showCreateDialog = ref(false)
const newGroupName = ref("")
const showEditDialog = ref(false)
const editGroupName = ref("")
const groupBeingEdited = ref<Group | null>(null)

getGroups();
function getGroups() {
	apiFetch<Group[]>("/api/group/me", "GET")
		.then((result) => groups.value = result)
		.catch((error) => {
			console.error(error);
		});
}

function submitCreateGroup() {
	const name = newGroupName.value.trim();
	if (!name) return;
	createGroup(name);
	showCreateDialog.value = false;
	newGroupName.value = "";
}

function openEditDialog(group: Group) {
	groupBeingEdited.value = group;
	editGroupName.value = group.name;
	showEditDialog.value = true;
}

function closeEditDialog() {
	showEditDialog.value = false;
	groupBeingEdited.value = null;
	editGroupName.value = "";
}

function submitEditGroup() {
	const name = editGroupName.value.trim();
	if (!name || !groupBeingEdited.value) return;
	updateGroup(groupBeingEdited.value, name);
	closeEditDialog();
}

function createGroup(name: string) {
	apiFetch<Group, Partial<Group>>("/api/group", "POST", { name })
		.then((result) => groups.value.push(result))
		.catch((error) => {
			console.error(error);
		});
}

function updateGroup(group: Group, name: string) {
	apiFetch<Group, Partial<Group>>("/api/group/" + group.id, "PUT", { name })
		.then((_) => getGroups())
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