package com.uadb.backend.identity.dto;

import com.uadb.backend.identity.enums.Role;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record ModifierRolesRequest(

        @NotEmpty(message = "Au moins un rôle doit être fourni.")
        Set<Role> roles

) {}
