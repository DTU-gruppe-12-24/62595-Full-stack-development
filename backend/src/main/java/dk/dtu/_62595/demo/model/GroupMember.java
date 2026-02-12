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

	@MapsId("user_id")
	@ManyToOne
	@JoinColumn(name = "user_id")
	public User user;

	@MapsId("group_id")
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
		public UUID user_id;
		public UUID group_id;

		public GroupMemberId() {}

		public GroupMemberId(UUID user_id, UUID group_id) {
			this.user_id = user_id;
			this.group_id = group_id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			GroupMemberId that = (GroupMemberId) o;
			return Objects.equals(user_id, that.user_id) &&
					Objects.equals(group_id, that.group_id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(user_id, group_id);
		}
	}
}