package com.flowup.api.user.entity;

import java.util.Set;

import com.flowup.api.team.entity.Team;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 20)
	private String role; // ADMIN, MEMBER

	// 중간 테이블 "user_teams"로 팀장, 팀원 역할 및 다중 팀 관리
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "user_teams",
		joinColumns = @JoinColumn(name="user_id"), // user_id로 join
		inverseJoinColumns = @JoinColumn(name="team_id"),
		uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "team_id"})
	)
	private Set<Team> team;
}
