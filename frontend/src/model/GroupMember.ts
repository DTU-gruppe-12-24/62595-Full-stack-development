import type { Group } from "./Group";
import type { User } from "./User";

export type GroupMember = {
	id: {
		userId: string;
		groupId: string;
	};
	user: User;
	group: Group;
	role: "OWNER" | "ADMIN" | "MEMBER";
};