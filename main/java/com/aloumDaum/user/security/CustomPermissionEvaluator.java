package com.aloumDaum.user.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(final Authentication auth, final Object targetDomainObject, final Object permission) {
        boolean hasPermission = false;
        if (auth != null && targetDomainObject != null && permission instanceof String) {
            hasPermission = hasPrivilege(auth, targetDomainObject.toString(), permission.toString());
        }

        return hasPermission;
    }

    @Override
    public boolean hasPermission(final Authentication auth, final Serializable targetId, final String targetType, final Object permission) {
        boolean hasPermission = false;
        if (auth != null && targetType != null && permission instanceof String) {
            hasPermission = hasPrivilege(auth, targetType, permission.toString());
        }
        return hasPermission;
    }

    private boolean hasPrivilege(final Authentication auth, final String targetType, final String permission) {
        boolean hasPrivilege = false;
        for (final GrantedAuthority grantedAuth : auth.getAuthorities()) {
            if (grantedAuth.getAuthority().startsWith(targetType) && grantedAuth.getAuthority().contains(permission)) {
                hasPrivilege = true;
                break;
            }
        }
        return hasPrivilege;
    }

}
