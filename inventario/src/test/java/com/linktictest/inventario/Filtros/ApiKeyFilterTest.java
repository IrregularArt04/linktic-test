package com.linktictest.inventario.Filtros;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiKeyFilterTest {

    @InjectMocks
    private ApiKeyFilter apiKeyFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private static final String API_TOKEN = "test-api-token-123";

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(apiKeyFilter, "apiToken", API_TOKEN);
    }

    @Test
    public void doFilter_BypassOptionsRequest() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("OPTIONS");

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, never()).getRequestURI();
    }

    @Test
    public void doFilter_ValidApiKey_Success() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/inventario/1");
        when(request.getHeader("X-API-KEY")).thenReturn(API_TOKEN);

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    public void doFilter_InvalidApiKey_Unauthorized() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/inventario/1");
        when(request.getHeader("X-API-KEY")).thenReturn("wrong-token");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json;charset=UTF-8");
        verify(filterChain, never()).doFilter(request, response);
        assertTrue(stringWriter.toString().contains("No autorizado"));
    }

    @Test
    public void doFilter_MissingApiKey_Unauthorized() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/inventario/1");
        when(request.getHeader("X-API-KEY")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
        assertTrue(stringWriter.toString().contains("No autorizado"));
    }

    @Test
    public void doFilter_NonApiUri_Bypass() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, never()).getHeader("X-API-KEY");
    }
}
