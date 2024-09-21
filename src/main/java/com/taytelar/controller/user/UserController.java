package com.taytelar.controller.user;

import com.taytelar.request.user.LoginRequest;
import com.taytelar.request.user.UserRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.request.user.AddressRequest;
import com.taytelar.response.user.AddressResponse;
import com.taytelar.response.user.LoginResponse;
import com.taytelar.response.user.RegisterResponse;
import com.taytelar.service.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * Endpoint to register a new user.
     *
     * @param userRequest The request object containing user registration details.
     * @return A ResponseEntity containing the RegisterResponse object and HTTP status 200 (OK) if registration is successful.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> userRegister(@Valid @RequestBody UserRequest userRequest){
        RegisterResponse registerResponse = userService.register(userRequest);
        return ResponseEntity.status(HttpStatus.OK).body(registerResponse);
    }


    /**
     * Endpoint to authenticate and log in a user.
     *
     * @param loginRequest The request object containing user login details (phone number, user type, and request type).
     * @return A ResponseEntity containing the LoginResponse object and HTTP status 200 (OK) if login is successful.
     *         If the login fails or validation errors occur, appropriate error responses will be returned.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }


    /**
     * Endpoint to add a new address for a user.
     *
     * @param addressRequest The request object containing the address details to be added (building name, street name, city, state, pin code, etc.).
     * @return A ResponseEntity containing the SuccessResponse object and HTTP status 200 (OK) if the address is added successfully.
     *         If the request contains validation errors or the operation fails, appropriate error responses will be returned.
     */
    @PostMapping("/addAddress")
    public ResponseEntity<SuccessResponse> addAddress(@Valid @RequestBody AddressRequest addressRequest) {
        SuccessResponse successResponse = userService.addAddress(addressRequest);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }


    /**
     * Endpoint to retrieve all addresses associated with a specific user.
     *
     * @param userId The ID of the user whose addresses are being fetched.
     * @return A ResponseEntity containing a list of AddressResponse objects and HTTP status 200 (OK) if the addresses are retrieved successfully.
     *         If the user ID is invalid or the operation fails, appropriate error responses will be returned.
     */
    @GetMapping("/getAddresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(@NotBlank @RequestParam String userId) {
        List<AddressResponse> addressResponseList = userService.getAddresses(userId);
        return ResponseEntity.status(HttpStatus.OK).body(addressResponseList);
    }


    /**
     * Endpoint to update an existing address of a user.
     *
     * @param addressRequest The request object containing the updated address details (including addressId to identify which address to update).
     * @return A ResponseEntity containing the SuccessResponse object and HTTP status 200 (OK) if the address is updated successfully.
     *         If validation errors occur or the update operation fails, appropriate error responses will be returned.
     */
    @PutMapping("/updateAddress")
    public ResponseEntity<SuccessResponse> updateAddress(@Valid @RequestBody AddressRequest addressRequest) {
        SuccessResponse successResponse = userService.updateAddress(addressRequest);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }


    /**
     * Endpoint to delete a specific address of a user.
     *
     * @param userId The ID of the user.
     * @param addressId The ID of the address to be deleted.
     * @return A ResponseEntity containing the SuccessResponse object and HTTP status 200 (OK) if the address is deleted successfully.
     *         If the user or address ID is invalid, or if the deletion operation fails, appropriate error responses will be returned.
     */
    @DeleteMapping("/deleteAddress")
    public ResponseEntity<SuccessResponse> deleteAddress(@NotBlank @RequestParam String userId, @NotNull @RequestParam Long addressId) {
        SuccessResponse successResponse = userService.deleteAddress(userId, addressId);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

}
