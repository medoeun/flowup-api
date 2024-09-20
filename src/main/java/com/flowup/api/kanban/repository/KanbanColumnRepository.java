package com.flowup.api.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowup.api.kanban.entity.KanbanColumn;

public interface KanbanColumnRepository extends JpaRepository<KanbanColumn, Long> {


}
