package com.blackwaterpragmatic.workouttracker.resource;

import static com.blackwaterpragmatic.workouttracker.constant.MediaType.JSON;

import com.blackwaterpragmatic.workouttracker.bean.Weather;
import com.blackwaterpragmatic.workouttracker.helper.ResponseHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import java.util.Random;

import io.swagger.annotations.ApiOperation;

@Service
@Path("/weather")
public class MockWeatherResource {

	private final ResponseHelper responseHelper;
	private final Random random;

	@Autowired
	public MockWeatherResource(
			final ResponseHelper responseHelper) {
		this.responseHelper = responseHelper;
		random = new Random();
	}

	@PermitAll
	@GET
	@Produces(JSON)
	@ApiOperation(value = "Mock weather service API", hidden = true)
	public Response getWeather(
			@QueryParam("dateMs") final Long dateMs,
			@QueryParam("postalCode") final String postalCode) {

		final WeatherCondition[] weatherConditions = WeatherCondition.values();
		final WeatherCondition currentCondition = weatherConditions[random.nextInt(weatherConditions.length + 1)]; // +1 to simulate random failure
		final Weather weather = new Weather() {
			{
				setDateMs(dateMs);
				setPostalCode(postalCode);
				setWeather(currentCondition.toString());
			}
		};
		return responseHelper.build(Response.Status.OK, weather);
	}

	private enum WeatherCondition {
		SUN,
		RAIN,
		SNOW,
		WIND,
		CLOUDY,
		SHARKNADO
	}

}

