package com.flowup.api.team.entity;

import com.flowup.api.common.entity.BaseEntity;
import com.flowup.api.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invitations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Invitation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User invitedUser;  // 초대받은 사용자

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inviter_user_id", nullable = false)
	private User inviter;  // 초대한 팀장

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InvitationStatus status;  // 초대 상태 (예: PENDING, ACCEPTED, REJECTED)

	// Enum 따로 빼서 관리
	public enum InvitationStatus {
		PENDING, ACCEPTED, REJECTED
	}
}