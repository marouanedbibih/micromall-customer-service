package org.micromall.customer.customer;

import java.util.Map;

import org.micromall.customer.utils.MyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // Endpoint to create a new customer
    @PostMapping("/api/v1/customer")
    public ResponseEntity<MyResponse> createCustomer(@RequestBody @Valid CustomerRequest request) {
        CustomerDTO customerDTO = customerService.create(request);
        return ResponseEntity.ok(MyResponse.builder()
                .message("Customer created successfully")
                .data(customerDTO)
                .build());
    }

    // Endpoint to update an existing customer
    @PutMapping("/api/v1/customer/{id}")
    public ResponseEntity<MyResponse> updateCustomer(
            @RequestBody @Valid CustomerRequest request,
            @PathVariable String id) {
        CustomerDTO customerDTO = customerService.update(request, id);
        return ResponseEntity.ok(MyResponse.builder()
                .message("Customer updated successfully")
                .data(customerDTO)
                .build());
    }

    // Endpoint to fetch a customer by ID
    @GetMapping("/api/v1/customer/{id}")
    public ResponseEntity<MyResponse> getCustomer(@PathVariable String id) {
        CustomerDTO customerDTO = customerService.fetchById(id);
        return ResponseEntity.ok(MyResponse.builder()
                .message("Customer fetched successfully")
                .data(customerDTO)
                .build());
    }

    // Endpoint to delete a customer by ID
    @DeleteMapping("/api/v1/customer/{id}")
    public ResponseEntity<MyResponse> deleteCustomer(@PathVariable String id) {
        customerService.delete(id);
        return ResponseEntity.ok(MyResponse.builder()
                .message("Customer deleted successfully")
                .build());
    }

    /**
     * Endpoint to fetch all customers
     * 
     * @param int    page
     * @param int    size
     * @param String sortBy
     * @param String orderBy
     * @param String search
     */
    @GetMapping("/api/v1/customers")
    public ResponseEntity<MyResponse> getAllCustomers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String orderBy,
            @RequestParam(required = false) String search) {

        // Create a pagable object
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.valueOf(orderBy.toUpperCase()), sortBy);

        // Fetch all customers by conditions
        Page<CustomerDTO> customersPage;
        if (search == null) {
            customersPage = customerService.fetchAll(pageable);
        } else {
            customersPage = customerService.search(search, pageable);
        }
        // Create a meta object
        Map<String, Object> meta = Map.of(
                "page", customersPage.getNumber() + 1,
                "size", customersPage.getSize(),
                "total", customersPage.getTotalElements(),
                "totalPages", customersPage.getTotalPages());

        return ResponseEntity.ok(MyResponse.builder()
                .message("Customers fetched successfully")
                .data(customersPage.getContent())
                .meta(meta)
                .build());
    }

}
