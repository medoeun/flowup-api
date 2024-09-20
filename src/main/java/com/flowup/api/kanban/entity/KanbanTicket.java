package com.flowup.api.kanban.entity;

import java.time.LocalDate;

import com.flowup.api.common.entity.BaseEntity;
import com.flowup.api.common.enums.Tag;
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
public class KanbanTicket extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column
	private String description;

	@Enumerated(EnumType.STRING)
	@Column
	private Tag tag;  // 티켓 태그 (Backend, Frontend 등 고정값)

	@Column
	private Double workload;  // 작업량 (Optional, 시간(H) 단위)

	@Column
	private LocalDate dueDate;  // 작업 기한 (일자만 설정)

	@Column(nullable = false)
	private Integer order;  // 티켓의 순서 (각 칸반 컬럼 내에서의 위치)

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "column_id", nullable = false)
	private KanbanColumn column;  // 티켓이 속한 칸반 컬럼

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assignee_id")
	private User assignee;  // 티켓 담당자 (Optional)
}