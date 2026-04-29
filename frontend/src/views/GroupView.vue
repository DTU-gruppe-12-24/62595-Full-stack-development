<template>
<div class="flex flex-col gap-4 justify-center items-center w-full">
	<AppCard
		v-for="group in groups"
		flex hover
		class="w-full max-w-xl hover:cursor-pointer"
		@click="() => openEditDialog(group.group, group.role != 'MEMBER')"
	>
		<div class="flex flex-row justify-between items-center w-full">
			<AppText variant="subtitle" tag="h3" class="max-w-fit">
				{{ group.group.name }}
			</AppText>
			<div v-if="group.role != 'MEMBER'" class="flex flex-row justify-end gap-1 w-fit">
				<AppButton 
					variant="secondary"
					aria-label="Edit"
					@click="() => openEditDialog(group.group, true)"
					>
					<font-awesome-icon icon="fa-solid fa-pen-to-square" />
				</AppButton>
				<AppButton variant="danger" aria-label="Delete" v-if="group.role == 'OWNER'" @click="() => { showConfirmDeleteDialog = true; groupBeingDeleted = group.group; }" v-on:click.stop>
					<font-awesome-icon icon="fa-solid fa-trash" class="text-white" />
				</AppButton>
			</div>
			<div v-else class="flex flex-row justify-end gap-1 w-fit">
				<AppButton variant="ghost" @click="() => requestRemoveMember({group: group.group, user: myUser, role: 'MEMBER'})" v-on:click.stop>
					<font-awesome-icon icon="fa-solid fa-right-from-bracket" />
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
				Create group
			</AppButton>
		</template>
	</AppDialog>

	<AppDialog v-model="showEditDialog" :title="allowEdit ? 'Edit group' : groupBeingShown?.name">
		<div class="flex flex-row justify-between w-full">
			<AppInput
				v-model="editGroupName"
				placeholder="Group name..."
				@keyup.enter="submitEditGroup"
				class="w-full pr-4"
				v-if="allowEdit"
			/>
			<AppButton variant="secondary" @click="submitEditGroup" v-if="allowEdit">
				Save
			</AppButton>
		</div>

		<AppSection v-if="groupMembers[groupBeingShown!.id]" class="flex flex-col h-fit gap-4">
			<AppCard v-for="member in groupMembers[groupBeingShown!.id]" flex >
				<div class="flex flex-row justify-between items-center mx-4 w-full">
					<AppText>{{ member.user.name }} ({{ member.user.email }})</AppText>
					<div class="flex flex-row justify-end gap-1 w-fit">
						<AppDropdown
							:values="['ADMIN', 'MEMBER']"
							v-model="member.role"
							:disabled="member.role == 'OWNER' || !allowEdit"
							v-on:change="() => updateMemberRole(member)"
						/>
						<AppButton variant="ghost" @click="() => requestRemoveMember(member)" v-if="allowEdit && member.role != 'OWNER'">
							<font-awesome-icon icon="fa-solid fa-right-from-bracket" />
						</AppButton>
					</div>
				</div>
			</AppCard>
			<AppCard flex v-if="allowEdit">
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

	<AppConfirmDialog
		v-model="showConfirmDeleteDialog"
		:title="`Delete group '${groupBeingDeleted?.name ?? ''}'?`"
		message="Deleting a group cannot be undone."
		confirm-label="Delete group"
		confirm-variant="danger"
		@confirm="() => { deleteGroup(groupBeingDeleted!); showConfirmDeleteDialog = false; }"
	/>

	<AppConfirmDialog
		v-model="showConfirmMemberRemovalDialog"
		:title="memberRemovalTitle"
		:message="memberRemovalMessage"
		:confirm-label="memberRemovalConfirmLabel"
		confirm-variant="danger"
		@confirm="confirmRemoveMember"
	/>
</div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

import type { Group } from "@/model/Group"

import AppCard from "@/components/AppCard.vue"
import AppButton from "@/components/AppButton.vue"
import AppText from "@/components/AppText.vue"
import AppDialog from "@/components/AppDialog.vue"
import AppConfirmDialog from '@/components/AppConfirmDialog.vue'
import AppInput from "@/components/AppInput.vue"
import AppDropdown from "@/components/AppDropdown.vue"

import { apiFetch, getMyUser } from "@/utilities/apiFetch"
import type { GroupMember } from '@/model/GroupMember'
import AppSection from '@/components/AppSection.vue'
import type { User } from '@/model/User'
import { showError, showSuccess } from '@/utilities/notifications'

const myUser = getMyUser()!;

const groups = ref([] as {group: Group, role: GroupMember["role"]}[])
const showCreateDialog = ref(false)
const newGroupName = ref("")
const showEditDialog = ref(false)
const allowEdit = ref(false)
const editGroupName = ref("")
const groupBeingShown = ref<Group | null>(null)

const groupMembers = ref<Record<Group["id"], GroupMember[]>>({})
const inviteEmail = ref("")

const groupBeingDeleted = ref<Group | null>(null)
const showConfirmDeleteDialog = ref(false)
const showConfirmMemberRemovalDialog = ref(false)
const pendingRemovalMember = ref<Omit<GroupMember, "id"> | null>(null)

const memberRemovalTitle = computed(() => {
	if (!pendingRemovalMember.value) return 'Confirm action'
	if (pendingRemovalMember.value.user.id === myUser.id) return `Leave group '${pendingRemovalMember.value.group.name}'?`
	return `Remove '${pendingRemovalMember.value.user.name}' from group?`
})

const memberRemovalMessage = computed(() => {
	if (!pendingRemovalMember.value) return ''
	if (pendingRemovalMember.value.user.id === myUser.id) return 'You will lose access to this group until you are invited again.'
	return 'This member will lose access to the group and its shared planning data.'
})

const memberRemovalConfirmLabel = computed(() => {
	if (!pendingRemovalMember.value) return 'Confirm'
	return pendingRemovalMember.value.user.id === myUser.id ? 'Leave group' : 'Remove member'
})


getGroups();
function getGroups() {
	apiFetch<{group: Group, role: GroupMember["role"]}[]>("/api/group/me", "GET")
		.then((result) => groups.value = result)
		.catch((error) => {
			showError(error instanceof Error ? error.message : "" + error)
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

function openEditDialog(group: Group, edit: boolean) {
    groupBeingShown.value = group;
    apiFetch<GroupMember[]>(`/api/group/${group.id}/members`)
        .then((members) => groupMembers.value[group.id] = members)
        .catch((error) => { showError(error instanceof Error ? error.message : "" + error) });
	editGroupName.value = group.name;
	showEditDialog.value = true;
	allowEdit.value = edit
}

function submitEditGroup() {
	const name = editGroupName.value.trim();
	if (!name || !groupBeingShown.value) return;
	updateGroup(groupBeingShown.value, name);
}

function createGroup(name: string) {
	apiFetch<Group, Partial<Group>>("/api/group", "POST", { name })
		.then((result) => {
			groups.value.push({group: result, role: "OWNER"})
			showSuccess('Group created successfully.')
		})
		.catch((error) => { showError(error instanceof Error ? error.message : "" + error) });
}

function updateGroup(group: Group, name: string) {
	apiFetch<Group, Partial<Group>>("/api/group/" + group.id, "PUT", { name })
		.then(_ => {
			getGroups()
			showSuccess('Group updated successfully.')
		})
		.catch((error) => { showError(error instanceof Error ? error.message : "" + error) });
}

function deleteGroup(group: Group) {
	apiFetch("/api/group/" + group.id, "DELETE")
		.then(_ => {
			getGroups()
			showSuccess('Group deleted successfully.')
		})
		.catch((error) => { showError(error instanceof Error ? error.message : "" + error) });
}

function inviteMember() {
    const group = groupBeingShown.value!;
    apiFetch<boolean>(`/api/group/${group.id}/invite/${inviteEmail.value}`, "POST")
		.then((response) => {
			if (response) apiFetch<GroupMember[]>(`/api/group/${group.id}/members`)
                            .then((members) => {
                                groupMembers.value[group.id] = members
                                inviteEmail.value = ""
                                showSuccess('Member invited successfully.')
                            })
                            .catch((error) => { showError(error instanceof Error ? error.message : "" + error) });
			else showError("Failed to invite member");
		})
		.catch((error) => { showError(error instanceof Error ? error.message : "" + error) });
}

function updateMemberRole(member: Omit<GroupMember, "id">) {
	apiFetch<GroupMember[], [{user: User, role: GroupMember["role"]}]>(`/api/group/${member.group.id}/members`, "POST", [member])
		.then((response) => {
			groupMembers.value[member.group.id] = response
			showSuccess('Member role updated.')
		})
        .catch((error) => {
            showError(error instanceof Error ? error.message : "" + error);
            console.error(error);
            openEditDialog(member.group, true);
        });
}

function removeMember(member: Omit<GroupMember, "id">) {
	apiFetch(`/api/group/${member.group.id}/members/${member.user.id}`, "DELETE")
        .then((_) => {
            getGroups();
            if (member.user.id != myUser.id) openEditDialog(member.group, allowEdit.value);
            else showEditDialog.value = false;
			showSuccess(member.user.id != myUser.id ? 'Member removed from group.' : 'You left the group.')
        })
		.catch((error) => { showError(error instanceof Error ? error.message : "" + error) });
}

function requestRemoveMember(member: Omit<GroupMember, "id">) {
	pendingRemovalMember.value = member
	showConfirmMemberRemovalDialog.value = true
}

function confirmRemoveMember() {
	if (!pendingRemovalMember.value) return
	const member = pendingRemovalMember.value
	pendingRemovalMember.value = null
	showConfirmMemberRemovalDialog.value = false
	removeMember(member)
}

</script>
