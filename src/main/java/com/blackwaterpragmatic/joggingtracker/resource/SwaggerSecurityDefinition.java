package com.blackwaterpragmatic.joggingtracker.resource;

import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.ApiKeyAuthDefinition.ApiKeyLocation;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(
		securityDefinition = @SecurityDefinition(
				apiKeyAuthDefinitions = {
						@ApiKeyAuthDefinition(
								key = "JWT",
								name = "Authorization",
								in = ApiKeyLocation.HEADER)
				}))
public interface SwaggerSecurityDefinition {

}
