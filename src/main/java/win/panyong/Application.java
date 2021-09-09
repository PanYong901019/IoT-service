package win.panyong;

import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.lang.annotation.Aspect;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import win.panyong.util.*;
import win.panyong.util.authority.AuthConfig;
import win.panyong.util.authority.interceptor.PromisionInterceptor;
import win.panyong.util.authority.util.JwtUtil;
import win.panyong.utils.ObjectUtil;
import win.panyong.utils.StringUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@EnableTransactionManagement
@ServletComponentScan
@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean(name = "jedisPool")
    public JedisPool jedisPool(@Value("${spring.redis.host}") String host, @Value("${spring.redis.port}") int port, @Value("${spring.redis.password}") String password, @Value("${spring.redis.database}") int database, @Value("${spring.redis.timeout}") int timeout, @Value("${spring.redis.pool.max-wait}") int maxWait, @Value("${spring.redis.pool.max-idle}") int maxIdle, @Value("${spring.redis.pool.min-idle}") int minIdle) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        return new JedisPool(jedisPoolConfig, host, port, timeout, StringUtil.invalid(password) ? null : password, database);
    }

    @Bean(name = "redisUtil")
    public RedisUtil redisUtil(@Value("${spring.redis.database}") int database) {
        return new RedisUtil(database);
    }

    @Bean(name = "appCache")
    public AppCache appCache() {
        return new AppCache();
    }

    @Bean
    public JwtUtil jwtUtil(AuthConfig authConfig) {
        return new JwtUtil();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxRequestSize(DataSize.parse("500MB"));
        factory.setMaxFileSize(DataSize.parse("500MB"));
        return factory.createMultipartConfig();
    }

    @Bean
    public PromisionInterceptor promisionInterceptor() {
        return new PromisionInterceptor();
    }
}

@Component
class AppRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    final ApplicationContext applicationContext;
    final AppCache appCache;
    @Value("${server.port}")
    String port;

    public AppRunner(ApplicationContext applicationContext, AppCache appCache) {
        this.applicationContext = applicationContext;
        this.appCache = appCache;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        try {
            logger.info("=====================|系统配置初始化|======================");
            AppInitFunction.getInstance().initSystemConfig();
            AppInitFunction.getInstance().initServletContex(applicationContext, appCache);
            logger.info("====================|系统配置初始化完成|====================");
            logger.info("|SystemConfig|查看命令：curl http://localhost:" + port + "/checkConfig");
            logger.info("======================|项目启动成功|=======================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@Configuration
@MapperScan(basePackages = "win.panyong.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Primary
    @Bean(name = "dataSource")
    @Qualifier("dataSource")
    @ConfigurationProperties(prefix = "datasource.mysql")
    public DataSource dataSource() {
        logger.info("-------------------- dataSource init ---------------------");
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Primary
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Primary
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}


@WebFilter(filterName = "sessionFilter", urlPatterns = {"/*"})
class SessionFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    @Autowired
    AppCache appCache;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        String body = StringUtil.InputStreamToString(request.getInputStream());
        String body = "";
        Map<String, String> requestParameter = request.getParameterMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue()[0]));
        String[] rpcApiList = {"/heartbeat", "/checkConfig", "/refreshConfig"};
        String[] whitelist = {"/apiError"};
        String uri = request.getRequestURI().substring(request.getContextPath().length());
        if (StringUtil.isHave(uri, whitelist) || uri.startsWith("/static") || "/favicon.ico".equals(uri)) {
            filterChain.doFilter(request, response);
        } else {
            if (!appCache.getRequestMappingList().contains(uri)) {
                logger.info("====【找不到路径】=== |" + request.getMethod() + "|" + uri + "|===|" + ObjectUtil.objectToJsonString(requestParameter) + "|===|" + body + "|");
                Map<String, Object> result = new LinkedHashMap<>() {{
                    put("rspCode", 0);
                    put("rspInfo", "Path request denied");
                }};
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(ObjectUtil.mapToJsonString(result));
            } else if (StringUtil.isHave(uri, rpcApiList)) {
                logger.info("========rmi======== |" + request.getMethod() + "|" + uri + "|===|" + ObjectUtil.objectToJsonString(requestParameter) + "|===|" + body + "|");
                filterChain.doFilter(request, response);
            } else {
                if (uri.startsWith("/api")) {
                    logger.info("========api======== |" + request.getMethod() + "|" + uri + "|===|" + ObjectUtil.objectToJsonString(requestParameter) + "|===|" + body + "|");
                } else {
                    logger.info("=================== |" + request.getMethod() + "|" + uri + "|===|" + ObjectUtil.objectToJsonString(requestParameter) + "|===|" + body + "|");
                }
                filterChain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
    }
}

@Configuration
class MvcConfig implements WebMvcConfigurer {

    @Autowired
    PromisionInterceptor promisionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(promisionInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("file:" + new ApplicationHome(getClass()) + "/");
    }
}


@RestControllerAdvice
class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception exception) {
        logger.error("error:", exception);
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).contentType(MediaType.APPLICATION_JSON).body(new Result.Builder().rspCode(0).rspInfo("服务器异常").buildJsonString());
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<String> appExceptionHandler(AppException appException) {
        if (appException.getErrorCode() == 80401) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).body(new Result.Builder().rspCode(80401).rspInfo(appException.getMessage()).buildJsonString());
        } else {
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).contentType(MediaType.APPLICATION_JSON).body(new Result.Builder().rspCode(0).rspInfo(appException.getMessage()).buildJsonString());
        }
    }
}


@Configuration
@EnableAsync
class AppScheduled {
    private static final Logger logger = LoggerFactory.getLogger(AppScheduled.class);

    @Bean
    public Executor taskExecutor() {
        int corePoolSize = 10;
        int maxPoolSize = 200;
        int queueCapacity = 10;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }

    @Async
    @Scheduled(cron = "0 * * * * ? ")
    void questionnaireScheduled() {
        if (!"dev".equals(AppCache.getConfigValue("serverType", "dev"))) {
            logger.info("|定时任务|");
        }
    }
}

@Aspect
@Component
class ServiceAop {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

}
