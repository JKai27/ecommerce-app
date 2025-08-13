// parseProblem.ts - TypeScript utility for handling RFC 7807 Problem Details

/**
 * RFC 7807 Problem Detail structure
 */
export interface ProblemDetail {
  type?: string;           // Problem type URI
  title?: string;          // Human-readable summary
  status?: number;         // HTTP status code
  detail?: string;         // Human-readable explanation
  instance?: string;       // URI reference to specific occurrence
  
  // Custom properties
  timestamp?: string;      // When the error occurred
  correlationId?: string;  // Unique request identifier
  
  // Validation-specific properties
  errors?: Record<string, string[]>;      // Field validation errors
  violations?: Record<string, string>;    // Constraint violations
  
  // Domain-specific properties (examples)
  email?: string;          // Invalid email value
  productId?: string;      // Product identifier
  sellerId?: string;       // Seller identifier
  companyName?: string;    // Company name that caused conflict
  
  [key: string]: any;      // Allow other custom properties
}

/**
 * Extract Problem Details from Axios error response
 * 
 * Usage:
 * ```typescript
 * try {
 *   await api.post('/api/users', userData);
 * } catch (error) {
 *   const problem = parseProblemFromAxiosError(error);
 *   
 *   if (problem.type === 'https://api.shopeazy.com/problems/validation-error') {
 *     // Handle validation errors
 *     console.log('Validation errors:', problem.errors);
 *   } else if (problem.status === 404) {
 *     // Handle not found
 *     console.log('Resource not found:', problem.detail);
 *   }
 *   
 *   // Always available
 *   console.log('Correlation ID:', problem.correlationId);
 * }
 * ```
 */
export function parseProblemFromAxiosError(error: any): ProblemDetail {
  // Default problem structure
  const defaultProblem: ProblemDetail = {
    type: 'https://api.shopeazy.com/problems/unknown-error',
    title: 'Unknown Error',
    status: 500,
    detail: 'An unexpected error occurred',
    timestamp: new Date().toISOString(),
    correlationId: 'unknown'
  };

  // Handle Axios errors
  if (error.response?.data) {
    const data = error.response.data;
    
    // If it's already a Problem Detail, return it
    if (data.type || data.title || data.status) {
      return { ...defaultProblem, ...data };
    }
    
    // Handle legacy error formats (during migration)
    if (typeof data === 'string') {
      return {
        ...defaultProblem,
        status: error.response.status || 500,
        detail: data
      };
    }
    
    if (data.message) {
      return {
        ...defaultProblem,
        status: error.response.status || 500,
        detail: data.message
      };
    }
  }
  
  // Handle network errors
  if (error.code === 'NETWORK_ERROR' || !error.response) {
    return {
      ...defaultProblem,
      type: 'https://api.shopeazy.com/problems/network-error',
      title: 'Network Error',
      status: 0,
      detail: 'Unable to connect to server. Please check your connection.'
    };
  }
  
  return defaultProblem;
}

/**
 * Extract validation errors in a frontend-friendly format
 * 
 * Returns either field validation errors or constraint violations
 */
export function getValidationErrors(problem: ProblemDetail): Record<string, string[]> {
  if (problem.errors) {
    return problem.errors;
  }
  
  if (problem.violations) {
    // Convert violations to errors format
    const errors: Record<string, string[]> = {};
    Object.entries(problem.violations).forEach(([field, message]) => {
      errors[field] = [message];
    });
    return errors;
  }
  
  return {};
}

/**
 * Check if error is a specific problem type
 * 
 * Usage:
 * ```typescript
 * if (isProblemType(problem, 'validation-error')) {
 *   // Handle validation specifically
 * }
 * ```
 */
export function isProblemType(problem: ProblemDetail, type: string): boolean {
  return problem.type?.includes(`/problems/${type}`) ?? false;
}

/**
 * Extract user-friendly error message
 * 
 * Priority: detail > title > generic message
 */
export function getErrorMessage(problem: ProblemDetail): string {
  if (problem.detail && problem.detail !== problem.title) {
    return problem.detail;
  }
  
  if (problem.title) {
    return problem.title;
  }
  
  return 'An error occurred. Please try again.';
}

/**
 * Check if this is a client error (4xx) vs server error (5xx)
 */
export function isClientError(problem: ProblemDetail): boolean {
  const status = problem.status ?? 500;
  return status >= 400 && status < 500;
}

export function isServerError(problem: ProblemDetail): boolean {
  const status = problem.status ?? 500;
  return status >= 500;
}

// Example usage in a React component:
/*
import { parseProblemFromAxiosError, getValidationErrors, isProblemType } from './parseProblem';

const handleSubmit = async (userData) => {
  try {
    await api.post('/api/users', userData);
    setSuccess('User created successfully!');
  } catch (error) {
    const problem = parseProblemFromAxiosError(error);
    
    if (isProblemType(problem, 'validation-error')) {
      const validationErrors = getValidationErrors(problem);
      setFieldErrors(validationErrors);
    } else {
      setGeneralError(getErrorMessage(problem));
    }
    
    // Log for debugging (include correlation ID for support)
    console.error('API Error:', {
      type: problem.type,
      correlationId: problem.correlationId,
      detail: problem.detail
    });
  }
};
*/