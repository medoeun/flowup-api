package com.flowup.api.kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnCreateDTO {
	@NotBlank(message = "제목은 필수입니다.")
	private String name;

	@NotNull(message = "팀 ID는 필수입니다.")
	private Long teamId;
}
