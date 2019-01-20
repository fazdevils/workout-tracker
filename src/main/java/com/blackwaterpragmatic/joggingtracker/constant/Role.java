package com.blackwaterpragmatic.joggingtracker.constant;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"checkstyle:magicnumber"})
public enum Role {
	USER(0x00000001),
	USER_MANAGER(USER.bitwisePermission << 1),
	ADMIN(USER.bitwisePermission << 2);

	private int bitwisePermission;

	private Role(final int bitwisePermission) {
		this.bitwisePermission = bitwisePermission;
	}

	public int getBitwisePermission() {
		return bitwisePermission;
	}

	public static Set<Role> getRoles(final Integer bitwiseRole) {
		final Set<Role> roles = new HashSet<>();
		for (final Role role : Role.values()) {
			if ((bitwiseRole & role.bitwisePermission) != 0) {
				roles.add(role);
			}
		}
		return roles;
	}

	public static Integer getRoles(final Set<Role> roles) {
		Integer bitwiseRole = 0x00000000;

		for (final Role role : roles) {
			bitwiseRole = bitwiseRole | role.getBitwisePermission();
		}

		return bitwiseRole;
	}
}
