package dk.dtu._62595.demo.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import java.util.Objects;

@Entity
@Table(name = "Group_Members")
public class GroupMember {
	@EmbeddedId
	public final GroupMemberId id;

	@MapsId("userId")
	@ManyToOne
	@JoinColumn(name = "user_id")
	public User user;

	@MapsId("groupId")
	@ManyToOne
	@JoinColumn(name = "group_id")
	public Group group;

	public String role;

	public GroupMember(GroupMemberId id) {
		this.id = id;
	}

	public GroupMember(User user, Group group, String role) {
		this.id = new GroupMemberId(user.id, group.id);
		this.user = user;
		this.group = group;
		this.role = role;
	}

	@Embeddable
	public static class GroupMemberId implements Serializable {
		public UUID userId;
		public UUID groupId;

		public GroupMemberId() {}

		public GroupMemberId(UUID userId, UUID groupId) {
			this.userId = userId;
			this.groupId = groupId;
		}

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
}