package com.userservice.main.controller;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import com.userservice.main.entity.Employee;
import com.userservice.main.registration.dto.EmpResponse;
import com.userservice.main.registration.dto.EmployeeLeaveDto;
import com.userservice.main.registration.dto.LoginForm;
import com.userservice.main.registration.dto.RegistrationDto;
import com.userservice.main.registration.dto.ResponseMsg;
import com.userservice.main.service.UserService;

import lombok.extern.slf4j.Slf4j;


@CrossOrigin(origins = "http://127.0.0.1:5504", methods = { RequestMethod.POST, RequestMethod.OPTIONS })
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

	private final UserService userService;

	@Autowired
	private RabbitTemplate template;

	public UserController(UserService userService) {

		this.userService = userService;
	}

	public RegistrationDto regitrationDto;

//	@GetMapping
//	public String showRegistrationForm() {
//		return "registration";
//	}

//	@PostMapping("register")
//	public ResponseEntity<String> registerUserAccount(@RequestBody RegistrationDto registrationDTO) {
//		userService.save(registrationDTO);
//		return ResponseEntity.ok("User Created Succesfully.");
//	}

//	@PostMapping("adduser")
//	public ResponseEntity<String> addUser(@RequestBody RegistrationDto user) {
//		userService.addUser(user);
//
//		return ResponseEntity.ok("Employee added Succesfully");
//	}

//	@PostMapping
//    public ResponseEntity<Void> createEmployeeLeave(@RequestBody Employee employee) {
//        // Create EmployeeLeave record in user service database based on the provided Employee data
//        // Example implementation:
//           userService.createEmployeeLeave(employee);
//
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

	@PutMapping("/updateemp/{gmail}")
	public ResponseEntity<ResponseMsg> UpdatingUserAccount(@PathVariable("gmail") String gmail,
			@RequestBody RegistrationDto registrationDTO) {
          log.info("updating employee by ID method running. ");
		ResponseMsg body = userService.updateEmp(gmail, registrationDTO);

		return new ResponseEntity<>(body, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public String userlogin(@RequestBody LoginForm loginform) {
		return userService.userLogin(loginform);
		
//		return ResponseEntity.ok(result);

	}

	@PostMapping("/forgotPassword")
	public ResponseEntity<EmpResponse> forgotPassword(@RequestBody LoginForm loginform) {

		EmpResponse msg1 = new EmpResponse();

		String msg = userService.forgotPassword(loginform);
//		msg1.setData(loginform);
		msg1.setMessage(msg);
		return new ResponseEntity<>(msg1, HttpStatus.OK);
	}

	@PostMapping("/verifyOtp")
	public ResponseEntity<String> VerifyOtp(@RequestParam("gmail") String gmail, @RequestParam("otp") String otp) {

		Boolean flag = userService.getOtp(gmail, otp);

		if (flag) {
			return ResponseEntity.ok("OTP Verification is Succesfull..");
		} else {
			return ResponseEntity.badRequest().body("Invalid OTP.");
		}
	}

	@PostMapping("/setPassword")
	public ResponseEntity<String> setPassword(@RequestParam("gmail") String gmail,
			@RequestParam("password") String password) {

		String result = userService.setpassword(gmail, password);

		return ResponseEntity.ok(result);

	}

	@GetMapping("/getemployees")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<List<Employee>> getAllEmployees() {
		List<Employee> emp = userService.getAllEmployees();
		return new ResponseEntity<>(emp, HttpStatus.OK);
	}

	@GetMapping("/getemployee/{id}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Employee> getEmpById(@PathVariable Long id) {
      
		log.info("Getting employee by ID method running. ");
		Employee emp = userService.getEmployeeById(id);

		if (emp != null) {
			return new ResponseEntity<>(emp, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(emp, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/logout")
	public String logout(){   
		log.warn("Employee Logout.");
		return "redirect:/SignIn?logout";
	}

	@GetMapping("/SignIn")
	public String logins() {
		return "SingIn.html"; // Assuming login.html is your login page
	}

	@DeleteMapping("/DropBy/{id}")
	public String DropUserById(@PathVariable("id") long id) {
		String data = userService.DeleteUserById(id);
		return data;
	}

	@PostMapping("/applyLeave/{guid}")
//	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseMsg> applyEmployeeLeave(@PathVariable("guid") String guid,
			String admingmail,  @RequestBody EmployeeLeaveDto empDto) {
		
        log.info("Employee Applying a Leave method running. ");
		ResponseMsg response = userService.saveLeaveDetails(guid, admingmail, empDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}