package com.uadb.backend.matching.dto;

import jakarta.validation.constraints.NotNull;

public record DecisionRequest(@NotNull Boolean accepter) {}
