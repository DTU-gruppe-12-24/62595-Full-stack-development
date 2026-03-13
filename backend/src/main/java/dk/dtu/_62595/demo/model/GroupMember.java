package dk.dtu._62595.demo.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "group_members")
public class GroupMember {

	@EmbeddedId
	private GroupMemberId id;

	@MapsId("userId")
	@ManyToOne
	@JoinColumn(name = "user_id", columnDefinition = "CHAR(36)")
	private User user;

	@MapsId("groupId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", columnDefinition = "CHAR(36)")
	private Group group;

	@Enumerated(EnumType.STRING)
	private Role role;

    public GroupMember() {}
	public GroupMember(User user, Group group, Role role) {
		this.id = new GroupMemberId(user.getId(), group.getId());
		this.user = user;
		this.group = group;
		this.role = role;
	}

    public GroupMemberId getId() {
        return id;
    }
	public User getUser() { return user; }
	public Group getGroup() { return group; }
	public Role getRole() { return role; }

	@Embeddable
	public static class GroupMemberId implements Serializable {

		@Column(name = "user_id", columnDefinition = "CHAR(36)")
		private UUID userId;

		@Column(name = "group_id", columnDefinition = "CHAR(36)")
		private UUID groupId;

		public GroupMemberId() {}

		public GroupMemberId(UUID userId, UUID groupId) {
			this.userId = userId;
			this.groupId = groupId;
		}

		public UUID getUserId() { return userId; }
		public UUID getGroupId() { return groupId; }

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			GroupMemberId that = (GroupMemberId) o;
			return Objects.equals(userId, that.userId) &&
					Objects.equals(groupId, that.groupId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(userId, groupId);
		}
	}

	public enum Role {
		OWNER,
		ADMIN,
		MEMBER
	}
}