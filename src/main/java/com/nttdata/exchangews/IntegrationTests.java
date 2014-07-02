package com.nttdata.exchangews;

public class IntegrationTests {

	public static void main(String[] args) {

		com.sun.security.auth.module.NTSystem NTSystem = new com.sun.security.auth.module.NTSystem();
		System.out.println(NTSystem.getName());
		System.out.println(NTSystem.getImpersonationToken());

	}

}
