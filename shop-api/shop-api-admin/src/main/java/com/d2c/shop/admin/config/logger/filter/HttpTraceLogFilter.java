package com.d2c.shop.admin.config.logger.filter;

import com.d2c.shop.admin.config.security.context.LoginUserHolder;
import com.d2c.shop.service.common.utils.RequestUtil;
import com.d2c.shop.service.common.utils.SpringUtil;
import com.d2c.shop.service.modules.logger.nosql.elasticsearch.document.BackTraceLog;
import com.d2c.shop.service.modules.logger.nosql.elasticsearch.repository.BackTraceLogRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * @author BaiCai
 */
@Slf4j
public class HttpTraceLogFilter extends OncePerRequestFilter implements Ordered {

    private static final String NEED_TRACE_PATH_PREFIX = "/back";
    private static final String IGNORE_TRACE_PATH = "/back/user/expired";
    private static final String IGNORE_CONTENT_TYPE = "multipart/form-data";
    private final MeterRegistry registry;

    public HttpTraceLogFilter(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!isRequestValid(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
            status = response.getStatus();
        } finally {
            String path = request.getRequestURI();
            if (path.startsWith(NEED_TRACE_PATH_PREFIX) && !path.equals(IGNORE_TRACE_PATH) && !Objects.equals(IGNORE_CONTENT_TYPE, request.getContentType())) {
                // 记录日志
                BackTraceLog log = new BackTraceLog();
                log.setId(System.currentTimeMillis());
                log.setIp(RequestUtil.getRequestIp(request));
                log.setTime(LocalDateTime.now().toString());
                log.setPath(path);
                log.setMethod(request.getMethod());
                log.setStatus(status);
                long latency = System.currentTimeMillis() - startTime;
                log.setTimeTaken(latency);
                log.setParameterMap(this.mapToString(request.getParameterMap()));
                log.setRequestBody(this.getRequestBody(request));
                log.setResponseBody(this.getResponseBody(response));
                log.setUsername(SpringUtil.getBean(LoginUserHolder.class).getUsername());
                SpringUtil.getBean(BackTraceLogRepository.class).save(log);
            }
            updateResponse(response);
        }
    }

    private boolean isRequestValid(HttpServletRequest request) {
        try {
            new URI(request.getRequestURL().toString());
            return true;
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    private String getRequestBody(HttpServletRequest request) {
        String requestBody = "";
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            try {
                requestBody = IOUtils.toString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
            } catch (IOException e) {
                // NOOP
            }
        }
        return requestBody;
    }

    private String getResponseBody(HttpServletResponse response) {
        String responseBody = "";
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            try {
                responseBody = IOUtils.toString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
            } catch (IOException e) {
                // NOOP
            }
        }
        return responseBody;
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        Objects.requireNonNull(responseWrapper).copyBodyToResponse();
    }

    // ParamsMap转成String
    private String mapToString(Map<?, ?> params) {
        String str = "";
        try {
            if (params != null && params.size() > 0) {
                for (Object key : params.keySet()) {
                    Object values = params.get(key);
                    str = str + key + "=";
                    if (values instanceof String[]) {
                        String[] strs = (String[]) values;
                        for (String value : strs) {
                            str = str + value + ",";
                        }
                        str = str.substring(0, str.length() - 1) + "&";
                    }
                }
                str = str.substring(0, str.length() - 1);
            } else {
                str = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

}
