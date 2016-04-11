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


You can configure your pom.xml to adjust your dependencies


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

	public class Greeting{...}   Define Controller 

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
  


## Spring JPA ##



 
