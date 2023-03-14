- Spring IOC에 있는 Bean 목록 보는 방법
  - ``` java
    public static void main(String[] args) {
            ConfigurableApplicationContext applicationContext = SpringApplication.run(BankApplication.class, args);
            String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
            for (String beanName : beanDefinitionNames) {
                System.out.println(beanName);
            }
        }
    ```

- ExceptionTranslationFilter
  - 인증, 권한 관련된 에러가 발생하면 ExceptionTranslationFilter가 가로챔
  - 
