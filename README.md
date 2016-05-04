# Spring Framework/Boot Sample #
This is a sample tutorial of **Spring Framework 4.x** (based on **Spring Boot**). Since samples from Spring official site are independent which is hard to have a big landscape from top, so:
- I create this single one project to integrate them all
- Also, as a practice of coding. (Talk is cheap, show me the code...)

Basically, it is an back-end side project, following topics will be covered:
* Spring Boot
* Spring MVC
* Spring Data
* Spring REST
* Spring Security
* Spring Test - TBC
	 
## Spring Boot - Setup ##
You need following tools to start:
- [STS](https://spring.io/tools/): Spring Tool Suit
- [Maven 3.x](http://maven.apache.org/download.cgi) (dependency and build)
- [Postman](http://www.getpostman.com/apps)
- Spring [Guide](https://spring.io/guides) and [Samples](https://github.com/spring-projects/spring-data-examples)

Then, you need to create a boot project from STS.
- File > New > **Spring Starter Project** ...
- Maven config
- Boot config, such as: boot version, dependencies (JPA, Security, REST...)


		// Spring Boot will generate a startup class for you automatically
		@SpringBootApplication
		public class WebappApplication {
		
			public static void main(String[] args) {
				SpringApplication.run(WebappApplication.class, args);
			}
		}

**Note**: You can configure your pom.xml to adjust your dependencies accordingly later.

##### Project Structure #####
	+ Project Name
	
		[BACK-END]
		- src/main/java
			- com.jasonshi.sample
				- config
				- controller
				- dto
				- init
				- entity
				- repository
				- security
				- service					
				
				
		- src/main/resources
			- template
		- src/test/java
		
		
		[FRONT-END]
		- src/main
			- webapp
				- resources
					- css
					- img
					- js
					- .html
			- test
			
		[OTHER]
		- pom.xml
		- README.md



---
## Spring Security ##

		@Configurable
		@EnableWebSecurity
		public class WebappSecurity extends WebSecurityConfigurerAdapter{
			
			protected void configure(HttpSecurity http) throws Exception {
				// filter and permit url
			}
			
			@Autowired
			public void configure(AuthenticationManagerBuilder auth) throws Exception {
				// auth in memory or database ...
			}						
		} 
## Spring Security - Customized UserDetailsService ##
In memory user/password/role is not suitable for production environment. We need to get them from database or somewhere else.
So you could define your own authentication service:
 
	 public class CustomizedUserDetailsService implements UserDetailsService {
	
		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			// Use Service -> Repository (JPA/JDBC) to get data and encapsulate as UserDetails 
		}
	}

Set this UserDetailsService into Security configuration.

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}		
		 
## Spring REST ##
Provide REST API and return json as response

	http://localhost:8080/greeting?name=Jason
	
	
	{
		"id":2,
		"content":"Hello, Jason!"
	}
	

### How? ###
* Define Model

	public class Greeting{...}
	
* Define Controller 

	@RestController
	public class GreetingController {
		private static final String template = "Hello, %s!";
		private AtomicLong counter = new AtomicLong();
		@RequestMapping("/greeting")
		public Greeting greeting(@RequestParam(value = "name", defaultValue = "King of the world") String name) {
				return new Greeting(counter.incrementAndGet(), String.format(template, name));
		}
	}
			
* Configure to scan controllers from package

	@Configuration
	@EnableWebMvc
	@ComponentScan("com.jasonshi.sample.controller")
	public class WebappMVC extends WebMvcConfigurerAdapter {...}
  


## Spring DATA - Repository REST ##

Provide REST API for Device entity from **Table Level** (h2 memory table)

		http://localhost:8080/device

POM:

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
		</dependency>
		
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-rest</artifactId>
       </dependency>
 
### How? ###

* Define Entity

		@Entity
		public class Device {...}


* Define REST Repository (DAO)  

		@RepositoryRestResource(collctionResourceRel = "device", path = "device")
		public interface DeviceRepository extends PagingAndSortingRepository<Device, Long> {
		
			List<Device> findByName(@Param("name") String name);
		
		}

* **Disable CSRF** for Basic authentication (or cannot get/post using Postman/Curl)

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
			.csrf().disable();
			...
		}


* Use **POSTMAN** or **cURL** to manipulate ... (Add a device)

		POST /device HTTP/1.1
		Host: localhost:8080
		Content-Type: application/json
		Cache-Control: no-cache
		Postman-Token: 47baf1c3-4785-5ba9-ac4b-a3714ff43fcf
		
		{
		    "name":"device1",
		    "desc":"this is device 1"
		}
 **NOTE: **This REST API is not secured. We will secure it with Basic Authentication Later.
 

[Spring REST Security](https://github.com/spring-projects/spring-data-examples/tree/master/rest/security)

### Spring JPA ###
Still a lot to learn the specification of JPA. TBC... 
* @Entity, @OneToMany, @ManyToMany, @JoinTable @JoinTable
* CrudRepository and its sub-classes 


## Spring MVC ##
### Repository ###
Entity is a Java Object mapping to table of database. Repository is an entity manipulator which should be pure and should not contain business logic.

	/** @Repository will only be needed here, but if you want to provide REST API for this repo, you can use @RepositoryRestResource */
	@RepositoryRestResource(collectionResourceRel = "device", path = "device")
	public interface DeviceRepository extends PagingAndSortingRepository<Device, Long> {
		List<Device> findByName(@Param("name") String name);
	}
	
### Service ###
Service is a reply on Repository and provide business extra logic, such as: check, exception, extra transition ...

	@Service
	public class DeviceService {
		
		@Autowired
		private DeviceRepository deviceRepo;
		
		@Transactional(readOnly = true)
		public List<Device> findDevice(String name) {}
	}
	
### Controller ###
Controller is a binder of Model and View
* Receive HTTPRequest
* Process... (invoke Services)
* Send back HTTPResponse

	@Controller
	@RequestMapping("dvc") // besides \device, this is another REST API
	public class DeviceController {
		@Autowired
		private DeviceService deviceService;
	
		@ResponseBody
		@ResponseStatus(HttpStatus.OK)
		@RequestMapping(method = RequestMethod.DELETE)
		public void deleteDevice(@RequestParam(value = "deviceId", defaultValue = "1") Long deviceId) {
			deviceService.deleteDevice(deviceId);
		}
	}

	
	Method: DELETE
	http://localhost:8080/devices?id=1

## Spring TEST ##
Spring is born for unit test. Why is that:
* Unit test's essential job is to: test its own logic.
* And eliminate the influence of other references. (use mock)

That's what Spring do: Using DI and AOP to loose coupling. All references are **@Autowired** 
* We just need to (define and) use the interface  
* Then use mockito to mock whatever(implementation of interface) we want


## Other projects ##
### Lombok ###
[Lombok](https://projectlombok.org/) is project to simplify your POJO class.
* Need to install for IDE (such as Eclipse or STS)
* Import to project

	@Data
	@NoArgsConstructor
	public class Device {
		private long id;
		private String name;
		private String desc;
	}


[Tutorial](http://jnb.ociweb.com/jnb/jnbJan2010.html)

## Streaming way ##
* By default, the controller is **asynchronized**: Need to check whether it has problem or not
* ObjectInputStream, ObjectOutStream:
	
		Client Side: get from URL connection
	
		Server side: get from HttpServletRequest and HttpServletResponse
	
## BEST PRACTICE ##
### Security - Support both FormLogin & Basic Auth ###

[Stackoverflow Answer](http://stackoverflow.com/questions/27774742/spring-security-http-basic-for-restful-and-formlogin-cookies-for-web-annotat)
* Beware of the annotation, should be the same!

## BOOKS ##
* Spring in Action (4th Edition)

## TODO LIST ##
* Basic Authentication config: support multiple rest api
* Multiple data sources
* Spring Test
* Project init some data

* DetailsService: Sill need to write (Service), Repository(DAO) to get user/role from database. 

* HttpSecurity interceptor syntax 

* Need to think about @RequestParam, @RequestBody and REST handling in Controller!


> 引用的文字 

