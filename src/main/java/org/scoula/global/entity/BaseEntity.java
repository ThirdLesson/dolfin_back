package org.scoula.global.entity;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public abstract class BaseEntity {
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
