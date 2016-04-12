# myBootApp #
This a sample tutorial of **Spring Boot 4.x** (based on samples from Spring official site). Since samples from Spring official site are separated. So:
- I create single one to integrate them all
- Also, as a practice of coding. (Talking is cheap, give me the code...)
	 
## Setup ##
You need following tools to start.
- STS: Spring Tool Suit
- Samples from Spring site (to refer)
- Maven 3.x (dependency and build)

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

**Note**
You can configure your pom.xml to adjust your dependencies accordingly.


## Spring Secrity ##

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
  


## Spring JPA - Repository REST ##

Provide REST API for Person entity from person table (h2 memory table)

		http://localhost:8080/people

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
		public class Person {...}


* Define REST Repository (DAO)  

		@RepositoryRestResource(collctionResourceRel = "people", path = "people")
		public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {
		
			List<Person> findByLastName(@Param("name") String name);
		
		}

* Disable CSRF (or cannot get/post using Postman/Curl)

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
			.csrf().disable();
		}


* Use **POSTMAN** or **cURL** to manipulate ... (Add a person)

		POST /people HTTP/1.1
		Host: localhost:8080
		Content-Type: application/json
		Cache-Control: no-cache
		Postman-Token: 47baf1c3-4785-5ba9-ac4b-a3714ff43fcf
		
		{
		    "firstName":"Jason",
		    "lastName":"Shi"
		}
 

### Spring Security - Customized UserDetailsService ###
In memory user/password/role is not suitable for production environment. We need to get them from database or somewhere else.
So you could define your own authentication service:
 
	 public class MyUserDetailService implements UserDetailsService {
	
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

## TODO ##
* Still have problem with HttpSecurity syntax, comment ApiWebSecurityConfigurerAdapter otherwise, it would have problem.

