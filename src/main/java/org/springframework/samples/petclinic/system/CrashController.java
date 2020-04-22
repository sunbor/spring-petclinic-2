/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.system;

import java.net.ConnectException;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;

/**
 * Controller used to showcase what happens when an exception is thrown
 *
 * @author Michael Isvy
 * <p/>
 * Also see how a view that resolves to "error" has been added ("error.html").
 */
@Controller
class CrashController {

	Logger logger = Logger.getLogger(CrashController.class);

	
	@Autowired
	CircuitBreaker cb;
	
	@GetMapping("/oups")
	public String exceptionGateway() throws Exception {
		Callable<String> callable = null;
		callable = () -> triggerException();
		
		Callable<String> decoratedCallable = Decorators.ofCallable(callable)
				.withCircuitBreaker(cb)
				.decorate();
		
		String result;
		try {
			result = decoratedCallable.call();
		} catch (CallNotPermittedException e) {
			logger.error("circuit breaker is open");
			throw e;
		} catch (ConnectException e) {
			logger.error("CBException: connection failed, from inside try catch");
			throw e;
		} catch (RuntimeException e) {
			logger.error("CBException: shows built in exception");
			throw e;
		}
		catch (Exception e) {
			logger.error("CBException: some other exception occurred");
			e.printStackTrace();
			throw e;
		}
		return result;
	}
	
	public String triggerException() {
		throw new RuntimeException(
				"Expected: controller used to showcase what " + "happens when an exception is thrown");
	}

}
