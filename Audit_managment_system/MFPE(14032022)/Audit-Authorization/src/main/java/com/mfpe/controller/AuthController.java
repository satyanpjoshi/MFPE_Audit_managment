package com.mfpe.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mfpe.model.AuthenticationRequest;
import com.mfpe.model.AuthenticationResponse;
import com.mfpe.model.ProjectManagerDetails;
import com.mfpe.service.JwtService;
import com.mfpe.service.ProjectManagerDetailsService;

@RestController
@RequestMapping("/auth")	
@CrossOrigin(origins = "*")
public class AuthController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private ProjectManagerDetailsService projectManagerDetailsService;
	
	@Autowired
	private JwtService jwtService;
	
	Logger logger = LoggerFactory.getLogger("Auth-Controller-Logger");
	// authentication - for the very first login
	@PostMapping("/authenticate")
	public ResponseEntity<String> generateJwt(@Valid @RequestBody AuthenticationRequest request){
		ResponseEntity<String> response = null;
		
		// authenticating the User-Credentials
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			// else when it authenticates successfully
			final ProjectManagerDetails projectManagerDetails = projectManagerDetailsService
																	.loadUserByUsername(request.getUsername());
			
			final String jwt = jwtService.generateToken(projectManagerDetails);	// returning the token as response
			
			//test
			logger.info("Authenticated User :: " + projectManagerDetails);
			
			response = new ResponseEntity<String>(jwt, HttpStatus.OK);
			
			logger.info("Successfully Authenticated user!");
			
		}catch (Exception e) {
			logger.error(e.getMessage() + "!! info about request-body : " + request);
			response = new ResponseEntity<String>("Not Authorized Project Manager", HttpStatus.FORBIDDEN);
		}
		logger.info("-------- Exiting /authenticate");
		return response;
	}
	
	// validate - for every request it validates the user-credentials from the provided Jwt token in Authorization req. header
	@PostMapping("/validate")
	public ResponseEntity<AuthenticationResponse> validateJwt(@RequestHeader("Authorization") String jwt){
		
		AuthenticationResponse authenticationResponse = new AuthenticationResponse("Invalid", "Invalid", false);
		ResponseEntity<AuthenticationResponse> response = null;

		//first remove Bearer from Header
		jwt = jwt.substring(7);
		
		//check token
		logger.info("--------JWT :: "+jwt);
		
		
		// check the jwt is proper or not
		final ProjectManagerDetails projectManagerDetails = projectManagerDetailsService
															.loadUserByUsername(jwtService.extractUsername(jwt));
		
		// now validating the jwt
		try {
			if(jwtService.validateToken(jwt, projectManagerDetails)) {
				authenticationResponse.setName(projectManagerDetails.getName());
				authenticationResponse.setProjectName(projectManagerDetails.getProjectName());
				authenticationResponse.setValid(true);
				response = new ResponseEntity<AuthenticationResponse>(authenticationResponse, HttpStatus.OK);
				logger.info("Successfully validated the jwt and sending response back!");
			}
			else {
				response = new ResponseEntity<AuthenticationResponse>(authenticationResponse, HttpStatus.OK);
				logger.error("JWT Token validation failed!");
			}
		}catch (Exception e) {
//			logger.error(e.getMessage());
			response = new ResponseEntity<AuthenticationResponse>(authenticationResponse, HttpStatus.OK);
			logger.error("Exception occured whil validating JWT : Exception info : " + e.getMessage());
		}
		logger.info("-------- Exiting /validate");
		return response;
	}
	
}
