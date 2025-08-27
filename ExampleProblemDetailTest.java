// ExampleProblemDetailTest.java - Example test showing RFC 7807 testing approach
// Just for learning Purpose (Not a part of code)

package shopeazy.com.ecommerce_app.common.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Example tests demonstrating how to test RFC 7807 Problem Details responses
 * 
 * Key testing principles:
 * 1. Assert Content-Type is "application/problem+json"  
 * 2. Verify all RFC 7807 required fields are present
 * 3. Check domain-specific properties are included
 * 4. Validate correlationId and timestamp are present
 * 5. Ensure no sensitive data leaks in 5xx errors
 */
@WebMvcTest(controllers = {/* Your controller here */})
class ExampleProblemDetailTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Mock your services as needed
    // @MockBean
    // private UserService userService;

    /**
     * Test validation error (400) returns proper Problem Detail
     */
    @Test
    void testValidationError_ReturnsProblemDetail() throws Exception {
        // Given: Invalid user registration data
        String invalidUserJson = """
            {
                "email": "invalid-email",
                "password": "123",
                "firstName": ""
            }
            """;

        // When: POST to user registration endpoint
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUserJson))
                
        // Then: Expect RFC 7807 Problem Detail response
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andReturn();

        // Parse and validate Problem Detail structure
        String responseBody = result.getResponse().getContentAsString();
        JsonNode problem = objectMapper.readTree(responseBody);
        
        // Assert RFC 7807 required fields
        assertThat(problem.has("type")).isTrue();
        assertThat(problem.has("title")).isTrue(); 
        assertThat(problem.has("status")).isTrue();
        assertThat(problem.has("detail")).isTrue();
        assertThat(problem.has("instance")).isTrue();
        
        // Assert custom fields
        assertThat(problem.has("timestamp")).isTrue();
        assertThat(problem.has("correlationId")).isTrue();
        
        // Assert validation-specific fields
        assertThat(problem.has("errors")).isTrue();
        assertThat(problem.get("errors").has("email")).isTrue();
        assertThat(problem.get("errors").has("password")).isTrue();
        assertThat(problem.get("errors").has("firstName")).isTrue();
        
        // Assert type URI
        assertThat(problem.get("type").asText())
            .isEqualTo("https://api.shopeazy.com/problems/validation-error");
    }

    /**
     * Test resource not found (404) returns proper Problem Detail
     */
    @Test
    void testResourceNotFound_ReturnsProblemDetail() throws Exception {
        // Given: Request for non-existent user
        String nonExistentId = "999";
        
        // When: GET non-existent user
        MvcResult result = mockMvc.perform(get("/api/users/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andReturn();

        // Then: Validate Problem Detail
        JsonNode problem = objectMapper.readTree(result.getResponse().getContentAsString());
        
        assertThat(problem.get("type").asText())
            .isEqualTo("https://api.shopeazy.com/problems/not-found");
        assertThat(problem.get("status").asInt()).isEqualTo(404);
        assertThat(problem.get("title").asText()).isEqualTo("Resource Not Found");
        assertThat(problem.get("instance").asText()).isEqualTo("/api/users/999");
    }

    /**
     * Test business logic error (409 Conflict) returns proper Problem Detail
     */
    @Test
    void testSellerAlreadyExists_ReturnsProblemDetail() throws Exception {
        // Given: Attempt to create duplicate seller
        String sellerJson = """
            {
                "companyName": "Existing Company",
                "contactEmail": "existing@company.com"
            }
            """;

        // Mock service to throw exception
        // when(sellerService.createSeller(any())).thenThrow(
        //     new SellerAlreadyExistsException("Seller already exists")
        // );

        // When: POST duplicate seller
        MvcResult result = mockMvc.perform(post("/api/sellers/apply")
                .contentType(MediaType.APPLICATION_JSON)  
                .content(sellerJson))
                .andExpect(status().isConflict())
                .andExpected(content().contentType("application/problem+json"))
                .andReturn();

        // Then: Validate Problem Detail
        JsonNode problem = objectMapper.readTree(result.getResponse().getContentAsString());
        
        assertThat(problem.get("type").asText())
            .isEqualTo("https://api.shopeazy.com/problems/seller-already-exists");
        assertThat(problem.get("status").asInt()).isEqualTo(409);
        assertThat(problem.get("title").asText()).isEqualTo("Seller Already Exists");
    }

    /**
     * Test server error (500) returns safe Problem Detail without stack traces
     */
    @Test
    void testInternalServerError_ReturnsSafeProblemDetail() throws Exception {
        // Given: Service throws unexpected exception
        // when(userService.getUsers()).thenThrow(new RuntimeException("Database connection failed"));

        // When: Request causes server error  
        MvcResult result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isInternalServerError())
                .andExpected(content().contentType("application/problem+json"))
                .andReturn();

        // Then: Validate safe error response
        JsonNode problem = objectMapper.readTree(result.getResponse().getContentAsString());
        
        assertThat(problem.get("type").asText())
            .isEqualTo("https://api.shopeazy.com/problems/internal-server-error");
        assertThat(problem.get("status").asInt()).isEqualTo(500);
        assertThat(problem.get("title").asText()).isEqualTo("Internal Server Error");
        
        // Assert no sensitive information leaked
        String detail = problem.get("detail").asText();
        assertThat(detail).doesNotContain("Database");
        assertThat(detail).doesNotContain("Exception");
        assertThat(detail).doesNotContain("SQLException");
        assertThat(detail).isEqualTo("An unexpected error occurred. Please try again later.");
    }

    /**
     * Test correlation ID is properly handled
     */
    @Test
    void testCorrelationId_IsProperlyHandled() throws Exception {
        // Given: Request with custom correlation ID
        String customCorrelationId = "test-correlation-123";
        
        // When: Make request with X-Correlation-Id header
        MvcResult result = mockMvc.perform(get("/api/users/999")
                .header("X-Correlation-Id", customCorrelationId))
                .andExpect(status().isNotFound())
                .andReturn();

        // Then: Correlation ID should be in response
        JsonNode problem = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(problem.get("correlationId").asText()).isEqualTo(customCorrelationId);
    }
}

/* 
Example curl/httpie commands for manual testing:

# Test validation error
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email": "invalid", "password": "123"}'

Expected response:
{
  "type": "https://api.shopeazy.com/problems/validation-error",
  "title": "Validation Failed", 
  "status": 400,
  "detail": "Request validation failed on 2 field(s)",
  "instance": "/api/users",
  "timestamp": "2024-01-15T10:30:45.123Z",
  "correlationId": "abc-123-def",
  "errors": {
    "email": ["Please enter a valid email address"],
    "password": ["Password must be at least 8 characters"]
  }
}

# Test not found
curl http://localhost:8080/api/users/999 \
  -H "X-Correlation-Id: my-test-123"

Expected response:  
{
  "type": "https://api.shopeazy.com/problems/not-found",
  "title": "Resource Not Found",
  "status": 404, 
  "detail": "User not found with ID: 999",
  "instance": "/api/users/999",
  "timestamp": "2024-01-15T10:30:45.123Z",
  "correlationId": "my-test-123"
}

# Test conflict error
curl -X POST http://localhost:8080/api/sellers/apply \
  -H "Content-Type: application/json" \
  -d '{"companyName": "Existing Company", "contactEmail": "existing@company.com"}'

Expected response:
{
  "type": "https://api.shopeazy.com/problems/seller-already-exists", 
  "title": "Seller Already Exists",
  "status": 409,
  "detail": "Seller already exists under this email: existing@company.com",
  "instance": "/api/sellers/apply",
  "timestamp": "2024-01-15T10:30:45.123Z",
  "correlationId": "def-456-ghi",
  "email": "existing@company.com"
}
*/