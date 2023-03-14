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
