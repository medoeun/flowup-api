package com.flowup.api.kanban.entity;

import java.util.Set;

import com.flowup.api.common.entity.BaseEntity;
import com.flowup.api.team.entity.Team;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class KanbanColumn extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	// InProgress, Review 등의 컬럼 이름
	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer order;  // 칸반 보드에서의 순서

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;  // 컬럼이 속한 팀

	@OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Ticket> tickets;  // 각 칸반 컬럼 내에 있는 티켓들
}
