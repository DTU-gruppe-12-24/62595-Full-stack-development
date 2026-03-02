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
				<AppButton variant="cancel" @click="() => { showConfirmDeleteDialog = true; groupBeingDeleted = group; }">
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
		<div class="flex flex-row justify-between w-full">
			<AppInput
				v-model="editGroupName"
				placeholder="Group name..."
				@keyup.enter="submitEditGroup"
				class="w-full pr-4"
			/>
			<AppButton variant="secondary" @click="submitEditGroup">
				Save
			</AppButton>
		</div>

		<AppSection v-if="groupMembers[groupBeingEdited!.id]">
			<AppCard v-for="member in groupMembers[groupBeingEdited!.id]" flex >
				<div class="flex flex-row justify-between items-center mx-4 w-full">
					<AppText>{{ member.user.name }} ({{ member.user.email }})</AppText>
					<div class="flex flex-row justify-end gap-1 w-fit">
						<AppDropdown
							:values="['ADMIN', 'MEMBER']"
							v-model="member.role"
							:disabled="member.role == 'OWNER'"
							v-on:change="() => updateMemberRole(member)"
						/>
						<AppButton variant="ghost" @click="() => removeMember(member)">
							<font-awesome-icon icon="fa-solid fa-right-from-bracket" />
						</AppButton>
					</div>
				</div>
			</AppCard>
			<AppCard flex >
				<div class="flex flex-row justify-between items-center mx-4 w-full">
					<AppInput
						v-model="inviteEmail"
						placeholder="Email..."
						@keyup.enter="inviteMember"
						class="w-full pr-4"
					/>
					<AppButton variant="secondary" @click="inviteMember">
						Invite
					</AppButton>
				</div>
			</AppCard>
		</AppSection>
	</AppDialog>

	<AppDialog v-model="showConfirmDeleteDialog" :title="'Are you sure you want to delete \'' + groupBeingDeleted?.name + '\'?'">
		<template #footer>
			<AppButton variant="cancel" @click="() => showConfirmDeleteDialog = false">
				Cancel
			</AppButton>
			<AppButton variant="secondary" @click="() => { deleteGroup(groupBeingDeleted!); showConfirmDeleteDialog = false; }">
				Yes
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
import AppDropdown from "@/components/AppDropdown.vue"

import { apiFetch } from "@/utilities/apiFetch"
import type { GroupMember } from '@/model/GroupMember'
import AppSection from '@/components/AppSection.vue'
import type { User } from '@/model/User'

const groups = ref([] as Group[])
const showCreateDialog = ref(false)
const newGroupName = ref("")
const showEditDialog = ref(false)
const editGroupName = ref("")
const groupBeingEdited = ref<Group | null>(null)

const groupMembers = ref<Record<Group["id"], GroupMember[]>>({})
const inviteEmail = ref("")

const groupBeingDeleted = ref<Group | null>(null)
const showConfirmDeleteDialog = ref(false)


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
    apiFetch<GroupMember[]>(`/api/group/${group.id}/members`)
        .then((members) => groupMembers.value[group.id] = members)
        .catch((error) => { console.error(error) });
	editGroupName.value = group.name;
	showEditDialog.value = true;
}

function submitEditGroup() {
	const name = editGroupName.value.trim();
	if (!name || !groupBeingEdited.value) return;
	updateGroup(groupBeingEdited.value, name);
}

function createGroup(name: string) {
	apiFetch<Group, Partial<Group>>("/api/group", "POST", { name })
		.then((result) => groups.value.push(result))
		.catch((error) => { console.error(error) });
}

function updateGroup(group: Group, name: string) {
	apiFetch<Group, Partial<Group>>("/api/group/" + group.id, "PUT", { name })
		.then((_) => getGroups())
		.catch((error) => { console.error(error) });
}

function deleteGroup(group: Group) {
	apiFetch("/api/group/" + group.id, "DELETE")
		.then((_) => getGroups())
		.catch((error) => { console.error(error); });
}

function inviteMember() {
    const group = groupBeingEdited.value!;
    apiFetch<boolean>(`/api/group/${group.id}/invite/${inviteEmail.value}`, "POST")
		.then((response) => {
		    if (response) apiFetch<GroupMember[]>(`/api/group/${group.id}/members`)
                            .then((members) => {
                                groupMembers.value[group.id] = members
                                inviteEmail.value = ""
                            })
                            .catch((error) => { console.error(error) });
			else console.log("Failed to invite member");
		})
		.catch((error) => { console.error(error); });
}

function updateMemberRole(member: GroupMember) {
    apiFetch<GroupMember[], [{user: User, role: GroupMember["role"]}]>(`/api/group/${member.group.id}/members`, "POST", [member])
		.then((response) => {
		    groupMembers.value[member.group.id] = response
		})
		.catch((error) => { console.error(error); });
}

function removeMember(member: GroupMember) {
    apiFetch(`/api/group/${member.group.id}/members/${member.user.id}`, "DELETE")
		.then((_) => openEditDialog(member.group))
		.catch((error) => { console.error(error); });
}

</script>