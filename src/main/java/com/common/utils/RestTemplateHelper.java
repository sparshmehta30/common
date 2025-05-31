package com.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * <h1>Examples</h1>
 * <p/>
 * <span style="color:yellow">Find by id</span>
 * <p>
 * UserDto userDto = restTemplateHelper.getForEntity(UserDto.class,
 * "http://localhost:8080/users/{id}", id);
 * </p>
 *
 * <span style="color:yellow">Find all</span>
 * <p>
 * List<T> userDto = restTemplateHelper.getForList(UserDto.class,
 * "http://localhost:8080/users");
 * </p>
 *
 * <span style="color:yellow">Save</span>
 * <p>
 * UserDto userDto = restTemplateHelper.postForEntity(UserDto.class,
 * "http://localhost:8080/users", userDto);
 * </p>
 *
 * <span style="color:yellow">Update</span>
 * <p>
 * UserDto userDto = restTemplateHelper.putForEntity(UserDto.class,
 * "http://localhost:8080/users/{id}", userDto, id);
 * </p>
 *
 * <span style="color:yellow">Delete</span>
 * <p>
 * restTemplateHelper.delete("http://localhost:8080/users/{id}", id);
 * </p>
 */
@Component
public class RestTemplateHelper {

    private static final Logger LOGGER = Logger.getLogger(RestTemplateHelper.class);

    private final Logger log = Logger.getLogger(getClass());

    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";

    private int connectTimeOut = 300000;

    private int readTimeOut = 300000;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    private static final String NOT_DATA_FOUND_LOG_FORMAT = "No data found {}";

    private Supplier<ClientHttpRequestFactory> requestFactorySupplier = () -> new BufferingClientHttpRequestFactory(
            new SimpleClientHttpRequestFactory());

    public void reset() {
        connectTimeOut = 300000;
        readTimeOut = 300000;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public RestTemplate initRestTemplate() {
        SimpleClientHttpRequestFactory simpleHttp = new SimpleClientHttpRequestFactory();
        simpleHttp.setConnectTimeout(connectTimeOut);
        simpleHttp.setReadTimeout(readTimeOut);
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(simpleHttp);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new LoggerInterceptor()));
        restTemplate.setErrorHandler(new ErrorHandler());
        return restTemplate;
    }

    @Autowired
    public RestTemplateHelper(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        restTemplateBuilder.requestFactory(requestFactorySupplier);
        this.restTemplate = restTemplateBuilder.build();
        // this.restTemplate
        // .setInterceptors(Collections.singletonList(new
        // RequestResponseLoggingInterceptor()));
        this.objectMapper = objectMapper;
    }

    public <T> T getForEntity(Class<T> clazz, String url, Object... uriVariables) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
            JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
            return readValue(response, javaType);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOGGER.info(NOT_DATA_FOUND_LOG_FORMAT, url);
            } else {
                LOGGER.info("rest client exception", exception.getMessage());
            }
        }
        return null;
    }

    public <T, R> T getForEntity(Class<T> clazz, String url, MultiValueMap<String, String> headers,
                                 Object... uriVariables) {
        try {
            HttpEntity<R> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class,
                    uriVariables);
            JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
            return readValue(response, javaType);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOGGER.info(NOT_DATA_FOUND_LOG_FORMAT, url);
            } else {
                LOGGER.info("rest client exception", exception.getMessage());
            }
        }
        return null;
    }

    public <T> List<T> getForList(Class<T> clazz, String url, Object... uriVariables) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return readValue(response, collectionType);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOGGER.info(NOT_DATA_FOUND_LOG_FORMAT, url);
            } else {
                LOGGER.info("rest client exception", exception.getMessage());
            }
        }
        return new ArrayList<>();
    }

    public <T, R> ResponseEntity<T> postForEntityWithOutRead(Class<T> clazz, String url, R body, Object... uriVariables)
            throws RestClientException {
        HttpEntity<R> request = new HttpEntity<>(body);
        return restTemplate.postForEntity(url, request, clazz, uriVariables);
    }

    public <T, R> List<T> postForList(Class<T> clazz, String url, R body, Object... uriVariables) {
        HttpEntity<R> request = new HttpEntity<>(body);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class, uriVariables);
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return readValue(response, collectionType);
    }

    public <T, R> T postForEntity(Class<T> clazz, String url, R body, Object... uriVariables) {
        HttpEntity<R> request = new HttpEntity<>(body);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class, uriVariables);
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        return readValue(response, javaType);
    }

    public <T, R> T postForEntityWithHeaders(Class<T> clazz, String url, MultiValueMap<String, String> headers, R body,
                                             Object... uriVariables) {
        HttpEntity<R> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class, uriVariables);
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        return readValue(response, javaType);
    }
    
    @Async
	public <T, R> void asyncPostForEntityWithHeaders(Class<T> clazz, String url, MultiValueMap<String, String> headers, R body,
			Object... uriVariables) {
    	postForEntityWithHeaders(clazz, url, headers, body, uriVariables);
	}

    @Async
    public <T, R> T asyncPostForEntity(Class<T> clazz, String url, R body, Object... uriVariables) {
        HttpEntity<R> request = new HttpEntity<>(body);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class, uriVariables);
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        return readValue(response, javaType);
    }

    public <T> T postForUrlEncodedEntity(Class<T> clazz, String url, MultiValueMap<String, String> headers,
                                         MultiValueMap<String, String> values, Object... uriVariables) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (Objects.nonNull(headers) && Boolean.FALSE.equals(headers.isEmpty()))
            header.addAll(headers);
        LOGGER.info("{}", header);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(values, header);
        LOGGER.info("{}, {}", url, request.getBody());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class, uriVariables);
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        return readValue(response, javaType);
    }

    public ResponseEntity<String> postForUrlEncodedEntity(String url, MultiValueMap<String, String> headers,
                                                          MultiValueMap<String, String> values, Object... uriVariables) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (Objects.nonNull(headers) && Boolean.FALSE.equals(headers.isEmpty()))
            header.addAll(headers);
        LOGGER.info("{}", header);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(values, header);
        LOGGER.info("{}, {}", url, request.getBody());
        return restTemplate.postForEntity(url, request, String.class, uriVariables);
    }

    public <T> T postForUrlEncodedEntityBasicAuth(Class<T> clazz, String url, String username, String credential,
                                                  MultiValueMap<String, String> headers, MultiValueMap<String, String> values, Object... uriVariables) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        header.addAll(headers);
        LOGGER.info("{}", header);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(values, header);
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient(username, credential));
        LOGGER.info("{}, {}", url, request.getBody());
        RestTemplate template = new RestTemplate(clientHttpRequestFactory);
        template.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
        ResponseEntity<String> response = template.postForEntity(url, request, String.class, uriVariables);
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        return readValue(response, javaType);
    }

    public <T, R> T putForEntity(Class<T> clazz, String url, R body, Object... uriVariables) {
        HttpEntity<R> request = new HttpEntity<>(body);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class,
                uriVariables);
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        return readValue(response, javaType);
    }

    public void delete(String url, Object... uriVariables) {
        try {
            restTemplate.delete(url, uriVariables);
        } catch (RestClientException exception) {
            LOGGER.info(exception.getMessage());
        }
    }

    private <T> T readValue(ResponseEntity<String> response, JavaType javaType) {
        T result = null;
        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
            try {
                result = objectMapper.readValue(response.getBody(), javaType);
            } catch (IOException e) {
                LOGGER.info(e.getMessage());
            }
        } else {
            LOGGER.info(NOT_DATA_FOUND_LOG_FORMAT, response.getStatusCode());
        }
        return result;
    }

    private HttpClient httpClient(String username, String credential) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, credential));

        return HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
    }

    public <T, R> T putForEntityWithHeaders(Class<T> clazz, String url, MultiValueMap<String, String> headers, R body, Object... uriVariables) {
        HttpEntity<R> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class,
                uriVariables);
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        return readValue(response, javaType);
    }

    public <T, R> ResponseEntity<T> postForEntityWithHeaders(ParameterizedTypeReference<T> clazz, String url,
                                                             MultiValueMap<String, String> headers, R body, Object... uriVariables) {
        headers.add(HttpHeaders.USER_AGENT, USER_AGENT);
        HttpEntity<R> request = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, clazz, uriVariables);
    }

    public <T, R> ResponseEntity<T> getForEntityWithHeaders(ParameterizedTypeReference<T> clazz, String url,
                                                            MultiValueMap<String, String> headers, Object... uriVariables) {
        headers.add(HttpHeaders.USER_AGENT, USER_AGENT);
        HttpEntity<R> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, request, clazz, uriVariables);
    }

    public <T, R> ResponseEntity<T> putForEntityWithHeaders(ParameterizedTypeReference<T> clazz, String url,
                                                            MultiValueMap<String, String> headers, R body, Object... uriVariables) {
        headers.add(HttpHeaders.USER_AGENT, USER_AGENT);
        HttpEntity<R> request = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.PUT, request, clazz, uriVariables);
    }

    public <T, R> ResponseEntity<T> deleteForEntityWithHeaders(ParameterizedTypeReference<T> clazz, String url,
                                                               MultiValueMap<String, String> headers, Object... uriVariables) {
        headers.add(HttpHeaders.USER_AGENT, USER_AGENT);
        HttpEntity<R> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, request, clazz, uriVariables);
    }


    @Async
    public <T, R> void asyncPostForEntityWithHeaders(ParameterizedTypeReference<T> clazz, String url,
                                                     MultiValueMap<String, String> headers, R body, Object... uriVariables) {
        postForEntityWithHeaders(clazz, url, headers, body, uriVariables);
    }

    @Async
    public <T, R> void asyncGetForEntityWithHeaders(ParameterizedTypeReference<T> clazz, String url,
                                                    MultiValueMap<String, String> headers, Object... uriVariables) {
        getForEntityWithHeaders(clazz, url, headers, uriVariables);
    }

    @Async
    public <T, R> void asyncPutForEntityWithHeaders(ParameterizedTypeReference<T> clazz, String url,
                                                    MultiValueMap<String, String> headers, R body, Object... uriVariables) {
        putForEntityWithHeaders(clazz, url, headers, body, uriVariables);
    }

    @Async
    public <T, R> void asyncDeleteForEntityWithHeaders(ParameterizedTypeReference<T> clazz, String url,
                                                       MultiValueMap<String, String> headers, Object... uriVariables) {
        deleteForEntityWithHeaders(clazz, url, headers, uriVariables);
    }

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
}
